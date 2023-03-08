package com.mda;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import com.mda.wordsuggestion.SymSpell;

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

    // Important vars for the skill editor
    SkillScanner skillScanner = new SkillScanner();
    SkillEditor skillEditor = new SkillEditor();
    String prototype;
    String valueSlots;
    ArrayList<String> placeHolders;
    ArrayList<ArrayList<String>> values;
    ArrayList<Slot> slotVals;
    int slotIndex = 0;
    String response;
    String slot;
    private String message;
    private Responder responder = new Responder();
    private int state = -1;
    private ArrayList<String> skills = new ArrayList<String>();
    private static boolean DARKMODE = false;
    private static ArrayList<HBox> hboxlist = new ArrayList<>();
    private static ArrayList<Text> textlist = new ArrayList<>();
    private int wordlength=0;

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
    @FXML
    private Button suggest1;
    @FXML
    private Button suggest2;
    @FXML
    private Button suggest3;
    @FXML
    private HBox suggestbox;

    public List<String> suggestwords(String wrongWord) throws IOException {
        SymSpell dl = new SymSpell();
        List<String> similarWords = dl.getSimilarWordsDistance(wrongWord, 3, 3);

        if (similarWords!=null && similarWords.size()!=0){
            System.out.println("Words within 3 Damerau-Levenshtein distance of " + wrongWord + ": " + similarWords);
        } else {
            System.out.println("The word is correct");
        }
        return similarWords;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        skills.add("calendar");
        skills.add("weather");
        skills.add("web search");


        suggestbox.setVisible(false);
        if(DARKMODE) {
            setDarkMode();

        } else {
            setLightMode();
        }

        scroll_pane.setHbarPolicy(ScrollBarPolicy.NEVER);
        scroll_pane.setVbarPolicy(ScrollBarPolicy.NEVER);

        vbox_message.heightProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                suggestbox.setVisible(false);
                scroll_pane.setVvalue((Double) newValue);
            }
        });

        // TODO: Duplicate of typing ENTER
        button_send.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                suggestbox.setVisible(false);
                String message = text_field.getText();
                if (!message.isEmpty()) {
                    addUMessage(message, vbox_message);
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

        suggest1.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                text_field.deleteText(text_field.getLength()-wordlength, text_field.getLength());
                text_field.appendText(" ");
                text_field.appendText(suggest1.getText());
            }
        });

        suggest2.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                text_field.deleteText(text_field.getLength()-wordlength, text_field.getLength());
                text_field.appendText(" ");
                text_field.appendText(suggest2.getText());
            }
        });

        suggest3.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                text_field.deleteText(text_field.getLength()-wordlength, text_field.getLength());
                text_field.appendText(" ");
                text_field.appendText(suggest3.getText());
            }
        });

        text_field.setOnKeyPressed(new EventHandler<KeyEvent>() {
            // Program jumps to this method anything ANY key is typed into the chat
            @Override
            public void handle(KeyEvent ke) {   
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    message = text_field.getText();
                    addUMessage(message, vbox_message);

                    // Shuts the program off and ignores case
                    if(message.toLowerCase().contains("quit")) {
                        System.exit(0);
                    }

                    if(message.equalsIgnoreCase("add")) {
                        state = 1;
                    } else if(message.equalsIgnoreCase("retrieve")) {
                        state = 6;
                    }

                    // Response generator
                    switch(state) {
                        case 0: // Main greeting; what the user actually wants to do
                            for (int i = 0; i < skills.size(); i++) {
                                if(message.compareToIgnoreCase(skills.get(i)) == 0) {
                                    // Directed to a specific skill
                                    state = 0;
                                    response = responder.getSkills(message);
                                    break;
                                }
                                if(i == skills.size() - 1)
                                response = "Sorry, I do not have that skill. Please try again.";
                            }
                            break;
                        case 1: // Creating a skill with a prototype sentence (format)
                            try {
                                skillEditor.setUp();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            response = "Please type the prototype sentence: ";
                            state = 2;
                            break;
                        case 2: // Which slots in the prototype sentence the user can change
                            prototype = message;
                            response = "Please type the slots you wish to set as placeholders: (separated by a coma) ";
                            state = 3;
                            break;
                        case 3: // Creating a list of slots/placeholders (ex: DAY, TIME)
                            placeHolders = new ArrayList<>(Arrays.asList(message.split("[^a-zA-Z0-9]+"))); 
                            values = new ArrayList<ArrayList<String>>(); 
                            slotVals = new ArrayList<>();
                            state = 4;
                        case 5: // Getting all the possible values for a slot as a list
                            ArrayList<String> placeValues = new ArrayList<>(Arrays.asList(message.split("[^a-zA-Z0-9]+"))); 
                            for (String vals : placeValues) {
                                Slot slotObject = new Slot(slot, vals); 
                                slotVals.add(slotObject);
                            }
                            if(slotIndex != 0)
                                values.add(placeValues);
                            state = 4;
                        case 4: // Getting possible values for a slot (ex: Mon, Tues, Wed for DAY)
                            if(slotIndex < placeHolders.size()) {
                                slot = placeHolders.get(slotIndex);
                                slotIndex++;
                                response = "Please type the values for place holder <" + slot.toUpperCase() + ">. (separated by a coma)";
                                state = 5;
                                break;
                            } else { // Adding the information to the text file once all slots have been looped through
                                // Reset slotIndex
                                slotIndex = 0;
                                try {
                                    skillEditor.addQuestion(prototype, placeHolders);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    skillEditor.addSlot(values, placeHolders);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                response = "Skill added!";
                                state = -1; // Goes to home page
                                break;
                            }
                        case 6:
                            response = "Please type the prototype sentence: ";
                            state = 7;
                            break;
                        case 7:
                            String sentence = "Question  " + message;
                            try {
                                skillScanner.setUp();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            response = skillScanner.scanSkill(sentence);
                            state = -1;
                            break;


                            // Add more cases/functionalities here



                        }



                        addBMessage(response, vbox_message);
                    }

                    String input = text_field.getText() + ke.getText();
                    if(!input.isEmpty() && !ke.getCode().equals(KeyCode.ENTER) && !input.substring(input.length()-1).equals(" ")) {
                        String word = new String();
                        for(int i=input.length()-1; i>=0; i--) {
                            if(input.substring(i,i+1).equals(" ")) {
                                word = input.substring(i,input.length());
                                break;
                            }
                        }

                        if(word.isEmpty()) {
                            word = input;
                        }
                        wordlength = word.length();

                        suggest1.setVisible(false);
                        suggest2.setVisible(false);
                        suggest3.setVisible(false);

                        try {
                            List<String>  suggestedwords = suggestwords(word);
                            if(suggestedwords!=null) {
                                if(suggestedwords.size()>0) {
                                    suggest1.setText(suggestedwords.get(0));
                                    suggest1.setVisible(true);
                                } 
                                if(suggestedwords.size()>1) {
                                    suggest2.setText(suggestedwords.get(1));
                                    suggest2.setVisible(true);
                                } 
                                if(suggestedwords.size()>2) {
                                    suggest3.setText(suggestedwords.get(2));
                                    suggest3.setVisible(true);
                                }
                            }

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        suggestbox.setVisible(true);
                    } else {
                        suggestbox.setVisible(false);
                    }
                }
            }
        );
        // Default opening message
        if(state == -1) {
            String greeting = String.join(" app, ", skills);
            greeting = ("Hello! How can I assist you? Would you like to access your " + greeting + " app, or quit? You can also create new skills!");
            state = 0;
            addBMessage(greeting, vbox_message);
        }
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
        suggestbox.setVisible(false);
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
        suggestbox.setVisible(false);
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
