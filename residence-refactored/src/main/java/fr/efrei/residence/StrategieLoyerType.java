package fr.efrei.residence;

public interface StrategieLoyerType {

    double loyerDeBase(Logement logement);

    double coefficientMenage();

    boolean fibreIncluse();

    double charges();

    void ajouterOccupation(Logement logement, CompteursOccupation compteurs);

    default double surfaceBalcon(Logement logement) {
        return 0;
    }
}
