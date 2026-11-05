package fr.efrei.residence;

public interface LogementOccupable extends ILogement {

    boolean estLibre();

    boolean estAccessiblePMR();

    void ajouterOccupant(Etudiant etudiant);

    void retirerOccupant(Etudiant etudiant);

    void libererApresSortie(Etudiant etudiant);

    Etudiant getOccupant();

    int getNombrePlaces();

    int getNombreOccupants();
}
