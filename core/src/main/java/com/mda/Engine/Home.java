package com.mda.Engine;
import java.util.Scanner;

public class Home extends Node{

    String settings = "1) Access my calendar.\n2) Access the weather.\n3) Quit.";
    // Must correspond with settings each time a new skill is added
    int quit = 3;
    Scanner scanner;

    Home() {
        // Home is the root node; think: homepage
        super("home", null);
        Calendar calendar = new Calendar(this);
        this.addChild(calendar);
        scanner = new Scanner(System.in);
    }

    public void initiateAction() {
        int action = 0;
        System.out.println("Hello! How may I help you today?");
        System.out.println();
        System.out.println("I would like to...");
        while(action != quit) {
            System.out.println(settings);
            action = scanner.nextInt();
            if(action == quit)
                break;
            if(action > numOfChildren) {
                System.out.println("Sorry, that is not a valid command. Please try again.");
                continue;
            }
            this.getChild(action-1).initiateAction();
        }
        // User quits
        System.out.println("See you later!");
        System.exit(0);
    }

    public static void main(String[] args) {
        Home home = new Home();
        // TODO: (Eventually) person detected
        home.initiateAction();
        home.scanner.close();
    }
}
