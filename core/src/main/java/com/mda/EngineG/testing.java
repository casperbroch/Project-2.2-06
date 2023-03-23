package com.mda.EngineG;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class testing {

    public static void main(String[] args) {
        String patternString = "how old is <A>?";
        String matcherString1 = "how old is gui a";

        String[] lineAdapted;
        String[] lineAdapt;
        Pattern pattern = Pattern.compile("\\<.+?\\>");
        Matcher matcher = pattern.matcher(patternString);

        while (matcher.find()) {
            String match = matcher.group();
            lineAdapt = patternString.replaceAll("\\p{Punct}", "").split("\\s+");
            lineAdapted = matcherString1.replaceAll("\\p{Punct}", "").split("\\s+");


            String [] test = new String[lineAdapt.length];
            int cntAdder = 0;
            for (int i = 0; i < lineAdapt.length; i++) {
                if(lineAdapt[i].equals(lineAdapted[cntAdder])){
                    test[i] = lineAdapted[cntAdder];
                    cntAdder++;
                } else{
                    test[i] = "";
                    if(i + 1 < lineAdapt.length){
                        while (!lineAdapt[i+1].equals(lineAdapted[cntAdder])) {
                            test[i] = test[i] + lineAdapted[cntAdder];
                            if(!lineAdapt[i+1].equals(lineAdapted[cntAdder])){
                                test[i] = test[i] + " ";
                                System.out.println("added");
                            }
                            cntAdder++;
                        } 
                    } else{
                        while (cntAdder  < lineAdapted.length) {
                            test[i] = test[i] + lineAdapted[cntAdder];
                            if(cntAdder + 1 < lineAdapted.length){
                                System.out.println("added");
                                test[i] = test[i] + " ";
                                System.out.println(test[i]);
                            }
                            cntAdder++;
                        }
                    }
                }
            }

            System.out.println(test.length);
            for (int i = 0; i < test.length; i++) {
                System.out.println(test[i]);
            }


        }

        
    
    }
}
