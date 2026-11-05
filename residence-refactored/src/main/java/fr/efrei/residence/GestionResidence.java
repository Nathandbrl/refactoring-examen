package fr.efrei.residence;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;

/**
 * Service principal de la résidence : inscriptions, attributions, baux, loyers, préavis, sorties.
 */
public class GestionResidence {

    private static final String PREFIXE_ID_BAIL = "BAIL-";
    private static final int LARGEUR_ID_BAIL = 3;
    private static final double CENT_POUR_CENT = 100.0;
    private static final int JOUR_ECHEANCE_LOYER = 5;
    private static final int DUREE_RESTITUTION_CAUTION_JOURS = 30;

    public static int compteurBail = 0;

    private Database db = Database.getInstance();
    private CalculateurLoyer calculateur = new CalculateurLoyer();
    private FabriqueLogement fabriqueLogement = new FabriqueLogement();
    private FabriqueNotification fabriqueNotification = new FabriqueNotification();
    private FabriqueStrategieProfil fabriqueStrategieProfil = new FabriqueStrategieProfil();
    private FabriqueStrategieLoyer fabriqueStrategieLoyer = new FabriqueStrategieLoyer();

    // ------------------------------------------------------------------ étudiants

    public Etudiant inscrireEtudiant(String nom, String prenom, String email, String telephone, String adresse,
                                     ProfilEtudiant profil, int echelonBourse, double ressources, boolean pmr,
                                     CanalNotification canalPrefere) {
        if (nom == null || nom.trim().isEmpty()) {
            System.out.println("Inscription refusée : nom obligatoire");
            return null;
        }
        if (!Utils.emailValide(email)) {
            System.out.println("Inscription refusée : email invalide (" + email + ")");
            return null;
        }
        if (db.trouverEtudiantParEmail(email) != null) {
            System.out.println("Inscription refusée : " + email + " déjà inscrit");
            return null;
        }
        if (profil == null) {
            System.out.println("Inscription refusée : profil inconnu " + profil);
            return null;
        }
        String erreurProfil = fabriqueStrategieProfil.creer(profil).erreurInscription(telephone, echelonBourse);
        if (erreurProfil != null) {
            System.out.println(erreurProfil);
            return null;
        }
        if (canalPrefere == CanalNotification.SMS && telephone == null) {
            System.out.println("Inscription refusée : téléphone obligatoire pour les SMS");
            return null;
        }
        if (canalPrefere == CanalNotification.COURRIER && (adresse == null || adresse.isEmpty())) {
            System.out.println("Inscription refusée : adresse obligatoire pour le courrier");
            return null;
        }
        if (canalPrefere == null) {
            System.out.println("Inscription refusée : canal inconnu " + canalPrefere);
            return null;
        }
        Etudiant e = new Etudiant(nom, prenom, email, telephone, adresse, profil, echelonBourse, ressources, pmr,
                canalPrefere);
        db.sauvegarderEtudiant(e);
        System.out.println("Étudiant inscrit : " + e.getId() + " " + e.getNomComplet() + " (" + profil + ")");
        return e;
    }

    // ------------------------------------------------------------------ logements

    public Logement ajouterLogement(TypeLogementInterne type, String numero, double surface, int etage,
                                    boolean meuble, int param) {
        if (Residence.trouver(numero) != null) {
            System.out.println("Logement " + numero + " existe déjà");
            return null;
        }
        if (type == null) {
            System.out.println("Type de logement inconnu : " + type);
            return null;
        }
        Logement l = fabriqueLogement.creer(type, numero, surface, etage, meuble, param);
        Residence.logements.add(l);
        System.out.println("Logement ajouté : " + numero + " (" + type + ")");
        return l;
    }

    // ------------------------------------------------------------------ attribution

