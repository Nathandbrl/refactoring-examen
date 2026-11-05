package fr.efrei.residence;

public class StrategieLoyerColocation implements StrategieLoyerType {

    private static final double LOYER_PAR_METRE_CARRE = 11;
    private static final double COEFFICIENT_MENAGE = 1.04;
    private static final double CHARGES = 25;

    @Override
    public double loyerDeBase(Logement logement) {
        return logement.getSurface() * LOYER_PAR_METRE_CARRE / logement.getNombrePlaces();
    }

    @Override
    public double coefficientMenage() {
        return COEFFICIENT_MENAGE;
    }

    @Override
    public boolean fibreIncluse() {
        return true;
    }

    @Override
    public double charges() {
        return CHARGES;
    }

    @Override
    public void ajouterOccupation(Logement logement, CompteursOccupation compteurs) {
        compteurs.ajouterColocation(logement.getNombrePlaces(), logement.getNombreOccupants());
    }
}
