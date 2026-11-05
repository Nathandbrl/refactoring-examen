package fr.efrei.residence;

import java.util.List;
import java.util.Optional;

public interface BailRepository {

    void sauvegarder(Bail bail);

    void mettreAJour(Bail bail);

    Optional<Bail> trouver(String id);

    List<Bail> tousLesBaux();
}
