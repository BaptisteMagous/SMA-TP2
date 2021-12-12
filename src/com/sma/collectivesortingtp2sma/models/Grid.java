package com.sma.collectivesortingtp2sma.models;

import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.beans.PropertyChangeSupport;

public class Grid {

    private int width;
    private int height;

    private PropertyChangeSupport observable;

    private Cell[][] grid;
    private final Semaphore available = new Semaphore(1);
    private ConcurrentLinkedQueue<Coordinates> updates;

    public Grid(int width, int height){
        this.width = width;
        this.height = height;
        this.grid = new Cell[width][height];

        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                this.grid[x][y] = new Cell(new Coordinates(x, y));
            }
        }

        this.observable = new PropertyChangeSupport(this);
    }


    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public Cell getCell(Coordinates coordinates) {
        if(coordinates == null) return null;
        if(coordinates.getX() >= this.width) return null;
        if(coordinates.getY() >= this.height) return null;
        if(coordinates.getX() < 0) return null;
        if(coordinates.getY() < 0) return null;
        return this.grid[coordinates.getX()][coordinates.getY()];
    }

    public boolean move(Coordinates from, Coordinates to) throws InterruptedException {
        available.acquire();
        boolean isMoveCorrect =     from != null && !this.getCell(from).isFree()
                                 && to   != null &&  this.getCell(to).isFree();

        if(isMoveCorrect)
            this.getCell(to).setElement(this.getCell(from).popElement());

        available.release();

        if(isMoveCorrect) {
            setUpdated(from);
            setUpdated(to);
        }

        return isMoveCorrect;
    }

    public boolean insert(IElement element, Coordinates to) throws InterruptedException {
        available.acquire();
        boolean isMoveCorrect = to != null && this.getCell(to).isFree();

        if(isMoveCorrect)
            this.getCell(to).setElement(element);

        available.release();

        if(isMoveCorrect) {
            setUpdated(to);
        }

        return isMoveCorrect;
    }

    public IElement pop(Coordinates from) throws InterruptedException {
        available.acquire();
        IElement element = this.getCell(from).popElement();
        available.release();

        setUpdated(from);

        return element;
    }

    public Coordinates getRandomFreeCell() {
        Coordinates coordinates;
        int tries = 50;
        do{
            coordinates = new Coordinates((int)(Math.random() * this.width), (int)(Math.random() * this.height));
            if(tries-- <= 0) return null;
        }while(!this.getCell(coordinates).isFree());
        return coordinates;
    }

    public Coordinates getRandomFreeCellAround(Coordinates coordinates) {
        return getRandomFreeCellAround(coordinates, 1);
    }

    public Coordinates getRandomFreeCellAround(Coordinates coordinates, int distance) {
        Coordinates new_coordinates;
        int tries = 30;
        do{
            if(tries-- <= 0) return null;

            // Get a cell around position
            new_coordinates = new Coordinates(
                    coordinates.getX() + new Random().nextInt(2 * distance + 1) - distance,
                    coordinates.getY() + new Random().nextInt(2 * distance + 1) - distance
                    );
        }while(this.getCell(new_coordinates) == null || !this.getCell(new_coordinates).isFree());

        return new_coordinates;
    }

    public void addObserver(ConcurrentLinkedQueue<Coordinates> updates) {
        this.updates = updates;
    }
    
    private void setUpdated(Coordinates coordinates){
        if(updates != null) updates.add(coordinates);
    }

}
