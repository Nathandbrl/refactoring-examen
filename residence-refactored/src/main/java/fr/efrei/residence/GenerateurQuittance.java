package fr.efrei.residence;

public class GenerateurQuittance {

    public String generer(Bail bail, String mois) {
        Paiement paiement = null;
        for (Paiement candidat : bail.paiements) {
            if (candidat.mois.equals(mois)) paiement = candidat;
        }
        if (paiement == null) {
            return "Aucun paiement pour " + mois;
        }
        String quittance = "";
        quittance += "==========================================\n";
        quittance += "   RÉSIDENCE CAMPUS VERT - QUITTANCE\n";
        quittance += "==========================================\n";
        quittance += "Bail      : " + bail.getId() + "\n";
        quittance += "Locataire : " + bail.getEtudiant().getNomComplet() + " ("
                + bail.getEtudiant().getProfil() + ")\n";
        quittance += "Logement  : " + bail.getLogement().getNumero() + " - "
                + bail.getLogement().getType() + " " + bail.getLogement().getSurface() + " m²";
        if (bail.getLogement().isMeuble()) quittance += " meublé";
        quittance += "\n";
        quittance += "Période   : " + mois + "\n";
        quittance += "Options   : " + bail.listerOptions() + "\n";
        quittance += "------------------------------------------\n";
        quittance += "Loyer HC  : " + Utils.euros(bail.getLoyerHorsCharges()) + "\n";
        quittance += "Charges   : " + Utils.euros(bail.getCharges()) + "\n";
        if (paiement.penalite > 0) {
            quittance += "Pénalité  : " + Utils.euros(paiement.penalite) + "\n";
        }
        quittance += "Total dû  : " + Utils.euros(paiement.du) + "\n";
        quittance += "Payé le   : " + Utils.date(paiement.date) + " : " + Utils.euros(paiement.montant) + "\n";
        if (paiement.montant < paiement.du) {
            quittance += "RESTE DÛ  : " + Utils.euros(Utils.arrondir(paiement.du - paiement.montant)) + "\n";
        }
        quittance += "==========================================";
        return quittance;
    }
}
