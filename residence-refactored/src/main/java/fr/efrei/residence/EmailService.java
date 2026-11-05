package fr.efrei.residence;

public class EmailService implements CanalNotificationService {

    public static final String SMTP_HOST = "smtp.campusvert.fr";
    public static final String EXPEDITEUR = "gestion@campusvert.fr";

    private final HistoriqueNotifications historique;

    public EmailService(HistoriqueNotifications historique) {
        this.historique = historique;
    }

    public boolean envoyer(String destinataire, String sujet, String corps) {
        try {
            if (!Utils.emailValide(destinataire)) {
                throw new Exception("Adresse invalide");
            }
            System.out.println("[EMAIL] " + EXPEDITEUR + " -> " + destinataire + " | " + sujet);
            System.out.println("        " + corps);
            historique.ajouter(CanalNotification.EMAIL, destinataire, sujet);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void notifier(Etudiant etudiant, String sujet, String message) {
        envoyer(etudiant.getEmail(), sujet, message);
    }

    @Override
    public void relancerImpayes(Etudiant etudiant, Bail bail) {
        envoyer(etudiant.getEmail(), "RELANCE - loyer impayé",
                "Bonjour " + etudiant.getPrenom() + ", il reste " + Utils.euros(bail.soldeDu)
                        + " à régler sur le bail " + bail.getId() + ".");
    }
}
