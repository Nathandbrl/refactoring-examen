package fr.efrei.residence;

public class Duplex extends Logement {

    public Duplex(String numero, double surface, int etage, boolean meuble) {
        super(numero, TypeLogementInterne.DUPLEX, surface, etage, meuble);
    }
}
