package com.sma.collectivesortingtp2sma;

import com.sma.collectivesortingtp2sma.models.Environment;
import com.sma.collectivesortingtp2sma.models.Simulation;
import javafx.application.Application;
import javafx.stage.Stage;

public class mainSolo extends Application {
    @Override
    public void start(Stage primaryStage) throws InterruptedException {

        // Creating the environment
        Environment environment = new Environment(20, 20);
        environment.setup(1, 100, 10);

        // Create simulation and link it to the stage
        Simulation simulation = new Simulation(environment, primaryStage);

        // Run simulation
        primaryStage.show();
        simulation.run(10000);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
