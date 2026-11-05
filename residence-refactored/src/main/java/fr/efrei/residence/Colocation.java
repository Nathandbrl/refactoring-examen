package fr.efrei.residence;

public class Colocation extends Logement implements LogementAvecEntretien {

    public Colocation(String numero, double surface, int etage, boolean meuble, int places) {
        super(numero, TypeLogementInterne.COLOCATION, surface, etage, meuble, places);
    }

    @Override
    public void nettoyerPartiesCommunes() {
        System.out.println("[ENTRETIEN] Nettoyage des parties communes de la colocation " + numero);
    }

    @Override
    protected void apresSortie() {
        nettoyerPartiesCommunes();
    }
}
