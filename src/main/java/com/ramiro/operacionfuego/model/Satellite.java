package com.ramiro.operacionfuego.model;


public class Satellite {

    private Position position;
    private String name;

    public Satellite(Position pos, String name){
        this.position = pos;
        this.name = name;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
