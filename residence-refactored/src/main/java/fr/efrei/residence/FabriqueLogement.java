package fr.efrei.residence;

import java.util.Map;

public class FabriqueLogement {

    private static final int PLACES_COLOCATION_MIN = 2;
    private static final Map<TypeLogementInterne, ConstructeurLogement> CONSTRUCTEURS = Map.of(
            TypeLogementInterne.STUDIO, (numero, surface, etage, meuble, parametre) ->
                    new Studio(numero, surface, etage, meuble),
            TypeLogementInterne.T1, (numero, surface, etage, meuble, parametre) ->
                    new T1(numero, surface, etage, meuble),
            TypeLogementInterne.T2, T2::new,
            TypeLogementInterne.COLOCATION, (numero, surface, etage, meuble, parametre) ->
                    new Colocation(numero, surface, etage, meuble,
                            Math.max(parametre, PLACES_COLOCATION_MIN)),
            TypeLogementInterne.DUPLEX, (numero, surface, etage, meuble, parametre) ->
                    new Duplex(numero, surface, etage, meuble));

    public Logement creer(TypeLogementInterne type, String numero, double surface, int etage, boolean meuble,
                          int parametre) {
        ConstructeurLogement constructeur = CONSTRUCTEURS.get(type);
        if (constructeur == null) {
            throw new IllegalArgumentException("Type de logement non pris en charge : " + type);
        }
        return constructeur.creer(numero, surface, etage, meuble, parametre);
    }

    @FunctionalInterface
    private interface ConstructeurLogement {
        Logement creer(String numero, double surface, int etage, boolean meuble, int parametre);
    }
}
