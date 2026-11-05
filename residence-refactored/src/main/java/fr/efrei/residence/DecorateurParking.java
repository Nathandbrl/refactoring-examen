package fr.efrei.residence;

public class DecorateurParking extends DecorateurLoyer {

    private static final double COUT_PARKING = 45;

    public DecorateurParking(Loyer loyer) {
        super(loyer);
    }

    @Override
    public double montant() {
        return loyer.montant() + COUT_PARKING;
    }
}
