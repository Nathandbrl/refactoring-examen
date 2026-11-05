package fr.efrei.residence;

public class FabriqueNotification {

    private final HistoriqueNotifications historique;

    public FabriqueNotification(HistoriqueNotifications historique) {
        this.historique = historique;
    }

    public CanalNotificationService creer(CanalNotification canal) {
        switch (canal) {
            case EMAIL:
                return new EmailService(historique);
            case SMS:
                return new SmsService(historique);
            case COURRIER:
                return new CourrierService(historique);
            default:
                throw new IllegalArgumentException("Canal de notification non pris en charge : " + canal);
        }
    }
}
