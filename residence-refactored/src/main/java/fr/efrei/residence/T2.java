package fr.efrei.residence;

public class T2 extends Logement implements LogementAvecBalcon {

    public T2(String numero, double surface, int etage, boolean meuble, double surfaceBalcon) {
        super(numero, TypeLogementInterne.T2, surface, etage, meuble);
        setSurfaceBalcon(surfaceBalcon);
    }

    @Override
    public double getSurfaceBalcon() {
        return surfaceBalconPourRapport();
    }
}
