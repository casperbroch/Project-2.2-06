import java.util.Scanner;

public class Interactor {
    // TODO: Restructure as tree
    static String home = "C: Access my calendar.\nW: Access the weather.\nQ: Quit.";
    static String calendar_home = "N: New event.\nF: Find event.\nQ: Quit.";

    Interactor() {
        Calendar calendar = new Calendar();
    }

    public static void main(String[] args) {
        Interactor interactor = new Interactor();
        String action = "";
        // Predefined skills the program has
        Skills skills = new Skills();
        Scanner scanner = new Scanner(System.in);
        // Initiates conversation with user
        System.out.println("Hello! How may I help you today?");
        System.out.println();
        // Keeps going as long as user does not want to quit
        while(!action.equalsIgnoreCase("Q")) {
            System.out.println("I would like to (press):"); // TODO: Make this more "natural" --> String search in a sentence instead
            System.out.println(home);
            action = scanner.nextLine();
            action = action.toUpperCase();
            // TODO: Account for typos; tries to match with "high enough" number of mutual letters
            if(skills.containsKey(action)) {
                System.out.println(skills.get(action)); // TODO: Expand on




                System.out.println("What else can I help you with?");
            } else {
                System.out.println("I'm sorry, but I do not know that command yet. Please try again.");
            }
        }

        // TODO: Actually define these skills and create a tree structure (add "Return to homepage" option)

        scanner.close();
    }

    private void getCalendar(Scanner scanner) {
        System.out.println("What would you like to do with your calendar? (Press)");
        System.out.println(calendar_home);
        String action = scanner.nextLine();
        action = action.toUpperCase();
        while(!action.equalsIgnoreCase("Q")) {

            System.out.println("What else can I help with?");
            System.out.println(calendar_home);
        }
        System.out.println("Exiting calendar...");
    }
}
