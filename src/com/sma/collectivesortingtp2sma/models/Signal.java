package com.sma.collectivesortingtp2sma.models;

public class Signal implements IElement {

    private Coordinates coordinates;
    private Environment environment;
    private float intensity;

    public Signal(int type){
        this.coordinates = null;
        this.intensity = 0;
    }
    public Signal(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public Signal(Coordinates coordinates, float intensity) {
        this.coordinates = coordinates;
        this.intensity = intensity;
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

    public float getIntensity()  {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public void reduceIntensity() {
        intensity /= 2;
    }

    public void increaseIntensity() {
        intensity *= 2;
    }
}
