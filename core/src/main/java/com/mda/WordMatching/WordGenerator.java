package com.mda.WordMatching;

import java.util.Random;

// For experiments
public class WordGenerator {
    String shortInput = "Apples.";
    String shortMedInput = "I need help and coffee.";
    String medInput = "Help, what is happening in my courses.";
    String longInput = "I'm looking for people to test my HCI prototype.";

    public String wordScrambler(String input, int numOfErrors) {
        StringBuilder output = new StringBuilder(input);
        for(int i = 0; i < numOfErrors; i++) {
            double prob = Math.random();
            int index = (int) (Math.random() * (output.length()));
            if(prob >= 2.0/3) {
                // Deletes a random character
                output.deleteCharAt(index);
            } else if(prob < 2.0/3 && prob >= 1.0/3) {
                // Adds a random character
                // Random char generator from https://stackoverflow.com/questions/2626835/is-there-functionality-to-generate-a-random-character-in-java
                Random r = new Random();
                char c = (char)(r.nextInt(26) + 'a');
                output.insert(index, c);
            } else {
                // Switch two random adjacent characters
                int index2;
                if(index == output.length()-1) {
                    index2 = index-1;
                } else {
                    index2 = index+1;
                }
                char temp = output.charAt(index);
                output.replace(index, index+1, Character.toString(output.charAt(index2)));
                output.replace(index2, index2+1, Character.toString(temp));
            }
        }
        return output.toString();
    }

    public static void main(String[] args) {
        WordGenerator gen = new WordGenerator();
        WordMatch match = new WordMatch();
        for(int i = 0; i < 5; i++) {
            int numOfErrors = 7;
            // Short input
            String input1 = gen.wordScrambler(gen.shortInput, numOfErrors);

            match.wordMatch(input1, gen.shortInput);
            // Short-medium input
            String input2 = gen.wordScrambler(gen.shortMedInput, numOfErrors);

            match.wordMatch(input2, gen.shortMedInput);
            // Medium input
            String input3 = gen.wordScrambler(gen.medInput, numOfErrors);

            match.wordMatch(input3, gen.medInput);
            // Long input
            String input4 = gen.wordScrambler(gen.longInput, numOfErrors);

            match.wordMatch(input4, gen.longInput);
        }
    }
}
