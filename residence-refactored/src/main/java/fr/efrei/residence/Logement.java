package fr.efrei.residence;

import java.util.ArrayList;
import java.util.List;

public abstract class Logement implements LogementOccupable {

    private static final int ETAGE_REZ_DE_CHAUSSEE = 0;
    private static final String BATIMENT_AVEC_ASCENSEUR = "A";

    protected String numero;
    protected TypeLogementInterne type;
    protected double surface;
    protected int etage;
    protected boolean meuble;
    private final int nombrePlaces;
    private final List<Etudiant> occupants = new ArrayList<>();
    private double surfaceBalcon;

    public Logement(String numero, TypeLogementInterne type, double surface, int etage, boolean meuble) {
        this(numero, type, surface, etage, meuble, 1);
    }

    public Logement(String numero, TypeLogementInterne type, double surface, int etage, boolean meuble,
                    int nombrePlaces) {
        this.numero = numero;
        this.type = type;
        this.surface = surface;
        this.etage = etage;
        this.meuble = meuble;
        this.nombrePlaces = nombrePlaces;
    }

    public String getNumero() { return numero; }
    public TypeLogementInterne getType() { return type; }
    public double getSurface() { return surface; }
    public int getEtage() { return etage; }
    public boolean isMeuble() { return meuble; }
    public Etudiant getOccupant() { return occupants.isEmpty() ? null : occupants.get(0); }

    @Override
    public boolean estLibre() {
        return occupants.size() < nombrePlaces;
    }

    @Override
    public boolean estAccessiblePMR() {
        // seul le bâtiment A a un ascenseur
        return etage == ETAGE_REZ_DE_CHAUSSEE || numero.startsWith(BATIMENT_AVEC_ASCENSEUR);
    }

    @Override
    public void ajouterOccupant(Etudiant etudiant) {
        if (!estLibre()) {
            throw new IllegalStateException("Logement " + numero + " complet");
        }
        occupants.add(etudiant);
    }

    @Override
    public void retirerOccupant(Etudiant etudiant) {
        occupants.remove(etudiant);
    }

    @Override
    public void libererApresSortie(Etudiant etudiant) {
        retirerOccupant(etudiant);
        apresSortie();
    }

    protected void apresSortie() {
    }

    @Override
    public int getNombrePlaces() {
        return nombrePlaces;
    }

    @Override
    public int getNombreOccupants() {
        return occupants.size();
    }

    protected void setSurfaceBalcon(double surfaceBalcon) {
        this.surfaceBalcon = surfaceBalcon;
    }

    double surfaceBalconPourRapport() {
        return surfaceBalcon;
    }
}