    public Bail attribuerLogement(Etudiant e, TypeLogementInterne typeSouhaite, Set<OptionLogement> options,
                                  LocalDate dateSignature) {
        System.out.println("--- Attribution d'un " + typeSouhaite + " à " + e.getNomComplet());
        StrategieProfil strategieProfil = fabriqueStrategieProfil.creer(e.getProfil());
        Logement choisi = null;
        double loyer = 0;
        double charges = 0;
        for (Logement l : Residence.logements) {
            if (l.getType() != typeSouhaite) continue;
            if (!l.estLibre()) continue;
            if (e.isPmr() && !l.estAccessiblePMR()) continue;

            double loyerCandidat = calculateur.calculerLoyer(l, e.getProfil(), e.getEchelonBourse(), options);
            double chargesCandidat = calculateur.calculerCharges(l);
            double total = loyerCandidat + chargesCandidat;

            if (!strategieProfil.estSolvable(e.getRessourcesMensuelles(), total)) {
                System.out.println("  " + l.getNumero() + " : ressources insuffisantes (" + Utils.euros(total) + "/mois)");
                continue;
            }
            choisi = l;
            loyer = loyerCandidat;
            charges = chargesCandidat;
            break;
        }
        if (choisi == null) {
            System.out.println("  Aucun logement disponible pour " + e.getNomComplet());
            return null;
        }

        if (choisi instanceof Colocation) {
            choisi.ajouterColocataire(e);
        } else {
            choisi.loger(e);
        }

        compteurBail++;
        String id = PREFIXE_ID_BAIL + String.format("%0" + LARGEUR_ID_BAIL + "d", compteurBail);
        Bail b = new Bail(id, e, choisi, dateSignature);
        b.options.addAll(options);
        b.setLoyerHorsCharges(loyer);
        b.setCharges(charges);

        b.setCaution(Utils.arrondir(strategieProfil.calculerCaution(loyer)));
        b.setFraisDossier(strategieProfil.fraisDossier());

        db.sauvegarderBail(b);
        System.out.println("  Bail " + id + " signé : " + choisi.getNumero() + ", " + Utils.euros(b.getLoyerChargesComprises())
                + " CC/mois, caution " + Utils.euros(b.getCaution()) + ", frais de dossier " + Utils.euros(b.getFraisDossier()));
        notifier(e, "Votre bail " + id,
                "Bienvenue à la " + Residence.NOM + " ! Logement " + choisi.getNumero() + ", loyer "
                        + Utils.euros(b.getLoyerChargesComprises()) + " charges comprises.");
        return b;
    }

    // ------------------------------------------------------------------ cycle de vie du bail

    public boolean entrerDansLesLieux(String idBail, LocalDate date) {
        Bail b = db.trouverBail(idBail);
        if (b == null || b.getStatut() != StatutBailInterne.SIGNE) {
            System.out.println("Entrée impossible pour " + idBail);
            return false;
        }
        if (date.isBefore(b.getDateSignature())) {
            System.out.println("Entrée impossible avant la signature du bail");
            return false;
        }
        b.setDateEntree(date);
        b.setStatut(StatutBailInterne.ACTIF);
        db.mettreAJourBail(b);
        System.out.println("Entrée dans les lieux : " + idBail + " le " + Utils.date(date));
        notifier(b.getEtudiant(), "État des lieux d'entrée",
                "Votre état des lieux d'entrée du logement " + b.getLogement().getNumero() + " est fait le "
                        + Utils.date(date) + ". Remise des clés effectuée. Pensez à souscrire une assurance habitation si ce n'est pas déjà fait.");
        return true;
    }

    public boolean payerLoyer(String idBail, String mois, LocalDate datePaiement, double montant) {
        Bail b = db.trouverBail(idBail);
        if (b == null || !(b.getStatut() == StatutBailInterne.ACTIF
                || b.getStatut() == StatutBailInterne.PREAVIS)) {
            System.out.println("Paiement refusé pour " + idBail);
            return false;
        }
        LocalDate echeance = YearMonth.parse(mois).atDay(JOUR_ECHEANCE_LOYER);
        double penalite = 0;
        if (datePaiement.isAfter(echeance)) {
            penalite = Utils.arrondir(fabriqueStrategieProfil.creer(b.getEtudiant().getProfil())
                    .penaliteRetard(b.getLoyerChargesComprises()));
        }
        double du = Utils.arrondir(b.getLoyerChargesComprises() + penalite);
        b.paiements.add(new Paiement(mois, datePaiement, montant, penalite, du));
        b.soldeDu = Utils.arrondir(b.soldeDu + du - montant);
        db.mettreAJourBail(b);

        System.out.println("Paiement " + idBail + " " + mois + " : " + Utils.euros(montant) + " / " + Utils.euros(du)
                + (penalite > 0 ? " (dont pénalité " + Utils.euros(penalite) + ")" : ""));
        if (montant < du) {
            notifier(b.getEtudiant(), "Paiement incomplet " + mois,
                    "Nous avons reçu " + Utils.euros(montant) + " sur " + Utils.euros(du) + ". Reste dû : "
                            + Utils.euros(Utils.arrondir(du - montant)) + ".");
        } else {
            notifier(b.getEtudiant(), "Quittance " + mois, "Merci, votre loyer de " + mois + " est réglé.");
        }
        return true;
    }

