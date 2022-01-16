package com.sma.collectivesortingtp2sma;

import com.sma.collectivesortingtp2sma.models.Agent;
import com.sma.collectivesortingtp2sma.models.Environment;
import com.sma.collectivesortingtp2sma.models.Object;
import com.sma.collectivesortingtp2sma.models.Simulation;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.Observer;

public class main extends Application {
    @Override
    public void start(Stage primaryStage) throws InterruptedException {

        // Creating the environment
        Environment environment = new Environment(50, 50);
        environment.setup(30, 400);

        // Create simulation and link it to the stage
        Simulation simulation = new Simulation(environment, primaryStage);

        // Run simulation
        primaryStage.show();
        simulation.run(128000);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
