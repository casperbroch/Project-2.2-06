import java.util.Scanner;

public class Calendar2 extends Node {

    String settings = "1) Add event.\n2) Remove event.\n3) Find event.\n4) See your events.\n5) Return to homepage." +
            "\n6) Quit.";
    int homepage = 5;
    int quit = 6;
    Scanner scanner;

    Calendar2(Node parent) {
        // Home is the root node; think: homepage
        super("calendar2", parent);
        scanner = new Scanner(System.in);
        // TODO: Add children?
    }

    // TODO: Implement calendar functions

    public void initiateAction() {
        int action = 0;
        System.out.println("Welcome to the calendar! What would you like to do?");
        System.out.println();

        while(action != quit || action != homepage) {
            System.out.println(settings);
            action = scanner.nextInt();
            if(action == quit) {
                System.out.println("See you later!");
                System.exit(0);
            }
            if(action == homepage) {
                System.out.println("Welcome back to the homepage! What would you like to do?");
                break;
            }
            if(action > 6) {
                System.out.println("Sorry, that is not a valid command. Please try again.");
                continue;
            }
            switch(action) {
                case 1: // add event
                    break;
                case 2: // remove event
                    break;
                case 3: // find event
                    break;
                case 4: // see your events
                    break;
            }
        }
    }
}
