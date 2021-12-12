package com.sma.collectivesortingtp2sma;

import com.sma.collectivesortingtp2sma.models.Agent;
import com.sma.collectivesortingtp2sma.models.Environment;
import com.sma.collectivesortingtp2sma.models.Simulation;
import javafx.application.Application;
import javafx.stage.Stage;

public class mainCollectData extends Application {
    @Override
    public void start(Stage primaryStage) throws InterruptedException {
        int nbTestPerSetings = 10;

        for (int speed = 1; speed <= 2; speed ++) {
            Agent.speed = speed;
            for (int vision = 0; vision <= 2; vision ++) {
                Agent.vision = vision;
                if(vision == 0 && speed == 1) continue; // Already tested // TODO remove after
                for (float k_minus = 0.05f; k_minus <= 0.38f; k_minus += 0.05f) {
                    Agent.kMinus = k_minus;
                    for (float k_plus = 0.05f; k_plus <= 0.38f; k_plus += 0.05f) {
                        Agent.kPlus = k_plus;
                        for (int memory = 5; memory <= 15; memory += 5) { //TODO tester avec plus de mémoire
                            Agent.memorySize = memory;

                            for (int agent = 10; agent <= 10; agent += 10) //TODO tester avec plus d'agents
                                for (int steps = 1000; steps < 130000; steps *= 2)
                                    for (int iteration = 0; iteration < nbTestPerSetings; iteration++) {
                                        // Creating the environment
                                        Environment environment = new Environment(50, 50);
                                        environment.setup(agent, 200, 200);

                                        // Create simulation WITHOUT linking it to the stage (headless mode)
                                        Simulation simulation = new Simulation(environment);

                                        // Run simulation
                                        simulation.run(steps);
                                    }
                        }
                    }
                }
            }
        }

        primaryStage.close();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
