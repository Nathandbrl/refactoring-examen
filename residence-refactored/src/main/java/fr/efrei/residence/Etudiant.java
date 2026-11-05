package fr.efrei.residence;

public class Etudiant {

    private String id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String adresse;
    private ProfilEtudiant profil;
    private int echelonBourse; // 0 à 7, seulement pour les boursiers
    private double ressourcesMensuelles;
    private boolean pmr;
    private CanalNotification canalPrefere;

    public Etudiant(String id, String nom, String prenom, String email, String telephone, String adresse,
                    ProfilEtudiant profil, int echelonBourse, double ressourcesMensuelles, boolean pmr,
                    CanalNotification canalPrefere) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.adresse = adresse;
        this.profil = profil;
        this.echelonBourse = echelonBourse;
        this.ressourcesMensuelles = ressourcesMensuelles;
        this.pmr = pmr;
        this.canalPrefere = canalPrefere;
    }

    public String getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getNomComplet() { return prenom + " " + nom.toUpperCase(); }
    public String getEmail() { return email; }
    public String getTelephone() { return telephone; }
    public String getAdresse() { return adresse; }
    public ProfilEtudiant getProfil() { return profil; }
    public void setProfil(ProfilEtudiant profil) { this.profil = profil; }
    public int getEchelonBourse() { return echelonBourse; }
    public double getRessourcesMensuelles() { return ressourcesMensuelles; }
    public boolean isPmr() { return pmr; }
    public CanalNotification getCanalPrefere() { return canalPrefere; }
    public void setCanalPrefere(CanalNotification canalPrefere) { this.canalPrefere = canalPrefere; }
}
