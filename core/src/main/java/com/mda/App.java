package com.mda;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class App extends Application {

    private static Controller controller;

    @Override
    public void start(Stage stage) throws Exception {
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

    public static void main(String[] args) {
        launch(args);
    }
}