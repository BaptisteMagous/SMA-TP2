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
    private Grid signalGrid;

    private final List<Agent> agents;
    private final List<Object> objects;

    private int runningAgent = 0;
    private boolean running = false;
    private final boolean verbose = false;
    private boolean slowMode = false;
    private Simulation simulation;

    static private int neighborhoodToConsiderForEvaluation = 1;

    public Environment(int width, int heigh){
        gridWidth = width;
        gridHeigh = heigh;

        setResourceGrid(new Grid(gridWidth, gridHeigh));
        setAgentGrid(new Grid(gridWidth, gridHeigh));
        setSignalGrid(new Grid(gridWidth, gridHeigh));

        agents = new ArrayList<Agent>();
        objects = new ArrayList<Object>();
    }

    //region GETTERs & SETTERs
    public int getNbAgents() {
        return agents.size();
    }
    public int getNbObjects() {
        return objects.size();
    }

    public Grid getAgentGrid(){
        return agentGrid;
    }

    public Grid getResourceGrid(){
        return resourceGrid;
    }

    public Grid getSignalGrid(){
        return signalGrid ;
    }

    public int getGridWidth(){
        return gridWidth;
    }
    public int getGridHeigh(){
        return gridHeigh;
    }

    public void setResourceGrid(Grid resourceGrid) {
        this.resourceGrid = resourceGrid;
    }

    public void setAgentGrid(Grid agentGrid) {
        this.agentGrid = agentGrid;
    }

    public void setSignalGrid(Grid signalGrid){
        this.signalGrid = signalGrid;
    }

    public void setSlowMode(boolean slow) {
        slowMode = slow;
        agents.forEach(agent -> agent.setSlowMode(slow));
    }

    public void setSimulation(Simulation simulation){
        this.simulation = simulation;
    }
    //endregion GETTERs & SETTERs

    //region Environment setup
    public void setup(int nbAgent, int nbObjet1, int nbObjet2) throws InterruptedException {

        this.setup(nbAgent, nbObjet1, nbObjet2, 0);
    }
    public void setup(int nbAgent, int nbObjet1, int nbObjet2, int nbObjet3) throws InterruptedException {

        this.setup(nbAgent, nbObjet1, nbObjet2, nbObjet3, 0);
    }
    public void setup(int nbAgent, int nbObjet1, int nbObjet2, int nbObjet3, int nbObjet4) throws InterruptedException {

        this.setup(nbAgent, nbObjet1, nbObjet2, nbObjet3, nbObjet4, 0);
    }
    public void setup(int nbAgent, int nbObjet1, int nbObjet2, int nbObjet3, int nbObjet4, int nbObjet5) throws InterruptedException {

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
        for(; nbObjet3 > 0; nbObjet3--){
            addObject(new Object(3));
        }
        for(; nbObjet4 > 0; nbObjet4--){
            addObject(new Object(4));
        }
        for(; nbObjet5 > 0; nbObjet5--){
            addObject(new Object(5));
        }
    }
    public void setup(int nbAgent, int nbObjet) throws InterruptedException {

        // Add agents
        for(; nbAgent > 0; nbAgent--){
            addAgent(new Agent());
        }
        for(; nbObjet > 0; nbObjet--){
            addObject(new Object(1 + (int)(Math.random() * 5)));
        }
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

    public void generateSignal(Coordinates location, Coordinates currentCoordinates, float intensity){
        Cell currentCell = getSignalGrid().getCell(currentCoordinates);

        //Check current cell to replace signal
        if(currentCell.isOccupied()){
            Signal currentSignal = (Signal) currentCell.getElement();
            if(intensity > currentSignal.getIntensity()) currentCell.setElement(new Signal(location, intensity));
        }

        //Spread signal
        if(intensity > 0.2){
            intensity = (float) (intensity * 1.2 - 0.3);

            for(Coordinates coordinates:getSignalGrid().getCellsAround(currentCoordinates)){
                generateSignal(location, coordinates, intensity);
            }
        }
    }

    public Signal sampleSignal(Coordinates coordinates) {
        Signal maxSignal = new Signal(null, 0);

        Signal signal = sampleSignalAt(coordinates);
        if(signal != null && signal.getIntensity() > maxSignal.getIntensity()) {
            maxSignal.setIntensity(signal.getIntensity());
            maxSignal.setCoordinates(signal.getCoordinates());
        }

        for(Coordinates coordinatesAround : getSignalGrid().getCellsAround(coordinates)){
            signal = sampleSignalAt(coordinatesAround);
            if(signal != null && signal.getIntensity() > maxSignal.getIntensity()) {
                maxSignal.setIntensity(signal.getIntensity());
                maxSignal.setCoordinates(signal.getCoordinates());
            }
        }

        if(maxSignal.getIntensity() == 0) return null;
        maxSignal.increaseIntensity(); //Compensate the intensity loose from sampling
        return maxSignal;
    }

    private Signal sampleSignalAt(Coordinates coordinates) {
        if(getSignalGrid().getCell(coordinates).isOccupied()){
            Signal signal = (Signal) getSignalGrid().getCell(coordinates).getElement();

            //Remove signal that get too low
            if(signal.getIntensity() < 0.2) getSignalGrid().getCell(coordinates).popElement();

            signal.reduceIntensity();


            return signal;
        }else{
            return null;
        }
    }
    //endregion Environment setup

    //region Running Environment
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
    //endregion Running Environment


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

}
