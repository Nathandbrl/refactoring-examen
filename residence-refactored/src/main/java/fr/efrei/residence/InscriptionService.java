package fr.efrei.residence;

public class InscriptionService {

    private static final int ECHELON_BOURSE_MIN = 0;
    private static final int ECHELON_BOURSE_MAX = 7;

    private final EtudiantRepository etudiants;
    private final GenerateurIdentifiants identifiants;
    private final FabriqueStrategieProfil fabriqueStrategieProfil;

    public InscriptionService(EtudiantRepository etudiants, GenerateurIdentifiants identifiants,
                             FabriqueStrategieProfil fabriqueStrategieProfil) {
        this.etudiants = etudiants;
        this.identifiants = identifiants;
        this.fabriqueStrategieProfil = fabriqueStrategieProfil;
    }

    public Etudiant inscrire(String nom, String prenom, String email, String telephone, String adresse,
                             ProfilEtudiant profil, int echelonBourse, double ressources, boolean pmr,
                             CanalNotification canalPrefere) {
        if (nom == null || nom.trim().isEmpty()) {
            System.out.println("Inscription refusée : nom obligatoire");
            return null;
        }
        if (!Utils.emailValide(email)) {
            System.out.println("Inscription refusée : email invalide (" + email + ")");
            return null;
        }
        if (etudiants.trouverParEmail(email) != null) {
            System.out.println("Inscription refusée : " + email + " déjà inscrit");
            return null;
        }
        if (profil == null) {
            System.out.println("Inscription refusée : profil inconnu " + profil);
            return null;
        }
        String erreurProfil = fabriqueStrategieProfil.creer(profil).erreurInscription(telephone, echelonBourse);
        if (erreurProfil != null) {
            System.out.println(erreurProfil);
            return null;
        }
        if (canalPrefere == CanalNotification.SMS && telephone == null) {
            System.out.println("Inscription refusée : téléphone obligatoire pour les SMS");
            return null;
        }
        if (canalPrefere == CanalNotification.COURRIER && (adresse == null || adresse.isEmpty())) {
            System.out.println("Inscription refusée : adresse obligatoire pour le courrier");
            return null;
        }
        if (canalPrefere == null) {
            System.out.println("Inscription refusée : canal inconnu " + canalPrefere);
            return null;
        }
        Etudiant etudiant = new Etudiant(identifiants.prochainIdEtudiant(), nom, prenom, email, telephone, adresse,
                profil, echelonBourse, ressources, pmr, canalPrefere);
        etudiants.sauvegarder(etudiant);
        System.out.println("Étudiant inscrit : " + etudiant.getId() + " " + etudiant.getNomComplet()
                + " (" + profil + ")");
        return etudiant;
    }
}
