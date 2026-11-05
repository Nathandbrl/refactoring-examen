package fr.efrei.residence;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Le parc de logements de la résidence.
 */
public class Residence {

    private static final int INDEX_TYPE = 0;
    private static final int INDEX_NUMERO = 1;
    private static final int INDEX_SURFACE = 2;
    private static final int INDEX_ETAGE = 3;
    private static final int INDEX_MEUBLE = 4;
    private static final int INDEX_PARAMETRE = 5;

    public static final String NOM = "Résidence Campus Vert";

    // TYPE;NUMERO;SURFACE;ETAGE;MEUBLE;PARAM (PARAM = nb de places en coloc, surface du balcon en T2)
    public static final String[] CATALOGUE = {
            "STUDIO;A001;26;0;true;0",
            "STUDIO;A102;22;1;true;0",
            "STUDIO;B103;20;1;false;0",
            "T1;A104;28;1;true;0",
            "T1;B105;30;1;false;0",
            "T2;A201;45;2;false;6",
            "T2;B202;48;2;true;0",
            "COLOCATION;B301;80;3;true;4",
            "COLOCATION;C001;63;0;true;3",
    };

    public static List<Logement> logements = new ArrayList<Logement>();

    public static void initialiser() {
        logements.clear();
        for (String ligne : CATALOGUE) {
            String[] t = ligne.split(";");
            Optional<TypeLogementInterne> type = TypeLogementInterne.depuisCode(t[INDEX_TYPE]);
            String numero = t[INDEX_NUMERO];
            double surface = Double.parseDouble(t[INDEX_SURFACE]);
            int etage = Integer.parseInt(t[INDEX_ETAGE]);
            boolean meuble = Boolean.parseBoolean(t[INDEX_MEUBLE]);
            int param = Integer.parseInt(t[INDEX_PARAMETRE]);
            if (type.isEmpty()) {
                System.out.println("Type inconnu ignoré : " + t[INDEX_TYPE]);
                continue;
            }
            Logement l;
            switch (type.get()) {
                case STUDIO:
                    l = new Studio(numero, surface, etage, meuble);
                    break;
                case T1:
                    l = new T1(numero, surface, etage, meuble);
                    break;
                case T2:
                    l = new T2(numero, surface, etage, meuble, param);
                    break;
                case COLOCATION:
                    l = new Colocation(numero, surface, etage, meuble, param);
                    break;
                default:
                    System.out.println("Type inconnu ignoré : " + type.get());
                    continue;
            }
            logements.add(l);
        }
    }

    public static Logement trouver(String numero) {
        for (Logement l : logements) {
            if (l.getNumero().equals(numero)) return l;
        }
        return null;
    }
}
