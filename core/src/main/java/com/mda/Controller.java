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

    /**
     * HOME - home state
     * SKILLC(x) - states where the user can choose which app to use
     * SCS(x) - skill creating states which are states where the user uses to add a new skill
     * SGS(x) - skill getting states which are state where the user can retrieve skill information by asking a question
     */
    public enum USERSTATE {
        HOME,
        SKILLC1, SKILLC2,
        SCS1, SCS2, SCS3, SCS4, SCS5, SCS6, SCS7,
        SGS1,
    }

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
    ArrayList<String> actionValues;
    String action;
    //private int state = -1;
    USERSTATE STATE = USERSTATE.HOME;
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
        skills.add("skills");



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

                    response = "Sorry, unknown action.";

                    // Response generator
                    switch(STATE) {
                        case SKILLC1: // Main greeting; what the user actually wants to do
                            for (int i = 0; i < skills.size(); i++) {
                                if(message.compareToIgnoreCase(skills.get(i)) == 0) {
                                    // Directed to a specific skill
                                    response = responder.getSkills(message);
                                    STATE = USERSTATE.SKILLC2;
                                    break;
                                }
                                if(i == skills.size() - 1)
                                response = "Sorry, I do not have that app. Please try again.";
                            }
                            break;

                        case SKILLC2:
                            if(message.equalsIgnoreCase("add")) {
                                STATE = USERSTATE.SCS1;
                                response = "Please type the prototype sentence: ";
                            } else if(message.equalsIgnoreCase("retrieve")) {
                                STATE = USERSTATE.SGS1;
                                response = "You can ask your question: ";
                            }
                            break;


                        case SCS1: // Which slots in the prototype sentence the user can change
                            prototype = message;
                            response = "Please type the slots you wish to set as placeholders: (separated by a coma) ";
                            STATE = USERSTATE.SCS2;
                            break;
                        case SCS2: // Creating a list of slots/placeholders (ex: DAY, TIME)
                            placeHolders = new ArrayList<>(Arrays.asList(message.split("[^a-zA-Z0-9]+"))); 
                            values = new ArrayList<ArrayList<String>>(); 
                            slotVals = new ArrayList<>();
                            STATE = USERSTATE.SCS3;
                        case SCS4: // Getting all the possible values for a slot as a list
                            ArrayList<String> placeValues = new ArrayList<>(Arrays.asList(message.split("[^a-zA-Z0-9]+"))); 
                            for (String vals : placeValues) {
                                Slot slotObject = new Slot(slot, vals); 
                                slotVals.add(slotObject);
                            }
                            if(slotIndex != 0)
                                values.add(placeValues);
                            STATE = USERSTATE.SCS3;
                        case SCS3: // Getting possible values for a slot (ex: Mon, Tues, Wed for DAY)
                            if(slotIndex < placeHolders.size()) {
                                slot = placeHolders.get(slotIndex);
                                slotIndex++;
                                response = "Please type the values for place holder <" + slot.toUpperCase() + ">. (separated by a coma)";
                                STATE = USERSTATE.SCS4;
                                break;
                            } else {                            
                                try {
                                    skillEditor.setUp();
                                    skillEditor.addQuestion(prototype, placeHolders);
                                    skillEditor.addSlot(values, placeHolders);
                                    skillEditor.closeUp();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                response = "Choose the holder values you would like to add actions for: (separated by a coma)";
                                STATE = USERSTATE.SCS5;
                                break;
                            } 

                        case SCS5:
                            actionValues = new ArrayList<>(Arrays.asList(message.split("[^a-zA-Z0-9]+")));
                            response = "What action would you like to add for the selected values?";
                            STATE = USERSTATE.SCS6;
                            break;
                        
                        case SCS6:
                            action = message;
                            try {
                                skillEditor.setUp();
                                skillEditor.addAction(actionValues, action, slotVals);
                                skillEditor.closeUp();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            response = "Do you want to create another action or quit and finalize the skill? Simply press 'Enter' to quit or type once again the holder values you would like to add actions for: (seperated by a coma)";
                            STATE = USERSTATE.SCS7;
                            break;
                        
                        case SCS7:
                            if(message.equals("")||message.equals(" ")||message.equals(null)) {
                                slotIndex = 0;
                                response = "Skill added!";
                                STATE = USERSTATE.HOME;
                            } else {
                                actionValues = new ArrayList<>(Arrays.asList(message.split("[^a-zA-Z0-9]+")));
                                response = "What action would you like to add for the selected values?";
                                STATE = USERSTATE.SCS6;
                            }
                            break;

                        case SGS1:
                            String sentence = "Question  " + message;
                            try {
                                skillScanner.setUp();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            response = skillScanner.scanSkill(sentence);
                            STATE = USERSTATE.HOME;
                            break;
                        }

                        addBMessage(response, vbox_message);
                        homegreeting();
                    }

                    String input = text_field.getText() + ke.getText();
                    if(!input.isEmpty() && ke.getCode().equals(KeyCode.BACK_QUOTE) && !input.substring(input.length()-1).equals(" ")) {
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
        // First home greeting
        homegreeting();
    }

    public void homegreeting() {
        if(STATE == USERSTATE.HOME) {
            String greeting = String.join(" app, ", skills);
            greeting = ("Hello! How can I assist you? Would you like to access your " + greeting + " app, or quit? You can also create new skills!");
            STATE = USERSTATE.SKILLC1;
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
