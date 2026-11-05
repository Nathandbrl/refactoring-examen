package fr.efrei.residence;

public class DecorateurMeuble extends DecorateurLoyer {

    private static final double COEFFICIENT_MEUBLE = 1.15;

    public DecorateurMeuble(Loyer loyer) {
        super(loyer);
    }

    @Override
    public double montant() {
        return loyer.montant() * COEFFICIENT_MEUBLE;
    }
}
