package com.sma.collectivesortingtp2sma.models;

public class Coordinates {
    private int x, y;

    public Coordinates(int x, int y) {
        setX(x);
        setY(y);
    }

    public Coordinates(Coordinates from) {
        setX(from.getX());
        setY(from.getY());
    }

    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    public static Coordinates getToward(Coordinates A, Coordinates B) {
        Coordinates toward = new Coordinates(A);

        // Avoid 0 distance
        if(!A.equals(B)) {
            boolean moveX = Math.random() < Coordinates.getXDistance(A, B) / Coordinates.getDistance(A, B);

            if (moveX) toward.addX(Coordinates.getXDirection(A, B));
            if (!moveX) toward.addY(Coordinates.getYDirection(A, B));
        }
        return toward;
    }

    public static int getDistance(Coordinates A, Coordinates B) {
        return Coordinates.getXDistance(A, B) + Coordinates.getYDistance(A, B);
    }

    public static int getXDistance(Coordinates A, Coordinates B) {
        return Math.abs(A.getX() - B.getX());
    }
    public static int getYDistance(Coordinates A, Coordinates B) {
        return Math.abs(A.getY() - B.getY());
    }

    // Return 1 or -1, depending on which shorten the distance between (A+-1) and B
    public static int getXDirection(Coordinates A, Coordinates B) {
        return Integer.compare(B.getX(), A.getX());
    }
    public static int getYDirection(Coordinates A, Coordinates B) {
        return Integer.compare(B.getY(), A.getY());
    }


    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }

    public void addX(int x) {
        setX(getX() + x);
    }
    public void addY(int y) {
        setY(getY() + y);
    }

    public boolean equals(Coordinates coordinates){
        return coordinates.getX() == this.getX() && coordinates.getY() == this.getY();
    }

    public String toString(){
        return "[" + this.getX() + ", " + this.getY() + "]";
    }
}
