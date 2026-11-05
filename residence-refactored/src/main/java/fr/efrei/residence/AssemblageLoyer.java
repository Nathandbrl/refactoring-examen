package fr.efrei.residence;

import java.util.Set;

public class AssemblageLoyer {

    private static final int ETAGE_REZ_DE_CHAUSSEE = 0;

    private final FabriqueStrategieLoyer fabriqueStrategieLoyer;
    private final FabriqueStrategieProfil fabriqueStrategieProfil;

    public AssemblageLoyer(FabriqueStrategieLoyer fabriqueStrategieLoyer,
                          FabriqueStrategieProfil fabriqueStrategieProfil) {
        this.fabriqueStrategieLoyer = fabriqueStrategieLoyer;
        this.fabriqueStrategieProfil = fabriqueStrategieProfil;
    }

    public double calculer(Logement logement, ProfilEtudiant profil, int echelonBourse,
                           Set<OptionLogement> options) {
        StrategieLoyerType strategieLoyer = fabriqueStrategieLoyer.creer(logement.getType());
        StrategieProfil strategieProfil = fabriqueStrategieProfil.creer(profil);

        Loyer loyer = new LoyerDeBase(strategieLoyer.loyerDeBase(logement));
        if (logement.isMeuble()) {
            loyer = new DecorateurMeuble(loyer);
        }
        if (logement.getEtage() == ETAGE_REZ_DE_CHAUSSEE) {
            loyer = new DecorateurRezDeChaussee(loyer);
        }
        if (options.contains(OptionLogement.MENAGE)) {
            loyer = new DecorateurMenage(loyer, strategieLoyer.coefficientMenage());
        }
        if (options.contains(OptionLogement.PARKING)) {
            loyer = new DecorateurParking(loyer);
        }
        if (options.contains(OptionLogement.LAVERIE)) {
            loyer = new DecorateurLaverie(loyer);
        }
        if (options.contains(OptionLogement.FIBRE)) {
            loyer = new DecorateurFibre(loyer, strategieLoyer.fibreIncluse());
        }
        if (options.contains(OptionLogement.ASSURANCE)) {
            loyer = new DecorateurAssurance(loyer);
        }
        loyer = new DecorateurRemiseProfil(loyer, strategieProfil, echelonBourse);

        return Utils.arrondir(loyer.montant());
    }
}
