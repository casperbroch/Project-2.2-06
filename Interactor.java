import java.util.Locale;
import java.util.Scanner;

public class Interactor {
    static String home = "C: Access my calendar.\nW: Access the weather.\nQ: Quit.";

    public static void main(String[] args) {
        String action = "";
        // Predefined skills the program has
        Skills skills = new Skills();
        Scanner scanner = new Scanner(System.in);
        // Initiates conversation with user
        System.out.println("Hello! How may I help you today?");
        System.out.println();

        while(!action.equalsIgnoreCase("Q")) {
            System.out.println("I would like to (press):");
            System.out.println(home);
            action = scanner.nextLine();
            action = action.toUpperCase();

            if(skills.containsKey(action)) {
                System.out.println(skills.get(action));
                System.out.println("What else can I help you with?");
            } else {
                System.out.println("I'm sorry, but I do not know that command yet. Please try again.");
            }
        }

        // TODO: Actually define these skills and create a tree structure (add "Return to homepage" option)

        scanner.close();
    }
}
