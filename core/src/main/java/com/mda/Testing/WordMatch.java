package com.mda;

public class WordMatch {
    double threshold = 0.8; // Requires 80% match in characters to be assumed as the same String

    public boolean wordMatch(String input, String template) {
        double length = template.length(); // Total length of the final string
        double matchingChars = 0;
        for(int i = 0; i < input.length(); i++) {
            int index = template.indexOf(input.charAt(i));
            if(index != -1) {
                // Increments number of matching characters
                matchingChars++;
                // Removes matching character
                StringBuilder sb = new StringBuilder(template);
                sb.deleteCharAt(index);
                template = sb.toString();
            }
        }
        double percentage = matchingChars/length;
        System.out.println(percentage);
        if(percentage >= threshold)
            return true;
        else
            return false;
    }
    public static void main(String[] args) {
        WordMatch match = new WordMatch();
        String input = "Whic lcture on Mday ta 11?";
        String template = "Which lectures on Monday at 11?";
        // String input = "aple";
        // String template = "apple";

        System.out.println(match.wordMatch(input, template));
    }
}
