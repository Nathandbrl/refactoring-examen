package fr.efrei.residence;

public interface StrategieProfil {

    double appliquerRemise(double loyer, int echelonBourse);

    boolean estSolvable(double ressourcesMensuelles, double loyerChargesComprises);

    double calculerCaution(double loyerHorsCharges);

    double fraisDossier();

    int dureePreavisJours();

    double penaliteRetard(double loyerChargesComprises);

    String erreurInscription(String telephone, int echelonBourse);

    boolean emailComplementaire();
}
