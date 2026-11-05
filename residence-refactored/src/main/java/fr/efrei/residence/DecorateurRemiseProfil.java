package fr.efrei.residence;

public class DecorateurRemiseProfil extends DecorateurLoyer {

    private final StrategieProfil strategieProfil;
    private final int echelonBourse;

    public DecorateurRemiseProfil(Loyer loyer, StrategieProfil strategieProfil, int echelonBourse) {
        super(loyer);
        this.strategieProfil = strategieProfil;
        this.echelonBourse = echelonBourse;
    }

    @Override
    public double montant() {
        return strategieProfil.appliquerRemise(loyer.montant(), echelonBourse);
    }
}
