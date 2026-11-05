package fr.efrei.residence;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class Bail {

    private String id;
    private Etudiant etudiant;
    private Logement logement;
    private LocalDate dateSignature;
    private LocalDate dateEntree;
    private LocalDate dateFin;
    private StatutBailInterne statut;

    private double loyerHorsCharges;
    private double charges;
    private double caution;
    private double fraisDossier;

    public Set<OptionLogement> options = EnumSet.noneOf(OptionLogement.class);

    public List<Paiement> paiements = new ArrayList<Paiement>();
    public double soldeDu = 0;
    public double retenues = 0;
    public double cautionRestituee = 0;

    public Bail(String id, Etudiant etudiant, Logement logement, LocalDate dateSignature) {
        this.id = id;
        this.etudiant = etudiant;
        this.logement = logement;
        this.dateSignature = dateSignature;
        this.statut = StatutBailInterne.SIGNE;
    }

    public String getId() { return id; }
    public Etudiant getEtudiant() { return etudiant; }
    public Logement getLogement() { return logement; }
    public LocalDate getDateSignature() { return dateSignature; }
    public LocalDate getDateEntree() { return dateEntree; }
    public void setDateEntree(LocalDate d) { this.dateEntree = d; }
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate d) { this.dateFin = d; }
    public StatutBailInterne getStatut() { return statut; }
    public void setStatut(StatutBailInterne statut) { this.statut = statut; }
    public double getLoyerHorsCharges() { return loyerHorsCharges; }
    public void setLoyerHorsCharges(double l) { this.loyerHorsCharges = l; }
    public double getCharges() { return charges; }
    public void setCharges(double c) { this.charges = c; }
    public double getLoyerChargesComprises() { return Utils.arrondir(loyerHorsCharges + charges); }
    public double getCaution() { return caution; }
    public void setCaution(double c) { this.caution = c; }
    public double getFraisDossier() { return fraisDossier; }
    public void setFraisDossier(double f) { this.fraisDossier = f; }

    public String listerOptions() {
        String s = "";
        if (options.contains(OptionLogement.MENAGE)) s = s + "ménage, ";
        if (options.contains(OptionLogement.PARKING)) s = s + "parking, ";
        if (options.contains(OptionLogement.LAVERIE)) s = s + "laverie, ";
        if (options.contains(OptionLogement.FIBRE)) s = s + "fibre, ";
        if (options.contains(OptionLogement.ASSURANCE)) s = s + "assurance, ";
        if (s.length() == 0) return "aucune";
        return s.substring(0, s.length() - 2);
    }

}
