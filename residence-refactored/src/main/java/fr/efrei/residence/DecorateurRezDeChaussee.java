package fr.efrei.residence;

public class DecorateurRezDeChaussee extends DecorateurLoyer {

    private static final double COEFFICIENT_REZ_DE_CHAUSSEE = 0.95;

    public DecorateurRezDeChaussee(Loyer loyer) {
        super(loyer);
    }

    @Override
    public double montant() {
        return loyer.montant() * COEFFICIENT_REZ_DE_CHAUSSEE;
    }
}
