package group6.Engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nd4j.common.loader.SourceFactory;

import group6.ChatGPTAPI;

/*
 * Parsing algorithm for skills CFG.
 */


public class cykAlgorithm {
    public String output;
    public ChatGPTAPI gpt;
    private File file = new File("src\\main\\java\\group6\\Engine\\cfgSkills.txt");
    private ArrayList<String> [][] cykTable;
    public static void main(String[] args) throws FileNotFoundException {
        cykAlgorithm test = new cykAlgorithm();
        test.cykRun("new york city to la.");
    }

    /*
    * Runs the CYK algorithm. 
    */
    public void cykRun(String question) throws FileNotFoundException{
        String[] initial = question.split("[^\\p{L}0-9']+");
        cykTable = initializeCYKtable(initial);
        for (int i = 0; i < initial.length; i++) {
            cykTable[initial.length-1][i] = checkTerminals(initial[i]);
        }
        int iterator = cykTable.length - 1;
        int increment = 2;
        ArrayList<String> toCheck = new ArrayList<>();
        for (int i = cykTable.length-2; i >=0 ;i--) {
            for (int j = 0; j < iterator; j++) {
                for (int k = j; k < j+increment; k++) {
                    toCheck.addAll(cykTable[cykTable.length - 1][k]);
                    toCheck.add(".");
                }
                cykTable[i][j] = checkNonTerminals(toCheck, increment);
                toCheck = new ArrayList<>();
            }
            increment++;
            iterator--;
        }
        //printTable(cykTable);
        cfgScanner testScan = new cfgScanner();
        cfgEditor testEditor = new cfgEditor();
        if(!cykTable[0][0].isEmpty()){
            System.out.println("s");
            
            for(int i = 0; i < cykTable[0][0].size(); i++){
                if(testEditor.skillExists(cykTable[0][0].get(i).toString())){
                    testScan.getAction(cykTable[0][0].get(i).toString(), question);
                    output = testScan.getOutput();
                    System.out.println("This is the out" + output);
                    break;
                }
            }
            
        } else {
            String similarityScore = "";
            try {
                System.out.println("Load python script");
                // Arg2 = existing skills
                String paramString = "";
                for (String element : testEditor.getClassesNaive()) {
                    paramString += element + ",";
                }
                paramString = paramString.substring(0, paramString.length() - 1);
                ProcessBuilder pb = new ProcessBuilder("python", "src\\main\\java\\group6\\NLPUpgrades\\TransformerBERT.py", question, paramString);
                Process process = pb.start();
                // Get the output from the Python script
                InputStream inputStream = process.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    similarityScore = line;
                    System.out.println(similarityScore);
                }
                int exitCode = process.waitFor();

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            double numericValue = Double.parseDouble(similarityScore);
            if (numericValue >= 0.973) {
                try {
                    // Arg1 = List of classes + documents
                    String documents = "";
                    ArrayList<String> docs =  testEditor.getDocumentsNaive();
                    ArrayList<String> classes = testEditor.getClassesNaive();
                    for (int i = 0; i < docs.size(); i++) {
                        documents += docs.get(i) + " | " + classes.get(i) + ",";
                    }
                    documents = documents.substring(0, documents.length() - 1);
                    System.out.println(documents);
                    ProcessBuilder pb = new ProcessBuilder("python", "src\\main\\java\\group6\\NLPUpgrades\\NaiveBayesClassifier.py",documents, question);
                    Process process = pb.start();
                    // Get the output from the Python script
                    InputStream inputStream = process.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output = line;
                    }
                    int exitCode = process.waitFor();

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            } else{
                gpt = new ChatGPTAPI();
                String answer =gpt.askGod(question);
                output = ("Sorry, I am not able to give you an answer for that from my native skills!\n\nI asked ChatGPT for you: \n"+answer);
            }
            
        }
        if(output.isEmpty()) {
            gpt = new ChatGPTAPI();
            String answer =gpt.askGod(question);
            output = ("Sorry, I am not able to give you an answer for that from my native skills!\n\nI asked ChatGPT for you: \n"+answer);
        }
    }

    public ArrayList<String> getFromCYKTable(ArrayList<String> transformedList){

        for (int index = 0; index < transformedList.size(); index++) {
            if(transformedList.get(index).equals(".")){
                transformedList.remove(index);
            }
        }
        int row = transformedList.size();
        ArrayList<String> transformedListResult = new ArrayList<>();
        for (int i = 0; i < cykTable[0].length; i++) {
            int test = 0;
            ArrayList<String> TableCheck = new ArrayList<>();
            for (int x = i; x < cykTable[0].length; x++) {
                TableCheck.addAll(cykTable[cykTable.length-1][x]);
                test++;
                if(TableCheck.size() >= row){
                    break;
                }
            }
            boolean found = true;
            if(row <= TableCheck.size()){
                for (int j = 0; j < transformedList.size(); j++) {
                    if(!(transformedList.get(j)).equals(TableCheck.get(j))){
                        found = false;
                    }
                }  
            }
            if(found){
                transformedListResult = cykTable[cykTable.length-test][i];
                return transformedListResult;
            }
        }
        return transformedListResult;
    }