    public boolean donnerPreavis(String idBail, LocalDate date) {
        Bail b = db.trouverBail(idBail);
        if (b == null || b.getStatut() != StatutBailInterne.ACTIF) {
            System.out.println("Préavis impossible pour " + idBail);
            return false;
        }
        int jours = fabriqueStrategieProfil.creer(b.getEtudiant().getProfil()).dureePreavisJours();
        b.setDateFin(date.plusDays(jours));
        b.setStatut(StatutBailInterne.PREAVIS);
        db.mettreAJourBail(b);
        System.out.println("Préavis " + idBail + " : fin du bail le " + Utils.date(b.getDateFin()));
        notifier(b.getEtudiant(), "Préavis de départ",
                "Votre préavis de " + jours + " jours est enregistré. Fin du bail le " + Utils.date(b.getDateFin()) + ".");
        return true;
    }

    public boolean sortir(String idBail, LocalDate date, double retenuesDegats) {
        Bail b = db.trouverBail(idBail);
        if (b == null || b.getStatut() != StatutBailInterne.PREAVIS) {
            System.out.println("Sortie impossible pour " + idBail);
            return false;
        }
        if (date.isBefore(b.getDateFin())) {
            System.out.println("Sortie impossible avant la fin du préavis (" + Utils.date(b.getDateFin()) + ")");
            return false;
        }
        Logement l = b.getLogement();
        if (l instanceof Colocation) {
            l.retirerColocataire(b.getEtudiant());
            l.nettoyerPartiesCommunes();
        } else {
            l.liberer(b.getEtudiant());
        }

        double impayes = b.soldeDu > 0 ? b.soldeDu : 0;
        double restitution = b.getCaution() - retenuesDegats - impayes;
        double resteAPayer = 0;
        if (restitution < 0) {
            resteAPayer = -restitution;
            restitution = 0;
        }
        b.retenues = Utils.arrondir(retenuesDegats);
        b.cautionRestituee = Utils.arrondir(restitution);
        b.soldeDu = Utils.arrondir(resteAPayer);
        b.setStatut(StatutBailInterne.TERMINE);
        db.mettreAJourBail(b);

        System.out.println("Sortie " + idBail + " le " + Utils.date(date) + " : caution " + Utils.euros(b.getCaution())
                + ", retenues " + Utils.euros(b.retenues) + ", impayés " + Utils.euros(impayes)
                + " -> restitué " + Utils.euros(b.cautionRestituee));
        String message = "Votre dépôt de garantie : " + Utils.euros(b.cautionRestituee)
                + " vous sera restitué sous " + DUREE_RESTITUTION_CAUTION_JOURS + " jours.";
        if (resteAPayer > 0) {
            message = message + " Il reste " + Utils.euros(b.soldeDu) + " à régler.";
        }
        notifier(b.getEtudiant(), "Sortie du logement " + l.getNumero(), message);
        return true;
    }

