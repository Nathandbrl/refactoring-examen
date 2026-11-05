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
    private static final int ECHELON_BOURSE_MIN = 0;
    private static final int ECHELON_BOURSE_MAX = 7;
    private static final int PLACES_COLOCATION_MIN = 2;
    private static final int JOUR_ECHEANCE_LOYER = 5;
    private static final double TAUX_PENALITE_RETARD = 0.05;
    private static final double PENALITE_RETARD_MINIMALE = 10;
    private static final int DUREE_PREAVIS_COURT_JOURS = 15;
    private static final int DUREE_PREAVIS_STANDARD_JOURS = 30;
    private static final int DUREE_RESTITUTION_CAUTION_JOURS = 30;
    private static final double MULTIPLICATEUR_CAUTION_INTERNATIONAL = 2;
    private static final double MULTIPLICATEUR_SOLVABILITE_ALTERNANT = 2;
    private static final double MULTIPLICATEUR_SOLVABILITE_STANDARD = 3;
    private static final double FRAIS_DOSSIER_STANDARD = 100;
    private static final double FRAIS_DOSSIER_INTERNATIONAL = 150;

    public static int compteurBail = 0;

    private Database db = Database.getInstance();
    private CalculateurLoyer calculateur = new CalculateurLoyer();

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
        if (profil == ProfilEtudiant.BOURSIER
                && (echelonBourse < ECHELON_BOURSE_MIN || echelonBourse > ECHELON_BOURSE_MAX)) {
            System.out.println("Inscription refusée : échelon de bourse invalide");
            return null;
        }
        if (profil == ProfilEtudiant.INTERNATIONAL && (telephone == null || !telephone.startsWith("+"))) {
            System.out.println("Inscription refusée : numéro international (+XX) obligatoire");
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
        Logement l;
        switch (type) {
            case STUDIO:
                l = new Studio(numero, surface, etage, meuble);
                break;
            case T1:
                l = new T1(numero, surface, etage, meuble);
                break;
            case T2:
                l = new T2(numero, surface, etage, meuble, param);
                break;
            case COLOCATION:
                l = new Colocation(numero, surface, etage, meuble, Math.max(param, PLACES_COLOCATION_MIN));
                break;
            default:
                throw new IllegalStateException("Type de logement non pris en charge : " + type);
        }
        Residence.logements.add(l);
        System.out.println("Logement ajouté : " + numero + " (" + type + ")");
        return l;
    }

    // ------------------------------------------------------------------ attribution

    public Bail attribuerLogement(Etudiant e, TypeLogementInterne typeSouhaite, Set<OptionLogement> options,
                                  LocalDate dateSignature) {
        System.out.println("--- Attribution d'un " + typeSouhaite + " à " + e.getNomComplet());
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

            // solvabilité
            boolean solvable;
            if (e.getProfil() == ProfilEtudiant.BOURSIER) {
                solvable = true; // garantie Visale
            } else if (e.getProfil() == ProfilEtudiant.ALTERNANT) {
                solvable = e.getRessourcesMensuelles() >= total * MULTIPLICATEUR_SOLVABILITE_ALTERNANT;
            } else {
                solvable = e.getRessourcesMensuelles() >= total * MULTIPLICATEUR_SOLVABILITE_STANDARD;
            }
            if (!solvable) {
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

        // dépôt de garantie
        double caution;
        if (e.getProfil() == ProfilEtudiant.BOURSIER) {
            caution = 0;
        } else if (e.getProfil() == ProfilEtudiant.INTERNATIONAL) {
            caution = loyer * MULTIPLICATEUR_CAUTION_INTERNATIONAL;
        } else {
            caution = loyer;
        }
        b.setCaution(Utils.arrondir(caution));

        // frais de dossier
        if (e.getProfil() == ProfilEtudiant.BOURSIER) {
            b.setFraisDossier(0);
        } else if (e.getProfil() == ProfilEtudiant.INTERNATIONAL) {
            b.setFraisDossier(FRAIS_DOSSIER_INTERNATIONAL);
        } else {
            b.setFraisDossier(FRAIS_DOSSIER_STANDARD);
        }

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
            if (b.getEtudiant().getProfil() != ProfilEtudiant.BOURSIER) {
                penalite = b.getLoyerChargesComprises() * TAUX_PENALITE_RETARD;
                if (penalite < PENALITE_RETARD_MINIMALE) penalite = PENALITE_RETARD_MINIMALE;
                penalite = Utils.arrondir(penalite);
            }
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
        int jours;
        ProfilEtudiant profil = b.getEtudiant().getProfil();
        if (profil == ProfilEtudiant.BOURSIER || profil == ProfilEtudiant.ALTERNANT) {
            jours = DUREE_PREAVIS_COURT_JOURS;
        } else {
            jours = DUREE_PREAVIS_STANDARD_JOURS;
        }
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
            switch (canal) {
                case EMAIL:
                    EmailService.envoyer(e.getEmail(), "RELANCE - loyer impayé",
                            "Bonjour " + e.getPrenom() + ", il reste " + Utils.euros(b.soldeDu) + " à régler sur le bail " + b.getId() + ".");
                    break;
                case SMS:
                    new SmsService().envoyerSms(e.getTelephone(),
                            "Campus Vert : " + Utils.euros(b.soldeDu) + " impayés sur " + b.getId());
                    break;
                case COURRIER:
                    new CourrierService().imprimerEtPoster(e.getNomComplet(), e.getAdresse(), "Relance loyer impayé",
                            "Il reste " + Utils.euros(b.soldeDu) + " à régler sur le bail " + b.getId() + ".");
                    break;
            }
        }
    }

    public void afficherRapport() {
        System.out.println();
        System.out.println(genererRapport());
    }

    public String genererRapport() {
        String r = "";
        r += "=============== RAPPORT " + Residence.NOM + " ===============" + "\n";
        int nbStudios = 0, nbStudiosOccupes = 0;
        int nbT1 = 0, nbT1Occupes = 0;
        int nbT2 = 0, nbT2Occupes = 0;
        int nbPlacesColoc = 0, nbPlacesColocOccupees = 0;
        double surfaceBalcons = 0;
        for (Logement l : Residence.logements) {
            if (l.getType() == TypeLogementInterne.STUDIO) {
                nbStudios++;
                if (!l.estLibre()) nbStudiosOccupes++;
            } else if (l.getType() == TypeLogementInterne.T1) {
                nbT1++;
                if (!l.estLibre()) nbT1Occupes++;
            } else if (l.getType() == TypeLogementInterne.T2) {
                nbT2++;
                if (!l.estLibre()) nbT2Occupes++;
            } else if (l.getType() == TypeLogementInterne.COLOCATION) {
                nbPlacesColoc += l.getNombrePlaces();
                nbPlacesColocOccupees += l.getColocataires().size();
            }
            try {
                surfaceBalcons += l.getSurfaceBalcon();
            } catch (UnsupportedOperationException ex) {
            }
        }
        r += "Studios      : " + nbStudiosOccupes + "/" + nbStudios + "\n";
        r += "T1           : " + nbT1Occupes + "/" + nbT1 + "\n";
        r += "T2           : " + nbT2Occupes + "/" + nbT2 + "\n";
        r += "Colocation   : " + nbPlacesColocOccupees + "/" + nbPlacesColoc + " places" + "\n";
        int total = nbStudios + nbT1 + nbT2 + nbPlacesColoc;
        int occupes = nbStudiosOccupes + nbT1Occupes + nbT2Occupes + nbPlacesColocOccupees;
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
        if (canal == CanalNotification.EMAIL) {
            EmailService.envoyer(e.getEmail(), sujet, message);
        } else if (canal == CanalNotification.SMS) {
            SmsService sms = new SmsService();
            sms.envoyerSms(e.getTelephone(), sujet + " - " + message);
        } else if (canal == CanalNotification.COURRIER) {
            CourrierService courrier = new CourrierService();
            courrier.imprimerEtPoster(e.getNomComplet(), e.getAdresse(), sujet, message);
        }
        // les internationaux reçoivent toujours un email en plus (demande de l'administration)
        if (e.getProfil() == ProfilEtudiant.INTERNATIONAL && canal != CanalNotification.EMAIL) {
            EmailService.envoyer(e.getEmail(), "[EN] " + sujet, message);
        }
    }
}
