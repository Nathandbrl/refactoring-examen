package fr.efrei.residence;

import java.util.Set;

public class CalculateurLoyer {

    private final FabriqueStrategieLoyer fabriqueStrategieLoyer = new FabriqueStrategieLoyer();
    private final AssemblageLoyer assemblageLoyer = new AssemblageLoyer();

    /**
     * Loyer mensuel hors charges, options comprises, remise déduite.
     */
    public double calculerLoyer(Logement l, ProfilEtudiant profil, int echelon, Set<OptionLogement> options) {
        return assemblageLoyer.calculer(l, profil, echelon, options);
    }

    public double calculerCharges(Logement l) {
        return fabriqueStrategieLoyer.creer(l.getType()).charges();
    }
}
