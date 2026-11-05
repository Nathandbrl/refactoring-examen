package fr.efrei.residence;

public class StrategieLoyerStudio implements StrategieLoyerType {

    private static final double LOYER_PAR_METRE_CARRE = 14;
    private static final double LOYER_MINIMUM = 350;
    private static final double COEFFICIENT_MENAGE = 1.08;
    private static final double CHARGES = 35;

    @Override
    public double loyerDeBase(Logement logement) {
        return Math.max(logement.getSurface() * LOYER_PAR_METRE_CARRE, LOYER_MINIMUM);
    }

    @Override
    public double coefficientMenage() {
        return COEFFICIENT_MENAGE;
    }

    @Override
    public boolean fibreIncluse() {
        return false;
    }

    @Override
    public double charges() {
        return CHARGES;
    }

    @Override
    public void ajouterOccupation(Logement logement, CompteursOccupation compteurs) {
        compteurs.ajouterStudio(!logement.estLibre());
    }
}
