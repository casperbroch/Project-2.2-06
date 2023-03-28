package com.mda.WordMatching;

// This is more robust with longer inputs (think: less susceptible to outliers)
public class WordMatch {
    double threshold = 0.85; // Requires 85% match in characters to be assumed as the same String

    public boolean wordMatch(String input, String template) {
        // Changes threshold based on length
        // Matching is more robust for longer inputs (think: less susceptible to anomalies)
        if(input.length() <= 30)
            threshold = 0.85;
        else
            threshold = 0.9;
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
        System.out.println(Math.max(percentage, 0));
        if(percentage >= threshold)
            return true;
        else
            return false;
    }
    public static void main(String[] args) {
        WordMatch match = new WordMatch();
        String input1 = "I nde covfef.e";
        String template1 = "I need coffee.";
        System.out.println(match.wordMatch(input1, template1));
        String input2 = "I' lgning for people to test y mHCI prototype.";
        String template2 = "I'm looking for people to test my HCI prototype.";
        System.out.println(match.wordMatch(input2, template2));
    }
}
