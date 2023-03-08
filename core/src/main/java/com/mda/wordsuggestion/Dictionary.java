package com.mda.wordsuggestion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Dictionary {
    public static void main(String[] args) {
        Dictionary run = new Dictionary();
        System.out.println(run.getWords());
    }
    private List<String> words;

    public Dictionary(){
        words = new ArrayList<>();
        String filePath = "src\\main\\java\\com\\mda\\wordsuggestion\\words\\words_alpha.txt";
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = br.readLine()) != null) {
                this.words.add(line);
            }
            br.close();
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
