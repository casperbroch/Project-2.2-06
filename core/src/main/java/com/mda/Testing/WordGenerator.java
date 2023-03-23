package com.mda;

import java.util.Random;

// For experiments
public class WordGenerator {
    String input1 = "I need help and coffee.";
    String input2 = "We are in the learning spaces.";
    String input3 = "I like my coffee with milk and syrup.";
    String input4 = "Help, what is happening in my courses.";
    String input5 = "I'm looking for people to test my HCI prototype.";

    public String wordScrambler(String input, int numOfErrors) {
        StringBuilder output = new StringBuilder(input);
        for(int i = 0; i < numOfErrors; i++) {
            double prob = Math.random();
            int index = (int) (Math.random() * (input.length()+1));
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
                // Switch two random characters
                int index2 = (int) (Math.random() * (input.length()+1));
                char temp = output.charAt(index);
                output.replace(index, index+1, Character.toString(output.charAt(index2)));
                output.replace(index2, index2+1, Character.toString(temp));
            }
        }
        return output.toString();
    }

    public static void main(String[] args) {
        WordGenerator gen = new WordGenerator();
        String random1 = gen.wordScrambler(gen.input1, 3);
        WordMatch match = new WordMatch();
        System.out.println(random1);
        System.out.println(gen.input1);
        System.out.println(match.wordMatch(random1, gen.input1));
    }
}
