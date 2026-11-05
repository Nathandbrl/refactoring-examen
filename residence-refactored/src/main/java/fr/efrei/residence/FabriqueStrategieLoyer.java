package fr.efrei.residence;

import java.util.Map;

public class FabriqueStrategieLoyer {

    private static final Map<TypeLogementInterne, StrategieLoyerType> STRATEGIES = Map.of(
            TypeLogementInterne.STUDIO, new StrategieLoyerStudio(),
            TypeLogementInterne.T1, new StrategieLoyerT1(),
            TypeLogementInterne.T2, new StrategieLoyerT2(),
            TypeLogementInterne.COLOCATION, new StrategieLoyerColocation());

    public StrategieLoyerType creer(TypeLogementInterne type) {
        StrategieLoyerType strategie = STRATEGIES.get(type);
        if (strategie == null) {
            throw new IllegalArgumentException("Type de logement non pris en charge : " + type);
        }
        return strategie;
    }
}
