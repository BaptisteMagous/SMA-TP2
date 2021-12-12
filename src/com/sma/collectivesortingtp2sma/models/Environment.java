package com.sma.collectivesortingtp2sma.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Environment{

    private final int gridWidth;
    private final int gridHeigh;
    private Grid resourceGrid;
    private Grid agentGrid;

    private final List<Agent> agents;
    private final List<Object> objects;

    private int runningAgent = 0;
    private boolean running = false;
    private final boolean verbose = false;
    private boolean slowMode = false;
    private Simulation simulation;

    public Grid getAgentGrid(){
        return agentGrid;
    }

    public Grid getResourceGrid(){
        return resourceGrid;
    }

    public int getGridWidth(){
        return gridWidth;
    }
    public int getGridHeigh(){
        return gridHeigh;
    }

    static private int neighborhoodToConsiderForEvaluation = 1;

    public Environment(int width, int heigh){
        gridWidth = width;
        gridHeigh = heigh;

        setResourceGrid(new Grid(gridWidth, gridHeigh));
        setAgentGrid(new Grid(gridWidth, gridHeigh));

        agents = new ArrayList<Agent>();
        objects = new ArrayList<Object>();
    }

    public void setup(int nbAgent, int nbObjet1, int nbObjet2) throws InterruptedException {

        // Add agents
        for(; nbAgent > 0; nbAgent--){
            addAgent(new Agent());
        }
        for(; nbObjet1 > 0; nbObjet1--){
            addObject(new Object(1));
        }
        for(; nbObjet2 > 0; nbObjet2--){
            addObject(new Object(2));
        }
    }

    public void setResourceGrid(Grid resourceGrid) {
        this.resourceGrid = resourceGrid;
    }

    public void setAgentGrid(Grid agentGrid) {
        this.agentGrid = agentGrid;
    }

    public void setSlowMode(boolean slow) {
        slowMode = slow;
        agents.forEach(agent -> agent.setSlowMode(slow));
    }

    public void setSimulation(Simulation simulation){
        this.simulation = simulation;
    }

    public void addAgent(Agent agent, Coordinates coordinates) throws InterruptedException {
        if(getAgentGrid().insert((IElement) agent, coordinates)) {
            agents.add(agent);
            agent.setEnvironment(this);
            agent.setSlowMode(this.slowMode);
        }
    }
    public void addAgent(Agent agent) throws InterruptedException {
        addAgent(agent, getAgentGrid().getRandomFreeCell());
    }

    public void addObject(Object object, Coordinates coordinates) throws InterruptedException {
        if(getResourceGrid().insert((IElement) object, coordinates))
            objects.add(object);
    }

    public void addObject(Object object) throws InterruptedException {
        addObject(object, getResourceGrid().getRandomFreeCell());
    }

    synchronized public void start(int maxStep) {
        if(verbose) System.out.println("Starting the environment with " + agents.size() + " agents !");
        agents.forEach(agent -> agent.setSteps(maxStep));
        runningAgent = agents.size();
        agents.forEach(Thread::start);

        running = true;
    }

    public void stop() {
        agents.forEach(Thread::interrupt);
        if(verbose) System.out.println("Stopped the environment.");
    }

    public void setUpdateQueue(ConcurrentLinkedQueue<Coordinates> updates) {
        this.resourceGrid.addObserver(updates);
        this.agentGrid.addObserver(updates);
    }

    synchronized public void notifyAgentFinish() {
        runningAgent--;

        if(!running) return;

        if(runningAgent == 0) { //If all agents are done
            running = false;
            agents.forEach(agent -> {
                try {
                    agent.drop();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            simulation.environmentStoped();

            if(verbose) System.out.println("Simulation ended !");
        }
    }

    public int evaluateEnvironment() {
        // Evaluating all object and summing thems
        IntStream scores = objects.stream().map(this::evaluateObject).mapToInt(Integer::intValue);
        return scores.sum();
    }

    private int evaluateObject(Object object) {
        if(object.getCoordinates() == null) return 0;
        int x = object.getCoordinates().getX();
        int y = object.getCoordinates().getY();

        int voisins = 0;

        for(int i = x - neighborhoodToConsiderForEvaluation; i <= x + neighborhoodToConsiderForEvaluation; i++)
            for(int j = y - neighborhoodToConsiderForEvaluation; j <= y + neighborhoodToConsiderForEvaluation; j++){
                Cell cell = getResourceGrid().getCell(new Coordinates(i, j));
                if(cell != null && cell.isOccupied())
                    if(((Object) cell.getElement()).getType() == object.getType()) voisins++;
            }

        return voisins * voisins;
    }

    private int evaluateCell(int x, int y) {
        if(getResourceGrid().getCell(new Coordinates(x, y)).isFree()) return 0;
        Object object = (Object) getResourceGrid().getCell(new Coordinates(x, y)).getElement();

        int voisins = 0;
        for(int i = x - neighborhoodToConsiderForEvaluation; i <= x + neighborhoodToConsiderForEvaluation; i++)
            for(int j = y - neighborhoodToConsiderForEvaluation; j <= y + neighborhoodToConsiderForEvaluation; j++){
                Cell cell = getResourceGrid().getCell(new Coordinates(i, j));
                if(cell != null && cell.isOccupied())
                    if(((Object) cell.getElement()).getType() == object.getType()) voisins++;
            }

        return voisins * voisins;
    }

    public String toString(){
        String display =  ("╔" + "═".repeat(gridWidth) + "╗\n")
                + ("║" + " ".repeat(gridWidth) + "║\n").repeat(gridHeigh)
                + ("╚" + "═".repeat(gridWidth) + "╝\n");

        int lineLength = 3 + gridWidth;
        StringBuilder displayBuilder = new StringBuilder(display);

        for (Agent agent : agents)
            displayBuilder.setCharAt(
                    lineLength // first line
                            + agent.getCoordinates().getY() * lineLength
                            + agent.getCoordinates().getX() + 1,
                    '*'
            );

        return displayBuilder.toString();
    }

    public int getNbAgents() {
        return agents.size();
    }
    public int getNbObjects() {
        return objects.size();
    }
}
