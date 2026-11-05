package fr.efrei.residence;

import java.util.List;

public class RapportService {

    private static final double CENT_POUR_CENT = 100.0;

    private final LogementRepository logements;
    private final BailRepository baux;
    private final FabriqueStrategieLoyer fabriqueStrategieLoyer;
    private final HistoriqueNotifications historique;

    public RapportService(LogementRepository logements, BailRepository baux,
                         FabriqueStrategieLoyer fabriqueStrategieLoyer,
                         HistoriqueNotifications historique) {
        this.logements = logements;
        this.baux = baux;
        this.fabriqueStrategieLoyer = fabriqueStrategieLoyer;
        this.historique = historique;
    }

    public void afficherRapport() {
        System.out.println();
        System.out.println(genererRapport());
    }

    public String genererRapport() {
        String rapport = "";
        rapport += "=============== RAPPORT " + Residence.NOM + " ===============" + "\n";
        CompteursOccupation compteurs = new CompteursOccupation();
        double surfaceBalcons = 0;
        for (Logement logement : logements.tousLesLogements()) {
            StrategieLoyerType strategie = fabriqueStrategieLoyer.creer(logement.getType());
            strategie.ajouterOccupation(logement, compteurs);
            surfaceBalcons += strategie.surfaceBalcon(logement);
        }
        rapport += "Studios      : " + compteurs.studiosOccupes() + "/" + compteurs.studios() + "\n";
        rapport += "T1           : " + compteurs.t1Occupes() + "/" + compteurs.t1() + "\n";
        rapport += "T2           : " + compteurs.t2Occupes() + "/" + compteurs.t2() + "\n";
        rapport += "Colocation   : " + compteurs.placesColocationOccupees() + "/"
                + compteurs.placesColocation() + " places" + "\n";
        int total = compteurs.studios() + compteurs.t1() + compteurs.t2() + compteurs.placesColocation();
        int occupes = compteurs.studiosOccupes() + compteurs.t1Occupes() + compteurs.t2Occupes()
                + compteurs.placesColocationOccupees();
        rapport += "Occupation   : " + Math.round(occupes * CENT_POUR_CENT / total) + " %" + "\n";
        rapport += "Balcons      : " + surfaceBalcons + " m²" + "\n";

        double loyersMensuels = 0;
        double impayes = 0;
        double cautions = 0;
        List<Bail> tousLesBaux = baux.tousLesBaux();
        for (Bail bail : tousLesBaux) {
            if (bail.getStatut() == StatutBailInterne.ACTIF || bail.getStatut() == StatutBailInterne.PREAVIS) {
                loyersMensuels += bail.getLoyerChargesComprises();
                cautions += bail.getCaution();
            }
            if (bail.soldeDu > 0) impayes += bail.soldeDu;
        }
        rapport += "Loyers CC    : " + Utils.euros(Utils.arrondir(loyersMensuels)) + " / mois" + "\n";
        rapport += "Cautions     : " + Utils.euros(Utils.arrondir(cautions)) + "\n";
        rapport += "Impayés      : " + Utils.euros(Utils.arrondir(impayes)) + "\n";
        rapport += "SMS envoyés  : " + historique.smsEnvoyes() + "\n";
        rapport += "Timbres      : " + Utils.euros(Utils.arrondir(historique.affranchissement())) + "\n";
        rapport += "============================================================";
        return rapport;
    }
}
