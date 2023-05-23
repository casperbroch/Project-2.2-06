package com.mda;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class Start {
    public static void main(String[] args) throws GeneralSecurityException, IOException {
        String prg = "import sys";
        BufferedWriter out = new BufferedWriter(new FileWriter("core/src/main/java/com/mda/Python/main.py"));
        out.write(prg);
        out.close();
        Process p = Runtime.getRuntime().exec("python core/src/main/java/com/mda/Python/main.py");
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String ret = in.readLine();
        System.out.println("value is : "+ret);

        App.main(args);
    }
}
