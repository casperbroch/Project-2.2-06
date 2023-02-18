package com.mda;

import java.net.URL;
import java.util.ArrayList;
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
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class Controller implements Initializable {

    private static boolean DARKMODE = true;
    private static ArrayList<HBox> hboxlist = new ArrayList<>();
    private static ArrayList<Text> textlist = new ArrayList<>();

    @FXML
    private Button button_send;
    @FXML
    private TextField text_field;
    @FXML
    private VBox vbox_message;
    @FXML
    private ScrollPane scroll_pane;
    @FXML
    private AnchorPane anchor_pane;
    @FXML
    private Label label1;
    @FXML 
    private Label label2;
    @FXML
    private Button dm_button;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        if(DARKMODE) {
            setDarkMode();
        } else {
            setLightMode();
        }

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

        dm_button.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                if(!DARKMODE) {
                    setDarkMode();
                    DARKMODE=true;
                } else {
                    setLightMode();
                    DARKMODE=false;
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

        addBMessage("Hello! how can I assist you?", vbox_message);
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

        if(DARKMODE) {
            TextFlow.setStyle("-fx-background-color: rgb(81,81,81);" +
            "-fx-background-radius: 20px;");
            text.setFill(Color.color(1,1,1));
        } else {
            TextFlow.setStyle("-fx-background-color: rgb(233,233,235);" +
            "-fx-background-radius: 20px;");
            text.setFill(Color.color(0,0,0));
        }
                    
        TextFlow.setPadding(new Insets(5,10,5,10));

        hBox.getChildren().add(TextFlow);

        hboxlist.add(hBox);
        textlist.add(text);

        vbox.getChildren().add(hBox);
    }

    public void setDarkMode() {
        // Set Anchor Pane dark
        anchor_pane.setStyle("-fx-background-color: rgb(16,16,18);");

        // Set Scroll Pane dark
        scroll_pane.setStyle("-fx-background-color: rgb(21,21,24);"+" -fx-background: rgb(21,21,24);");

        // Recolour the label text
        label1.setTextFill(Color.color(1, 1, 1));
        label2.setTextFill(Color.color(1, 1, 1));

        for(int i=0; i<hboxlist.size(); i++) {
            hboxlist.get(i).getChildren().get(0).setStyle("-fx-background-color: rgb(81,81,81);" +
                                                                "-fx-background-radius: 20px;");
            textlist.get(i).setFill(Color.color(1,1,1));
        }

        Image img = new Image(getClass().getResourceAsStream("dmicon2.png"));
        ImageView iv = new ImageView(img);
        iv.setFitHeight(50);
        iv.setFitWidth(50);
        dm_button.setGraphic(iv);
        dm_button.setStyle("-fx-background-color: rgb(16,16,18)");
    }

    public void setLightMode() {
        // Set Anchor Pane light
        anchor_pane.setStyle("-fx-background-color: rgb(255,255,255);");

        // Set Scroll Pane light
        scroll_pane.setStyle("-fx-background-color: rgb(200,200,200);"+" -fx-background: rgb(255,255,255);");

        // Recolour the label text
        label1.setTextFill(Color.color(0, 0, 0));
        label2.setTextFill(Color.color(0, 0, 0));

        for(int i=0; i<hboxlist.size(); i++) {
            hboxlist.get(i).getChildren().get(0).setStyle("-fx-background-color: rgb(233,233,235);" +
                                                                "-fx-background-radius: 20px;");
            textlist.get(i).setFill(Color.color(0,0,0));
        }

        Image img = new Image(getClass().getResourceAsStream("dmicon.png"));
        ImageView iv = new ImageView(img);
        iv.setFitHeight(50);
        iv.setFitWidth(50);
        dm_button.setGraphic(iv);
        dm_button.setStyle("-fx-background-color: rgb(255,255,255)");
    
    }
    
}
