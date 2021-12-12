package com.sma.collectivesortingtp2sma.models;

import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Simulation{
    private Environment environment;
    private Display displayer;
    private ConcurrentLinkedQueue<Coordinates> updates = new ConcurrentLinkedQueue<Coordinates>();
    private int steps;
    public boolean running;

    public Simulation(){
    }
    public Simulation(Environment environment){
        setEnvironement(environment);
        setDisplayer(new Display(environment));
    }

    public Simulation(Environment environment, Stage stage){
        setEnvironement(environment);
        setDisplayer(new Display(environment, stage));
        this.environment.setSlowMode(true);
    }

    private void setDisplayer(Display display) {
        this.displayer = display;
        this.displayer.setUpdateQueue(updates);
    }

    private void setEnvironement(Environment environment) {
        this.environment = environment;
        this.environment.setUpdateQueue(updates);
        this.environment.setSimulation(this);
    }

    private void setStage(Stage stage){
        displayer.setStage(stage);
        environment.setSlowMode(true);
    }

    public Environment getEnvironment(){
        return environment;
    }

    public void run(int maxSteps){
        steps = maxSteps;
        running = true;
        displayer.start();
        environment.start(maxSteps);
    }

    public void environmentStoped(){
        //displayer.start();
        running = false;
        displayer.stop();
        int score = getEnvironment().evaluateEnvironment();
        System.out.println("Simulation stopped " + score);
        try {
            File file = new File("reports.csv");
            FileWriter myWriter = new FileWriter("reports.csv", true);

            if (file.createNewFile()) {
                myWriter.write("width, heigh, nbAgent, nbObject, steps, score, speed, vision, memorySize, kPlus, kMinus, error\n");
            }

            myWriter.write(environment.getGridWidth() + ","
                    + environment.getGridHeigh() + ","
                    + environment.getNbAgents() + ","
                    + environment.getNbObjects() + ","
                    + steps + ","
                    + score + ","
                    + Agent.speed + ","
                    + Agent.vision + ","
                    + Agent.memorySize + ","
                    + Agent.kPlus + ","
                    + Agent.kMinus + ","
                    + Agent.error + "\n"

            );
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
