package fr.efrei.residence;

import fr.efrei.residence.contrat.Canal;
import fr.efrei.residence.contrat.DemandeAttribution;
import fr.efrei.residence.contrat.DemandeDevis;
import fr.efrei.residence.contrat.DemandeInscription;
import fr.efrei.residence.contrat.DemandeLogement;
import fr.efrei.residence.contrat.EtatBail;
import fr.efrei.residence.contrat.EtatLogement;
import fr.efrei.residence.contrat.Notification;
import fr.efrei.residence.contrat.Option;
import fr.efrei.residence.contrat.StatutBail;
import fr.efrei.residence.contrat.SystemeResidence;
import fr.efrei.residence.contrat.TypeLogement;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;

/**
 * Branche le code historique sur les tests de contrat.
 * <p>
 * Tout le « bricolage » nécessaire pour tester l'ancien code (état statique à vider,
 * conversion des codes {@code String}, booléens d'options…) est confiné ici.
 */
public class NouveauSystemeAdapter implements SystemeResidence {

    private ContexteResidence contexte;

    @Override
    public void reinitialiser() {
        contexte = new ContexteResidence();
    }

    @Override
    public Optional<String> inscrire(DemandeInscription d) {
        Etudiant e = contexte.gestionResidence().inscrireEtudiant(d.nom(), d.prenom(), d.email(), d.telephone(), d.adresse(),
                ProfilEtudiant.valueOf(d.profil().name()), d.echelonBourse(), d.ressourcesMensuelles(), d.pmr(),
                CanalNotification.valueOf(d.canalPrefere().name()));
        return Optional.ofNullable(e).map(Etudiant::getId);
    }

    @Override
    public boolean ajouterLogement(DemandeLogement d) {
        int param = d.type() == TypeLogement.COLOCATION ? d.places() : d.surfaceBalcon();
        return contexte.gestionResidence().ajouterLogement(TypeLogementInterne.valueOf(d.type().name()), d.numero(), d.surface(),
                d.etage(), d.meuble(), param) != null;
    }

    @Override
    public Optional<EtatLogement> logement(String numero) {
        Logement l = contexte.residence().trouver(numero).orElse(null);
        if (l == null) {
            return Optional.empty();
        }
        int occupants = l.getNombreOccupants();
        return Optional.of(new EtatLogement(l.getNumero(), TypeLogement.valueOf(l.getType().name()), l.getNombrePlaces(),
                occupants, l.estAccessiblePMR()));
    }

    @Override
    public OptionalDouble devisLoyer(DemandeDevis d) {
        Logement l = contexte.residence().trouver(d.numeroLogement()).orElse(null);
        if (l == null) {
            return OptionalDouble.empty();
        }
        Set<OptionLogement> options = convertirOptions(d.options());
        return OptionalDouble.of(contexte.calculateurLoyer().calculerLoyer(l,
                ProfilEtudiant.valueOf(d.profil().name()), d.echelonBourse(), options));
    }

    @Override
    public Optional<String> attribuer(DemandeAttribution d) {
        Etudiant e = contexte.database().trouverParId(d.refEtudiant());
        if (e == null) {
            return Optional.empty();
        }
        Set<OptionLogement> options = convertirOptions(d.options());
        Bail b = contexte.gestionResidence().attribuerLogement(e, TypeLogementInterne.valueOf(d.type().name()), options,
                d.dateSignature());
        return Optional.ofNullable(b).map(Bail::getId);
    }

    @Override
    public Optional<EtatBail> consulter(String idBail) {
        Bail b = contexte.database().trouver(idBail).orElse(null);
        if (b == null) {
            return Optional.empty();
        }
        return Optional.of(new EtatBail(b.getId(), b.getLogement().getNumero(),
                StatutBail.valueOf(b.getStatut().name()),
                b.getLoyerHorsCharges(), b.getCharges(), b.getLoyerChargesComprises(), b.getCaution(),
                b.getFraisDossier(), b.getDateFin(), b.soldeDu, b.cautionRestituee));
    }

    @Override
    public boolean entrer(String idBail, LocalDate date) {
        return contexte.gestionResidence().entrerDansLesLieux(idBail, date);
    }

    @Override
    public boolean payer(String idBail, YearMonth mois, LocalDate date, double montant) {
        return contexte.gestionResidence().payerLoyer(idBail, mois.toString(), date, montant);
    }

    @Override
    public boolean donnerPreavis(String idBail, LocalDate date) {
        return contexte.gestionResidence().donnerPreavis(idBail, date);
    }

    @Override
    public boolean sortir(String idBail, LocalDate date, double retenuesDegats) {
        return contexte.gestionResidence().sortir(idBail, date, retenuesDegats);
    }

    @Override
    public boolean annuler(String idBail) {
        return contexte.gestionResidence().annulerBail(idBail);
    }

    @Override
    public Optional<String> quittance(String idBail, YearMonth mois) {
        return Optional.ofNullable(contexte.gestionResidence().genererQuittance(idBail, mois.toString()));
    }

    @Override
    public void relancerImpayes() {
        contexte.gestionResidence().relancerImpayes();
    }

    @Override
    public String rapport() {
        return contexte.gestionResidence().genererRapport();
    }

    @Override
    public List<Notification> notifications() {
        List<Notification> notifications = new ArrayList<>();
        for (Canal canal : Canal.values()) {
            for (NotificationEnvoyee envoyee : contexte.historiqueNotifications().notifications()) {
                if (envoyee.canal().name().equals(canal.name())) {
                    notifications.add(new Notification(canal, envoyee.destinataire(), envoyee.sujet()));
                }
            }
        }
        return notifications;
    }

    private static Set<OptionLogement> convertirOptions(Set<Option> options) {
        Set<OptionLogement> resultat = EnumSet.noneOf(OptionLogement.class);
        for (Option option : options) {
            resultat.add(OptionLogement.valueOf(option.name()));
        }
        return resultat;
    }
}
