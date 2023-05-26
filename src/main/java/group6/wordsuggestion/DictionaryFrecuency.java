package group6.wordsuggestion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
public class DictionaryFrecuency {
    public static void main(String[] args) throws IOException {
        DictionaryFrecuency r = new DictionaryFrecuency();
    }
    private HashMap<String, Long> words;
    public DictionaryFrecuency() throws IOException {
        this.words = new HashMap<>();
        String filePath = "src/main/java/group6/wordsuggestion/words/en-80k.txt";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line = reader.readLine();
            while (line != null) {
                split(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void split(String line){
        String [] parts = line.split(" ");
        String word = parts[0];
        long frequency = Long.parseLong(parts[1]);
        words.put(word, frequency);
    }

    public HashMap<String, Long> getWords(){
        return words;
    }
}
