package com.mda;

public class Responder {
    int state = 0;

    Responder() {}


    // TODO: Make some switch statement for stemming and getting states

    String getResponse(String message) {
        // TODO: Linking to txt files to extract (question) format in first line
        return ("Welcome to the " + message + " app! What would you like to do?");
    }


}
