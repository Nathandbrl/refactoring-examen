package fr.efrei.residence;

import java.time.LocalDate;
import java.util.Set;

public class GestionResidence {

    private final InscriptionService inscriptions;
    private final AttributionService attributions;
    private final CycleVieBailService cycleBail;
    private final GestionNotifications notifications;
    private final RapportService rapport;
    private final BailRepository baux;

    public GestionResidence(InscriptionService inscriptions, AttributionService attributions,
                            CycleVieBailService cycleBail, GestionNotifications notifications,
                            RapportService rapport, BailRepository baux) {
        this.inscriptions = inscriptions;
        this.attributions = attributions;
        this.cycleBail = cycleBail;
        this.notifications = notifications;
        this.rapport = rapport;
        this.baux = baux;
    }

    public Etudiant inscrireEtudiant(String nom, String prenom, String email, String telephone, String adresse,
                                     ProfilEtudiant profil, int echelonBourse, double ressources, boolean pmr,
                                     CanalNotification canalPrefere) {
        return inscriptions.inscrire(nom, prenom, email, telephone, adresse, profil, echelonBourse,
                ressources, pmr, canalPrefere);
    }

    public Logement ajouterLogement(TypeLogementInterne type, String numero, double surface, int etage,
                                    boolean meuble, int param) {
        return attributions.ajouterLogement(type, numero, surface, etage, meuble, param);
    }

    public Bail attribuerLogement(Etudiant etudiant, TypeLogementInterne typeSouhaite,
                                  Set<OptionLogement> options, LocalDate dateSignature) {
        return attributions.attribuer(etudiant, typeSouhaite, options, dateSignature);
    }

    public boolean entrerDansLesLieux(String idBail, LocalDate date) {
        return cycleBail.entrerDansLesLieux(idBail, date);
    }

    public boolean payerLoyer(String idBail, String mois, LocalDate datePaiement, double montant) {
        return cycleBail.payerLoyer(idBail, mois, datePaiement, montant);
    }

    public boolean donnerPreavis(String idBail, LocalDate date) {
        return cycleBail.donnerPreavis(idBail, date);
    }

    public boolean sortir(String idBail, LocalDate date, double retenuesDegats) {
        return cycleBail.sortir(idBail, date, retenuesDegats);
    }

    public boolean annulerBail(String idBail) {
        return cycleBail.annulerBail(idBail);
    }

    public void relancerImpayes() {
        notifications.relancerImpayes(baux);
    }

    public void afficherRapport() {
        rapport.afficherRapport();
    }

    public String genererRapport() {
        return rapport.genererRapport();
    }

    public String genererQuittance(String idBail, String mois) {
        return cycleBail.quittance(idBail, mois);
    }

    public String genererQuittance(Bail bail, String mois) {
        return cycleBail.quittance(bail, mois);
    }
}
