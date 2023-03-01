package com.mda.Engine;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;


// change variable names and method names, should we consider more than 2 actions for 1 thing


public class skillScanner {

    File file; 
    FileReader fileReader; 
    public static void main(String[] args) {
        String fileName = "core/src/main/java/com/mda/Engine/skills.txt";
        Scanner scanSkill = new Scanner(System.in);
        System.out.println("Please type the prototype sentence: ");
        String sentence = scanSkill.nextLine();
        sentence = "Question  " + sentence;
        boolean isPresent = skillScanner.isSentencePresent(fileName, sentence);
        if (isPresent) {
            
            System.out.println("Check if in slots.");
        } else {
            System.out.println("Not available.");
        }
        scanSkill.close();
    }

    public static boolean isSentencePresent(String fileName, String sentence) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (matches(line, sentence)) {
                    System.out.println(line); 
                    System.out.println(sentence); 
                    Pattern pattern = Pattern.compile("\\<.+?\\>");
                    Matcher matcher = pattern.matcher(line);
                    while (matcher.find()) {
                        String match = matcher.group();
                        String[] words = line.replaceAll("\\p{Punct}", "").split("\\s+");
                        String[] words2 = sentence.replaceAll("\\p{Punct}", "").split("\\s+");
                        for (int index = 0; index < words2.length; index++) {
                            if(match.substring(1, match.length() - 1).equals(words[index])){
                                System.out.println(match.substring(1, match.length() - 1));
                                System.out.println(words2[index]);
                            }
                        }
                    }
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean matches(String template, String sentence) {
        String[] slots = template.split("\\<.+?\\>");
        StringBuilder regex = new StringBuilder();
        for (String slot : slots) {
            regex.append(slot).append("(.+?)");
        }
        return sentence.matches(regex.toString());
    }

    public skillScanner() throws FileNotFoundException{
        file = new File("core/src/main/java/com/mda/Engine/skills.txt");
        fileReader = new FileReader(file); 
    }
}