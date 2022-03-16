package com.ramiro.operacionfuego.model;

public class SecretServiceRequest {

    private double distance;
    private String name;
    private String[] message;

    public SecretServiceRequest(double distance, String name, String[] message) {
        this.distance = distance;
        this.name = name;
        this.message = message;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getMessage() {
        return message;
    }

    public void setMessage(String[] message) {
        this.message = message;
    }
}
