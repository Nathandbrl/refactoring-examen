package fr.efrei.residence;

public class FabriqueNotification {

    public CanalNotificationService creer(CanalNotification canal) {
        switch (canal) {
            case EMAIL:
                return new EmailService();
            case SMS:
                return new SmsService();
            case COURRIER:
                return new CourrierService();
            default:
                throw new IllegalArgumentException("Canal de notification non pris en charge : " + canal);
        }
    }
}
