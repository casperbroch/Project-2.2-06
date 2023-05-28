package group6;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.Thread.State;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import group6.Engine.Slot;
import group6.Engine.skillEditor;
import group6.Engine.cfgEditor;
import group6.Engine.skillScanner;
import group6.Engine.cykAlgorithm;
import group6.Engine.cfgScanner;
import group6.wordsuggestion.SymSpell;
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

        GOOGLECAL, GOOGLECALDELETE, GOOGLECALFETCH, GOOGLECALINSERT, GOOGLECALFETCHONDATE

    }

    // Important vars for the skill editor
    private String skillopening;
    private String googleopening;
    private skillScanner skillScanner;
    private cfgScanner cfgScanner;
    private skillEditor skillEditor;
    private cykAlgorithm cykAlg = new cykAlgorithm();;
    private cfgEditor cfgEditor = new cfgEditor();;
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
    private boolean editing;
    private ArrayList<String> actionValues2;
    private String action;
    private USERSTATE STATE = USERSTATE.HOME;
    private ArrayList<String> skills = new ArrayList<String>();
    private static boolean DARKMODE = false;
    private static ArrayList<HBox> hboxlist = new ArrayList<>();
    private static ArrayList<Text> textlist = new ArrayList<>();
    private CalendarConnection cal;
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

        return similarWords;
    }

    public void init() {
        skillopening = "Welcome to the Skills application! Do you wish to: \n1) Ask a question\n2) Add a new skill\n3) delete a skill\n4) edit a kill\n5) view a skill\nPlease type 'exit' if you want to exit this application.";
        googleopening = "Welcome to the Google Calendar application! Do you wish to: \n1) Insert an event\n2) Delete an event\n3) Fetch next 10 events\n4) Find a specific event\n5) Fetch events on a specific date\nPlease type 'exit' if you want to exit this application.";
        try {
            sp = new SymSpell();
            skillEditor = new skillEditor();
            skillScanner = new skillScanner();
        } catch (Exception e) {
            e.printStackTrace();
        }
        skills.add("Skills");
        skills.add("Google Calendar");
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
                                if(message.equalsIgnoreCase("Skills")) {
                                    response = skillopening;
                                    STATE = USERSTATE.SKILLHOME;
                                } else if(message.equalsIgnoreCase("Google Calendar")) {
                                    response = googleopening;
                                    STATE = USERSTATE.GOOGLECAL;
                                }
                                break;
                            }

                            if(i == skills.size() - 1)
                                // ? bot response, state remains the same
                                response = "Sorry, I do not have that application. Please try again.";
                            }
                            break;

                        // ! state description: skillhome is the state where the user has been prompted with the options and how selects one
                        // ! state type: USER CHOOSING 
                        case SKILLHOME:
                            if(message.equalsIgnoreCase("1")) {
                                // ? input 1 leads the user to asking a question
                                response = "You can ask your question.";
                                STATE = USERSTATE.SKILLQ1;
                            } else if (message.equalsIgnoreCase("2")) {
                                // ? input 2 leads the user to the adding a skill state
                                response = "Please type the sentence of the skill you would like to add.\n(e.g. How do I get from A to B?)";
                                STATE = USERSTATE.SKILLA1;
                            } else if (message.equalsIgnoreCase("3")) {
                                // ? input 3 leads the user to the deleting of a skill state
                                try {
                                    response = "Which skill would you like to delete?\n"+ cfgEditor.showskills();
                                } catch (Exception e) {}
                                STATE = USERSTATE.SKILLD1;
                            } else if (message.equalsIgnoreCase("4")) {
                                // ? input 4 leads the user to the editing of a skill state
                                response = "Which skill would you like to edit?\n"+ cfgEditor.showskills();
                                STATE = USERSTATE.SKILLE1;

                            } else if (message.equalsIgnoreCase("5")) {
                                // ? input 5 leads the user to the viewing of a skill state
                                try {
                                    response = "Which skill would you like to view?\n"+ cfgEditor.showskills();
                                } catch (Exception e) {}
                                STATE = USERSTATE.SKILLV1;
                            } else if(message.equalsIgnoreCase("exit")) {
                                STATE = USERSTATE.HOME;
                            }
                            break;

                        case SKILLE1:
                            if(message.equalsIgnoreCase("exit")) {
                                response=skillopening;
                                STATE = USERSTATE.SKILLHOME;
                                break;
                            }
                            int skillamounte = cfgEditor.getSkillAmount();
                            choiceedit = Integer.parseInt(message);

                            if(choiceedit <= skillamounte) {
                                response = "What action would you like to take?\n1) Add a slot\n2) Add an action\n3) Delete a slot\n4) Delete an action";
                                STATE = USERSTATE.SKILLE2;
                                break;
                            } else {
                                response = "Please choose a skill from the prompted list:\n"+ skillEditor.showskills();
                                STATE = USERSTATE.SKILLE1;
                                break;
                            }

                        case SKILLE2:
                            if(message.equalsIgnoreCase("exit")) {
                                response=skillopening;
                                STATE = USERSTATE.SKILLHOME;
                                break;
                            }
                            if(message.equalsIgnoreCase("1")) {
                                response = "What do you want to add as a slot?";
                                STATE = USERSTATE.SKILLEAddS1;
                            } else if (message.equalsIgnoreCase("2")) {
                                response = "What do you want to add as an action?";
                                editing = true;
                                STATE = USERSTATE.SKILLEAddA1;
                            } else if (message.equalsIgnoreCase("3")) {
                                ArrayList<String> questions = cfgEditor.getSkillQuestions();
                                response = "Which slot would you like to delete ?\n"+skillEditor.getSlots(questions.get(choiceedit-1));
                                STATE = USERSTATE.SKILLEDelS;
                            } else if (message.equalsIgnoreCase("4")) {
                                ArrayList<String> questions = cfgEditor.getSkillQuestions();
                                response = "Which action would you like to delete ?\n"+skillEditor.getActions(questions.get(choiceedit-1));
                                STATE = USERSTATE.SKILLEDelA;
                            }
                            break;

                        case SKILLEAddA1:
                            actionV = new ArrayList<>();
                            actionValues1 = new ArrayList<>();
                            actionindex=0;
                            addedaction = message;
                            ArrayList<String> questionA1 = cfgEditor.getSkillQuestions();
                            actionT = skillEditor.showSlots(questionA1.get(choiceedit-1));
                            response = "To which slot(s) does this action belong to? If the action belongs to multiple slots, type both numbers seperated by a ',' (e.g. 1,2,3).\nChoose from the available options below:\n";

                            String a2 = new String();
                            for(int i=0; i<actionT.size(); i++) {
                                a2 = a2+(i+1)+") "+actionT.get(i)+"\n";
                            }

                            response = response + a2;
                            STATE = USERSTATE.SKILLEAddA2;
                            break;

                        case SKILLEAddA2:
                            ArrayList<String> questionA3 = cfgEditor.getSkillQuestions();
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
                            response = "What is the value of slot " + actionValues1.get(actionindex) +"? \n"+skillEditor.printSlotsSpecasString(questionA3.get(choiceedit-1), actionValues1.get(actionindex));
                            STATE = USERSTATE.SKILLEAddA3;
                            break;

                        case SKILLEAddA3:
                            ArrayList<String> questionA2 = cfgEditor.getSkillQuestions();
                            ArrayList<String> slots2 = skillEditor.printSlotsSpec(questionA2.get(choiceedit-1), actionValues1.get(actionindex));
                            actionindex++;
                            actionV.add(slots2.get(Integer.parseInt(message)-1));

                            if(actionindex>=actionValues1.size()) {
                                if(skillEditor.duplicateAction(questionA2.get(choiceedit-1), addedaction, actionValues1, actionV)) {
                                    response = "That action already exists, please retry.\nWhat do you want to add as an action?";
                                    STATE = USERSTATE.SKILLEAddA1;
                                } else {
                                    skillEditor.removeEmptyLines();
                                    if(!editing){
                                        skillEditor.addActionNewSkill(questionA2.get(choiceedit-1), addedaction, actionValues1, actionV); 
                                    } else skillEditor.addAction(questionA2.get(choiceedit-1), addedaction, actionValues1, actionV); 
                                    editing = false;
                                    response = skillopening;
                                    STATE = USERSTATE.SKILLHOME;
                                }
                            } else {
                                response = "What is the value of slot " + actionValues1.get(actionindex) +"? \n"+skillEditor.printSlotsSpecasString(questionA2.get(choiceedit-1), actionValues1.get(actionindex));
                            }
                            break;

                        case SKILLEAddS1:
                            addedslot = message;
                            ArrayList<String> questionsas = cfgEditor.getSkillQuestions();
                            ArrayList<String> slotslist = skillEditor.showSlots(questionsas.get(choiceedit-1));
                            String a = new String();
                            for(int i=0; i<slotslist.size(); i++) {
                                a = a+(i+1)+") "+slotslist.get(i)+"\n";
                            }
                            response = "And to which slot would you like to add '" + message + "' to? Choose one from the options below:\n"+a;

                            STATE = USERSTATE.SKILLEAddS2;
                            break;

                        case SKILLEAddS2:
                            int choiceadds = Integer.parseInt(message);
                            ArrayList<String> questionsas2 = cfgEditor.getSkillQuestions();
                            ArrayList<String> slots = skillEditor.showSlots(questionsas2.get(choiceedit-1));
                            if(skillEditor.duplicateSlot(questionsas2.get(choiceedit-1), addedslot, slots.get(choiceadds-1))) {
                                response = "That slot already exists, please retry.";
                            } else {
                                skillEditor.addSlot(questionsas2.get(choiceedit-1), addedslot, slots.get(choiceadds-1));
                                skillEditor.removeEmptyLines();
                                response = skillopening;
                                STATE = USERSTATE.SKILLHOME;                            }
                            break;

                        case SKILLEDelA:
                            ArrayList<String> questionsdela = cfgEditor.getSkillQuestions();
                            ArrayList<String> actionsdela = skillEditor.printActions(questionsdela.get(choiceedit-1));
                            int choicedela = Integer.parseInt(message);
                            skillEditor.deleteAction(questionsdela.get(choiceedit-1), actionsdela.get(choicedela-1));
                            response = "Action deleted!\n"+skillopening;
                            STATE = USERSTATE.SKILLHOME;
                            break;

                        case SKILLEDelS:
                            ArrayList<String> questionsdels = cfgEditor.getSkillQuestions();
                            ArrayList<String> slotsdels = skillEditor.printSlots(questionsdels.get(choiceedit-1));
                            int choicedels = Integer.parseInt(message);
                            skillEditor.deleteSlot(questionsdels.get(choiceedit-1), slotsdels.get(choicedels-1));
                            response = "Slot deleted!\n"+skillopening;
                            STATE = USERSTATE.SKILLHOME;
                            break;

                        case SKILLV1:
                            if(message.equalsIgnoreCase("exit")) {
                                response=skillopening;
                                STATE = USERSTATE.SKILLHOME;
                                break;
                            }
                            int skillamountv = cfgEditor.getSkillAmount();
                            int choicev = Integer.parseInt(message);

                            if(choicev <= skillamountv) {
                                ArrayList<String> questions = cfgEditor.getSkillQuestions();
                                String ab = questions.get(choicev-1);
                                int parIndex = ab.indexOf(")");
                                String okay = ab.substring(parIndex+2, ab.length()).trim();
                                response = "Skill: " + questions.get(choicev-1).substring(parIndex+2, ab.length()).trim() + "\n"+ "\n" + cfgEditor.getSlots(okay) + "\n" + cfgEditor.getActions(okay);
                                response = response + "\n"+skillopening;
                                STATE = USERSTATE.SKILLHOME;
                                break;
                            } else {
                                response = "Please choose a skill from the prompted list,\n"+ skillEditor.showskills();
                                STATE = USERSTATE.SKILLV1;
                                break;
                            }


                        case SKILLD1:
                            if(message.equalsIgnoreCase("exit")) {
                                response=skillopening;
                                STATE = USERSTATE.SKILLHOME;
                                break;
                            }

                            int skillamountd = cfgEditor.getSkillAmount();
                            int choiced = Integer.parseInt(message);
                            
                            if(choiced <= skillamountd) {
                                ArrayList<String> questions = cfgEditor.getSkillQuestions();
                                String ab = questions.get(choiced-1);
                                int parIndex = ab.indexOf(")");
                                String okay = ab.substring(parIndex+2, ab.length()).trim();

                                cfgEditor.deleteSkill(okay);
                                response = "Skill removed! \n \n"+ skillopening;
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
                            slotIndex=0;
                            response = "Please type the slots you wish to set as placeholders (separated by a coma).\n(e.g. A, B)";
                            STATE = USERSTATE.SKILLA2;
                            break;
                        
                        // ! state description: skilla2 is the second of the skill adding sequence
                        case SKILLA2:
                            placeHolders = new ArrayList<>(Arrays.asList(message.split("[^a-zA-Z0-9]+"))); 
                            values = new ArrayList<ArrayList<String>>(); 
                            slotVals = new ArrayList<>();
                            STATE = USERSTATE.SKILLA4;
                            slot = placeHolders.get(slotIndex);
                            response = "Please type the different values for place holder <" + slot.toUpperCase() + "> (separated by a coma).\n(e.g. New York City, Maastricht, Eindhoven)";
                            break;

                         // ! state description: skilla4 is the fourth of the skill adding sequence
                        case SKILLA4:
                            ArrayList<String> placeValues = new ArrayList<>(Arrays.asList(message.split("\\s*,\\s*"))); 
                            for (String vals : placeValues) {
                                Slot slotObject = new Slot(slot, vals); 
                                slotVals.add(slotObject);
                            }
                            values.add(placeValues);

                            slotIndex++;

                            STATE = USERSTATE.SKILLA3;
                        
                        // ! state description: skilla3 is the third of the skill adding sequence
                        case SKILLA3:
                            if(slotIndex < placeHolders.size()) {
                                // ? if not every slot has been filled, fill the others
                                slot = placeHolders.get(slotIndex);
                                response = "Please type the different values for place holder <" + slot.toUpperCase() + "> (separated by a coma).\n(e.g. New York City, Maastricht, Eindhoven)";
                                STATE = USERSTATE.SKILLA4;
                                break;
                            } else {                            
                                try {
                                    // ? create the question and the slots
                                    skillEditor.setUp();
                                    skillEditor.addQuestion(prototype, placeHolders);
                                    skillEditor.addSlot(values, placeHolders);
                                    skillEditor.removeEmptyLines();
                                    skillEditor.closeUp();
                                } catch (IOException e) {e.printStackTrace();}
                                    // ? prompt the user to select the holder values
                                    response = "Do you wish to add a default action (this is the default answer when a specefic action has not been provided)?\n1) Yes\n2) No";
                                    STATE = USERSTATE.SKILLA5;
                            }
                            break;

                        // ! state description: skilla5 is the fifth of the skill adding sequence
                        case SKILLA5:
                            if(message.equalsIgnoreCase("1")) {
                                response = "Please type in your default action.\n(e.g. I don't know the answer to this question.)";
                                STATE = USERSTATE.SKILLA6;
                            } else if (message.equalsIgnoreCase("2")){
                                response = "Please type an action you would like to add";
                                ArrayList<String> questions = cfgEditor.getSkillQuestions();
                                choiceedit = questions.size();
                                STATE = USERSTATE.SKILLEAddA1;
                            } else {
                                response = "Please choose a valid option.";
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
                            response = "Please type an action you would like to add.\n(e.g. The distance is 101 km.)";
                            ArrayList<String> questions2 = cfgEditor.getSkillQuestions();
                            choiceedit = questions2.size();
                            STATE = USERSTATE.SKILLEAddA1;
                            break;

                        
                        case SKILLQ1:
                            if(message.equalsIgnoreCase("exit")) {
                                response = skillopening;
                                STATE = USERSTATE.SKILLHOME;
                            } else {
                                String sentence = message;
                                try {
                                    cykAlg.cykRun(sentence);
                                } catch (FileNotFoundException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                response = cykAlg.output + "\n\nYou can now ask another question, want to go back to the skill application menu? Type 'exit'.";
                                STATE = USERSTATE.SKILLQ1;
                            }
                        
                            break;


                            case GOOGLECAL:
                                if(message.equalsIgnoreCase("exit")) {
                                    STATE = USERSTATE.HOME;
                                    break;
                                }

                                try {
                                    switch(message){
                                        case "1":
                                            cal =new CalendarConnection();
                                            response ="Please enter a name for the event";
                                            STATE = USERSTATE.GOOGLECALINSERT;
                                            //cal.insertEvent(name, desc, date, start, end);
                                            break;
                                        case "2":
                                            cal =new CalendarConnection();
                                            response ="Which event do you want to delete?";
                                            STATE = USERSTATE.GOOGLECALDELETE;
                                            break;
                                        case "3":
                                            cal =new CalendarConnection();
                                            response ="Events list:";
                                            ArrayList<String> arr =cal.getNext10Events();
                                            for (int i =0; i< arr.size();i++){
                                                response=response+"\n"+arr.get(i);
                                            }
                                            response = response + "\n" + googleopening;
                                            break;
    
                                        case "4":
                                            cal =new CalendarConnection();
                                            response ="What event are you looking for?";
                                            STATE = USERSTATE.GOOGLECALFETCH;
                                            break;
    
                                        case "5":
                                            cal =new CalendarConnection();
                                            response ="On what date?";
                                            STATE = USERSTATE.GOOGLECALFETCHONDATE;
                                            break;
    
                                        default:
                                            response ="That is not an option";
                                            break;
                                    }
    
                                } catch (GeneralSecurityException | IOException e) {
                                    throw new RuntimeException(e);
                                }
                                break;
    
                            case GOOGLECALINSERT:
                                switch(cal.getCalInsertState()) {

                                    case 0:
                                        if(cal.setInsert1(message)){
                                            cal.incrementInsertCalState();
                                            response = "Give a short summary of the event";
                                        }else{
                                            response = "Yikes, that's not gonna work - try again";
                                        }
                                        break;
                                    case 1:
                                        if(cal.setInsert2(message)){
                                            cal.incrementInsertCalState();
                                            response = "What is the date? Use the format yyyy-mm-dd";
                                        }else{
                                            response = "Yikes, that's not gonna work - try again";
                                        }
                                        break;
                                    case 2:
                                        if(cal.setInsert3(message)){
                                            cal.incrementInsertCalState();
                                            response = "What is the start time? Use the format XX:XX";
                                        }else{
                                            response = "Yikes, that's not gonna work - try again";
                                        }
                                        break;
                                    case 3:
                                        if(cal.setInsert4(message)){
                                            cal.incrementInsertCalState();
                                            response = "What is the end time? Use the format XX:XX";
                                        }else{
                                            response = "Yikes, that's not gonna work - try again";
                                        }
                                        break;
                                    case 4:
                                        if(cal.setInsert5(message)){
                                            try {
                                                response = cal.insertEvent()+"\n"+googleopening;
    
                                                STATE =USERSTATE.GOOGLECAL;
    
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                            cal.incrementInsertCalState();
    
                                        }else{
                                            response = "Yikes, that's not gonna work - try again";
                                        }
                                }
                                break;

                                //response = "Inserting events is currently under development.\n"+googleopening;
                                //STATE = USERSTATE.GOOGLECAL;
                                //break;

    
                            case GOOGLECALFETCH:
                                //if(cal.isCalFetchState()){
                                //response ="You have no events matching that description... Try again or type 'back' to go back";
                                if(message.equalsIgnoreCase("exit")){
                                    response =googleopening;
                                    STATE =USERSTATE.GOOGLECAL;
                                }else{
                                    try {
                                        response =cal.getEvent(message)+"\nType 'exit' to go back";
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
    

                            break;
    
                            case GOOGLECALDELETE:
    
                                if(message.equalsIgnoreCase("exit")) {
                                    response = googleopening;
                                    STATE = USERSTATE.GOOGLECAL;
                                }else{
                                    try {
                                        if(cal.deleteEvent(message)){
                                            response = "Event deleted!"+"\nType 'exit' to go back";
                                        }else{
                                            response = "No event found"+"\nType 'exit' to go back";
                                        }
    
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
    
    
                                break;
                                
                            case GOOGLECALFETCHONDATE:
                            if(message.equalsIgnoreCase("exit")){
                                response ="Do you wish to 1) Insert event, or 2) Delete event, 3) Fetch next 10 events, 4) Find a specific event or 5) Find events on a specific date?";
                                STATE =USERSTATE.GOOGLECAL;
                            }else{
                                try {
                                    ArrayList<String> events =cal.getEventsOnDate(message);
                                    response ="Events list:";
                                    for(String str : events){
                                        response=response+"\n"+str;
                                    }
                                    response =response+"\nType 'exit' to go back";
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            break;

                            }
                        
                        if(!homegreeting()) {
                            addBMessage(response, vbox_message);
                        }
                    }

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
    

    public boolean homegreeting() {
        if(STATE == USERSTATE.HOME) {
            String greeting = String.join(" app, ", skills);
            greeting = ("Hello! How can I assist you? Would you like to access your " + greeting + " application, or quit? Please type the name of the desired application you would like to access.");
            STATE = USERSTATE.APPC;
            addBMessage(greeting, vbox_message);
            return true;
        } else return false;
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
