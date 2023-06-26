package group6.Engine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class cfgEditor {

    File file; 
    String fileName;
    String output = "";

    public static void main(String[] args) throws FileNotFoundException {
        cfgEditor test = new cfgEditor();
        String[][] slots = {{"a", "nyc", "new york city", "la", "one", "one hand"}, 
                            {"b", "nyc", "new york city", "la", "one", "one hand"}};

        test.inputSentence("a to b", "movse", slots);
        String[][] slotsInput = {{"a", "new york city"}, {"b", "la"}};
        test.addAction("movse", "yay", slotsInput); 

    }
   
    public cfgEditor(){
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")){
            file = new File("src\\main\\java\\group6\\Engine\\cfgSkills.txt");
            fileName = ("src\\main\\java\\group6\\Engine\\cfgSkills.txt");
        } else if (os.contains("os x")){
            file = new File("src/cfgSkills.txt");
            fileName = ("src/cfgSkills.txt");
        }     
    }

    public ArrayList<String> showSlots(String skill){
        String[] parts = null;
        try {
            BufferedReader readerDel = new BufferedReader(new FileReader(file));
            String current;
            int counter = 1;
            boolean print = false;
            while((current = readerDel.readLine()) != null) {
                if (current.startsWith("-------------------------------- Printing --------------------------------")) {
                    print = true;
                }
                if(print && current.startsWith(skill)) {
                    int parIndex = current.indexOf(">");
                    parts = (current.substring(parIndex+2).trim().split("/"));
                }   
                
            }
            readerDel.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        ArrayList<String> a = new ArrayList<>(Arrays.asList(parts));
        return a;
    } 

    public void deleteAction(String actions, int choice){
        String[] parts = actions.split("\n");

        try {
            FileInputStream fs = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fs));
            ArrayList<String> lines = new ArrayList<>();
            String current;
            boolean print = true;
            while((current = br.readLine()) != null) {
                boolean copy = true;
                int dashIndex = current.indexOf("*");
                if(dashIndex != -1){
                    if(parts[choice-1].substring(9).trim().equalsIgnoreCase(current.trim().substring(dashIndex+2, current.length()))){
                        copy = false;
                    } 
                }
                if(copy) lines.add(current);
                
            }
            br.close();
            FileOutputStream fos = new FileOutputStream(file);
            BufferedWriter bnew = new BufferedWriter(new OutputStreamWriter(fos));
            for (String str : lines) {
               bnew.write(str);
               bnew.newLine();
            }
            bnew.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void arrangePrint(String input, String skillName, String[][] slots){
        try {
            FileInputStream fs = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fs));
            ArrayList<String> lines = new ArrayList<>();
            String line;
            boolean print = true;
            while ((line = br.readLine()) != null) {
                if(line.startsWith("-------------------------------- Printing --------------------------------")){
                    if(print){
                        lines.add(line);
                        String append = skillName + " - " + input + " > ";
                        for (int i = 0; i < slots.length; i++) {
                            append += slots[i][0] + " / ";
                        }
                        lines.add(append);
                        print = false;
                    }
                } else lines.add(line);
                
            }
            br.close();
            FileOutputStream fos = new FileOutputStream(file);
            BufferedWriter bnew = new BufferedWriter(new OutputStreamWriter(fos));
            for (String str : lines) {
               bnew.write(str);
               bnew.newLine();
            }
            bnew.close();
        } catch (IOException e) {}
    }

    public ArrayList<String> getSkillQuestions() {
        String a = new String();
        String line = "";
        int counter = 1;
        ArrayList<String> questions = new ArrayList<>();
        BufferedReader reader;
        boolean print = false;
        try {
            reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {

                if (line.startsWith("-------------------------------- Printing --------------------------------")) {
                    print = true;
                }
                if(print && !line.startsWith("-------------------------------- Printing")){
                    int dashIndex = line.indexOf("-");
                    String output = line.substring(0, dashIndex - 1).trim();
                    a = counter + ") " + output +"\n";
                    questions.add(a);
                    counter++;
                }
            }
            reader.close();
        } catch (Exception e) {}
        
        return questions;
    }

    public String getSlots(String skill){
        String a = new String(); 
        try {
            BufferedReader readerDel = new BufferedReader(new FileReader(file));
            String current;
            int counter = 1;
            while((current = readerDel.readLine()) != null) {
                if(current.startsWith(skill)){
                    int startIndex = current.indexOf('<');
                    if(startIndex != -1){
                        a = a + "Slot: " +  current.substring(startIndex, current.length()) + "\n";
                        counter++;
                    }else {
                        a = a + "Slot: " +  current + "\n";
                        counter++;
                    }
                   
                }
            }
            readerDel.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return a;
    }

    public String getSlotsScanner(String skill){
        String a = new String(); 
        try {
            BufferedReader readerDel = new BufferedReader(new FileReader(file));
            String current;
            int counter = 1;
            while((current = readerDel.readLine()) != null) {
                if(current.startsWith(skill+ " - ")){
                    int startIndex = current.indexOf('<');
                    a = current;
                }
            }
            readerDel.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return a;
    }

    public String getActions(String skill){
        String a = "";
        ArrayList<String> slots = new ArrayList<>();
        try {
            BufferedReader readerDel = new BufferedReader(new FileReader(file));
            String current;
            boolean delete = false;
            int counter = 1;
            while((current = readerDel.readLine()) != null) {
                if(current.startsWith("Action <"+skill+">")){
                    a = a + "Action: " +  current.substring(11+skill.length(), current.length()) + "\n";
                    counter++;
                }
            }
            readerDel.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return a;
    }


    public void inputSentence(String input, String skillName, String[][] slots){
        arrangePrint(input, skillName, slots);
        String[] initial = input.split("[^\\p{L}0-9']+");
        int cnt = 1;
        addSkill("Rule <action>", "<" + skillName + ">", slots, initial.length, false);
        for (String string : initial) {
            boolean isSlot = false;
            int slot = 0;
            for (int i = 0; i < slots.length; i++) {
                if(slots[i][0].equals(string)){
                    isSlot = true;
                    slot = i;
                }
            }
            if(isSlot){
                if(cnt == 1){
                    addSkill("Rule <"+ skillName +">", " <" + slots[slot][0] +"1> <"+skillName+"2> | " + "<" + slots[slot][0] +"> <"+skillName+"2> | ", slots, initial.length, true);
                    addSkill("Rule <" + cnt + ">", initial[cnt-1], slots, initial.length, false);
                } else if(cnt == initial.length-1){
                    boolean isSlotDetail = false;
                    int slotDetail = 0;
                    for (int i = 0; i < slots.length; i++) {
                        if(slots[i][0].equals(initial[initial.length-1])){
                            isSlotDetail = true;
                            slotDetail = i;
                        }
    
                    }
                    if(isSlotDetail){
                        addSkill("Rule <"+skillName + cnt +">", " <" + slots[slot][0] + "1>" +" <" +slots[slotDetail][0]+"> |" + " <" + slots[slot][0] + ">" +" <" +slots[slotDetail][0]+">", slots, initial.length, true);
                        addSkill("Rule <" + cnt + ">", initial[cnt-1], slots, initial.length, false);
                    } else{
                        addSkill("Rule <"+skillName + cnt +">", " <" + slots[slot][0] + ">" +" <" +(cnt+1)+"> |" + " <" + slots[slot][0] + "1>" +" <" +(cnt+1)+">", slots, initial.length, true);
                        addSkill("Rule <" + cnt + ">", initial[cnt-1], slots, initial.length, false);
                    }
                    
                } else if(cnt == initial.length){
                    addSkill("Rule <" + cnt + ">", initial[cnt-1], slots, initial.length, false);
                } else{
                    addSkill("Rule <"+skillName + cnt +">", "<" + slots[slot][0] +"> <"+skillName +(cnt+1)+">" + " | <" + slots[slot][0] +"1> <"+skillName +(cnt+1)+">", slots, initial.length, true);
                    addSkill("Rule <" + cnt + ">", initial[cnt-1], slots, initial.length, false);
                }
            } else{
                if(cnt == 1){
                    addSkill("Rule <"+ skillName +">", " <1> <"+skillName+"2> ", slots, initial.length, true);
                    addSkill("Rule <" + cnt + ">", initial[cnt-1], slots, initial.length, false);
                } else if(cnt == initial.length-1){
                    boolean isSlotDetail = false;
                    int slotDetail = 0;
                    for (int i = 0; i < slots.length; i++) {
                        if(slots[i][0].equals(initial[initial.length-1])){
                            isSlotDetail = true;
                            slotDetail = i;
                        }
                    }
                    if(isSlotDetail){
                        addSkill("Rule <"+skillName + cnt +">", "<"+(cnt) + "> <" +slots[slotDetail][0]+">" + " | <"+(cnt) + "> <" +slots[slotDetail][0]+"1>", slots, initial.length, true);
                        addSkill("Rule <" + cnt + ">", initial[cnt-1], slots, initial.length, false);
                    } else{
                        addSkill("Rule <"+skillName + cnt +">", "<"+(cnt) + "> <" +(cnt+1)+">", slots, initial.length, true);
                        addSkill("Rule <" + cnt + ">", initial[cnt-1], slots, initial.length, false);
                    }
                    
                } else if(cnt == initial.length){
                    addSkill("Rule <" + cnt + ">", initial[cnt-1], slots, initial.length, false);
                } else{
                    addSkill("Rule <"+skillName + cnt +">", "<"+(cnt) + "> <"+skillName +(cnt+1)+">", slots, initial.length, true);
                    addSkill("Rule <" + cnt + ">", initial[cnt-1], slots, initial.length, false);
                }
            }
            cnt++;
        }
        // Add slots. 
        addSlot(skillName, slots, initial.length-slots.length, skillName);
    } 

    public boolean duplicate(String skill, String actionToadd, ArrayList<String> holders, ArrayList<String> guess){
        String action = "Action <" + skill + "> *";
        ArrayList<String> halList = new ArrayList<>(guess.subList(guess.size()/2, guess.size()));
        for (int index = 0; index < holders.size(); index++) {
            action += " <" + holders.get(index) + "> " + halList.get(index);
        }
        String line = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            boolean actions = false;
            while ((line = reader.readLine()) != null) {

                if (actions){
                    int parIndex = line.indexOf("-");
                    if (line.substring(0,parIndex-1).equals(action)) {
                        return true;
                    }
                }
                if(line.startsWith("-------------------------------- Actions")) {
                    actions = true;
                }
            }
            reader.close();
        } catch (Exception e) {}
        return false;
    }   

    public void addAction(String skill, String actionToadd, ArrayList<String> holders, ArrayList<String> guess){
        String action = "Action <" + skill + "> * ";
        ArrayList<String> halList = new ArrayList<>(guess.subList(guess.size()/2, guess.size()));
        for (int index = 0; index < holders.size(); index++) {
            action += "<" + holders.get(index) + "> " + halList.get(index) + " ";
        }
        action += "- " + actionToadd;
        
        try(FileInputStream fs = new FileInputStream(file)) {
            BufferedReader br = new BufferedReader(new InputStreamReader(fs));
            ArrayList<String> lines = new ArrayList<>();
            String line = "";
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("-------------------------------- Actions")) {
                    lines.add(line);
                    lines.add(action);
                } else lines.add(line);
                
            }
            reader.close();
            br.close();
            FileOutputStream fos = new FileOutputStream(file);
            BufferedWriter bnew = new BufferedWriter(new OutputStreamWriter(fos));
            for (String str : lines) {
               bnew.write(str);
               bnew.newLine();
            }
            bnew.close();
        } catch (Exception e) {}
    }   

    public ArrayList<String> specificSlot(String placeholder){
        ArrayList<String> slots = new ArrayList<>(); 
        String line = "";
        try {
            BufferedReader reader;
            reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                if (line.contains("<"+ placeholder +"Print>")) {
                    int parIndex = line.indexOf(">");
                    slots.add(line.substring(parIndex+2)); 
                }
            }
            reader.close();
        } catch (Exception e) {}
        return slots;
    }  

    public void addSlot(String rule, String[][] slots, int counter, String skill){
        try {
            int cntTemp = 0;
            FileInputStream fs = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fs));
            ArrayList<String> lines = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                if(line.startsWith("Rule <verb>")){
                    for (int i = 0; i < slots.length; i++) {
                        String slot = "Rule <" + slots[i][0] + "> <"+slots[i][0]+"1> <"+slots[i][0]+"1> | <"+slots[i][0]+"1> <"+slots[i][0]+"> ";
                        lines.add(slot);
                    }                    
                }
                if(line.startsWith("Rule <1>")){
                    for (int i = 0; i < slots.length; i++) {
                        String slot = "Rule <" + slots[i][0] + "1> ";
                        String slotPrint = skill + "Rule <" + slots[i][0] + "Print>";
                        String slotRecursion = "Rule <" + slots[i][0] + "> <" + slots[i][0] + "1> <" + slots[i][0] + "1> | " + "<" + slots[i][0] + "1> <" + slots[i][0] + ">";
                        for (int j = 0; j < slots[0].length; j++) {
                            int cnt = 0;
                            if(j > 0) slotPrint += " " + slots[i][j] + " | ";
                            if(!slots[i][j].equals("") && j > 0){
                                String[] slotDivided = slots[i][j].split("[^\\p{L}0-9']+");
                                if(slotDivided.length > 1){
                                    for (String slotTemp : slotDivided) {
                                        slot += slotTemp + " | ";
                                        cnt++;
                                    }
                                } else slot += slots[i][j] + " | ";
                            } 
                        }
                        lines.add(slotRecursion);
                        lines.add(slotPrint);
                        lines.add(slot);
                    }                    
                }
                if(line.startsWith("-------------------------------- Actions --------------------------------") && cntTemp == 0){
                    lines.add("<"+rule+"> - "+counter);
                    cntTemp++;
                }
                lines.add(line);
            }
            br.close();
            FileOutputStream fos = new FileOutputStream(file);
            BufferedWriter bnew = new BufferedWriter(new OutputStreamWriter(fos));
            for (String str : lines) {
               bnew.write(str);
               bnew.newLine();
            }
            bnew.close();
        
        } catch (IOException e) {}
    }


    public void addSkill(String rule, String info, String[][] slots, int size, boolean newRule){
        ArrayList<String> slotsToAdd = new ArrayList<>();
        try {
            FileInputStream fs = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fs));
            ArrayList<String> lines = new ArrayList<>();
            String line;
            boolean found = false;
            int linecnt = 0;
            while ((line = br.readLine()) != null) {
                linecnt++;
                if(line.startsWith(rule) && !newRule){
                    line = line + " " + info + " |";
                    found = true;
                }
                if(newRule && line.equals("-------------------------------- Terminals --------------------------------")){
                    lines.add(rule+ " " + info);
                }
                
                lines.add(line);
            }
            br.close();
            FileOutputStream fos = new FileOutputStream(file);
            BufferedWriter bnew = new BufferedWriter(new OutputStreamWriter(fos));
            for (String str : lines) {
               bnew.write(str);
               bnew.newLine();
            }
            bnew.close();
        
        } catch (IOException e) {}
    }

    public int getSkillAmount() {
        String line = "";
        int skills = 0;
        try {
            BufferedReader reader;
            reader = new BufferedReader(new FileReader(file));
            boolean print = false;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("-------------------------------- Printing --------------------------------")) {
                    print = true;
                }
                if(print && !line.startsWith("-------------------------------- Printing")) skills++;
            }
            reader.close();
        } catch (Exception e) {}
        return skills;
    }

    public void addAction(String rule, String info, String[][] slots){
        try {
            FileInputStream fs = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fs));
            ArrayList<String> lines = new ArrayList<>();
            String line;

            String actionToAdd = "Action <" + rule +"> * ";
            for (int i = 0; i < slots.length; i++) {
                actionToAdd +="<" + slots[i][0]+"> "  + slots[i][1]+ " ";
            }
            actionToAdd += "- " + info;
            while ((line = br.readLine()) != null) {
                lines.add(line);
                if(line.equals("-------------------------------- Actions --------------------------------")){
                    lines.add(actionToAdd);
                }
            }
            br.close();
            FileOutputStream fos = new FileOutputStream(file);
            BufferedWriter bnew = new BufferedWriter(new OutputStreamWriter(fos));
            for (String str : lines) {
               bnew.write(str);
               bnew.newLine();
            }
            bnew.close();
        } catch (IOException e) {}
    }
    
    public String showskills() {
        String line = "";
        String a = "";
        List<String> skills = new ArrayList<>();
        int counter = 1;
        try {
            BufferedReader reader;
            reader = new BufferedReader(new FileReader(file));
            boolean print = false;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("-------------------------------- Printing --------------------------------")) {
                    print = true;
                }
                if(print && !line.startsWith("-------------------------------- Printing")) {
                    int parIndex = line.indexOf(">");
                    a += (counter + ") "+ line.substring(0, parIndex) + "\n");
                    counter++;
                }
            }
            reader.close();
        } catch (Exception e) {}
        return a;
    }

    public String[] getSlotsSpec(String skill){
        String line = "";
        String[] parts = null;
        try {
            BufferedReader reader;
            reader = new BufferedReader(new FileReader(file));
            boolean print = false;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("-------------------------------- Printing --------------------------------")) {
                    print = true;
                }
                if(print && line.startsWith(skill)) {
                    int parIndex = line.indexOf(">");
                    parts = (line.substring(parIndex+2).trim().split("/"));
                }   
            }
            reader.close();
        } catch (Exception e) {}
        return parts;

    } 

    public boolean skillExists(String skill){
        String line = "";
        boolean print = false;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Rule <action>")){
                    if(line.contains(skill)){
                        return true;
                    }
                }
            }
            reader.close();
        } catch (Exception e) {}
        return print;
    } 

    public boolean titleExists(String skill){
        String line = "";
        boolean print = false;
        try {
            BufferedReader reader;
            reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Rule <"+ skill+ ">")){
                    print = true;
                }
            }
            reader.close();
        } catch (Exception e) {}
        return print;
    } 

    public boolean slotsExists(ArrayList<String> slots){
        String line = "";
        boolean print = false;
        try {
            for (String slot : slots) {

                BufferedReader reader;
                reader = new BufferedReader(new FileReader(file));
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("Rule <"+ slot+ ">")){
                        reader.close();
                        return true;
                    }
                }
                reader.close();
            }
        } catch (Exception e) {}
        return print;
    } 

    public void deleteSkill(String skill){
        try {
            String[] slots = getSlotsSpec(skill);  
            String current = "";
            BufferedReader readerDel1 = new BufferedReader(new FileReader(file));
            StringBuilder stringBuilder1 = new StringBuilder();
            while((current = readerDel1.readLine()) != null) {
                if(current.startsWith(skill) || current.startsWith("<"+skill+">") || current.startsWith("Action <"+skill+">") || current.startsWith("Rule <"+skill)){
                   
                } else{
                    boolean slot = false; 
                    for (String string : slots) {
                        if(current.startsWith("Rule <" + string.trim() + ">") || current.startsWith("Rule <" + string.trim() + "1>")){
                            slot = true;
                        }  
                    }
                    if(!slot){
                        stringBuilder1.append(current);
                        stringBuilder1.append(System.getProperty("line.separator"));
                    }  
                }    
            }
            readerDel1.close();
            FileWriter writerDel1 = new FileWriter(file);
            writerDel1.write(stringBuilder1.toString());
            writerDel1.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
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
}