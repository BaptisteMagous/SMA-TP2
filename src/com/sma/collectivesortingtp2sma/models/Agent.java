package com.sma.collectivesortingtp2sma.models;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import static java.lang.Math.pow;
import static java.lang.Math.random;

public class Agent extends Thread implements IElement{
    protected Coordinates coordinates = null;
    private Environment environment;
    protected Object hold = null;

    private boolean verbose = false;

    private Queue<Integer> lastVisited = new LinkedList<Integer>();

    public static int speed = 1;
    public static int vision = 1;
    static boolean analyseSurrondings = true; //Options to enhance agent performance by allowing him to look at his surronding and not only the cell he is on
    public static int memorySize = 10;
    public static float kPlus = 0.10f;
    public static float kMinus = 0.42f;
    public static float error = 0f;

    private int steps = 0;

    private HashMap<Integer, Float> f = new HashMap<Integer, Float>();
    private boolean slowMode = false;

    public Agent(){
        this.coordinates = null;
        f.put(0, 0f);
        f.put(1, 0f);
        f.put(2, 0f);
    }
    public Agent(Coordinates coordinates){
        this.coordinates = coordinates;
    }

    @Override
    public Coordinates getCoordinates() {
        return this.coordinates;
    }

    @Override
    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setSlowMode(boolean slow){
        slowMode = slow;
    }

    @Override
    public Environment getEnvironment() {
        return environment;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public void setSteps(int steps){
        this.steps = steps;
    }

    public void run(){
        init();
        if(slowMode) {
            try {
                Thread.sleep((long) (2000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        while (!(isInterrupted()
                ||
                (steps-- <= 0 && !isHolding())
                ||
                steps < -1000 //Avoid Diamond hands situations (the agent never give up his held object)
            )){
            try{
                execute();
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
        terminate();
    }

    protected void init() {
        if(verbose) System.out.println("BOT started");
    }

    protected void execute() throws InterruptedException {
        if(verbose) System.out.println("BOT try moving");
        boolean action;

        Cell currentRessourceCell = getEnvironment().getResourceGrid().getCell(getCoordinates());

        int currentRessourceObjectType = -1;

        if(!currentRessourceCell.isFree()) //Can lift an object from cell
                currentRessourceObjectType = ((Object) currentRessourceCell.getElement()).getType();

        if(!isHolding()
        && !currentRessourceCell.isFree()
        && random() < pow(kPlus / (kPlus + f.get(currentRessourceObjectType)), 2)) {
            action = lift();
            if(verbose) System.out.println("Lift");
        }

        else if(isHolding()
        && currentRessourceCell.isFree()
        && random() < pow(f.get(getHeldObject().getType()) / (kMinus + f.get(getHeldObject().getType())), 2)) {
            action = drop();
            if(verbose) System.out.println("Drop");
        }

        else{
            if(isHolding())
                if(verbose) System.out.println("Won't drop " + getHeldObject().getType() + " because " + f.get(getHeldObject().getType()) + " is too low (" + pow(f.get(getHeldObject().getType()) / (kMinus + f.get(getHeldObject().getType())), 2) + ")");

            action = getEnvironment().getAgentGrid().move(
                    this.getCoordinates(),
                    this.getEnvironment().getAgentGrid().getRandomFreeCellAround(this.coordinates, speed)
            );
        }

        if(action)
            analyseSurrondings();

        if(slowMode) Thread.sleep((long) (30));
    }

    protected void terminate() {
        if(verbose) System.out.println("BOT stopped");
        getEnvironment().notifyAgentFinish();
    }


    protected boolean lift() throws InterruptedException {
        if(hold == null)
            hold = (Object) getEnvironment().getResourceGrid().pop(getCoordinates());

        return hold != null;
    }

    protected boolean drop() throws InterruptedException {
        if(hold != null)
            if(getEnvironment().getResourceGrid().insert(hold, getCoordinates()))
                hold = null;

        return hold == null;
    }

    protected boolean isHolding() {
        return this.hold != null;
    }

    protected Object getHeldObject() {
        return this.hold;
    }

    private void analyseSurrondings(){
        if(!analyseSurrondings) {
            visitCell(getEnvironment().getResourceGrid().getCell(getCoordinates()));
            return;
        }
        for(int x = getCoordinates().getX()-vision; x <= getCoordinates().getX()+vision; x++)
            for(int y = getCoordinates().getY()-vision; y <= getCoordinates().getY()+vision; y++)
                visitCell(getEnvironment().getResourceGrid().getCell(new Coordinates(x, y)));

    }

    private void visitCell(Cell cell){
        // Add the cell in the list of last visited cell
        if(cell == null) lastVisited.add(0);
        else {
            int type = 0;
            if(!cell.isFree())
                type = ((Object) cell.getElement()).getType();

            if(random() < error) switch (type) { //Switch type on error
                case 1 -> type = 2;
                case 2 -> type = 1;
                case 0 -> type = 0; //No false positive, but it can be a good idea to have the agent mistakin an empty cell for an object
            }

            if(verbose) System.out.println("Visiting " + type + " (" + f.get(type) + " will be remplaced with " + (f.get(type) + (float)1/memorySize) + ")");

            if(!f.containsKey(type)) f.put(type, 0f);

            f.put(type, f.get(type) + (float)1/memorySize);
            lastVisited.add(type);
        }

        // Remove elements if the queue is too big
        if(lastVisited.size() > memorySize) {
            try{
                int type = lastVisited.poll();
                f.put(type, f.get(type) - (float)1/memorySize);
            }catch (NullPointerException error){
                System.err.println(error);
            }
        }
    }
}
