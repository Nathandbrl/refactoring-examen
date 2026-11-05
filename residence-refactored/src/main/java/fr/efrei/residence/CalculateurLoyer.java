package fr.efrei.residence;

import java.util.Set;

public class CalculateurLoyer {

    private static final double LOYER_STUDIO_PAR_METRE_CARRE = 14;
    private static final double LOYER_MINIMUM_STUDIO = 350;
    private static final double LOYER_T1_PAR_METRE_CARRE = 13;
    private static final double SUPPLEMENT_T1 = 50;
    private static final double LOYER_T2_PAR_METRE_CARRE = 12;
    private static final double SUPPLEMENT_T2 = 100;
    private static final double LOYER_COLOCATION_PAR_METRE_CARRE = 11;
    private static final int ETAGE_REZ_DE_CHAUSSEE = 0;
    private static final double COEFFICIENT_PLEIN_TARIF = 1;
    private static final double MAJORATION_MEUBLE = 1.15;
    private static final double REDUCTION_REZ_DE_CHAUSSEE = 0.95;
    private static final double MAJORATION_MENAGE = 1.08;
    private static final double MAJORATION_MENAGE_COLOCATION = 1.04;
    private static final double COUT_PARKING = 45;
    private static final double COUT_LAVERIE = 12;
    private static final double COUT_FIBRE = 20;
    private static final double COUT_ASSURANCE = 9;
    private static final double REMISE_PAR_ECHELON = 0.05;
    private static final double REMISE_BOURSIER_MAXIMALE = 0.30;
    private static final double REMISE_ALTERNANT = 0.10;
    private static final double CHARGES_STANDARD = 35;
    private static final double CHARGES_COLOCATION = 25;

    /**
     * Loyer mensuel hors charges, options comprises, remise déduite.
     */
    public double calculerLoyer(Logement l, ProfilEtudiant profil, int echelon, Set<OptionLogement> options) {
        // loyer de base selon le type
        double loyer;
        switch (l.getType()) {
            case STUDIO:
                loyer = Math.max(l.getSurface() * LOYER_STUDIO_PAR_METRE_CARRE, LOYER_MINIMUM_STUDIO);
                break;
            case T1:
                loyer = l.getSurface() * LOYER_T1_PAR_METRE_CARRE + SUPPLEMENT_T1;
                break;
            case T2:
                loyer = l.getSurface() * LOYER_T2_PAR_METRE_CARRE + SUPPLEMENT_T2;
                break;
            case COLOCATION:
                loyer = l.getSurface() * LOYER_COLOCATION_PAR_METRE_CARRE / l.getNombrePlaces();
                break;
            default:
                throw new IllegalStateException("Type de logement non pris en charge : " + l.getType());
        }

        if (l.isMeuble()) loyer = loyer * MAJORATION_MEUBLE;
        if (l.getEtage() == ETAGE_REZ_DE_CHAUSSEE) loyer = loyer * REDUCTION_REZ_DE_CHAUSSEE;

        // options
        if (options.contains(OptionLogement.MENAGE)) {
            if (l.getType() == TypeLogementInterne.COLOCATION) {
                loyer = loyer * MAJORATION_MENAGE_COLOCATION;
            } else {
                loyer = loyer * MAJORATION_MENAGE;
            }
        }
        if (options.contains(OptionLogement.PARKING)) loyer = loyer + COUT_PARKING;
        if (options.contains(OptionLogement.LAVERIE)) loyer = loyer + COUT_LAVERIE;
        if (options.contains(OptionLogement.FIBRE) && l.getType() != TypeLogementInterne.COLOCATION) {
            loyer = loyer + COUT_FIBRE;
        }
        if (options.contains(OptionLogement.ASSURANCE)) loyer = loyer + COUT_ASSURANCE;

        // remise selon le profil
        if (profil == ProfilEtudiant.BOURSIER) {
            double taux = echelon * REMISE_PAR_ECHELON;
            if (taux > REMISE_BOURSIER_MAXIMALE) taux = REMISE_BOURSIER_MAXIMALE;
            loyer = loyer * (COEFFICIENT_PLEIN_TARIF - taux);
        } else if (profil == ProfilEtudiant.ALTERNANT) {
            loyer = loyer * (COEFFICIENT_PLEIN_TARIF - REMISE_ALTERNANT);
        }

        return Utils.arrondir(loyer);
    }

    public double calculerCharges(Logement l) {
        if (l.getType() == TypeLogementInterne.COLOCATION) return CHARGES_COLOCATION;
        return CHARGES_STANDARD;
    }
}
