package group6;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;

public class Start {
    public static void main(String[] args) throws GeneralSecurityException, IOException {

        // Execute python file
        String commandIsItMe = "python src/main/java/group6/Python/IsItMe.py";
        Process pIsItMe = Runtime.getRuntime().exec(commandIsItMe);

        String commandSign = "python src/main/java/group6/Python/Sign.py";
        Process pSign = Runtime.getRuntime().exec(commandSign);

        App.main(args);
    }
}
