package fr.efrei.residence;

public abstract class Logement implements ILogement {

    private static final int ETAGE_REZ_DE_CHAUSSEE = 0;
    private static final String BATIMENT_AVEC_ASCENSEUR = "A";

    protected String numero;
    protected TypeLogementInterne type;
    protected double surface;
    protected int etage;
    protected boolean meuble;
    protected Etudiant occupant;

    public Logement(String numero, TypeLogementInterne type, double surface, int etage, boolean meuble) {
        this.numero = numero;
        this.type = type;
        this.surface = surface;
        this.etage = etage;
        this.meuble = meuble;
    }

    public String getNumero() { return numero; }
    public TypeLogementInterne getType() { return type; }
    public double getSurface() { return surface; }
    public int getEtage() { return etage; }
    public boolean isMeuble() { return meuble; }
    public Etudiant getOccupant() { return occupant; }

    public boolean estLibre() {
        return occupant == null;
    }

    public boolean estAccessiblePMR() {
        // seul le bâtiment A a un ascenseur
        return etage == ETAGE_REZ_DE_CHAUSSEE || numero.startsWith(BATIMENT_AVEC_ASCENSEUR);
    }

    public void loger(Etudiant e) {
        if (occupant != null) {
            throw new IllegalStateException("Logement " + numero + " déjà occupé");
        }
        occupant = e;
    }

    public void liberer(Etudiant e) {
        occupant = null;
    }
}
