package fr.efrei.residence;

import java.util.List;
import java.util.Optional;

public interface LogementRepository {

    void ajouter(Logement logement);

    Optional<Logement> trouver(String numero);

    List<Logement> tousLesLogements();
}
