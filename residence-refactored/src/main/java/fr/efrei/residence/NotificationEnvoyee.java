package fr.efrei.residence;

public record NotificationEnvoyee(CanalNotification canal, String destinataire, String sujet) {
}
