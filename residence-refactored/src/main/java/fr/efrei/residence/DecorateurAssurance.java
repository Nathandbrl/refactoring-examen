package fr.efrei.residence;

public class DecorateurAssurance extends DecorateurLoyer {

    private static final double COUT_ASSURANCE = 9;

    public DecorateurAssurance(Loyer loyer) {
        super(loyer);
    }

    @Override
    public double montant() {
        return loyer.montant() + COUT_ASSURANCE;
    }
}
