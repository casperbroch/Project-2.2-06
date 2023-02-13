package com.mda;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        //Group group = new Group();
        Parent root = FXMLLoader.load(getClass().getResource("App.fxml"));
        Scene scene = new Scene(root);

        //stage.setTitle("Multi-modal Digital Assistant");
        //stage.setResizable(false);
        //stage.setWidth(1280);
        //stage.setHeight(720);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}