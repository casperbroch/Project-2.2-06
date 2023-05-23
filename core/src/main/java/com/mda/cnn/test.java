package com.mda.cnn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class test {
    public static void main(String[] args) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader("core/src/main/java/com/mda/cnn/facedata.csv"))) {
            int counter=0;
            String line;
            while((line = br.readLine()) != null) {
                String[] vals = line.split(";");
                System.out.println(vals.length);
                counter++;
            }

            System.out.println(counter);
        }
    }
}
