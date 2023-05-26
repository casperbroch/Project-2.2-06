package com.mda;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;

public class Start {
    public static void main(String[] args) throws GeneralSecurityException, IOException {

        // Execute python file
        String command = "python core/src/main/java/com/mda/Python/IsItMe.py";
        Process p = Runtime.getRuntime().exec(command);

        App.main(args);
    }
}
