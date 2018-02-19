package com.kome.hp.komegarooandroid.Object;

/**
 * Created by Patricio on 17-08-2017.
 */

public class AvailableDrivers {
    String uidDriver;
    double distancia;

    public AvailableDrivers() {
    }

    public AvailableDrivers(String uidDriver, double distancia) {
        this.uidDriver = uidDriver;
        this.distancia = distancia;
    }

    public String getUidDriver() {
        return uidDriver;
    }

    public void setUidDriver(String uidDriver) {
        this.uidDriver = uidDriver;
    }

    public double getDistancia() {
        return distancia;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }
}
