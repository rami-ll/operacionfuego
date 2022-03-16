package com.ramiro.operacionfuego.model;

import java.util.ArrayList;

public class SecretServiceRequestWrapper {

    private ArrayList<SecretServiceRequest> satellites;

    public ArrayList<SecretServiceRequest> getSatellites() {
        return satellites;
    }

    public void setSatellites(ArrayList<SecretServiceRequest> satellites) {
        this.satellites = satellites;
    }
}
