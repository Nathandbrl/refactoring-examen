package fr.efrei.residence;

public class DecorateurLocalVelo extends DecorateurLoyer {

    private static final double COUT_LOCAL_VELO = 6;

    public DecorateurLocalVelo(Loyer loyer) {
        super(loyer);
    }

    @Override
    public double montant() {
        return loyer.montant() + COUT_LOCAL_VELO;
    }
}
