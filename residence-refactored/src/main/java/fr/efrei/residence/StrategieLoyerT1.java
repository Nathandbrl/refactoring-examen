package fr.efrei.residence;

public class StrategieLoyerT1 implements StrategieLoyerType {

    private static final double LOYER_PAR_METRE_CARRE = 13;
    private static final double SUPPLEMENT = 50;
    private static final double COEFFICIENT_MENAGE = 1.08;
    private static final double CHARGES = 35;

    @Override
    public double loyerDeBase(Logement logement) {
        return logement.getSurface() * LOYER_PAR_METRE_CARRE + SUPPLEMENT;
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
        compteurs.ajouterT1(!logement.estLibre());
    }
}
