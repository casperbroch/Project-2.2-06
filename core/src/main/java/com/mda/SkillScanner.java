package com.mda;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.control.SpinnerValueFactory;

import java.io.BufferedReader;

public class skillScanner {

    File file;
    String fileName;
    String output = "";

    public skillScanner() throws FileNotFoundException {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            file = new File(App.TEXTPATH);
            fileName = App.TEXTPATH;
        } else if (os.contains("os x")) {
            file = new File(App.TEXTPATH);
            fileName = App.TEXTPATH;
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner scanSkill = new Scanner(System.in);
        System.out.println("Please type the prototype sentence: ");
        String sentence = scanSkill.nextLine();
        sentence = "Question  " + sentence;
        skillScanner test = new skillScanner();
        test.scanSkill(sentence);
        scanSkill.close();
    }

    public boolean isSlotAvailable(int startLine, String slot) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
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
        System.out.println(sentence);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
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
                if (matches(line, sentence)) {
                    Pattern pattern = Pattern.compile("\\<.+?\\>");
                    Matcher matcher = pattern.matcher(line);
                    while (matcher.find()) {
                        match = matcher.group();
                        lineAdapt = line.replaceAll("\\p{Punct}", "").split("\\s+");
                        lineAdapted = sentence.replaceAll("\\p{Punct}", "").split("\\s+");
                        for (int index = 0; index < lineAdapted.length; index++) {
                            // System.out.println(match.substring(1, match.length() - 1));
                            // System.out.println(lineAdapt[index]);
                            System.out.println(lineAdapt[index]);
                            if (match.substring(1, match.length() - 1).equals(lineAdapt[index])) {
                                String temp = "Slot  <" + match.substring(1, match.length() - 1) + ">  "
                                        + lineAdapted[index];
                                if (!isSlotAvailable(lineNumber + 1, temp)) {
                                    getAction(actionLineNumber + 1, action);
                                    System.out.println(output);
                                }
                                action.add("<" + match.substring(1, match.length() - 1) + ">  " + lineAdapted[index]
                                        + " ");
                            }
                        }
                    }
                    getAction(actionLineNumber, action);
                    System.out.println(output);
                    return output;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Question not found!");
        return output;
    }

    public boolean matches(String template, String input) {
        template = template.toLowerCase();
        input = input.toLowerCase();
        String[] slots = template.split("\\<.+?\\>");
        StringBuilder sloted = new StringBuilder();
        for (String slot : slots) {
            sloted.append(slot).append("(.+?)");
        }
        return input.matches(sloted.toString());
    }

    public String getAction(int startLine, ArrayList<String> actionList) {
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
                    if (line.startsWith(action + "<")) {
                        continue;
                    }
                    output = returnLine.substring(action.length());
                    return returnLine.substring(action.length());
                }
                if (line.startsWith("Question")) {
                    if (actionList.size() == 1) {
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

    public String getOutput() {
        return this.output;
    }
}
