package fr.efrei.residence;

import java.util.ArrayList;
import java.util.List;

public class SmsService implements CanalNotificationService {

    private static final int LONGUEUR_MAX_SMS = 160;
    private static final int LONGUEUR_SUFFIXE_TRONCATURE = 3;
    private static final int LONGUEUR_TELEPHONE_MINIMALE = 10;

    public static int compteurSms = 0;
    public static List<String[]> journal = new ArrayList<String[]>();

    public void envoyerSms(String numero, String message) {
        if (numero == null || numero.length() < LONGUEUR_TELEPHONE_MINIMALE) {
            System.out.println("[SMS] numéro invalide : " + numero);
            return;
        }
        if (message.length() > LONGUEUR_MAX_SMS) {
            message = message.substring(0, LONGUEUR_MAX_SMS - LONGUEUR_SUFFIXE_TRONCATURE) + "...";
        }
        compteurSms++;
        journal.add(new String[]{numero, message});
        System.out.println("[SMS] -> " + numero + " : " + message);
    }

    @Override
    public void notifier(Etudiant etudiant, String sujet, String message) {
        envoyerSms(etudiant.getTelephone(), sujet + " - " + message);
    }

    @Override
    public void relancerImpayes(Etudiant etudiant, Bail bail) {
        envoyerSms(etudiant.getTelephone(),
                "Campus Vert : " + Utils.euros(bail.soldeDu) + " impayés sur " + bail.getId());
    }
}
