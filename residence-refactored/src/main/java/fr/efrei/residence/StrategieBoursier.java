package fr.efrei.residence;

public class StrategieBoursier implements StrategieProfil {

    private static final double REMISE_PAR_ECHELON = 0.05;
    private static final double REMISE_MAXIMALE = 0.30;
    private static final double LOYER_GARANTI_PAR_ETAT = 0;
    private static final int DUREE_PREAVIS_JOURS = 15;
    private static final int ECHELON_MIN = 0;
    private static final int ECHELON_MAX = 7;

    @Override
    public double appliquerRemise(double loyer, int echelonBourse) {
        double remise = Math.min(echelonBourse * REMISE_PAR_ECHELON, REMISE_MAXIMALE);
        return loyer * (1 - remise);
    }

    @Override
    public boolean estSolvable(double ressourcesMensuelles, double loyerChargesComprises) {
        return true;
    }

    @Override
    public double calculerCaution(double loyerHorsCharges) {
        return LOYER_GARANTI_PAR_ETAT;
    }

    @Override
    public double fraisDossier() {
        return 0;
    }

    @Override
    public int dureePreavisJours() {
        return DUREE_PREAVIS_JOURS;
    }

    @Override
    public double penaliteRetard(double loyerChargesComprises) {
        return 0;
    }

    @Override
    public String erreurInscription(String telephone, int echelonBourse) {
        if (echelonBourse < ECHELON_MIN || echelonBourse > ECHELON_MAX) {
            return "Inscription refusée : échelon de bourse invalide";
        }
        return null;
    }

    @Override
    public boolean emailComplementaire() {
        return false;
    }
}
