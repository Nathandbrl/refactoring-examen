package fr.efrei.residence;

public class DecorateurLaverie extends DecorateurLoyer {

    private static final double COUT_LAVERIE = 12;

    public DecorateurLaverie(Loyer loyer) {
        super(loyer);
    }

    @Override
    public double montant() {
        return loyer.montant() + COUT_LAVERIE;
    }
}
