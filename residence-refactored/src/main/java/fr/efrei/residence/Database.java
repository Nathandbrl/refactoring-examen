package fr.efrei.residence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Accès à la base de données (simulée en mémoire pour l'instant).
 */
public class Database implements EtudiantRepository, BailRepository {

    private final Map<String, Etudiant> etudiants = new HashMap<>();
    private final Map<String, Bail> baux = new HashMap<>();

    public Database() {
        System.out.println("[DB] Connexion à jdbc:postgresql://db.campusvert.local:5432/residence");
    }

    public void executer(String sql) {
        System.out.println("[SQL] " + sql);
    }

    @Override
    public void sauvegarder(Etudiant etudiant) {
        executer("INSERT INTO etudiant (id, nom, email, profil) VALUES ('" + etudiant.getId() + "', '"
                + etudiant.getNom() + "', '" + etudiant.getEmail() + "', '" + etudiant.getProfil() + "')");
        etudiants.put(etudiant.getEmail(), etudiant);
    }

    @Override
    public Etudiant trouverParEmail(String email) {
        executer("SELECT * FROM etudiant WHERE email = '" + email + "'");
        return etudiants.get(email);
    }

    @Override
    public Etudiant trouverParId(String id) {
        executer("SELECT * FROM etudiant WHERE id = '" + id + "'");
        return etudiants.values().stream().filter(etudiant -> etudiant.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public Collection<Etudiant> tousLesEtudiants() {
        return List.copyOf(etudiants.values());
    }

    @Override
    public void sauvegarder(Bail bail) {
        executer("INSERT INTO bail (id, etudiant, logement, statut, loyer) VALUES ('" + bail.getId() + "', '"
                + bail.getEtudiant().getId() + "', '" + bail.getLogement().getNumero() + "', '"
                + bail.getStatut() + "', " + bail.getLoyerChargesComprises() + ")");
        baux.put(bail.getId(), bail);
    }

    @Override
    public void mettreAJour(Bail bail) {
        executer("UPDATE bail SET statut = '" + bail.getStatut() + "', solde = " + bail.soldeDu
                + " WHERE id = '" + bail.getId() + "'");
    }

    @Override
    public Optional<Bail> trouver(String id) {
        executer("SELECT * FROM bail WHERE id = '" + id + "'");
        return Optional.ofNullable(baux.get(id));
    }

    @Override
    public List<Bail> tousLesBaux() {
        executer("SELECT * FROM bail");
        List<Bail> liste = new ArrayList<Bail>(baux.values());
        liste.sort((a, b) -> a.getId().compareTo(b.getId()));
        return liste;
    }
}
