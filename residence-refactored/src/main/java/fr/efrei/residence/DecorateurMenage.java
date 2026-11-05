package fr.efrei.residence;

public class DecorateurMenage extends DecorateurLoyer {

    private final double coefficient;

    public DecorateurMenage(Loyer loyer, double coefficient) {
        super(loyer);
        this.coefficient = coefficient;
    }

    @Override
    public double montant() {
        return loyer.montant() * coefficient;
    }
}
