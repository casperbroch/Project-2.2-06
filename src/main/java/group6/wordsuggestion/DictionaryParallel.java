package group6.wordsuggestion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DictionaryParallel {
    public static void main(String[] args) {
        DictionaryParallel run = new DictionaryParallel();
    }
    private List<String> words;

    public DictionaryParallel() {
        String filePath = "src/main/java/group6/wordsuggestion/words/words_alpha.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            this.words = br.lines().parallel().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean contains(String word) {
        return this.words.contains(word);
    }
    public List<String> getWords(){
        return words;
    }
}
