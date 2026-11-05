package fr.efrei.residence;

public class DecorateurFibre extends DecorateurLoyer {

    private static final double COUT_FIBRE = 20;

    private final boolean incluse;

    public DecorateurFibre(Loyer loyer, boolean incluse) {
        super(loyer);
        this.incluse = incluse;
    }

    @Override
    public double montant() {
        return incluse ? loyer.montant() : loyer.montant() + COUT_FIBRE;
    }
}
