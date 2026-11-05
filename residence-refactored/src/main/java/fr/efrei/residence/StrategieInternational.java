package fr.efrei.residence;

public class StrategieInternational implements StrategieProfil {

    private static final double MULTIPLICATEUR_SOLVABILITE = 3;
    private static final double MULTIPLICATEUR_CAUTION = 2;
    private static final double FRAIS_DOSSIER = 150;
    private static final int DUREE_PREAVIS_JOURS = 30;
    private static final double TAUX_PENALITE_RETARD = 0.05;
    private static final double PENALITE_RETARD_MINIMALE = 10;

    @Override
    public double appliquerRemise(double loyer, int echelonBourse) {
        return loyer;
    }

    @Override
    public boolean estSolvable(double ressourcesMensuelles, double loyerChargesComprises) {
        return ressourcesMensuelles >= loyerChargesComprises * MULTIPLICATEUR_SOLVABILITE;
    }

    @Override
    public double calculerCaution(double loyerHorsCharges) {
        return loyerHorsCharges * MULTIPLICATEUR_CAUTION;
    }

    @Override
    public double fraisDossier() {
        return FRAIS_DOSSIER;
    }

    @Override
    public int dureePreavisJours() {
        return DUREE_PREAVIS_JOURS;
    }

    @Override
    public double penaliteRetard(double loyerChargesComprises) {
        return Math.max(loyerChargesComprises * TAUX_PENALITE_RETARD, PENALITE_RETARD_MINIMALE);
    }

    @Override
    public String erreurInscription(String telephone, int echelonBourse) {
        if (telephone == null || !telephone.startsWith("+")) {
            return "Inscription refusée : numéro international (+XX) obligatoire";
        }
        return null;
    }

    @Override
    public boolean emailComplementaire() {
        return true;
    }
}
