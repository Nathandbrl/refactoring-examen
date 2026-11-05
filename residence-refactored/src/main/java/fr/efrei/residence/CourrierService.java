package fr.efrei.residence;

public class CourrierService implements CanalNotificationService {

    public static final double PRIX_TIMBRE = 1.29;
    private final HistoriqueNotifications historique;

    public CourrierService(HistoriqueNotifications historique) {
        this.historique = historique;
    }

    public void imprimerEtPoster(String nom, String adresse, String objet, String texte) {
        historique.enregistrerCourrier(adresse, objet);
        System.out.println("[COURRIER] " + nom + ", " + adresse + " | Objet : " + objet);
        System.out.println("        " + texte);
    }

    @Override
    public void notifier(Etudiant etudiant, String sujet, String message) {
        imprimerEtPoster(etudiant.getNomComplet(), etudiant.getAdresse(), sujet, message);
    }

    @Override
    public void relancerImpayes(Etudiant etudiant, Bail bail) {
        imprimerEtPoster(etudiant.getNomComplet(), etudiant.getAdresse(), "Relance loyer impayé",
                "Il reste " + Utils.euros(bail.soldeDu) + " à régler sur le bail " + bail.getId() + ".");
    }
}