    public ArrayList<String> findCartesianProductsAndFindResult(ArrayList<String> transformedList, ArrayList<String> transformedListSecond){
        ArrayList<String> nonTerminalsPresent = new ArrayList<>();
        ArrayList<String> word = new ArrayList<>();
        // Make all combinations
        ArrayList<ArrayList<String>> transformedLists = new ArrayList<>();
        for (String string : transformedList) {
            for (String string2 : transformedListSecond) {
                word = new ArrayList<>();
                word.add(string);
                word.add(string2);
                transformedLists.add(word);
            }
        }
        for(ArrayList<String> words: transformedLists){
            word = words;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(" ");
            for (String element : word) {
                stringBuilder.append(element + " ");
            }
            String variable = stringBuilder.toString();
            try (BufferedReader readerDel1 = new BufferedReader(new FileReader(file))){
                String current = "";
                while((current = readerDel1.readLine()) != null) {
                    if(current.startsWith("Rule ")) {
                        Pattern patternB = Pattern.compile(">");
                        Matcher matcherB = patternB.matcher(current);
                        Pattern patternS = Pattern.compile("<");
                        Matcher matcherS = patternS.matcher(current);
                        int cnt = 0;
                        int idxStart = 0;
                        int idxEnd = 0;
                        while(matcherB.find() && matcherS.find()){
                            if(cnt == 0){
                                idxStart = matcherB.start();
                                idxEnd = matcherS.start();
                            }
                            cnt++;
                        }
                        String[] terminalSymbols = current.substring(idxStart+1).split("\\|");
                        for (String currentSymbol : terminalSymbols) {
                            currentSymbol = currentSymbol.replaceAll("\\s", "");
                            variable = variable.replaceAll("\\s", "");
                            if((currentSymbol).equalsIgnoreCase(variable)){
                                if(!nonTerminalsPresent.contains(current.substring(idxEnd, idxStart+1))){
                                    nonTerminalsPresent.add(current.substring(idxEnd, idxStart+1));
                                }
                            }
                        }
                    }
                }
                readerDel1.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        // This is the final symbols going into the array. 
        return nonTerminalsPresent;
    }

    /*
    * This method checks for all non-terminal rules in the language.
    * That is a finite set of symbols working as variables. 
    */
    public ArrayList<String> checkNonTerminals(ArrayList<String> word, int iterator){
        ArrayList<String> nonTerminalsPresent = new ArrayList<>();
        int fixedWordSize = word.size();
        // generate all possibilities too and then run it all
        int dotCounter = 0;
        for (int i = 0; i < fixedWordSize; i++) {
            if(word.get(i).equals(".")){
                dotCounter++;
            }
        }
        ArrayList<String> transformedList = new ArrayList<>();
        ArrayList<String> transformedListSecond = new ArrayList<>();
        int cntFlag = 1;
        while(cntFlag < iterator){
            if(dotCounter == 2){
                boolean found = false;
                for (int i = 0; i < fixedWordSize; i++) {
                    if(word.get(i).equals(".")){
                        found=true;
                    } else if (found && !word.get(i).equals(".")){
                        transformedListSecond.add(word.get(i));
                    } else if(!found){
                        transformedList.add(word.get(i));
                    }
                }
            } else{
                for (int i = 0; i < word.size(); i++) {
                    if(i < cntFlag*2){
                        transformedList.add(word.get(i));
                    } else{
                        transformedListSecond.add(word.get(i));
                    }
                }
            }
            ArrayList<String> transformedListChecked = new ArrayList<>();
            ArrayList<String> transformedListSecondChecked = new ArrayList<>();
            transformedListChecked.addAll(getFromCYKTable(transformedList));
            transformedListSecondChecked.addAll(getFromCYKTable(transformedListSecond));
            nonTerminalsPresent.addAll(findCartesianProductsAndFindResult(transformedListChecked, transformedListSecondChecked));
            cntFlag++;
            transformedList = new ArrayList<>();
            transformedListSecond = new ArrayList<>();
        }
        return nonTerminalsPresent;
    }
    
    /*
    * This method checks for all terminal rules in the language.
    * That is symbols of the alphabet of the language being defined.
    */
    public ArrayList<String> checkTerminals(String word){
        ArrayList<String> terminalsPresent = new ArrayList<>();
        try (BufferedReader readerDel1 = new BufferedReader(new FileReader(file))){
            String current = "";
            while((current = readerDel1.readLine()) != null) {
                if(current.startsWith("Rule ")) {
                    Pattern patternB = Pattern.compile(">");
                    Matcher matcherB = patternB.matcher(current);
                    Pattern patternS = Pattern.compile("<");
                    Matcher matcherS = patternS.matcher(current);
                    int cnt = 0;
                    int idxStart = 0;
                    int idxEnd = 0;
                    while(matcherB.find() && matcherS.find()){
                        idxStart = matcherB.start();
                        idxEnd = matcherS.start();
                        cnt++;
                    }
                    if(cnt==1){
                        String[] terminalSymbols = current.substring(idxStart+1).split("\\|");
                        for (String currentSymbol : terminalSymbols) {
                            if(currentSymbol.trim().equalsIgnoreCase(word)) terminalsPresent.add(current.substring(idxEnd, idxStart+1));
                        }
                    } 
                }
            }
            readerDel1.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(terminalsPresent.isEmpty()){
            terminalsPresent.add(word);
        }
        return terminalsPresent;
    }

    public ArrayList<String> [][] initializeCYKtable(String[] initial){
        ArrayList<String> [][] cykTable = new ArrayList[initial.length][initial.length];
        for (int i = 0; i < initial.length; i++) {
            for (int j = 0; j < initial.length; j++) {
                cykTable[i][j] = new ArrayList<>();
            }
        }
        return cykTable;
    }

    // Prints the table for testing purposes. 
    public void printTable(ArrayList<String> [][] cykTable){
        System.out.println();
        System.out.println("-----CYK Table-----");
        for (int i = 0; i < cykTable.length; i++) {
            for (int j = 0; j < cykTable.length; j++) {
                System.out.print(cykTable[i][j].toString() + " | ");
            }
            System.out.println();
        }
        System.out.println();
    }
}
