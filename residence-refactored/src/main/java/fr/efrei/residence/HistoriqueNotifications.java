package fr.efrei.residence;

import java.util.ArrayList;
import java.util.List;

public class HistoriqueNotifications {

    private final List<NotificationEnvoyee> notifications = new ArrayList<>();
    private int smsEnvoyes;
    private double affranchissement;

    public void ajouter(CanalNotification canal, String destinataire, String sujet) {
        notifications.add(new NotificationEnvoyee(canal, destinataire, sujet));
    }

    public void enregistrerSms(String numero, String message) {
        smsEnvoyes++;
        ajouter(CanalNotification.SMS, numero, message);
    }

    public void enregistrerCourrier(String adresse, String objet) {
        affranchissement += CourrierService.PRIX_TIMBRE;
        ajouter(CanalNotification.COURRIER, adresse, objet);
    }

    public List<NotificationEnvoyee> notifications() {
        return List.copyOf(notifications);
    }

    public int smsEnvoyes() {
        return smsEnvoyes;
    }

    public double affranchissement() {
        return affranchissement;
    }
}
