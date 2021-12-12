package com.sma.collectivesortingtp2sma.models;

public class Object implements IElement {

    private Coordinates coordinates;
    private Environment environment;
    private int type;

    public Object(int type){
        this.coordinates = null;
        this.type = type;
    }
    public Object(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public Object(Coordinates coordinates, int type) {
        this.coordinates = coordinates;
        this.type = type;
    }

    @Override
    public Coordinates getCoordinates() {
        return coordinates;
    }

    @Override
    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }


    @Override
    public Environment getEnvironment() {
        return environment;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public int getType() {
        return type;
    }
}
