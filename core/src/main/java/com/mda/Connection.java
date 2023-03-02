package com.mda;

import javafx.scene.layout.VBox;

public class Connection {

    private Controller controller;
    private VBox vbox;

    public Connection() {
        this.controller = App.getController();
        this.vbox = controller.getvBox();
    }

    public void sendMessage(String message) {
        controller.addBMessage(message, vbox);
    }

}
