package fr.efrei.residence;

public class StrategieAlternant implements StrategieProfil {

    private static final double REMISE = 0.10;
    private static final double MULTIPLICATEUR_SOLVABILITE = 2;
    private static final double FRAIS_DOSSIER = 100;
    private static final int DUREE_PREAVIS_JOURS = 15;
    private static final double TAUX_PENALITE_RETARD = 0.05;
    private static final double PENALITE_RETARD_MINIMALE = 10;

    @Override
    public double appliquerRemise(double loyer, int echelonBourse) {
        return loyer * (1 - REMISE);
    }

    @Override
    public boolean estSolvable(double ressourcesMensuelles, double loyerChargesComprises) {
        return ressourcesMensuelles >= loyerChargesComprises * MULTIPLICATEUR_SOLVABILITE;
    }

    @Override
    public double calculerCaution(double loyerHorsCharges) {
        return loyerHorsCharges;
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
        return null;
    }

    @Override
    public boolean emailComplementaire() {
        return false;
    }
}
