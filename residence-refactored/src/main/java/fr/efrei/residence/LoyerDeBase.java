package fr.efrei.residence;

public class LoyerDeBase implements Loyer {

    private final double montant;

    public LoyerDeBase(double montant) {
        this.montant = montant;
    }

    @Override
    public double montant() {
        return montant;
    }
}
