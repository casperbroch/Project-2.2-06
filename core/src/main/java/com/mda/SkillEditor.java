package com.mda;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class SkillEditor {
    
    private File file;
    private FileWriter writer; 
    private BufferedWriter br;
    private String prototype;
    public boolean addingAction;

    public static void main(String[] args) throws IOException {
        SkillEditor editor = new SkillEditor();
        editor.setUp();
    }

    public void setUp() throws IOException{
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")){
            file = new File("src\\main\\java\\com\\mda\\skills.txt");
        }
        else if (os.contains("os x")){
            file = new File("src/main/java/com/mda/skills.txt");
        }   
        writer = new FileWriter(file, true); 
        br = new BufferedWriter(writer);
        //addDefaultSkills();
        //addNewSkill();
        closeUp();
    }

    public void closeUp() throws IOException {
        br.close();
        writer.close();
    }

    public void addNewSkill() throws IOException{
        Scanner scanSkill = new Scanner(System.in);
        System.out.println("Please type the prototype sentence: ");
        prototype = scanSkill.nextLine();
        System.out.println("Please type the slots you wish to set as placeholders: (separated by a coma) ");
        String asdf = scanSkill.nextLine();

        ArrayList<String> placeHolders = new ArrayList<>(Arrays.asList(asdf.split("[^a-zA-Z0-9]+"))); 
        ArrayList<ArrayList<String>> values = new ArrayList<ArrayList<String>> (); 
        ArrayList<Slot> slotVals = new ArrayList<>(); 
        for (String slot : placeHolders) {
            System.out.println("Please type the values for place holder <" + slot.toUpperCase() + ">. (separated by a coma)");
            ArrayList<String> placeValues = new ArrayList<>(Arrays.asList(scanSkill.nextLine().split("[^a-zA-Z0-9]+"))); 

            for (String vals : placeValues) {
                Slot slotObject = new Slot(slot, vals); 
                slotVals.add(slotObject);
            }
            values.add(placeValues); 
        }

        addQuestion(prototype, placeHolders);
        addSlot(values, placeHolders);

        // addingAction = true;
        // while (addingAction) {
        //     System.out.println("Choose the holder values you would like to add actions for: (separated by a coma / To quit type 'quit')");
        //     ArrayList<String> actionValues = new ArrayList<>(Arrays.asList(scanSkill.nextLine().split("[^a-zA-Z0-9]+"))); 

        //     if(actionValues.get(0).equalsIgnoreCase("quit")){
        //         addingAction = false; 
        //     } else{
        //         System.out.println("What action would you like to add for the selected values?");
        //         String action = scanSkill.nextLine(); 
        //         addAction(actionValues, action, slotVals);
        //     }   
        // }
        scanSkill.close();
    }

    public void addQuestion(String question, ArrayList<String> placeHolders) throws IOException{
        question = question.toLowerCase();
        StringBuilder sb = new StringBuilder(question);
        for (String holder : placeHolders) {
            int i = sb.indexOf(holder);
            sb.insert(i, '<');
            sb.insert(i+holder.length()+1, '>');
            String substr = sb.substring(i, i+holder.length()+1);
            sb.replace(i, i+holder.length()+1, substr.toUpperCase());
        }
        br.write("\nQuestion  " + sb.toString());
    }

    public void addSlot(ArrayList<ArrayList<String>>  slots, ArrayList<String> placeHolders) throws IOException{
        for(int i = 0; i < placeHolders.size() ;i++){
            for(Object val : slots.get(i)){
                br.write("\nSlot  <" + placeHolders.get(i).toString().toUpperCase() + ">  "+ val);
            }
        }
    }

    public void addAction(ArrayList<String> actionValues, String action, ArrayList<Slot> slotVals) throws IOException{
        String finalAction = ""; 
        StringBuilder sb = new StringBuilder(finalAction);
        sb.append("\nAction"); 
        for(int i = 0; i < actionValues.size(); i++){                       
            for (Slot sl : slotVals) {
                if(sl.getParent().equals(actionValues.get(i).toString())){
                    sb.append("  <" + sl.getSlot().toUpperCase() + ">  "); 
                }
            }
            sb.append(actionValues.get(i).toString());                      
        }
        sb.append("  " + action);
        br.write(sb.toString());
    }

    // In case the .txt file content is deleted, call this. 
    public void addDefaultSkills() throws IOException{
        writer.write("Question  Which lectures are there on <DAY> at <TIME>\n");
        writer.write("Slot  <DAY>  Monday\n");
        writer.write("Slot  <DAY>  Tuesday\n");
        writer.write("Slot  <DAY>  Wednesday\n"); 
        writer.write("Slot  <DAY>  Thursday\n"); 
        writer.write("Slot  <DAY>  Friday\n"); 
        writer.write("Slot  <DAY>  Saturday\n"); 
        writer.write("Slot  <DAY>  Sunday\n"); 
        writer.write("Slot  <TIME>  9\n"); 
        writer.write("Slot  <TIME>  11\n"); 
        writer.write("Slot  <TIME>  13\n"); 
        writer.write("Slot  <TIME>  15\n"); 
        writer.write("Action  <DAY>  Saturday  There are no lectures on Saturday\n"); 
        writer.write("Action  <DAY>  Monday  <TIME>  9  We start the week with math\n"); 
        writer.write("Action  <DAY>  Monday  <TIME>  11  On Monday noon we have Theoratical Computer Science\n"); 
        writer.write("Action  I have no idea"); 
    }
















}
