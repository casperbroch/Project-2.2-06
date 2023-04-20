package com.mda.Engine;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mda.App;


public class skillEditor {

    private File file;
    private FileWriter writer; 
    private BufferedWriter br;
    private String prototype;
    private boolean addingAction;

    public static void main(String[] args) throws IOException {
        skillEditor editor = new skillEditor();
        editor.setUp();
    }

    public void setUp() throws IOException{
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")){
            file = new File(App.TEXTPATH);
        }
        else if (os.contains("os x")){
            file = new File(App.TEXTPATH);
        }   
        writer = new FileWriter(file, true); 
        br = new BufferedWriter(writer);
        removeEmptyLines();
        //getSkillAndAction();
        //removeEmptyLines();
    }

    public void removeEmptyLines(){
        try {
            String current = "";
            BufferedReader readerDel1 = new BufferedReader(new FileReader(file));
            StringBuilder stringBuilder1 = new StringBuilder();
            while((current = readerDel1.readLine()) != null) {
                if(current.length() != 0){
                    stringBuilder1.append(current);
                    stringBuilder1.append(System.getProperty("line.separator"));
                }
            }
            readerDel1.close();
            FileWriter writerDel1 = new FileWriter(file);
            writerDel1.write(stringBuilder1.toString());
            writerDel1.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeUp() throws IOException {
        br.close();
        writer.close();
    }


    public void addSlot(String skill, String data, String slotLoc){
        // check if it exists 
        try {
            FileInputStream fs = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fs));
            ArrayList<String> lines = new ArrayList<>();
            String line;
            boolean relevant = false;
            int index = 0;
            int counter = 1;
            while ((line = br.readLine()) != null) {
                if(line.equals(skill)){
                    relevant = true;
                }
                if(relevant && line.startsWith("Action")){
                 index = counter-1; 
                 relevant = false;  
                }
                counter++;
                lines.add(line);
            }
            br.close();
            lines.add(index, "Slot  <"+slotLoc+">  " + data );
            FileOutputStream fos = new FileOutputStream(file);
            BufferedWriter bnew = new BufferedWriter(new OutputStreamWriter(fos));
            for (String str : lines) {
               bnew.write(str);
               bnew.newLine();
            }
            bnew.close();
        
         } catch (IOException e) {
         }
    }

    public void deleteSlot(String skill, String slot){
        try {
            BufferedReader readerDel = new BufferedReader(new FileReader(file));
            StringBuilder stringBuilder = new StringBuilder();
            String current;
            boolean delete = false;
            if(printSlots(skill).size() != 1){
                while((current = readerDel.readLine()) != null) {
                    String trimmedLine = current.trim();
                    if(delete && current.startsWith("Question")){
                        delete = false;
                    }
                    if(trimmedLine.equals(skill)){
                        delete = true;
                    } 
                    if (delete && trimmedLine.equals(slot)){
                        continue;
                    } else{
                        stringBuilder.append(current);
                        stringBuilder.append(System.getProperty("line.separator"));
                    }
                }
                readerDel.close();
                FileWriter writerDel = new FileWriter(file);
                writerDel.write(stringBuilder.toString());
                writerDel.close();
            } 
            
        } catch(IOException e) {
        }
    }

    public void deleteAction(String skill, String action){
        try {
            BufferedReader readerDel = new BufferedReader(new FileReader(file));
            StringBuilder stringBuilder = new StringBuilder();
            String current;
            boolean delete = false;
            if(printActions(skill).size() != 1){
                while((current = readerDel.readLine()) != null) {
                    String trimmedLine = current.trim();
                    if(delete && current.startsWith("Question")){
                        delete = false;
                    }
                    if(trimmedLine.equals(skill)){
                        delete = true;
                    } 
                    if (delete && trimmedLine.equals(action)){
                        continue;
                    } else{
                        stringBuilder.append(current);
                        stringBuilder.append(System.getProperty("line.separator"));
                    }
                }
                readerDel.close();
                FileWriter writerDel = new FileWriter(file);
                writerDel.write(stringBuilder.toString());
                writerDel.close();
            } 

        
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void addNewSkill() throws IOException{        
        Scanner scanSkill = new Scanner(System.in);
        prototype = scanSkill.nextLine();
        ArrayList<String> placeHolders = new ArrayList<>(Arrays.asList(scanSkill.nextLine().split("[^a-zA-Z0-9]+"))); 
        ArrayList<ArrayList<String>> values = new ArrayList<ArrayList<String>> (); 
        ArrayList<Slot> slotVals = new ArrayList<>(); 
        boolean flag = false;
        ArrayList<String> questions = new ArrayList<>();

        try(BufferedReader readerQ = new BufferedReader(new FileReader(file))){ 
            String line = "";
            while ((line = readerQ.readLine()) != null) {
                if (line.startsWith("Question")) {
                    questions.add(line);
                }
            } 
        }

        StringBuilder sb = new StringBuilder(prototype);
        for (String holder : placeHolders) {
            boolean found = false;
            int current = 0; 
            int i = 0;
            while(!found){
                i = sb.indexOf(holder, current);
                if(sb.indexOf(holder) == 0 && sb.substring(i+holder.length(), i+holder.length()+1).equals(" ")){
                    found = true;

                }else if (i == sb.length()-holder.length() && sb.substring(i-1, i).equals(" ")){
                    found = true;

                }else if(sb.substring(i-1, i).equals(" ")  && sb.substring(i+holder.length(), i+holder.length()+1).equals(" ")){
                    found = true;
                }
                if(!found){
                    current += holder.length();
                }
            }
            sb.insert(i, '<');
            sb.insert(i+holder.length()+1, '>');
            String substr = sb.substring(i, i+holder.length()+1);
            sb.replace(i, i+holder.length()+1, substr.toUpperCase());
        }

        for (String question : questions) {
            if(question.equalsIgnoreCase("Question  " + sb.toString() + "?")){
                flag = true;
            }
        }
        if (!flag){
            for (String slot : placeHolders) {
                ArrayList<String> placeValues = new ArrayList<>(Arrays.asList(scanSkill.nextLine().split("\\s*,\\s*"))); 
    
                for (String vals : placeValues) {
                    Slot slotObject = new Slot(slot, vals); 
                    slotVals.add(slotObject);
                }
                values.add(placeValues); 
            }
    
            addQuestion(prototype, placeHolders);
            addSlot(values, placeHolders);
            boolean addedDefault = false;
            addingAction = true;
            while (addingAction) {
                ArrayList<String> actionVals = new ArrayList<>();
                String dataA = "";
                actionVals = new ArrayList<>();
                Scanner scan2 = new Scanner(System.in);
                if(!addedDefault){
                    if(scan2.nextLine().equalsIgnoreCase("1")){
                        dataA = scan2.nextLine();
                        addAction(new ArrayList<String>(), dataA, new ArrayList<Slot>());
                    }
                    addedDefault = true;
                }


                dataA = scan2.nextLine();
                if(dataA.equalsIgnoreCase("quit")){
                    addingAction = false;
                    continue;
                }
                int cnt = 1;
                for (int i = 0; i < placeHolders.size(); i++) {
                    cnt++;
                }
                ArrayList<String> actionNums =new ArrayList<>(Arrays.asList(scan2.nextLine().split("[^a-zA-Z0-9]+"))); 
                ArrayList<Integer> actionNumsOrdered = new ArrayList<>();
                for (String string : actionNums) {
                    actionNumsOrdered.add(Integer.parseInt(string));
                }
                Collections.sort(actionNumsOrdered);

                actionVals = new ArrayList<>(); 
                ArrayList<Slot> actionValss = new ArrayList<>(); 
                ArrayList<Slot> actionValsSend = new ArrayList<>(); 

                for (int i = 0; i < actionNumsOrdered.size(); i++) {
                    actionValss = new ArrayList<>(); 
                    int count = 1;
                    for (int j = 0; j < slotVals.size(); j++) {
                        if(slotVals.get(j).getSlot().equals(placeHolders.get(actionNumsOrdered.get(i)-1))){
                            actionValss.add(slotVals.get(j));
                            count++;
                        }
                    }
                    int holder = scan2.nextInt()-1;
                    actionVals.add(actionValss.get(holder).getParent());
                    actionValsSend.add(actionValss.get(holder));
                }
                addAction(actionVals, dataA, actionValsSend);
            }
            scanSkill.close();
        }
    }


    public void deleteSkill(String skill){
        try {
            BufferedReader readerDel = new BufferedReader(new FileReader(file));
            StringBuilder stringBuilder = new StringBuilder();
            String current;
            boolean delete = false;
            while((current = readerDel.readLine()) != null) {
                String trimmedLine = current.trim();
                if(delete && current.startsWith("Question")){
                    delete = false;
                }
                if(trimmedLine.equals(skill)){
                    delete = true;
                } 
                if (!delete){
                    stringBuilder.append(current);
                    stringBuilder.append(System.getProperty("line.separator"));
                }
            }
            readerDel.close();
            FileWriter writerDel = new FileWriter(file);
            writerDel.write(stringBuilder.toString());
            writerDel.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void addQuestion(String question, ArrayList<String> placeHolders) throws IOException{
        StringBuilder sb = new StringBuilder(question);
        for (String holder : placeHolders) {
            boolean found = false;
            int current = 0; 
            int i = 0;
            while(!found){
                i = sb.indexOf(holder, current);
                if(sb.indexOf(holder) == 0 && sb.substring(i+holder.length(), i+holder.length()+1).equals(" ")){
                    found = true;

                }else if (i == sb.length()-holder.length() && sb.substring(i-1, i).equals(" ")){
                    found = true;

                }else if(sb.substring(i-1, i).equals(" ")  && sb.substring(i+holder.length(), i+holder.length()+1).equals(" ")){
                    found = true;
                }
                if(!found){
                    current += holder.length();
                }
            }
            sb.insert(i, '<');
            sb.insert(i+holder.length()+1, '>');
            String substr = sb.substring(i, i+holder.length()+1);
            sb.replace(i, i+holder.length()+1, substr.toUpperCase());
        }
        br.write("\nQuestion  " + sb.toString() + "?");
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

    public String showskills() {
        String a = new String();
        String line = "";
        int counter = 1;
        ArrayList<String> questions = new ArrayList<>();
        BufferedReader reader;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")){
            file = new File(App.TEXTPATH);
        }
        else if (os.contains("os x")){
            file = new File(App.TEXTPATH);
        } 

        try {
            reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Question")) {
                    a = a + counter + ") " + line.substring(10)+"\n";
                    questions.add(line);
                    counter++;
                }
            }
            reader.close();
        } catch (Exception e) {}
        
        return a;
    }

    public ArrayList<String> getSkillQuestions() {
        String a = new String();
        String line = "";
        int counter = 1;
        ArrayList<String> questions = new ArrayList<>();
        BufferedReader reader;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")){
            file = new File(App.TEXTPATH);
        }
        else if (os.contains("os x")){
            file = new File(App.TEXTPATH);
        } 

        try {
            reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Question")) {
                    a = a + counter + ") " + line.substring(10)+"\n";
                    questions.add(line);
                    counter++;
                }
            }
            reader.close();
        } catch (Exception e) {}
        
        return questions;
    }

    public int getSkillAmount() {
        String line = "";
        int counter = 0;
        BufferedReader reader;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")){
            file = new File(App.TEXTPATH);
        }
        else if (os.contains("os x")){
            file = new File(App.TEXTPATH);
        } 

        try {
            reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Question")) {
                    counter++;
                }
            }
            reader.close();
        } catch (Exception e) {}
        
        return counter;
    }

    public void getSkillAndAction(){
        Scanner scanSkill = new Scanner(System.in);
        int act = scanSkill.nextInt();
        int skill = 0;
        String line = "";
        int counter = 1;
        ArrayList<String> questions = new ArrayList<>();
        if (act == 1) {
            try (BufferedReader reader1 = new BufferedReader(new FileReader(file))){

                addNewSkill();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            try(BufferedReader reader = new BufferedReader(new FileReader(file))){ 
                counter = 1;
                switch (act) {
                    case 2:
                        Scanner scan= new Scanner(System.in);
                        while ((line = reader.readLine()) != null) {
                            if (line.startsWith("Question")) {
                                questions.add(line);
                                counter++;
                            }
                        } 
                        skill = scan.nextInt();
                        if(skill >= questions.size()+1){
                            break;
                        }
                        deleteSkill(questions.get(skill-1));
                        break;
                    case 3:          
                        Scanner scan39 = new Scanner(System.in);
                        while ((line = reader.readLine()) != null) {
                            if (line.startsWith("Question")) {
                                questions.add(line);
                                counter++;
                            }
                        } 
                        skill = scan39.nextInt();
                        if(skill >= questions.size()+1){
                            break;
                        }
                        Scanner scan33 = new Scanner(System.in);
                        int action = scan33.nextInt();

                        switch (action) {
                            case 1:
                                boolean duplicateS = true;
                                boolean quitted = false;
                                String data = "";
                                int slotT = 0;
                                ArrayList<String> slotTemp = new ArrayList<>();
                                while(duplicateS){
                                    Scanner scan1 = new Scanner(System.in);
                                    data = scan1.nextLine();
                                    // print available slots
                                    slotTemp = showSlots(questions.get(skill-1));
                                    slotT = scan1.nextInt();
                                    duplicateS = duplicateSlot(questions.get(skill-1), data, slotTemp.get(slotT-1));
                                    if(duplicateS){
                                        Scanner scan7 = new Scanner(System.in);
                                        String data2 = scan7.nextLine();
                                        if(data2.equalsIgnoreCase("quit")) {
                                            quitted = true;
                                            break;
                                        }
                                    }
                                }
                                if(!quitted){
                                    addSlot(questions.get(skill-1), data, slotTemp.get(slotT-1));
                                }
                                break;
                            case 2:
                                boolean duplicateAction = true;
                                boolean quittedAction = false;
                                ArrayList<String> actionVals = new ArrayList<>();
                                ArrayList<String> actionValues = new ArrayList<>();
                                String dataA = "";
                                while(duplicateAction){
                                    actionVals = new ArrayList<>();
                                    actionValues = new ArrayList<>();
                                    Scanner scan2 = new Scanner(System.in);
                                    dataA = scan2.nextLine();
                                    ArrayList<String> actionTemp = showSlots(questions.get(skill-1));
                                    ArrayList<String> actionNums =new ArrayList<>(Arrays.asList(scan2.nextLine().split("[^a-zA-Z0-9]+"))); 
                                    int cnt = 0;
                                    // order action nums
                                    ArrayList<Integer> actionNumsOrdered = new ArrayList<>();
                                    for (String string : actionNums) {
                                        actionNumsOrdered.add(Integer.parseInt(string));
                                    }
                                    Collections.sort(actionNumsOrdered);
                                    for (String string : actionNums) {
                                        actionValues.add(actionTemp.get(actionNumsOrdered.get(cnt)-1));
                                        cnt++;
                                    }
                                    actionVals = new ArrayList<>(); 
                                    for (int i = 0; i < actionValues.size(); i++) {
                                        ArrayList<String> slots = printSlotsSpec(questions.get(skill-1), actionValues.get(i));
                                        actionVals.add(slots.get(scan2.nextInt()-1));
                                    }
                                    duplicateAction = duplicateAction(questions.get(skill-1), dataA, actionValues, actionVals);
                                    if(duplicateAction){
                                    }
                                }
                                if(!quittedAction){
                                    addAction(questions.get(skill-1), dataA, actionValues, actionVals);    
                                }
                                break;

                            case 3:
                                Scanner scan3 = new Scanner(System.in);
                                ArrayList<String> slots = printSlots(questions.get(skill-1));
                                int slot = scan3.nextInt();
                                deleteSlot(questions.get(skill-1), slots.get(slot-1));                           
                                break;
                            case 4:
                                Scanner scan4 = new Scanner(System.in);
                                ArrayList<String> actions = printActions(questions.get(skill-1));
                                int actionH = scan4.nextInt();
                                deleteAction(questions.get(skill-1), actions.get(actionH-1));                           
                                break;
                            default:
                        }
                        break;
                    case 4:
                        Scanner scan34 = new Scanner(System.in);
                        while ((line = reader.readLine()) != null) {
                            if (line.startsWith("Question")) {
                                questions.add(line);
                                counter++;
                            }
                        } 
                        skill = scan34.nextInt();
                        printSlots(questions.get(skill-1));
                        printActions(questions.get(skill-1));
                        break;    
                    default:
            }
            reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }    
    }

    public ArrayList<String> showSlots(String skill){
        ArrayList<String> slots = new ArrayList<>();
        Pattern pattern = Pattern.compile("<(.*?)>");
        Matcher matcher = pattern.matcher(skill);
        while (matcher.find()) {
            slots.add(matcher.group(1));
        }
        return slots;
    }

    public ArrayList<String> printSlotsSpec(String skill, String slott) {
        ArrayList<String> slots = new ArrayList<>();
        try {
            BufferedReader readerDel = new BufferedReader(new FileReader(file));
            String current;
            boolean delete = false;
            int counter = 1;
            while((current = readerDel.readLine()) != null) {
                String trimmedLine = current.trim();
                if(delete && current.startsWith("Action")){
                    delete = false;
                }
                if(trimmedLine.equals(skill)){
                    delete = true;
                } 
                if (delete && current.startsWith("Slot  <" + slott + ">  ")){
                    int endIndex = current.indexOf(">"); 
                    slots.add(current.substring(endIndex+3));
                    counter++;
                }
            }
            readerDel.close();
        } catch(IOException e) {
        }
        return slots;
    }

    public String printSlotsSpecasString(String skill, String slott) {
        String a = new String();
        ArrayList<String> slots = new ArrayList<>();
        try {
            BufferedReader readerDel = new BufferedReader(new FileReader(file));
            String current;
            boolean delete = false;
            int counter = 1;
            while((current = readerDel.readLine()) != null) {
                String trimmedLine = current.trim();
                if(delete && current.startsWith("Action")){
                    delete = false;
                }
                if(trimmedLine.equals(skill)){
                    delete = true;
                } 
                if (delete && current.startsWith("Slot  <" + slott + ">  ")){
                    int endIndex = current.indexOf(">"); 
                    a = a+counter+ ") " + slott + " - " + current.substring(endIndex+3)+"\n";
                    slots.add(current.substring(endIndex+3));
                    counter++;
                }
            }
            readerDel.close();
        } catch(IOException e) {
            a = "An error occurred: " + e.getMessage();
        }
        return a;
    }

    public boolean duplicateSlot(String slot, String skill, String slotHolder){
        boolean duplicate = false;
        String slotTest = "Slot  <"+slotHolder+">  " + skill; 
        try {
            BufferedReader readerDel = new BufferedReader(new FileReader(file));
            String current;
            boolean delete = false;
            while((current = readerDel.readLine()) != null) {
                String trimmedLine = current.trim();
                if(delete && current.startsWith("Question")){
                    delete = false;
                }
                if(trimmedLine.equals(slot)){
                    delete = true;
                } 
                if (delete && trimmedLine.equalsIgnoreCase(slotTest)){
                    duplicate = true;
                    continue;
                }
            }
            readerDel.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return duplicate;
    }

    

    public boolean duplicateAction(String skill, String data, ArrayList<String> slotLoc, ArrayList<String> slotVal){
        boolean duplicate = false;
        String tempAction = "Action  ";
        for (int i = 0; i < slotLoc.size(); i++) {
            tempAction += "<"+slotLoc.get(i)+">  "+slotVal.get(i)+"  ";
        }
        tempAction = tempAction.toUpperCase();
        try {
            BufferedReader readerDel = new BufferedReader(new FileReader(file));
            String current;
            boolean delete = false;
            while((current = readerDel.readLine()) != null) {
                String trimmedLine = current.trim();
                trimmedLine = trimmedLine.toUpperCase();
                if(delete && current.startsWith("Question")){
                    delete = false;
                }
                if(trimmedLine.equalsIgnoreCase(skill)){
                    delete = true;
                } 
                if (delete && trimmedLine.startsWith(tempAction)){
                    duplicate = true;
                    continue;
                }
            }
            readerDel.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return duplicate;
    }

    public void addAction(String skill, String data, ArrayList<String> slotLoc, ArrayList<String> slotVal){
       
        try {
            FileInputStream fs = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fs));
            ArrayList<String> lines = new ArrayList<>();
            String line;
            boolean relevant = false;
            int index = 0;
            int counter = 1;
            while ((line = br.readLine()) != null) {
                if(line.equals(skill)){
                    relevant = true;
                }
                if(relevant && line.startsWith("Action")){
                 index = counter-1; 
                 relevant = false;  
                } 
                counter++;
                lines.add(line);
            }
            br.close();
            String tempLine = "Action  ";
            for (int i = 0; i < slotLoc.size(); i++) {
                tempLine += "<"+slotLoc.get(i).toUpperCase()+">  "+slotVal.get(i)+"  ";
            }
            lines.add(index,tempLine + data );  
            FileOutputStream fos = new FileOutputStream(file);
            BufferedWriter bnew = new BufferedWriter(new OutputStreamWriter(fos));
            for (String str : lines) {
               bnew.write(str);
               bnew.newLine();
            }
            bnew.close();
            
         } catch (IOException e) {
            e.printStackTrace();
         }
    }
    
    public void addDefaultAction(String skill, String data){
       
        try {
            FileInputStream fs = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fs));
            ArrayList<String> lines = new ArrayList<>();
            String line;
            boolean relevant = false;
            int index = 0;
            int counter = 1;
            while ((line = br.readLine()) != null) {
                if(line.equals(skill)){
                    relevant = true;
                }
                if(relevant && line.startsWith("Action")){
                 index = counter-1; 
                 relevant = false;  
                } 
                counter++;
                lines.add(line);
            }
            br.close();
            String tempLine = "Action  ";
            lines.add(index,tempLine + data );  
            FileOutputStream fos = new FileOutputStream(file);
            BufferedWriter bnew = new BufferedWriter(new OutputStreamWriter(fos));
            for (String str : lines) {
               bnew.write(str);
               bnew.newLine();
            }
            bnew.close();
            
         } catch (IOException e) {
            e.printStackTrace();
         }
    }


    public void addActionNewSkill(String skill, String data, ArrayList<String> slotLoc, ArrayList<String> slotVal){
       
        try {
            
            String current = "";
            BufferedReader readerDel1 = new BufferedReader(new FileReader(file));
            StringBuilder stringBuilder1 = new StringBuilder();
            while((current = readerDel1.readLine()) != null) {
                if(current.length() != 0){
                    stringBuilder1.append(current);
                    stringBuilder1.append(System.getProperty("line.separator"));
                }
            }
            readerDel1.close();
            FileWriter writerDel1 = new FileWriter(file);
            String tempLine = "Action  ";
            for (int i = 0; i < slotLoc.size(); i++) {
                tempLine += "<"+slotLoc.get(i).toUpperCase()+">  "+slotVal.get(i)+"  ";
            }
            tempLine += data;
            stringBuilder1.append(tempLine);
            stringBuilder1.append(System.getProperty("line.separator"));
            writerDel1.write(stringBuilder1.toString());
            writerDel1.close();
            
         } catch (IOException e) {
            e.printStackTrace();
         }
    }

    public ArrayList<String> printActions(String skill){
        ArrayList<String> slots = new ArrayList<>();
        try {
            BufferedReader readerDel = new BufferedReader(new FileReader(file));
            String current;
            boolean delete = false;
            int counter = 1;
            while((current = readerDel.readLine()) != null) {
                String trimmedLine = current.trim();
                if(delete && current.startsWith("Question")){
                    delete = false;
                }
                if(trimmedLine.equals(skill)){
                    delete = true;
                } 
                if (delete && current.startsWith("Action")){
                    slots.add(current);
                    counter++;
                }
            }
            readerDel.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return slots;
    }

    public ArrayList<String> printSlots(String skill){
        ArrayList<String> slots = new ArrayList<>();
        try {
            BufferedReader readerDel = new BufferedReader(new FileReader(file));
            String current;
            boolean delete = false;
            int counter = 1;
            while((current = readerDel.readLine()) != null) {
                String trimmedLine = current.trim();
                if(delete && current.startsWith("Action")){
                    delete = false;
                }
                if(trimmedLine.equals(skill)){
                    delete = true;
                } 
                if (delete && current.startsWith("Slot")){

                    int startIndex = current.indexOf("<") + 1; 
                    int endIndex = current.indexOf(">"); 
                    slots.add(current);
                    counter++;
                }
            }
            readerDel.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return slots;
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
        writer.write("Slot  <TIME>  9\n"); 
        writer.write("Slot  <TIME>  11\n"); 
        writer.write("Slot  <TIME>  13\n"); 
        writer.write("Slot  <TIME>  15\n"); 
        writer.write("Action  <DAY>  Saturday  There are no lectures on Saturday\n"); 
        writer.write("Action  <DAY>  Monday  <TIME>  9  We start the week with math\n"); 
        writer.write("Action  <DAY>  Monday  <TIME>  11  On Monday noon we have Theoratical Computer Science\n"); 
        writer.write("Action  I have no idea"); 
    }

    public String getActions(String skill){
        String a = new String();
        ArrayList<String> slots = new ArrayList<>();
        try {
            BufferedReader readerDel = new BufferedReader(new FileReader(file));
            String current;
            boolean delete = false;
            int counter = 1;
            while((current = readerDel.readLine()) != null) {
                String trimmedLine = current.trim();
                if(delete && current.startsWith("Question")){
                    delete = false;
                }
                if(trimmedLine.equals(skill)){
                    delete = true;
                } 
                if (delete && current.startsWith("Action")){
                    a = a + counter+ ") " + current.substring(8)+"\n";
                    slots.add(current);
                    counter++;
                }
            }
            readerDel.close();
        } catch(IOException e) {
            e.printStackTrace();
        }

        return a;
    }

    public String getSlots(String skill){
        String a = new String();
        ArrayList<String> slots = new ArrayList<>();
        try {
            BufferedReader readerDel = new BufferedReader(new FileReader(file));
            String current;
            boolean delete = false;
            int counter = 1;
            while((current = readerDel.readLine()) != null) {
                String trimmedLine = current.trim();
                if(delete && current.startsWith("Action")){
                    delete = false;
                }
                if(trimmedLine.equals(skill)){
                    delete = true;
                } 
                if (delete && current.startsWith("Slot")){

                    int startIndex = current.indexOf("<") + 1; 
                    int endIndex = current.indexOf(">"); 
                    a = a + counter+ ") " + current.substring(startIndex, endIndex) + " - " + current.substring(11) + "\n";
                    slots.add(current);
                    counter++;
                }
            }
            readerDel.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return a;
    }
}