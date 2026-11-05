package fr.efrei.residence;

import java.time.LocalDate;
import java.util.Set;

public class AttributionService {

    private static final int PLACES_COLOCATION_MIN = 2;

    private final LogementRepository logements;
    private final BailRepository baux;
    private final GenerateurIdentifiants identifiants;
    private final FabriqueLogement fabriqueLogement;
    private final CalculateurLoyer calculateur;
    private final FabriqueStrategieProfil fabriqueStrategieProfil;
    private final GestionNotifications notifications;

    public AttributionService(LogementRepository logements, BailRepository baux, GenerateurIdentifiants identifiants,
                              FabriqueLogement fabriqueLogement, CalculateurLoyer calculateur,
                              FabriqueStrategieProfil fabriqueStrategieProfil, GestionNotifications notifications) {
        this.logements = logements;
        this.baux = baux;
        this.identifiants = identifiants;
        this.fabriqueLogement = fabriqueLogement;
        this.calculateur = calculateur;
        this.fabriqueStrategieProfil = fabriqueStrategieProfil;
        this.notifications = notifications;
    }

    public Logement ajouterLogement(TypeLogementInterne type, String numero, double surface, int etage,
                                    boolean meuble, int param) {
        if (logements.trouver(numero).isPresent()) {
            System.out.println("Logement " + numero + " existe déjà");
            return null;
        }
        if (type == null) {
            System.out.println("Type de logement inconnu : " + type);
            return null;
        }
        int parametre = type == TypeLogementInterne.COLOCATION ? Math.max(param, PLACES_COLOCATION_MIN) : param;
        Logement logement = fabriqueLogement.creer(type, numero, surface, etage, meuble, parametre);
        logements.ajouter(logement);
        System.out.println("Logement ajouté : " + numero + " (" + type + ")");
        return logement;
    }

    public Bail attribuer(Etudiant etudiant, TypeLogementInterne typeSouhaite, Set<OptionLogement> options,
                          LocalDate dateSignature) {
        System.out.println("--- Attribution d'un " + typeSouhaite + " à " + etudiant.getNomComplet());
        StrategieProfil strategieProfil = fabriqueStrategieProfil.creer(etudiant.getProfil());
        Logement choisi = null;
        double loyer = 0;
        double charges = 0;
        for (Logement logement : logements.tousLesLogements()) {
            if (logement.getType() != typeSouhaite || !logement.estLibre()) continue;
            if (etudiant.isPmr() && !logement.estAccessiblePMR()) continue;
            double loyerCandidat = calculateur.calculerLoyer(logement, etudiant.getProfil(),
                    etudiant.getEchelonBourse(), options);
            double chargesCandidat = calculateur.calculerCharges(logement);
            double total = loyerCandidat + chargesCandidat;
            if (!strategieProfil.estSolvable(etudiant.getRessourcesMensuelles(), total)) {
                System.out.println("  " + logement.getNumero() + " : ressources insuffisantes ("
                        + Utils.euros(total) + "/mois)");
                continue;
            }
            choisi = logement;
            loyer = loyerCandidat;
            charges = chargesCandidat;
            break;
        }
        if (choisi == null) {
            System.out.println("  Aucun logement disponible pour " + etudiant.getNomComplet());
            return null;
        }
        choisi.ajouterOccupant(etudiant);

        Bail bail = new Bail(identifiants.prochainIdBail(), etudiant, choisi, dateSignature);
        bail.options.addAll(options);
        bail.setLoyerHorsCharges(loyer);
        bail.setCharges(charges);
        bail.setCaution(Utils.arrondir(strategieProfil.calculerCaution(loyer)));
        bail.setFraisDossier(strategieProfil.fraisDossier());
        baux.sauvegarder(bail);
        System.out.println("  Bail " + bail.getId() + " signé : " + choisi.getNumero() + ", "
                + Utils.euros(bail.getLoyerChargesComprises()) + " CC/mois, caution "
                + Utils.euros(bail.getCaution()) + ", frais de dossier " + Utils.euros(bail.getFraisDossier()));
        notifications.notifier(etudiant, "Votre bail " + bail.getId(),
                "Bienvenue à la " + Residence.NOM + " ! Logement " + choisi.getNumero() + ", loyer "
                        + Utils.euros(bail.getLoyerChargesComprises()) + " charges comprises.");
        return bail;
    }
}
