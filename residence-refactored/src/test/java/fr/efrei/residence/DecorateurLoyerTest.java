package fr.efrei.residence;

import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DecorateurLoyerTest {

    private static final double DELTA = 0.000001;
    private static final double LOYER_BASE = 100;

    @Test
    void decorateurMeubleAjouteLaMajoration() {
        assertEquals(115, new DecorateurMeuble(loyerBase()).montant(), DELTA);
    }

    @Test
    void decorateurRezDeChausseeAppliqueLaReduction() {
        assertEquals(95, new DecorateurRezDeChaussee(loyerBase()).montant(), DELTA);
    }

    @Test
    void decorateurMenageAppliqueSonCoefficient() {
        assertEquals(108, new DecorateurMenage(loyerBase(), 1.08).montant(), DELTA);
    }

    @Test
    void decorateurParkingAjouteSonCout() {
        assertEquals(145, new DecorateurParking(loyerBase()).montant(), DELTA);
    }

    @Test
    void decorateurLaverieAjouteSonCout() {
        assertEquals(112, new DecorateurLaverie(loyerBase()).montant(), DELTA);
    }

    @Test
    void decorateurFibreAjouteSonCoutSiElleNestPasIncluse() {
        assertEquals(120, new DecorateurFibre(loyerBase(), false).montant(), DELTA);
    }

    @Test
    void decorateurFibreNeFacturePasUneFibreIncluse() {
        assertEquals(LOYER_BASE, new DecorateurFibre(loyerBase(), true).montant(), DELTA);
    }

    @Test
    void decorateurAssuranceAjouteSonCout() {
        assertEquals(109, new DecorateurAssurance(loyerBase()).montant(), DELTA);
    }

    @Test
    void decorateurRemiseProfilAppliqueLaRemiseBoursier() {
        Loyer loyer = new DecorateurRemiseProfil(loyerBase(), new StrategieBoursier(), 4);

        assertEquals(80, loyer.montant(), DELTA);
    }

    @Test
    void assemblageAppliqueLaRemiseApresLesOptions() {
        Logement logement = new T1("T101", 30, 1, false);
        AssemblageLoyer assemblage = new AssemblageLoyer(new FabriqueStrategieLoyer(),
                new FabriqueStrategieProfil());
        double loyer = assemblage.calculer(logement, ProfilEtudiant.BOURSIER, 2,
                EnumSet.of(OptionLogement.MENAGE, OptionLogement.PARKING));

        assertEquals(468.18, loyer, DELTA);
    }

    private Loyer loyerBase() {
        return new LoyerDeBase(LOYER_BASE);
    }
}