    public boolean annulerBail(String idBail) {
        Bail b = db.trouverBail(idBail);
        if (b == null || b.getStatut() != StatutBailInterne.SIGNE) {
            System.out.println("Annulation impossible pour " + idBail);
            return false;
        }
        Logement l = b.getLogement();
        if (l instanceof Colocation) {
            l.retirerColocataire(b.getEtudiant());
        } else {
            l.liberer(b.getEtudiant());
        }
        b.setStatut(StatutBailInterne.ANNULE);
        db.mettreAJourBail(b);
        System.out.println("Bail " + idBail + " annulé, frais de dossier conservés : " + Utils.euros(b.getFraisDossier()));
        notifier(b.getEtudiant(), "Annulation du bail " + idBail,
                "Votre bail est annulé. Les frais de dossier (" + Utils.euros(b.getFraisDossier()) + ") restent acquis.");
        return true;
    }

    // ------------------------------------------------------------------ relances et rapport

    public void relancerImpayes() {
        System.out.println("--- Relance des impayés");
        for (Bail b : db.tousLesBaux()) {
            if (b.soldeDu <= 0) continue;
            if (b.getStatut() == StatutBailInterne.ANNULE) continue;
            Etudiant e = b.getEtudiant();
            CanalNotification canal = e.getCanalPrefere();
            fabriqueNotification.creer(canal).relancerImpayes(e, b);
        }
    }

    public void afficherRapport() {
        System.out.println();
        System.out.println(genererRapport());
    }

    public String genererRapport() {
        String r = "";
        r += "=============== RAPPORT " + Residence.NOM + " ===============" + "\n";
        CompteursOccupation compteurs = new CompteursOccupation();
        double surfaceBalcons = 0;
        for (Logement l : Residence.logements) {
            fabriqueStrategieLoyer.creer(l.getType()).ajouterOccupation(l, compteurs);
            try {
                surfaceBalcons += l.getSurfaceBalcon();
            } catch (UnsupportedOperationException ex) {
            }
        }
        r += "Studios      : " + compteurs.studiosOccupes() + "/" + compteurs.studios() + "\n";
        r += "T1           : " + compteurs.t1Occupes() + "/" + compteurs.t1() + "\n";
        r += "T2           : " + compteurs.t2Occupes() + "/" + compteurs.t2() + "\n";
        r += "Colocation   : " + compteurs.placesColocationOccupees() + "/"
                + compteurs.placesColocation() + " places" + "\n";
        int total = compteurs.studios() + compteurs.t1() + compteurs.t2()
                + compteurs.placesColocation();
        int occupes = compteurs.studiosOccupes() + compteurs.t1Occupes() + compteurs.t2Occupes()
                + compteurs.placesColocationOccupees();
        r += "Occupation   : " + Math.round(occupes * CENT_POUR_CENT / total) + " %" + "\n";
        r += "Balcons      : " + surfaceBalcons + " m²" + "\n";

        double loyersMensuels = 0;
        double impayes = 0;
        double cautions = 0;
        List<Bail> baux = db.tousLesBaux();
        for (Bail b : baux) {
            if (b.getStatut() == StatutBailInterne.ACTIF || b.getStatut() == StatutBailInterne.PREAVIS) {
                loyersMensuels += b.getLoyerChargesComprises();
                cautions += b.getCaution();
            }
            if (b.soldeDu > 0) impayes += b.soldeDu;
        }
        r += "Loyers CC    : " + Utils.euros(Utils.arrondir(loyersMensuels)) + " / mois" + "\n";
        r += "Cautions     : " + Utils.euros(Utils.arrondir(cautions)) + "\n";
        r += "Impayés      : " + Utils.euros(Utils.arrondir(impayes)) + "\n";
        r += "SMS envoyés  : " + SmsService.compteurSms + "\n";
        r += "Timbres      : " + Utils.euros(Utils.arrondir(CourrierService.totalAffranchissement)) + "\n";
        r += "============================================================";
        return r;
    }

    // ------------------------------------------------------------------ notifications

    private void notifier(Etudiant e, String sujet, String message) {
        CanalNotification canal = e.getCanalPrefere();
        fabriqueNotification.creer(canal).notifier(e, sujet, message);
        // les internationaux reçoivent toujours un email en plus (demande de l'administration)
        if (canal != CanalNotification.EMAIL
                && fabriqueStrategieProfil.creer(e.getProfil()).emailComplementaire()) {
            fabriqueNotification.creer(CanalNotification.EMAIL).notifier(e, "[EN] " + sujet, message);
        }
    }
}
