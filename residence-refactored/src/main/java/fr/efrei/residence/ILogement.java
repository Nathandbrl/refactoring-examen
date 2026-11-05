package fr.efrei.residence;

public interface ILogement {

    String getNumero();

    TypeLogementInterne getType();

    double getSurface();

    int getEtage();

    boolean isMeuble();
}
