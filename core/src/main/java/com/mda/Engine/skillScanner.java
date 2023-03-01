package com.mda.Engine;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;

public class skillScanner {

    File file; 
    FileReader fileReader; 
    String fileName;

    public skillScanner() throws FileNotFoundException{
        file = new File("core/src/main/java/com/mda/Engine/skills.txt");
        fileReader = new FileReader(file); 
        fileName = "core/src/main/java/com/mda/Engine/skills.txt";
    }

    public void main(String[] args) {
        Scanner scanSkill = new Scanner(System.in);
        System.out.println("Please type the prototype sentence: ");
        String sentence = scanSkill.nextLine();
        sentence = "Question  " + sentence;
        isSentencePresent(fileName, sentence);
        scanSkill.close();
    }

    private boolean isSlotAvailable(int startLine, String slot) {
        try(BufferedReader reader = new BufferedReader(fileReader)){ 
            String line = "";
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber < startLine) {
                    continue;
                }
                if (!line.startsWith("Action")) {
                    if (line.equalsIgnoreCase(slot)) {
                        return true;
                    }
                    System.out.println(line);
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

    private boolean isSentencePresent(String fileName, String sentence) {
        try (BufferedReader reader = new BufferedReader(fileReader)) {
            String line;
            String match;
            String[] lineAdapt;
            String[] lineAdapted;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (matches(line, sentence)) {
                    Pattern pattern = Pattern.compile("\\<.+?\\>");
                    Matcher matcher = pattern.matcher(line);
                    while (matcher.find()) {
                        match = matcher.group();
                        lineAdapt = line.replaceAll("\\p{Punct}", "").split("\\s+");
                        lineAdapted = sentence.replaceAll("\\p{Punct}", "").split("\\s+");
                        for (int index = 0; index < lineAdapted.length; index++) {
                            if(match.substring(1, match.length() - 1).equals(lineAdapt[index])){
                                System.out.println(match.substring(1, match.length() - 1));
                                System.out.println(lineAdapted[index]);
                                String temp = "Slot  <" + match.substring(1, match.length() - 1) + ">  " + lineAdapted[index];
                                if(!isSlotAvailable(lineNumber+1,temp)){
                                    System.out.println("Question found but slot not available!");
                                    return false;
                                }
                            }
                        }
                    }
                    // getAction()
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Question not found!");
        return false;
    }

    private boolean matches(String template, String input) {
        String[] slots = template.split("\\<.+?\\>");
        StringBuilder sloted = new StringBuilder();
        for (String slot : slots) {
            sloted.append(slot).append("(.+?)");
        }
        return input.matches(sloted.toString());
    }

    /*  ----------------------------- To complete ----------------------------- 
    private boolean getAction() {
        String filePath = "core/src/main/java/com/mda/Engine/skills.txt";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    + Consider several actions for the same input.
     ------------------------------------------------------------------------- */
}