package fr.efrei.residence;

public class Studio extends Logement {

    public Studio(String numero, double surface, int etage, boolean meuble) {
        super(numero, TypeLogementInterne.STUDIO, surface, etage, meuble);
    }

}
