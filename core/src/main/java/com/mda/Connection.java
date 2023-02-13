package com.mda;

import javafx.scene.layout.VBox;

public class Connection {

    private String message;

    public void sendMessage(VBox vbox) {
        Controller.addMessage("test",vbox);
    }

}
