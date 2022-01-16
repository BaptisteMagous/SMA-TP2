package com.sma.collectivesortingtp2sma.models;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Display extends Thread{
    private Environment environment;
    private Stage stage = null;

    private GridPane grid;
    private ImageView[][] gridImages;
    private int heigh, width;

    private boolean running = false;

    private Image imgAgent = new Image("file:img/agent.png", 16, 16, false, false);
    private Image imgEmpty = new Image("file:img/empty.png", 16, 16, false, false);
    private Image[] imgObject = {
            new Image("file:img/object0.png", 16, 16, false, false),
            new Image("file:img/object1.png", 16, 16, false, false),
            new Image("file:img/object2.png", 16, 16, false, false),
            new Image("file:img/object3.png", 16, 16, false, false),
            new Image("file:img/object4.png", 16, 16, false, false),
            new Image("file:img/object5.png", 16, 16, false, false),
    };

    private Image[] imgAgentHoldObject = {
            new Image("file:img/agentHolding0.png", 16, 16, false, false),
            new Image("file:img/agentHolding1.png", 16, 16, false, false),
            new Image("file:img/agentHolding2.png", 16, 16, false, false),
            new Image("file:img/agentHolding3.png", 16, 16, false, false),
            new Image("file:img/agentHolding4.png", 16, 16, false, false),
            new Image("file:img/agentHolding5.png", 16, 16, false, false)
    };

    private Image[] imgAgentOverObject = {
            new Image("file:img/agentOver0.png", 16, 16, false, false),
            new Image("file:img/agentOver1.png", 16, 16, false, false),
            new Image("file:img/agentOver2.png", 16, 16, false, false),
            new Image("file:img/agentOver3.png", 16, 16, false, false),
            new Image("file:img/agentOver4.png", 16, 16, false, false),
            new Image("file:img/agentOver5.png", 16, 16, false, false)
    };

    private ConcurrentLinkedQueue<Coordinates> updates;
    private boolean verbose = false;

    public Display(){
    }

    public Display(Environment environment, Stage stage){
        setEnvironment(environment);
        setStage(stage);
    }

    public Display(Environment environment){
        setEnvironment(environment);
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;

        this.running = true;

        // Setup the stage to fullscreen (margin 100px)
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        this.stage.setX(bounds.getMinX()+100);
        this.stage.setY(bounds.getMinY()+100);
        this.stage.setWidth(bounds.getWidth()-200);
        this.stage.setHeight(bounds.getHeight()-200);

        // Set the name
        this.stage.setTitle("Simulation de tri multi-agents");

        // Setup the root borderpane
        BorderPane root = new BorderPane();
        root.setBackground(new Background(new BackgroundFill(Color.rgb(199, 250, 230), CornerRadii.EMPTY, Insets.EMPTY)));

        //Setup the center
        root.setCenter(grid);
        grid.setAlignment(Pos.CENTER);

        // Display the grid
        grid.requestFocus();
        this.updateGrid();

        // Display scene
        this.stage.setScene(new Scene(root, 400, 400));
        this.stage.show();
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.grid = new GridPane();
        this.environment = environment;
        this.width = this.environment.getGridWidth();
        this.heigh = this.environment.getGridHeigh();

        gridImages = new ImageView[width][heigh];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < heigh; y++) {
                ImageView imageView = new ImageView();
                gridImages[x][y] = imageView;
                grid.add(imageView, x, y);
            }
        }
    }

    private void updateGrid(){
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < heigh; y++) {
                updateGridCell(new Coordinates(x, y));
            }
        }
    }

    public void setUpdateQueue(ConcurrentLinkedQueue<Coordinates> updates) {
        this.updates = updates;
    }

    public void run(){
        init();
        while (!isInterrupted()){
            try{
                execute();
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
        terminate();
    }

    protected void init() {
        if(verbose) System.out.println("Display started");
        updateGrid();
    }

    protected void execute() throws InterruptedException {
        if(updates.isEmpty()) return;

        updateGridCell(updates.poll());
    }

    private void updateGridCell(Coordinates coordinates) {
        if(!running) return;

        Image newImage = imgEmpty;
        
        if (getEnvironment().getAgentGrid().getCell(coordinates).isOccupied()){
            Agent agent = (Agent) getEnvironment().getAgentGrid().getCell(coordinates).getElement();
            newImage = imgAgent;
            if(agent.isHolding())
                newImage = imgAgentHoldObject[agent.getHeldObject().getType()];

            else if(getEnvironment().getResourceGrid().getCell(coordinates).isOccupied()) {
                Object object = (Object) getEnvironment().getResourceGrid().getCell(coordinates).getElement();
                newImage = imgAgentOverObject[object.getType()];
            }
        }

        else if(getEnvironment().getResourceGrid().getCell(coordinates).isOccupied()){
            Object object = (Object) getEnvironment().getResourceGrid().getCell(coordinates).getElement();
            newImage = imgObject[object.getType()];
        }

        gridImages[coordinates.getX()][coordinates.getY()].setImage(newImage);

    }

    protected void terminate() {
        if(verbose) System.out.println("Display stopped");
    }
}
