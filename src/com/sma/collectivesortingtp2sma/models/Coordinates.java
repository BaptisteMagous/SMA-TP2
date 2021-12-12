package com.sma.collectivesortingtp2sma.models;

public class Coordinates {
    private int x, y;

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean equals(Coordinates coordinates){
        return coordinates.getX() == this.getX() && coordinates.getY() == this.getY();
    }

    public String toString(){
        return "[" + this.getX() + ", " + this.getY() + "]";
    }
}
