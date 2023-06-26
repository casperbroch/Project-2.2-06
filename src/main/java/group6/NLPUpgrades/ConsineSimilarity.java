package group6.NLPUpgrades;

import java.util.*;

public class ConsineSimilarity {

    public static double cosineSimilarity(String question1, String question2) {
        String cleanedQuestion1 = clean(question1);
        String cleanedQuestion2 = clean(question2);
        List<String> tokens1 = tokenize(cleanedQuestion1);
        List<String> tokens2 = tokenize(cleanedQuestion2);

        tokens1 = removeStopWords(tokens1);
        tokens2 = removeStopWords(tokens2);
        Map<String, Integer> vector1 = computeWordFrequency(tokens1);
        Map<String, Integer> vector2 = computeWordFrequency(tokens2);

        double dotProduct = computeDotProduct(vector1, vector2);
        double magnitude1 = computeMagnitude(vector1);
        double magnitude2 = computeMagnitude(vector2);

        if (magnitude1 == 0.0 || magnitude2 == 0.0) {
            return 0.0;
        } else {
            return dotProduct / (magnitude1 * magnitude2);
        }
    }

    private static String clean(String question) {
        String cleanedQuestion = question.trim();
        cleanedQuestion = cleanedQuestion.replaceAll("[^a-zA-Z0-9\\s]", "");
        cleanedQuestion = cleanedQuestion.toLowerCase();
        cleanedQuestion = cleanedQuestion.replaceAll("\\s+", " ");
        cleanedQuestion = cleanedQuestion.replaceAll("whats ", "what is ");
        cleanedQuestion = cleanedQuestion.replaceAll("'ve", " have ");
        cleanedQuestion = cleanedQuestion.replaceAll(" can't ", " cannot ");
        cleanedQuestion = cleanedQuestion.replaceAll("n't ", " not ");
        cleanedQuestion = cleanedQuestion.replaceAll("i'm ", "i am ");
        cleanedQuestion = cleanedQuestion.replaceAll("'ll' ", "will ");
        cleanedQuestion = cleanedQuestion.replaceAll(" e - mail ", " email ");
        cleanedQuestion = cleanedQuestion.replaceAll("[!]+", ".");

        return cleanedQuestion;
    }

    private static List<String> tokenize(String question) {
        String[] tokens = question.split(" ");
        return Arrays.asList(tokens);
    }

    private static List<String> removeStopWords(List<String> tokens) {
        List<String> filteredTokens = new ArrayList<>();

        String[] stopWords = { "a", "an", "the", "in", "on", "at", "to", "for", "is", "are", "was", "were", "am", "be",
                "being", "been", "and", "or", "but" }; 

        // Remove stop words
        for (String token : tokens) {
            if (!Arrays.asList(stopWords).contains(token)) {
                filteredTokens.add(token);
            }
        }
        return filteredTokens;
    }

    private static Map<String, Integer> computeWordFrequency(List<String> tokens) {
        Map<String, Integer> wordFrequency = new HashMap<>();
        for (String token : tokens) {
            wordFrequency.put(token, wordFrequency.getOrDefault(token, 0) + 1);
        }
        return wordFrequency;
    }

    private static double computeDotProduct(Map<String, Integer> vector1, Map<String, Integer> vector2) {
        double dotProduct = 0.0;
        for (String token : vector1.keySet()) {
            if (vector2.containsKey(token)) {
                dotProduct += vector1.get(token) * vector2.get(token);
            }
        }
        return dotProduct;
    }

    private static double computeMagnitude(Map<String, Integer> vector) {
        double magnitude = 0.0;
        for (int frequency : vector.values()) {
            magnitude += Math.pow(frequency, 2);
        }
        return Math.sqrt(magnitude);
    }

    public static void main(String[] args) {
        String question1 = "what is jeff doing today?";
        String question2 = "How is jeff today?";

        String cleanedQuestion1 = clean(question1);
        String cleanedQuestion2 = clean(question2);

        System.out.println("Cleaned question 1: " + cleanedQuestion1);
        System.out.println("Cleaned question 2: " + cleanedQuestion2);
        double similarity = cosineSimilarity(question1, question2);

        System.out.println("Cosine similarity: " + similarity );
    }
}