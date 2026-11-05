package fr.efrei.residence;

public class ContexteResidence {

    private final Residence residence;
    private final Database database;
    private final HistoriqueNotifications historiqueNotifications;
    private final CalculateurLoyer calculateurLoyer;
    private final GestionResidence gestionResidence;

    public ContexteResidence() {
        database = new Database();
        FabriqueLogement fabriqueLogement = new FabriqueLogement();
        residence = new Residence(fabriqueLogement);
        residence.initialiser();
        historiqueNotifications = new HistoriqueNotifications();
        GenerateurIdentifiants identifiants = new GenerateurIdentifiants();
        FabriqueStrategieProfil fabriqueStrategieProfil = new FabriqueStrategieProfil();
        FabriqueStrategieLoyer fabriqueStrategieLoyer = new FabriqueStrategieLoyer();
        GestionNotifications notifications = new GestionNotifications(
                new FabriqueNotification(historiqueNotifications), fabriqueStrategieProfil);
        calculateurLoyer = new CalculateurLoyer(fabriqueStrategieLoyer,
                new AssemblageLoyer(fabriqueStrategieLoyer, fabriqueStrategieProfil));
        GenerateurQuittance generateurQuittance = new GenerateurQuittance();

        InscriptionService inscriptions = new InscriptionService(database, identifiants, fabriqueStrategieProfil);
        AttributionService attributions = new AttributionService(residence, database, identifiants,
                fabriqueLogement, calculateurLoyer, fabriqueStrategieProfil, notifications);
        CycleVieBailService cycleBail = new CycleVieBailService(database, generateurQuittance,
                fabriqueStrategieProfil, notifications);
        RapportService rapport = new RapportService(residence, database, fabriqueStrategieLoyer,
                historiqueNotifications);
        gestionResidence = new GestionResidence(inscriptions, attributions, cycleBail, notifications, rapport,
                database);
    }

    public Residence residence() {
        return residence;
    }

    public Database database() {
        return database;
    }

    public HistoriqueNotifications historiqueNotifications() {
        return historiqueNotifications;
    }

    public CalculateurLoyer calculateurLoyer() {
        return calculateurLoyer;
    }

    public GestionResidence gestionResidence() {
        return gestionResidence;
    }
}
