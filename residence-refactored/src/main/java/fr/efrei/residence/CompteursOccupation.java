package fr.efrei.residence;

class CompteursOccupation {

    private int studios;
    private int studiosOccupes;
    private int t1;
    private int t1Occupes;
    private int t2;
    private int t2Occupes;
    private int duplex;
    private int duplexOccupes;
    private int placesColocation;
    private int placesColocationOccupees;

    void ajouterStudio(boolean occupe) {
        studios++;
        if (occupe) studiosOccupes++;
    }

    void ajouterT1(boolean occupe) {
        t1++;
        if (occupe) t1Occupes++;
    }

    void ajouterT2(boolean occupe) {
        t2++;
        if (occupe) t2Occupes++;
    }

    void ajouterDuplex(boolean occupe) {
        duplex++;
        if (occupe) duplexOccupes++;
    }

    void ajouterColocation(int places, int placesOccupees) {
        placesColocation += places;
        placesColocationOccupees += placesOccupees;
    }

    int studios() { return studios; }
    int studiosOccupes() { return studiosOccupes; }
    int t1() { return t1; }
    int t1Occupes() { return t1Occupes; }
    int t2() { return t2; }
    int t2Occupes() { return t2Occupes; }
    int totalLogements() { return studios + t1 + t2 + duplex + placesColocation; }
    int totalOccupes() { return studiosOccupes + t1Occupes + t2Occupes + duplexOccupes
            + placesColocationOccupees; }
    int placesColocation() { return placesColocation; }
    int placesColocationOccupees() { return placesColocationOccupees; }
}
