package fr.efrei.residence;

public class FabriqueLogement {

    private static final int PLACES_COLOCATION_MIN = 2;

    public Logement creer(TypeLogementInterne type, String numero, double surface, int etage, boolean meuble,
                          int parametre) {
        switch (type) {
            case STUDIO:
                return new Studio(numero, surface, etage, meuble);
            case T1:
                return new T1(numero, surface, etage, meuble);
            case T2:
                return new T2(numero, surface, etage, meuble, parametre);
            case COLOCATION:
                return new Colocation(numero, surface, etage, meuble,
                        Math.max(parametre, PLACES_COLOCATION_MIN));
            default:
                throw new IllegalArgumentException("Type de logement non pris en charge : " + type);
        }
    }
}
