package com.mda.Engine;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Calendar extends Node {

    String settings = "1) Add event.\n2) Remove event.\n3) Find event.\n4) See your events.\n5) Return to homepage." +
            "\n6) Quit.";
    int homepage = 5;
    int quit = 6;
    Scanner scanner;
    Map<String, String> calendar_times = new HashMap<String, String>();
    Map<String, String> calendar_events = new HashMap<String, String>();


    Calendar(Node parent) {
        // Home is the root node; think: homepage
        super("calendar", parent);
        scanner = new Scanner(System.in);
        // TODO: Add children?
    }

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
                case 1:
                    newEvent();
                    break;
                case 2:
                    removeEvent();
                    break;
                case 3:
                    findEvent();
                    break;
                case 4:
                    seeAllEvents();
                    break;
            }
        }
    }

    private void seeAllEvents() {
        for(Map.Entry<String, String> entry : calendar_times.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    private void findEvent() {
        System.out.println("What would you like to know?");
        System.out.println("A) What time my event is.\nB) What event I have at this time.");
        String type = scanner.nextLine();
        type = scanner.nextLine();
        if(type.equalsIgnoreCase("A")) {
            // TODO: Case of multiple events?
            // TODO: Case-sensitive
            System.out.println("What is the event called?");
            String eventName = scanner.nextLine();
            if(calendar_events.containsKey(eventName)) {
                System.out.println("Your event is on " + calendar_events.get(eventName) + ".");
            } else {
                System.out.println("This event does not exist!");
            }
        } else if(type.equalsIgnoreCase("B")) {
            // TODO: Case of multiple events?
            // TODO: Get rid of this duration
            String date = getDate();
            System.out.println("What time does the event start? Please put in the format XX:XX.");
            String startTime = scanner.nextLine();
            startTime = scanner.nextLine();
            System.out.println("How many hours is this event?");
            double duration = scanner.nextDouble();
            String time = (date + " " + startTime + " for " + duration + " hours");
            if(calendar_times.containsKey(time)) {
                System.out.println("You have " + calendar_times.get(time) + " at this time.");
            } else {
                System.out.println("This event does not exist!");
            }
        } else {
            System.out.println("Invalid option! Try again.");
        }
    }

    private void removeEvent() {
        // TODO: Removes all events of this name
        System.out.println("What event(s) would you like to remove?");
        String eventName = scanner.nextLine();
        eventName = scanner.nextLine();
        if(calendar_events.containsKey(eventName)) {
            String time = calendar_events.get(eventName);
            calendar_events.remove(eventName, time);
            calendar_times.remove(time, eventName);
            System.out.println("This event has been removed.");
        } else {
            System.out.println("This event does not exist!");
        }

    }

    private String getDate() {
        // TODO: Make more natural
        System.out.println("What year is your event? Please put in format YYYY.");
        int year = scanner.nextInt();
        while(String.valueOf(year).length() != 4) {
            System.out.println("Please provide a valid year in the format YYYY.");
            year = scanner.nextInt();
        }
        System.out.println("What month is your event? Please put in format MM.");
        int month = scanner.nextInt();
        while(month < 1 || month > 12) {
            System.out.println("Please provide a valid month in the format MM.");
            month = scanner.nextInt();
        }
        System.out.println("What day is your event? Please put in the format DD.");
        int day = scanner.nextInt();
        while(!validDate(year, month, day)) {
            System.out.println("Please provide a valid day in the format DD.");
            day = scanner.nextInt();
        }
        return (month + "-" + day + "-" + year);
    }

    private void newEvent() {
        String date = getDate();

        System.out.println("Great! What time will the event start? Please put in the format XX:XX.");
        String startTime = scanner.nextLine();
        startTime = scanner.nextLine();
        while(!validTimeFormat(startTime)) {
            System.out.println("Please provide a valid time in the format XX:XX.");
            startTime = scanner.nextLine();
        }

        System.out.println("How many hours will the event last?");
        double length = scanner.nextDouble();
        while(length < 0) {
            System.out.println("Please provide a valid number.");
            length = scanner.nextDouble();
        }
        String duration = ("" + length);

        System.out.println("Finally, let's name this event!");
        String eventName = scanner.nextLine();
        eventName = scanner.nextLine();

        addEvent(date, startTime, duration, eventName);
    }

    private boolean validDate(int year, int month, int day) {
        if(day < 1)
            return false;
        if(year % 4 == 0 && month == 2 && day == 29)
            return true;
        if(month == 2 && day > 28)
            return false;
        if(month == 4 || month == 6 || month == 9 || month == 11) {
            return day <= 30;
        } else {
            return day <= 31;
        }
    }

    private boolean validTimeFormat(String time) {
        if(!time.matches("\\d{2}:\\d{2}"))
            return false;
        int hour = Integer.parseInt(time.substring(0, 2));
        int min = Integer.parseInt(time.substring(3));
        return hour >= 0 && hour <= 23 && min >= 0 && min <= 59;
    }

    // TODO: Optional to add more features (ex: repeats)
    // Requires date to be in DD-MM-YYYY format; time to be in XX:XX format
    public boolean addEvent(String date, String startTime, String duration, String eventName) {
        String index = (date + " " + startTime + " for " + duration + " hours");
        String event = (eventName);
        if(calendar_times.containsKey(index) && calendar_times.get(index).equalsIgnoreCase(event)) {
            System.out.println("You already have this event in your calendar!");
            return false;
        } else if(calendar_events.containsKey(event) && calendar_events.get(event).equalsIgnoreCase(index)) {
            System.out.println("You already have this event in your calendar!");
            return false;
        } else {
            calendar_times.put(index, event);
            calendar_events.put(event, index);
            System.out.println("Event added to your calendar!");
            return true;
        }
    }
}
