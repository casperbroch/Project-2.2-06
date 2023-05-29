package group6.Engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;

public class cfgScanner {

    File file; 
    String fileName;
    String output = "Sorry I don't have an answer for that.";

    
    public cfgScanner() throws FileNotFoundException{
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")){
            file = new File("src\\main\\java\\group6\\Engine\\cfgSkills.txt");
            fileName = ("src\\main\\java\\group6\\Engine\\cfgSkills.txt");
        } else if (os.contains("os x")){
            file = new File("src/cfgSkills.txt");
            fileName = ("src/cfgSkills.txt");
        }     
    }

    public void getAction(String action, String question) {
        int skillSize = 0;
        List<List<String>> FinalList = new ArrayList<>();
        List<String> responses = new ArrayList<>();
        String responseRecord = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))){
            String line = "";
            action = action.toLowerCase();
            while ((line = reader.readLine()) != null) {
                String keep = line;
                line = line.toLowerCase();
                if (line.startsWith("action " + action + " *")) {
                    int dashIndex = line.indexOf("-");
                    int starIndex = line.indexOf("*");
                    String input = keep.substring(starIndex+2, dashIndex).trim();
                    List<String> outputList = new ArrayList<>();
                    boolean bracketFlag = false;
                    StringBuilder current = new StringBuilder();
                    int cnt = 0;
                    for (int i = 0; i < input.length(); i++) {
                        char currentChar = input.charAt(i);
                        if (currentChar == '<') {
                            if (cnt > 0){
                                outputList.add(current.toString().trim());
                                current.setLength(0);
                            }
                            bracketFlag = true;
                        } else if (currentChar == '>') {
                            bracketFlag = false;
                            cnt++;
                        } else if (!bracketFlag) {
                            current.append(currentChar);
                        }
                    }
                    outputList.add(current.toString().trim());
                    List<String> outputListNew = new ArrayList<>();
                    for (String str : outputList) {
                        String[] words = str.split("\\s+");
                        outputListNew.addAll(Arrays.asList(words));
                    }
                    responses.add(keep.substring(dashIndex+2, keep.length()));
                    FinalList.add(outputListNew);
                }
                action = action.toLowerCase();
                if(line.startsWith(action)){
                    skillSize = Integer.parseInt(line.substring(action.length()+3));
                }
            }
            List<List<String>> FinalListKeep = new ArrayList<>(FinalList);
            FinalList.sort(Comparator.comparingInt(list -> ((List<List<String>>) list).size()).reversed());
            question = question.replaceAll("[^a-zA-Z0-9\\s]", "");
            String[] words = question.split("\\W+");
            System.out.println(Arrays.toString(words));
            System.out.println(FinalList.toString());
            cfgEditor cfgEditor = new cfgEditor();

            Pattern pattern2 = Pattern.compile("<(.*?)>");
            Matcher matcher2 = pattern2.matcher(action);
            String[] stringArray = null;
            if (matcher2.find()){
                String extracted = matcher2.group(1);
                int ind = cfgEditor.getSlotsScanner(extracted).toString().indexOf(">");
                stringArray = cfgEditor.getSlotsScanner(extracted).toString().substring(extracted.length()+3,ind).trim().split("\\s+");
            }
            String tempStr = "";
            List<String> tempList = new ArrayList<>();
            boolean adding = false;
            int cntC = 0;
            for (int i = 0; i < words.length; i++) {
                if(!words[i].equalsIgnoreCase(stringArray[cntC])){
                    while (!words[i].equalsIgnoreCase(stringArray[cntC])) {
                        tempStr += stringArray[i] + " ";
                        cntC++;
                    }
                    tempList.add(tempStr.trim()); 
                    tempStr = "";
                } else cntC++;
            }
            System.out.println("sssssss");
            System.out.println(tempList.toString());

        
            

            for (List<String> listToCheck : FinalList) {
                int cnt = 0;
                for (String string : words) {
                    if(cnt >=listToCheck.size()) break;
                    if(string.equalsIgnoreCase(listToCheck.get(cnt))){
                        cnt++;
                    }
                    if(cnt == (words.length-skillSize)){
                        responseRecord = (listToCheck.toString());
                        break;
                    }
                }
            }

            for (int i = 0; i < FinalListKeep.size(); i++) {
                if(FinalListKeep.get(i).toString().equals(responseRecord)){
                    this.output = responses.get(i).toString();
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getOutput(){
        return this.output;
    }
}