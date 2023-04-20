package com.mda.SpellingEngines.wordsuggestion;


import java.util.List;
import java.util.stream.Collectors;

public class DamerauLevenshteinParallel {
    public static void main(String[] args) {
        DamerauLevenshteinParallel dl = new DamerauLevenshteinParallel();
        String word = "hell";
        List<String> similarWords = dl.getSimilarWordsDistance(word, 1);
    }
    DictionaryParallel dict;
    public DamerauLevenshteinParallel(){
        this.dict = new DictionaryParallel();
    }
    public List<String> getSimilarWordsDistance(String word, int maximumDistance) {
        List<String> dictionary = dict.getWords();

        return dictionary.parallelStream()
                .filter(candidate -> getDistance(word, candidate) <= maximumDistance)
                .collect(Collectors.toList());
    }

    public int getDistance(String a, String b){
        int[][] distance = new int[a.length()+1][b.length()+1];
        for (int i = 0; i <= a.length(); i++) {
            distance[i][0] = i;
        }
        for (int i = 0; i <= b.length(); i++) {
            distance[0][i] = i;
        }

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int subsCost = (a.charAt(i-1) == b.charAt(j-1)) ? 0 : 1;
                distance[i][j] = Math.min(distance[i - 1][j] + 1, Math.min(distance[i][j - 1] + 1, distance[i - 1][j - 1] + subsCost)); //deletion, insertion, or substitution

                if (i > 1 && j > 1 && a.charAt(i - 1) == b.charAt(j - 2) && a.charAt(i - 2) == b.charAt(j - 1)){
                    distance[i][j] = Math.min(distance[i][j], distance[i-2][j-2]+subsCost); // transpostion
                }
            }
        }
        return distance[a.length()][b.length()];
    }
}































