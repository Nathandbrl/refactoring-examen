package fr.efrei.residence;

import java.util.Set;

public class CalculateurLoyer {

    private static final int ETAGE_REZ_DE_CHAUSSEE = 0;
    private static final double MAJORATION_MEUBLE = 1.15;
    private static final double REDUCTION_REZ_DE_CHAUSSEE = 0.95;
    private static final double COUT_PARKING = 45;
    private static final double COUT_LAVERIE = 12;
    private static final double COUT_FIBRE = 20;
    private static final double COUT_ASSURANCE = 9;

    private final FabriqueStrategieLoyer fabriqueStrategieLoyer = new FabriqueStrategieLoyer();
    private final FabriqueStrategieProfil fabriqueStrategieProfil = new FabriqueStrategieProfil();

    /**
     * Loyer mensuel hors charges, options comprises, remise déduite.
     */
    public double calculerLoyer(Logement l, ProfilEtudiant profil, int echelon, Set<OptionLogement> options) {
        StrategieLoyerType strategieLoyer = fabriqueStrategieLoyer.creer(l.getType());
        StrategieProfil strategieProfil = fabriqueStrategieProfil.creer(profil);
        double loyer = strategieLoyer.loyerDeBase(l);

        if (l.isMeuble()) loyer = loyer * MAJORATION_MEUBLE;
        if (l.getEtage() == ETAGE_REZ_DE_CHAUSSEE) loyer = loyer * REDUCTION_REZ_DE_CHAUSSEE;

        // options
        if (options.contains(OptionLogement.MENAGE)) {
            loyer = loyer * strategieLoyer.coefficientMenage();
        }
        if (options.contains(OptionLogement.PARKING)) loyer = loyer + COUT_PARKING;
        if (options.contains(OptionLogement.LAVERIE)) loyer = loyer + COUT_LAVERIE;
        if (options.contains(OptionLogement.FIBRE) && !strategieLoyer.fibreIncluse()) {
            loyer = loyer + COUT_FIBRE;
        }
        if (options.contains(OptionLogement.ASSURANCE)) loyer = loyer + COUT_ASSURANCE;

        loyer = strategieProfil.appliquerRemise(loyer, echelon);

        return Utils.arrondir(loyer);
    }

    public double calculerCharges(Logement l) {
        return fabriqueStrategieLoyer.creer(l.getType()).charges();
    }
}
