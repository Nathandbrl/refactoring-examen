package fr.efrei.residence;

import java.util.Map;

public class FabriqueStrategieProfil {

    private static final Map<ProfilEtudiant, StrategieProfil> STRATEGIES = Map.of(
            ProfilEtudiant.STANDARD, new StrategieStandard(),
            ProfilEtudiant.BOURSIER, new StrategieBoursier(),
            ProfilEtudiant.ALTERNANT, new StrategieAlternant(),
            ProfilEtudiant.INTERNATIONAL, new StrategieInternational(),
            ProfilEtudiant.SPORTIF, new StrategieSportif());

    public StrategieProfil creer(ProfilEtudiant profil) {
        StrategieProfil strategie = STRATEGIES.get(profil);
        if (strategie == null) {
            throw new IllegalArgumentException("Profil non pris en charge : " + profil);
        }
        return strategie;
    }
}
