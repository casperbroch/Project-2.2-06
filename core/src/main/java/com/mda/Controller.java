package com.mda;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.Thread.State;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
        APPC,

        SKILLHOME,
        SKILLQ1,
        SKILLA1, SKILLA2, SKILLA3, SKILLA4, SKILLA5, SKILLA6, SKILLA7,
        SKILLD1,
        SKILLE1, SKILLE2,
        SKILLEAddS1, SKILLEAddS2, SKILLEAddS3,
        SKILLEAddA1, SKILLEAddA2, SKILLEAddA3, SKILLEAddA4,
        SKILLEDelS,
        SKILLEDelA,
        SKILLV1,
    
    }

    // Important vars for the skill editor
    private skillScanner skillScanner;
    private skillEditor skillEditor;

    private String prototype;
    private String addedslot;
    private String addedaction;
    ArrayList<Integer> actionNumsOrdered;
    ArrayList<String> actionT;
    ArrayList<String> actionV;
    private ArrayList<String> actionValues1;
    int actionindex;
    
    private String valueSlots;
    private int choiceedit;
    private ArrayList<String> placeHolders;
    private ArrayList<ArrayList<String>> values;
    private ArrayList<Slot> slotVals;
    private int slotIndex = 0;
    private String response;
    private String slot;

    private String message;

    private ArrayList<String> actionValues2;
    private String action;

    private USERSTATE STATE = USERSTATE.HOME;
    private ArrayList<String> skills = new ArrayList<String>();
    private static boolean DARKMODE = false;
    private static ArrayList<HBox> hboxlist = new ArrayList<>();
    private static ArrayList<Text> textlist = new ArrayList<>();
    private int wordlength=0;

    private SymSpell sp;

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
        List<String> similarWords = sp.getSimilarWordsDistance(wrongWord, 3, 3);

        if (similarWords!=null && similarWords.size()!=0){
            System.out.println("Words within 3 Damerau-Levenshtein distance of " + wrongWord + ": " + similarWords);
        } else {
            System.out.println("The word is correct");
        }
        return similarWords;
    }

    public void init() {
        try {
            sp = new SymSpell();
            skillEditor = new skillEditor();
            skillScanner = new skillScanner();
        } catch (Exception e) {
            e.printStackTrace();
        }
        skills.add("skills");
        suggestbox.setVisible(false);
        if(DARKMODE) {
            setDarkMode();
        } else {
            setLightMode();
        }
        scroll_pane.setHbarPolicy(ScrollBarPolicy.NEVER);
        scroll_pane.setVbarPolicy(ScrollBarPolicy.NEVER);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        init();

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
            @Override
            public void handle(KeyEvent ke) {   
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    // * print the user message on enter click
                    message = text_field.getText();
                    addUMessage(message, vbox_message);

                    // * shuts the program off when quit is typed
                    if(message.toLowerCase().contains("quit")) {
                        System.exit(0);
                    }

                    // * default response if no action is satisfied
                    response = "Sorry, unknown action.";

                    // * the state system is shown below
                    switch(STATE) {

                    // ! state description: appc is the state of the user inputting the app they want to choose 
                    // ! state type: USER CHOOSING 
                        case APPC:
                        for (int i = 0; i < skills.size(); i++) {
                            if(message.compareToIgnoreCase(skills.get(i)) == 0) {                                
                                // ? if user chose the app 'skills' the user gets progressed to the correct state & is given the correct bot response
                                if(message.equalsIgnoreCase("skills")) {
                                    response = "Welcome to the Skill APP!\nDo you wish to 1) ask a question, or 2) add, 3) delete, 4) edit, 5) view a skill?";
                                    STATE = USERSTATE.SKILLHOME;
                                }
                                break;
                            }

                            if(i == skills.size() - 1)
                                // ? bot response, state remains the same
                                response = "Sorry, I do not have that app. Please try again.";
                            }
                            break;

                        // ! state description: skillhome is the state where the user has been prompted with the options and how selects one
                        // ! state type: USER CHOOSING 
                        case SKILLHOME:
                            if(message.equalsIgnoreCase("1")) {
                                // ? input 1 leads the user to asking a question
                                response = "You can ask your question: ";
                                STATE = USERSTATE.SKILLQ1;
                            } else if (message.equalsIgnoreCase("2")) {
                                // ? input 2 leads the user to the adding a skill state
                                response = "Please type the prototype sentence: ";
                                STATE = USERSTATE.SKILLA1;
                            } else if (message.equalsIgnoreCase("3")) {
                                // ? input 3 leads the user to the deleting of a skill state
                                try {
                                    response = "Which skill would you like to delete?\n"+ skillEditor.showskills();
                                } catch (Exception e) {}
                                STATE = USERSTATE.SKILLD1;
                            } else if (message.equalsIgnoreCase("4")) {
                                // ? input 4 leads the user to the editing of a skill state
                                response = "Which skill would you like to edit?\n"+ skillEditor.showskills();
                                STATE = USERSTATE.SKILLE1;

                            } else if (message.equalsIgnoreCase("5")) {
                                // ? input 5 leads the user to the viewing of a skill state
                                try {
                                    response = "Which skill would you like to view?\n"+ skillEditor.showskills();
                                } catch (Exception e) {}
                                STATE = USERSTATE.SKILLV1;
                            }
                            break;

                        case SKILLE1:
                            int skillamounte = skillEditor.getSkillAmount();
                            choiceedit = Integer.parseInt(message);

                            if(choiceedit <= skillamounte) {
                                response = "What action would you like to take?\n1) Add a slot\n2) Add an action\n3) Delete a slot\n4) Delete an action";
                                STATE = USERSTATE.SKILLE2;
                                break;
                            } else {
                                response = "Please choose a skill from the prompted list,\n"+ skillEditor.showskills();
                                STATE = USERSTATE.SKILLE1;
                                break;
                            }

                        case SKILLE2:
                            if(message.equalsIgnoreCase("1")) {
                                response = "What do you want to add as a slot?";
                                STATE = USERSTATE.SKILLEAddS1;
                            } else if (message.equalsIgnoreCase("2")) {
                                response = "What do you want to add as an action?";
                                STATE = USERSTATE.SKILLEAddA1;
                            } else if (message.equalsIgnoreCase("3")) {
                                ArrayList<String> questions = skillEditor.getSkillQuestions();
                                response = "Which slot would you like to delete ?\n"+skillEditor.getSlots(questions.get(choiceedit-1));
                                STATE = USERSTATE.SKILLEDelS;
                            } else if (message.equalsIgnoreCase("4")) {
                                ArrayList<String> questions = skillEditor.getSkillQuestions();
                                response = "Which action would you like to delete ?\n"+skillEditor.getActions(questions.get(choiceedit-1));
                                STATE = USERSTATE.SKILLEDelA;
                            }
                            break;

                        case SKILLEAddA1:
                            actionV = new ArrayList<>();
                            actionValues1 = new ArrayList<>();
                            actionindex=0;
                            addedaction = message;
                            ArrayList<String> questionA1 = skillEditor.getSkillQuestions();
                            actionT = skillEditor.showSlots(questionA1.get(choiceedit-1));
                            response = "To which slot(s) does this data belong to? You can find the slots below: ";
                            response = response + actionT;
                            STATE = USERSTATE.SKILLEAddA2;
                            break;

                        case SKILLEAddA2:
                            ArrayList<String> questionA3 = skillEditor.getSkillQuestions();
                            int cnt = 0;
                            
                            ArrayList<String> actionNums = new ArrayList<>(Arrays.asList(message.split("[^a-zA-Z0-9]+")));
                            actionNumsOrdered = new ArrayList<>();
                            for (String string : actionNums) {
                                actionNumsOrdered.add(Integer.parseInt(string));
                            }
                            Collections.sort(actionNumsOrdered);
                            actionV = new ArrayList<>(); 
                            for (String string : actionNums) {
                                actionValues1.add(actionT.get(actionNumsOrdered.get(cnt)-1));
                                cnt++;
                            }
                            System.out.println(actionValues1.size());
                            response = "What is the value of slot " + actionValues1.get(actionindex) +"? \n"+skillEditor.printSlotsSpecasString(questionA3.get(choiceedit-1), actionValues1.get(actionindex));
                            STATE = USERSTATE.SKILLEAddA3;
                            break;

                        case SKILLEAddA3:
                            ArrayList<String> questionA2 = skillEditor.getSkillQuestions();
                            

                            ArrayList<String> slots2 = skillEditor.printSlotsSpec(questionA2.get(choiceedit-1), actionValues1.get(actionindex));
                            actionindex++;
                            actionV.add(slots2.get(Integer.parseInt(message)-1));

                            if(actionindex>=actionValues1.size()) {
                                if(skillEditor.duplicateAction(questionA2.get(choiceedit-1), addedaction, actionValues1, actionV)) {
                                    response = "That action already exisits, please retry.\nWhat do you want to add as an action?";
                                    STATE = USERSTATE.SKILLEAddA1;
                                } else {
                                    skillEditor.addAction(questionA2.get(choiceedit-1), addedaction, actionValues1, actionV); 
                                    skillEditor.removeEmptyLines();
                                    response = "Action added!\nDo you wish to 1) ask a question, or 2) add, 3) delete, 4) edit, 5) view a skill?";
                                    STATE = USERSTATE.SKILLHOME;
                                }
                            } else {
                                response = "What is the value of slot " + actionValues1.get(actionindex) +"? \n"+skillEditor.printSlotsSpecasString(questionA2.get(choiceedit-1), actionValues1.get(actionindex));
                            }
                            break;

                        case SKILLEAddS1:
                            addedslot = message;
                            ArrayList<String> questionsas = skillEditor.getSkillQuestions();
                            response = "And to which slot would you like to add '" + message + "' to? Choose one from the options below:\n"+skillEditor.showSlots(questionsas.get(choiceedit-1));
                            STATE = USERSTATE.SKILLEAddS2;
                            break;

                        case SKILLEAddS2:
                            int choiceadds = Integer.parseInt(message);
                            ArrayList<String> questionsas2 = skillEditor.getSkillQuestions();
                            ArrayList<String> slots = skillEditor.showSlots(questionsas2.get(choiceedit-1));
                            if(skillEditor.duplicateSlot(questionsas2.get(choiceedit-1), addedslot, slots.get(choiceadds-1))) {
                                response = "That slot already exists, please retry.";
                            } else {
                                skillEditor.addSlot(questionsas2.get(choiceedit-1), addedslot, slots.get(choiceadds-1));
                                skillEditor.removeEmptyLines();
                                response = "Slot added!\nDo you wish to 1) ask a question, or 2) add, 3) delete, 4) edit, 5) view a skill?";
                                STATE = USERSTATE.SKILLHOME;                            }
                            break;

                        case SKILLEDelA:
                            ArrayList<String> questionsdela = skillEditor.getSkillQuestions();
                            ArrayList<String> actionsdela = skillEditor.printActions(questionsdela.get(choiceedit-1));
                            int choicedela = Integer.parseInt(message);
                            skillEditor.deleteAction(questionsdela.get(choiceedit-1), actionsdela.get(choicedela-1));
                            response = "Action deleted!\nDo you wish to 1) ask a question, or 2) add, 3) delete, 4) edit, 5) view a skill?";
                            STATE = USERSTATE.SKILLHOME;
                            break;

                        case SKILLEDelS:
                            ArrayList<String> questionsdels = skillEditor.getSkillQuestions();
                            ArrayList<String> slotsdels = skillEditor.printSlots(questionsdels.get(choiceedit-1));
                            int choicedels = Integer.parseInt(message);
                            skillEditor.deleteSlot(questionsdels.get(choiceedit-1), slotsdels.get(choicedels-1));
                            response = "Slot deleted!\nDo you wish to 1) ask a question, or 2) add, 3) delete, 4) edit, 5) view a skill?";
                            STATE = USERSTATE.SKILLHOME;
                            break;

                        case SKILLV1:
                            int skillamountv = skillEditor.getSkillAmount();
                            int choicev = Integer.parseInt(message);

                            if(choicev <= skillamountv) {
                                ArrayList<String> questions = skillEditor.getSkillQuestions();
                                response = questions.get(choicev-1) + "\n" + skillEditor.getSlots(questions.get(choicev-1)) + "\n" + skillEditor.getActions(questions.get(choicev-1));
                                response = response + "\nDo you wish to 1) ask a question, or 2) add, 3) delete, 4) edit, 5) view a skill?";
                                STATE = USERSTATE.SKILLHOME;
                                break;
                            } else {
                                response = "Please choose a skill from the prompted list,\n"+ skillEditor.showskills();
                                STATE = USERSTATE.SKILLV1;
                                break;
                            }


                        case SKILLD1:
                            int skillamountd = skillEditor.getSkillAmount();
                            int choiced = Integer.parseInt(message);
                            
                            if(choiced <= skillamountd) {
                                ArrayList<String> questions = skillEditor.getSkillQuestions();
                                skillEditor.deleteSkill(questions.get(choiced-1));
                                response = "Skill removed!\nDo you wish to 1) ask a question, or 2) add, 3) delete, 4) edit, 5) view a skill?";
                                STATE = USERSTATE.SKILLHOME;
                                break;
                            } else {
                                response = "Please choose a skill from the prompted list,\n"+ skillEditor.showskills();
                                STATE = USERSTATE.SKILLD1;
                                break;
                            }



                        // ! state description: skilla1 is the first of the skill adding sequence
                        case SKILLA1:
                            prototype = message;
                            response = "Please type the slots you wish to set as placeholders: (separated by a coma) ";
                            STATE = USERSTATE.SKILLA2;
                            break;
                        
                        // ! state description: skilla2 is the second of the skill adding sequence
                        case SKILLA2:
                            placeHolders = new ArrayList<>(Arrays.asList(message.split("[^a-zA-Z0-9]+"))); 
                            values = new ArrayList<ArrayList<String>>(); 
                            slotVals = new ArrayList<>();
                            STATE = USERSTATE.SKILLA3;

                         // ! state description: skilla4 is the fourth of the skill adding sequence
                        case SKILLA4:
                            ArrayList<String> placeValues = new ArrayList<>(Arrays.asList(message.split("[^a-zA-Z0-9]+"))); 
                            for (String vals : placeValues) {
                                Slot slotObject = new Slot(slot, vals); 
                                slotVals.add(slotObject);
                            }

                            if(slotIndex != 0) {
                                values.add(placeValues);
                            }

                            STATE = USERSTATE.SKILLA3;
                        
                        // ! state description: skilla3 is the third of the skill adding sequence
                        case SKILLA3:
                            if(slotIndex < placeHolders.size()) {
                                // ? if not every slot has been filled, fill the others
                                slot = placeHolders.get(slotIndex);
                                slotIndex++;
                                response = "Please type the values for place holder <" + slot.toUpperCase() + ">. (separated by a coma)";
                                STATE = USERSTATE.SKILLA4;
                                break;
                            } else {                            
                                try {
                                    System.out.println("adding a skill rn");
                                    // ? create the question and the slots
                                    skillEditor.setUp();
                                    skillEditor.addQuestion(prototype, placeHolders);
                                    skillEditor.addSlot(values, placeHolders);
                                    skillEditor.removeEmptyLines();
                                    skillEditor.closeUp();
                                } catch (IOException e) {e.printStackTrace();}
                                    // ? prompt the user to select the holder values
                                    response = "Do you wish to add a default action?\n1) Yes\n2) No";
                                    STATE = USERSTATE.SKILLA5;
                            }
                            break;

                        // ! state description: skilla5 is the fifth of the skill adding sequence
                        case SKILLA5:
                            if(message.equalsIgnoreCase("1")) {
                                response = "Please type in your default action";
                                STATE = USERSTATE.SKILLA6;
                            } else if (message.equalsIgnoreCase("2")){
                                response = "Please type an action you would like to add";
                                ArrayList<String> questions = skillEditor.getSkillQuestions();
                                choiceedit = questions.size()-1;
                                STATE = USERSTATE.SKILLEAddA1;

                            }
                            break;

                        case SKILLA6:
                            try {
                                skillEditor.setUp();
                                skillEditor.addAction(new ArrayList<String>(), message, new ArrayList<Slot>());
                                skillEditor.removeEmptyLines();
                                skillEditor.closeUp();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            response = "Please type an action you would like to add";
                            ArrayList<String> questions2 = skillEditor.getSkillQuestions();
                            choiceedit = questions2.size();
                            STATE = USERSTATE.SKILLEAddA1;
                            break;

                        
                            case SKILLQ1:
                                if(message.equalsIgnoreCase("back")) {
                                    response = "Welcome to the Skill APP!\nDo you wish to 1) ask a question, or 2) add, 3) delete, 4) edit, 5) view a skill?";
                                    STATE = USERSTATE.SKILLHOME;
                                } else {
                                    String sentence = "Question  " + message;
                                    response = skillScanner.scanSkill(sentence)+ "\nWant to go back to the skill app menu? Type 'back'.";
                                    STATE = USERSTATE.SKILLQ1;
                                }
                        
                                break;
                            }
                        
                        System.out.println(STATE);
                        addBMessage(response, vbox_message);
                        // ! greet the user with home menu if they are in home state
                        homegreeting();
                    }

                    

                    // // Response generator
                    // switch(STATE) {
                    //     case SKILLC1: // Main greeting; what the user actually wants to do
                    //         for (int i = 0; i < skills.size(); i++) {
                    //             if(message.compareToIgnoreCase(skills.get(i)) == 0) {
                    //                 // Directed to a specific skill
                    //                 response = responder.getSkills(message);
                    //                 STATE = USERSTATE.SKILLC2;
                    //                 break;
                    //             }
                    //             if(i == skills.size() - 1)
                    //             response = "Sorry, I do not have that app. Please try again.";
                    //         }
                    //         break;

                    //     case SKILLC2:
                    //         if(message.equalsIgnoreCase("add")) {
                    //             STATE = USERSTATE.SCS1;
                    //             response = "Please type the prototype sentence: ";
                    //         } else if(message.equalsIgnoreCase("retrieve")) {
                    //             STATE = USERSTATE.SGS1;
                    //             response = "You can ask your question: ";
                    //         }
                    //         break;


                    //     case SCS1: // Which slots in the prototype sentence the user can change
                    //         prototype = message;
                    //         response = "Please type the slots you wish to set as placeholders: (separated by a coma) ";
                    //         STATE = USERSTATE.SCS2;
                    //         break;
                    //     case SCS2: // Creating a list of slots/placeholders (ex: DAY, TIME)
                    //         placeHolders = new ArrayList<>(Arrays.asList(message.split("[^a-zA-Z0-9]+"))); 
                    //         values = new ArrayList<ArrayList<String>>(); 
                    //         slotVals = new ArrayList<>();
                    //         STATE = USERSTATE.SCS3;
                    //     case SCS4: // Getting all the possible values for a slot as a list
                    //         ArrayList<String> placeValues = new ArrayList<>(Arrays.asList(message.split("[^a-zA-Z0-9]+"))); 
                    //         for (String vals : placeValues) {
                    //             Slot slotObject = new Slot(slot, vals); 
                    //             slotVals.add(slotObject);
                    //         }
                    //         if(slotIndex != 0)
                    //             values.add(placeValues);
                    //         STATE = USERSTATE.SCS3;
                    //     case SCS3: // Getting possible values for a slot (ex: Mon, Tues, Wed for DAY)
                    //         if(slotIndex < placeHolders.size()) {
                    //             slot = placeHolders.get(slotIndex);
                    //             slotIndex++;
                    //             response = "Please type the values for place holder <" + slot.toUpperCase() + ">. (separated by a coma)";
                    //             STATE = USERSTATE.SCS4;
                    //             break;
                    //         } else {                            
                    //             try {
                    //                 skillEditor.setUp();
                    //                 skillEditor.addQuestion(prototype, placeHolders);
                    //                 skillEditor.addSlot(values, placeHolders);
                    //                 skillEditor.closeUp();
                    //             } catch (IOException e) {
                    //                 e.printStackTrace();
                    //             }
                    //             response = "Choose the holder values you would like to add actions for: (separated by a coma)";
                    //             STATE = USERSTATE.SCS5;
                    //             break;
                    //         } 

                    //     case SCS5:
                    //         actionValues = new ArrayList<>(Arrays.asList(message.split("[^a-zA-Z0-9]+")));
                    //         response = "What action would you like to add for the selected values?";
                    //         STATE = USERSTATE.SCS6;
                    //         break;
                        
                    //     case SCS6:
                    //         action = message;
                    //         try {
                    //             skillEditor.setUp();
                    //             skillEditor.addAction(actionValues, action, slotVals);
                    //             skillEditor.closeUp();
                    //         } catch (IOException e) {
                    //             e.printStackTrace();
                    //         }
                    //         response = "Do you want to create another action or quit and finalize the skill? Simply press 'Enter' to quit or type once again the holder values you would like to add actions for: (seperated by a coma)";
                    //         STATE = USERSTATE.SCS7;
                    //         break;
                        
                    //     case SCS7:
                    //         if(message.equals("")||message.equals(" ")||message.equals(null)) {
                    //             slotIndex = 0;
                    //             response = "Skill added!";
                    //             STATE = USERSTATE.HOME;
                    //         } else {
                    //             actionValues = new ArrayList<>(Arrays.asList(message.split("[^a-zA-Z0-9]+")));
                    //             response = "What action would you like to add for the selected values?";
                    //             STATE = USERSTATE.SCS6;
                    //         }
                    //         break;

                    //     case SGS1:
                    //         String sentence = "Question  " + message;
                    //         try {
                    //             skillScanner.setUp();
                    //         } catch (FileNotFoundException e) {
                    //             e.printStackTrace();
                    //         }
                    //         response = skillScanner.scanSkill(sentence);
                    //         STATE = USERSTATE.HOME;
                    //         break;
                    //     }

                    //     System.out.println(STATE);

                    //     addBMessage(response, vbox_message);
                    //     homegreeting();
                    // }

                    // ! code below is for the word suggestion 
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
            });
            // ! First home greeting when program is started
            homegreeting();
    }
    

    public void homegreeting() {
        if(STATE == USERSTATE.HOME) {
            String greeting = String.join(" app, ", skills);
            greeting = ("Hello! How can I assist you? Would you like to access your " + greeting + " app, or quit? You can also create new skills!");
            STATE = USERSTATE.APPC;
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
