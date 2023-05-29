package group6;

import java.io.File;
import java.util.Scanner;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class App extends Application {

    private static Controller controller;
    public static final String TEXTPATH = "src/main/java/group6/Python/Connection.txt";
    private String[] whitelist = {"Casper", "Marian", "Unknown"};

    @Override
    public void start(Stage stage) throws Exception {
        long start = System.currentTimeMillis();
        long end = System.currentTimeMillis();
        boolean notfound=true;
       

        FXMLLoader loader = new FXMLLoader(getClass().getResource("App.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);        

        controller = loader.getController();

        Image image = new Image(getClass().getResourceAsStream("icon.png"));
        stage.getIcons().add(image);        
        stage.setScene(scene);
        stage.setTitle("Multi-Modal Digital Assistant");
        stage.show();
    }

    public static Controller getController() {
        return controller;
    }

    public boolean checkName(String name) {
        for(int i=0; i<whitelist.length; i++) {
            if(whitelist[i].equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        launch(args);
    }
}