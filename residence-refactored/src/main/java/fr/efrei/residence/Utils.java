package fr.efrei.residence;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Utils {

    private static final double CENTIMES_PAR_EURO = 100.0;

    public static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static String date(LocalDate d) {
        if (d == null) return "-";
        return d.format(FORMAT);
    }

    public static String euros(double montant) {
        return String.format(Locale.FRANCE, "%.2f €", montant);
    }

    public static double arrondir(double x) {
        return Math.round(x * CENTIMES_PAR_EURO) / CENTIMES_PAR_EURO;
    }

    public static boolean emailValide(String email) {
        return email != null && email.indexOf("@") > 0 && email.indexOf(".") > 0;
    }
}
