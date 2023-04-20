package com.mda.SkillEngine;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mda.GUI.App;
import com.mda.SpellingEngines.WordMatching.WordMatch;

import java.io.BufferedReader;

public class skillScanner {

    File file; 
    String fileName;
    String output = "";
    WordMatch matchAlg;

    public skillScanner() throws FileNotFoundException{
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")){
            file = new File(App.TEXTPATH);
            fileName = App.TEXTPATH;
        } else if (os.contains("os x")){
            file = new File(App.TEXTPATH);
            fileName = App.TEXTPATH;
        }     
    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner scanSkill = new Scanner(System.in);
        String sentence = scanSkill.nextLine();
        sentence = "Question  " + sentence;
        skillScanner test = new skillScanner();
        test.scanSkill(sentence);
        scanSkill.close();
    }

    private boolean isSlotAvailable(int startLine, String slot) {
        try(BufferedReader reader = new BufferedReader(new FileReader(file))){ 
            String line = "";
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber < startLine) {
                    continue;
                }
                
                if (!line.startsWith("Action")) {
                    if (slot.equalsIgnoreCase(line)) {
                        return true;
                    }
                }
                if (line.startsWith("Action")) {
                    break;
                }
            } 
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String scanSkill(String sentence) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            ArrayList<String> defaultAction = new ArrayList<>();
            defaultAction.add("Action ");
            ArrayList<String> action = new ArrayList<>();
            action.add("Action ");
            String line;
            String match;
            String[] lineAdapt;
            String[] lineAdapted;
            int lineNumber = 0;
            int actionLineNumber = 0; 
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                actionLineNumber = lineNumber;
                matchAlg = new WordMatch();
                lineAdapt = line.replaceAll("\\p{Punct}", "").split("\\s+");
                lineAdapted = sentence.replaceAll("\\p{Punct}", "").split("\\s+");
                ArrayList<String> adapted = new ArrayList<>();
                ArrayList<String> adaptedTemplate = new ArrayList<>();

                int cntAdd = 0;
                for (int i = 0; i < lineAdapt.length; i++) {
                    if(!lineAdapt[i].equals(lineAdapt[i].toUpperCase())){
                        if(i<lineAdapted.length){
                            adapted.add(lineAdapted[i]);
                            adaptedTemplate.add(lineAdapt[i]);
                        }
                    }
                }

                // if matches or meets threshold
                if (matches(line, sentence) || matchAlg.wordMatch(adapted.toString(), adaptedTemplate.toString())) {
                    Pattern pattern = Pattern.compile("\\<.+?\\>");
                    Matcher matcher = pattern.matcher(line);
                    while (matcher.find()) {
                        match = matcher.group();
                        lineAdapt = line.replaceAll("\\p{Punct}", "").split("\\s+");
                        lineAdapted = sentence.replaceAll("\\p{Punct}", "").split("\\s+");
                        String [] test = new String[lineAdapt.length];
                        int cntAdder = 0;
                        for (int i = 0; i < lineAdapt.length; i++) {
                            test[i] = "";
                            if(lineAdapt[i].equalsIgnoreCase(lineAdapted[cntAdder])){
                                test[i] = lineAdapted[cntAdder];
                                cntAdder++;
                            } else{
                                boolean caps = true;
                                for (int j = 0; j < lineAdapt.length; j++) {
                                    if(j == i){
                                        for (int k = 0; k < lineAdapt[j].length(); k++) {
                                            if(!Character.isUpperCase((lineAdapt[j].charAt(k)))){
                                                caps = false;
                                            }
                                        }
                                    }
                                }
                                if(i + 1 < lineAdapt.length){
                                    while (!lineAdapt[i+1].equalsIgnoreCase(lineAdapted[cntAdder]) && caps) {
                                        test[i] = test[i] + lineAdapted[cntAdder];
                                        if(!lineAdapt[i+1].equalsIgnoreCase(lineAdapted[cntAdder+1])){
                                            test[i] = test[i] + " ";
                                        }
                                        cntAdder++;
                                    }
                                    if(!caps){
                                        test[i] = lineAdapted[cntAdder];
                                        cntAdder++;
                                    }
                                } else{
                                    while (cntAdder < lineAdapted.length) {
                                        test[i] = test[i] + lineAdapted[cntAdder];
                                        if(cntAdder + 1 < lineAdapted.length){
                                            test[i] = test[i] + " ";
                                        }
                                        cntAdder++;
                                    }
                                }
                            }
                        }
                        lineAdapted = test;
                        for (int index = 0; index < lineAdapted.length; index++) {
                            if(match.substring(1, match.length() - 1).equals(lineAdapt[index])){
                                String temp = "Slot  <" + match.substring(1, match.length() - 1) + ">  " + lineAdapted[index];
                                if(!isSlotAvailable(lineNumber+1,temp)){
                                    return getAction(actionLineNumber+1, defaultAction);
                                }
                                action.add("<" + match.substring(1, match.length() - 1) + ">  " + lineAdapted[index] + " ");
                            }
                        }
                    }
                    getAction(actionLineNumber, action);
                    return output;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Question not found!");
        return "Question not found!";
    }

    private boolean matches(String template, String input) {
        template = template.toLowerCase();
        input = input.toLowerCase();
        String[] slots = template.split("\\<.+?\\>");
        StringBuilder sloted = new StringBuilder();
        for (String slot : slots) {
            sloted.append(slot).append("(.+?)");
        }
        return input.matches(sloted.toString());
    }

    private String getAction(int startLine, ArrayList<String> actionList) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line = "";
            String returnLine = "";
            int lineNumber = 0;
            StringBuilder builder = new StringBuilder();
            for (String str : actionList) {
                builder.append(str);
                builder.append(" ");
            }
            String action = builder.toString();
            action = action.toLowerCase();
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber < startLine) {
                    continue;
                }
                returnLine = line;
                line = line.toLowerCase();
                if (line.startsWith(action)) {
                    if(line.startsWith(action + "<")){
                        continue;
                    } 
                    output = returnLine.substring(action.length());
                    return returnLine.substring(action.length());
                }
                if (line.startsWith("Question")) {
                    if(actionList.size() == 1){
                        return "Action for that input not found.";
                    }
                    actionList.remove(actionList.size() - 1);
                    getAction(startLine, actionList);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        actionList.remove(actionList.size() - 1);
        getAction(startLine, actionList);
        return "Error ocurred, please try again.";
    }

    public String getOutput(){
        return this.output;
    }
}