package fr.efrei.residence;

public interface CanalNotificationService {

    void notifier(Etudiant etudiant, String sujet, String message);

    void relancerImpayes(Etudiant etudiant, Bail bail);
}
