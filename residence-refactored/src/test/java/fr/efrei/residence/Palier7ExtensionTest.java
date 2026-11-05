package fr.efrei.residence;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Palier7ExtensionTest {

    private static final double DELTA = 0.000001;

    @Test
    void duplexEstConstruitAvecSonLoyerEtUnePlace() {
        FabriqueLogement fabrique = new FabriqueLogement();
        Logement logement = fabrique.creer(TypeLogementInterne.DUPLEX, "D001", 40, 1, false, 4);

        assertInstanceOf(Duplex.class, logement);
        assertEquals(1, logement.getNombrePlaces());
        assertEquals(580, new FabriqueStrategieLoyer().creer(TypeLogementInterne.DUPLEX)
                .loyerDeBase(logement), DELTA);
        assertEquals(0, new FabriqueStrategieLoyer().creer(TypeLogementInterne.DUPLEX)
                .surfaceBalcon(logement), DELTA);

        CompteursOccupation compteurs = new CompteursOccupation();
        new StrategieLoyerDuplex().ajouterOccupation(logement, compteurs);
        assertEquals(0, compteurs.totalOccupes());
        logement.ajouterOccupant(etudiant(CanalNotification.EMAIL));
        compteurs = new CompteursOccupation();
        new StrategieLoyerDuplex().ajouterOccupation(logement, compteurs);
        assertEquals(1, compteurs.totalOccupes());
    }

    @Test
    void localVeloAjouteSixEurosApresAssurance() {
        Loyer loyer = new DecorateurLocalVelo(new DecorateurAssurance(new LoyerDeBase(100)));
        AssemblageLoyer assemblage = new AssemblageLoyer(new FabriqueStrategieLoyer(),
                new FabriqueStrategieProfil());
        double loyerAssemble = assemblage.calculer(new T1("T101", 30, 1, false), ProfilEtudiant.STANDARD, 0,
                EnumSet.of(OptionLogement.ASSURANCE, OptionLogement.LOCAL_VELO));

        assertEquals(115, loyer.montant(), DELTA);
        assertEquals(106, new DecorateurLocalVelo(new LoyerDeBase(100)).montant(), DELTA);
        assertEquals(455, loyerAssemble, DELTA);
    }

    @Test
    void sportifAppliqueSesReglesDeProfil() {
        StrategieProfil strategie = new FabriqueStrategieProfil().creer(ProfilEtudiant.SPORTIF);

        assertEquals(85, strategie.appliquerRemise(100, 0), DELTA);
        assertTrue(strategie.estSolvable(200, 100));
        assertFalse(strategie.estSolvable(199.99, 100));
        assertEquals(500, strategie.calculerCaution(500), DELTA);
        assertEquals(50, strategie.fraisDossier(), DELTA);
        assertEquals(15, strategie.dureePreavisJours());
        assertEquals(10, strategie.penaliteRetard(100), DELTA);
        assertEquals(25, strategie.penaliteRetard(500), DELTA);
    }

    @Test
    void pushEstConstruitParLaFabriqueEtEnregistreLaNotification() {
        HistoriqueNotifications historique = new HistoriqueNotifications();
        CanalNotificationService canal = new FabriqueNotification(historique).creer(CanalNotification.PUSH);
        ByteArrayOutputStream sortie = new ByteArrayOutputStream();
        PrintStream sortieOriginale = System.out;
        try {
            System.setOut(new PrintStream(sortie));
            canal.notifier(etudiant(CanalNotification.PUSH), "Bienvenue", "Message de test");
        } finally {
            System.setOut(sortieOriginale);
        }

        assertInstanceOf(PushService.class, canal);
        assertEquals("[PUSH] -> etudiant@mail.fr : Bienvenue", sortie.toString().trim());
        assertEquals(CanalNotification.PUSH, historique.notifications().get(0).canal());
        assertEquals("etudiant@mail.fr", historique.notifications().get(0).destinataire());
        assertEquals("Bienvenue", historique.notifications().get(0).sujet());
    }

    private Etudiant etudiant(CanalNotification canal) {
        return new Etudiant("ETU-TEST", "Test", "Etudiant", "etudiant@mail.fr", null, null,
                ProfilEtudiant.STANDARD, 0, 1000, false, canal);
    }
}
