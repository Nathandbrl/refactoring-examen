package fr.efrei.residence;

public class PushService implements CanalNotificationService {

    private final HistoriqueNotifications historique;

    public PushService(HistoriqueNotifications historique) {
        this.historique = historique;
    }

    @Override
    public void notifier(Etudiant etudiant, String sujet, String message) {
        System.out.println("[PUSH] -> " + etudiant.getEmail() + " : " + sujet);
        historique.ajouter(CanalNotification.PUSH, etudiant.getEmail(), sujet);
    }

    @Override
    public void relancerImpayes(Etudiant etudiant, Bail bail) {
        notifier(etudiant, "RELANCE - loyer impayé",
                "Bonjour " + etudiant.getPrenom() + ", il reste " + Utils.euros(bail.soldeDu)
                        + " à régler sur le bail " + bail.getId() + ".");
    }
}
