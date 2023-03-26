package com.mda.Testing;

// This is more robust with longer inputs (think: less susceptible to outliers)
public class WordMatch {
    double threshold = 0.8; // Requires 80% match in characters to be assumed as the same String

    public boolean wordMatch(String input, String template) {
        double length = template.length(); // Total length of the final string
        double matchingChars = 0;
        double numOfExtras = input.length();
        for(int i = 0; i < input.length(); i++) {
            int index = template.indexOf(input.charAt(i));
            if(index != -1) {
                // Increments number of matching characters
                matchingChars++;
                // Removes matching character
                StringBuilder temp_sb = new StringBuilder(template);
                temp_sb.deleteCharAt(index);
                template = temp_sb.toString();
                // Each matching char means one less extra character
                numOfExtras--;
            }
        }
        matchingChars -= numOfExtras;
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
        // String input = "aples";
        // String template = "apple";
        System.out.println(match.wordMatch(input, template));
    }
}
