package fr.efrei.residence;

public class GestionNotifications {

    private final FabriqueNotification fabrique;
    private final FabriqueStrategieProfil fabriqueStrategieProfil;

    public GestionNotifications(FabriqueNotification fabrique, FabriqueStrategieProfil fabriqueStrategieProfil) {
        this.fabrique = fabrique;
        this.fabriqueStrategieProfil = fabriqueStrategieProfil;
    }

    public void notifier(Etudiant etudiant, String sujet, String message) {
        CanalNotification canal = etudiant.getCanalPrefere();
        fabrique.creer(canal).notifier(etudiant, sujet, message);
        if (canal != CanalNotification.EMAIL
                && fabriqueStrategieProfil.creer(etudiant.getProfil()).emailComplementaire()) {
            fabrique.creer(CanalNotification.EMAIL).notifier(etudiant, "[EN] " + sujet, message);
        }
    }

    public void relancerImpayes(BailRepository baux) {
        System.out.println("--- Relance des impayés");
        for (Bail bail : baux.tousLesBaux()) {
            if (bail.soldeDu <= 0 || bail.getStatut() == StatutBailInterne.ANNULE) continue;
            fabrique.creer(bail.getEtudiant().getCanalPrefere()).relancerImpayes(bail.getEtudiant(), bail);
        }
    }
}
