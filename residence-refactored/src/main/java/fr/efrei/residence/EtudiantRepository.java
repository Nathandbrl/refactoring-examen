package fr.efrei.residence;

import java.util.Collection;

public interface EtudiantRepository {

    void sauvegarder(Etudiant etudiant);

    Etudiant trouverParEmail(String email);

    Etudiant trouverParId(String id);

    Collection<Etudiant> tousLesEtudiants();
}
