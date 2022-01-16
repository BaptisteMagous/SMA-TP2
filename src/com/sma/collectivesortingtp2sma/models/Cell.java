package com.sma.collectivesortingtp2sma.models;

public class Cell {

    private IElement element;
    private final Coordinates coordinates;

    public Cell(Coordinates coordinates) {
        this.coordinates = coordinates;
        this.element = null;
    }
    public Cell(Coordinates coordinates, IElement element) {
        this.coordinates = coordinates;
        this.element = element;
    }

    public Coordinates getCoordinates() {
        return this.coordinates;
    }

    public IElement getElement() {
        return element;
    }
    public boolean isFree() {
        return element == null;
    }

    public void setElement(IElement element) {
        this.element = element;
        this.element.setCoordinates(this.getCoordinates());
    }

    public IElement popElement(){
        IElement popedElement = this.element;
        popedElement.setCoordinates(null);
        this.element = null;
        return popedElement;
    }

    public boolean isOccupied() {
        return !isFree();
    }
}
