package fr.efrei.residence;

public class GenerateurIdentifiants {

    private static final String PREFIXE_ETUDIANT = "ETU-";
    private static final String PREFIXE_BAIL = "BAIL-";

    private int prochainEtudiant = 1;
    private int prochainBail = 1;

    public String prochainIdEtudiant() {
        return PREFIXE_ETUDIANT + prochainEtudiant++;
    }

    public String prochainIdBail() {
        return PREFIXE_BAIL + String.format("%03d", prochainBail++);
    }
}
