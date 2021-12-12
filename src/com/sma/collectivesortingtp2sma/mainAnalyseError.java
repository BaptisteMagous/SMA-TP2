package com.sma.collectivesortingtp2sma;

import com.sma.collectivesortingtp2sma.models.Agent;
import com.sma.collectivesortingtp2sma.models.Environment;
import com.sma.collectivesortingtp2sma.models.Simulation;
import javafx.application.Application;
import javafx.stage.Stage;

public class mainAnalyseError extends Application {
    @Override
    public void start(Stage primaryStage) throws InterruptedException {
        int nbTestPerSetings = 10;

        for (float error = 0.05f; error <= 0.101f; error += 0.05f) {
            Agent.error = error;
            for (int speed = 1; speed <= 1; speed ++) {
                Agent.speed = speed;
                for (int vision = 0; vision <= 0; vision ++) {
                    Agent.vision = vision;
                    for (float k_minus = 0.42f; k_minus <= 0.42; k_minus += 0.03f) {
                        Agent.kMinus = k_minus;
                        for (float k_plus = 0.1f; k_plus <= 0.15f; k_plus += 0.025f) {
                            Agent.kPlus = k_plus;
                            for (int memory = 10; memory <= 10; memory += 5) {
                                Agent.memorySize = memory;

                                for (int agent = 30; agent <= 30; agent += 10)
                                    for (int steps = 128000; steps < 130000; steps *= 2)
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
        }

        primaryStage.close();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
