package com.mda.EngineG;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;


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
            file = new File("core\\src\\main\\java\\com\\mda\\EngineG\\skills.txt");
        }
        else if (os.contains("os x")){
            file = new File("core/src/main/java/com/mda/EngineG/skills.txt");
        }   
        writer = new FileWriter(file, true); 
        br = new BufferedWriter(writer);
        //addDefaultSkills();
        getSkillAndAction();
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
            System.out.println("Line added successfully.");
            
         } catch (IOException e) {
            System.out.println("Error while adding a line to the file: " + e.getMessage());
         }
    }

    public void deleteSlot(String skill, String slot){
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
        } catch(IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    public void deleteAction(String skill, String action){
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
        } catch(IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    public void addNewSkill() throws IOException{
        // check if exists 
        Scanner scanSkill = new Scanner(System.in);
        System.out.println("Please type the prototype sentence: ");
        prototype = scanSkill.nextLine();
        System.out.println("Please type the slots you wish to set as placeholders: (separated by a coma) ");

        ArrayList<String> placeHolders = new ArrayList<>(Arrays.asList(scanSkill.nextLine().split("[^a-zA-Z0-9]+"))); 
        ArrayList<ArrayList<String>> values = new ArrayList<ArrayList<String>> (); 
        ArrayList<slot> slotVals = new ArrayList<>(); 
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
            int i = sb.indexOf(holder);
            sb.insert(i, '<');
            sb.insert(i+holder.length()+1, '>');
            String substr = sb.substring(i, i+holder.length()+1);
            sb.replace(i, i+holder.length()+1, substr.toUpperCase());
        }

        for (String question : questions) {
            if(question.substring(10).equalsIgnoreCase(sb.toString())){
                flag = true;
            }
        }
        if (!flag){
            for (String slot : placeHolders) {
                System.out.println("Please type the values for place holder <" + slot.toUpperCase() + ">. (separated by a coma)");
                ArrayList<String> placeValues = new ArrayList<>(Arrays.asList(scanSkill.nextLine().split("[^a-zA-Z0-9]+"))); 
    
                for (String vals : placeValues) {
                    slot slotObject = new slot(slot, vals); 
                    slotVals.add(slotObject);
                }
                values.add(placeValues); 
            }
    
            addQuestion(prototype, placeHolders);
            addSlot(values, placeHolders);
    
            addingAction = true;
            while (addingAction) {
                System.out.println("Choose the holder values you would like to add actions for: (separated by a coma / To quit type 'quit')");
                ArrayList<String> actionValues = new ArrayList<>(Arrays.asList(scanSkill.nextLine().split("[^a-zA-Z0-9]+"))); 
    
                if(actionValues.get(0).equalsIgnoreCase("quit")){
                    addingAction = false; 
                } else{
                    System.out.println("What action would you like to add for the selected values?");
                    String action = scanSkill.nextLine(); 
                    addAction(actionValues, action, slotVals);
                }   
            }
            scanSkill.close();
        }
        else System.out.println("Question already exists. Try editing or deleting it to make changes.");
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
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    public void addQuestion(String question, ArrayList<String> placeHolders) throws IOException{
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

    public void addAction(ArrayList<String> actionValues, String action, ArrayList<slot> slotVals) throws IOException{
        String finalAction = ""; 
        StringBuilder sb = new StringBuilder(finalAction);
        sb.append("\nAction"); 
        for(int i = 0; i < actionValues.size(); i++){                       
            for (slot sl : slotVals) {
                if(sl.getParent().equals(actionValues.get(i).toString())){
                    sb.append("  <" + sl.getSlot().toUpperCase() + ">  "); 
                }
            }
            sb.append(actionValues.get(i).toString());                      
        }
        sb.append("  " + action);
        br.write(sb.toString());
    }

    public void getSkillAndAction(){
        Scanner scanSkill = new Scanner(System.in);
        System.out.println();
        System.out.println("Welcome to the Skill Editor!");
        System.out.println("Do you wish to 1) add, 2) delete, 3) edit, 4) view a skill?");
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
            System.out.println("--");
            try(BufferedReader reader = new BufferedReader(new FileReader(file))){ 
                counter = 1;
                switch (act) {
                    case 2:
                        Scanner scan= new Scanner(System.in);
                        System.out.println("Which skill would you like to delete? ");
                        while ((line = reader.readLine()) != null) {
                            if (line.startsWith("Question")) {
                                System.out.println(counter + ") " + line.substring(10));
                                questions.add(line);
                                counter++;
                            }
                        } 
                        skill = scan.nextInt();
                        deleteSkill(questions.get(skill-1));
                        break;
                    case 3:          
                        Scanner scan33 = new Scanner(System.in);
                        System.out.println("Which skill would you like to edit? ");
                        while ((line = reader.readLine()) != null) {
                            if (line.startsWith("Question")) {
                                System.out.println(counter + ") " + line.substring(10));
                                questions.add(line);
                                counter++;
                            }
                        } 
                        skill = scan33.nextInt();
                        System.out.println("--");
                        System.out.println("What action would you like to take? ");
                        System.out.println("1) Add a slot.");
                        System.out.println("2) Add an action.");
                        System.out.println("3) Delete a slot.");
                        System.out.println("4) Delete an action.");
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
                                    System.out.println("--");
                                    System.out.println("What do you want to add as a slot?");
                                    data = scan1.nextLine();
                                    System.out.println("And to which slot would you like to add '" + data + "' to? Choose one from the options below:");
                                    // print available slots
                                    slotTemp = showSlots(questions.get(skill-1));
                                    slotT = scan1.nextInt();
                                    duplicateS = duplicateSlot(questions.get(skill-1), data, slotTemp.get(slotT-1));
                                    if(duplicateS){
                                        System.out.println("That slot already exists, do you wish to 'add' another slot or 'quit'?");
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
                                    Scanner scan2 = new Scanner(System.in);
                                    System.out.println("--");
                                    System.out.println("What do you want to add as an action?");
                                    dataA = scan2.nextLine();
                                    System.out.println("To which slot(s) does this data belong to? You can find the slots below: ");
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
                                        System.out.println("What is the value of slot " + actionValues.get(i) +"? ");
                                        actionVals.add(scan2.nextLine());
                                    }
                                    duplicateAction = duplicateAction(questions.get(skill-1), dataA, actionValues, actionVals);
                                    if(duplicateAction){
                                        System.out.println("That action already exists, do you wish to 'add' another action, 'overwrite' the current one or 'quit'?");
                                        Scanner scan7 = new Scanner(System.in);
                                        String data2 = scan7.nextLine();
                                        if(data2.equalsIgnoreCase("quit")) {
                                            quittedAction = true;
                                            break;
                                        } else if(data2.equalsIgnoreCase("modify")){

                                        }
                                    }
                                }
                                if(!quittedAction){
                                    addAction(questions.get(skill-1), dataA, actionValues, actionVals);    
                                }
                                break;

                            case 3:
                                Scanner scan3 = new Scanner(System.in);
                                System.out.println("--");
                                System.out.println("Which slot would you like to delete? Please choose one of the following:");
                                System.out.println();
                                ArrayList<String> slots = printSlots(questions.get(skill-1));
                                int slot = scan3.nextInt();
                                deleteSlot(questions.get(skill-1), slots.get(slot-1));                           
                                break;
                            case 4:
                                Scanner scan4 = new Scanner(System.in);
                                System.out.println("Which action would you like to delete? Please choose one of the following:");
                                System.out.println();
                                ArrayList<String> actions = printActions(questions.get(skill-1));
                                int actionH = scan4.nextInt();
                                deleteAction(questions.get(skill-1), actions.get(actionH-1));                           
                                break;
                        }
                        break;
                    case 4:
                        Scanner scan34 = new Scanner(System.in);
                        System.out.println("Which skill would you like to view? ");
                        while ((line = reader.readLine()) != null) {
                            if (line.startsWith("Question")) {
                                System.out.println(counter + ") " + line.substring(10));
                                questions.add(line);
                                counter++;
                            }
                        } 
                        skill = scan34.nextInt();
                        System.out.println("Here is your skill: ");
                        System.out.println(questions.get(skill-1));
                        printSlots(questions.get(skill-1));
                        printActions(questions.get(skill-1));
                        break;    
                    default:
                        System.out.println("Invalid choice. Restart.");
            }
            reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }    
    }

    public ArrayList<String> showSlots(String skill){
        ArrayList<String> slots = new ArrayList<>();
        boolean checkIfSeen = false;
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
                    for (String sl : slots) {
                        if(sl.equalsIgnoreCase(current.substring(startIndex, endIndex))){
                            checkIfSeen = true;
                        }
                    }
                    if(!checkIfSeen){
                        System.out.println(counter+ ") " + current.substring(startIndex, endIndex));
                        slots.add(current.substring(startIndex, endIndex));
                        counter++;
                    }
                    checkIfSeen = false;
                }
            }
            readerDel.close();
        } catch(IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
        return slots;
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
            System.out.println("An error occurred: " + e.getMessage());
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
        System.out.println(tempAction);
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
            System.out.println("An error occurred: " + e.getMessage());
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
            System.out.println("Line added successfully.");
            
         } catch (IOException e) {
            System.out.println("Error while adding a line to the file: " + e.getMessage());
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
                    System.out.println(counter+ ") " + current.substring(8));
                    slots.add(current);
                    counter++;
                }
            }
            readerDel.close();
        } catch(IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
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
                    System.out.println(counter+ ") " + current.substring(startIndex, endIndex) + " - " + current.substring(13));
                    slots.add(current);
                    counter++;
                }
            }
            readerDel.close();
        } catch(IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
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
}