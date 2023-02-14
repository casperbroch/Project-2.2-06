package com.mda;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class Controller implements Initializable {
    @FXML
    private Button button_send;
    @FXML
    private TextField text_field;
    @FXML
    private VBox vbox_message;
    @FXML
    private ScrollPane scroll_pane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        scroll_pane.setHbarPolicy(ScrollBarPolicy.NEVER);
        scroll_pane.setVbarPolicy(ScrollBarPolicy.NEVER);
        
        vbox_message.heightProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                scroll_pane.setVvalue((Double) newValue);
            }
        });

        button_send.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                String message = text_field.getText();
                if (!message.isEmpty()) {
                    addUMessage(message, vbox_message);
                    Connection conn = new Connection();
                    conn.sendMessage("A response after you pressed the 'send' button.");
                }
            }
        });

        text_field.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    String message = text_field.getText();
                    if (!message.isEmpty()) {
                        addUMessage(message, vbox_message);
                        Connection conn = new Connection();
                        conn.sendMessage("A response after you pressed 'enter'.");
                    }
                }
            }
        });

    }

    public VBox getvBox() {
        return vbox_message;
    }

    public void addUMessage(String message, VBox vbox) {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.setPadding(new Insets(5,5,5,10));

        Text text = new Text(message);
        TextFlow TextFlow = new TextFlow(text);
        TextFlow.setStyle("-fx-color: rgb(239,242,255);" +
                        "-fx-background-color: rgb(15,125,242);" +
                        " -fx-background-radius: 20px;");
                    
        TextFlow.setPadding(new Insets(5,10,5,10));
        text.setFill(Color.color(0.934,0.945,0.996));

        hBox.getChildren().add(TextFlow);
        vbox.getChildren().add(hBox);

        text_field.clear();
    }

    public static void addBMessage(String message, VBox vbox) {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5,5,5,10));

        Text text = new Text(message);
        TextFlow TextFlow = new TextFlow(text);
        TextFlow.setStyle("-fx-background-color: rgb(233,233,235);" +
                        "-fx-background-radius: 20px;");
                    
        TextFlow.setPadding(new Insets(5,10,5,10));
        text.setFill(Color.color(0,0,0));

        hBox.getChildren().add(TextFlow);
        vbox.getChildren().add(hBox);
    }

    
}
