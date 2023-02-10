import java.util.HashMap;
import java.util.Map;

public class Calendar {
    // First String is the time: in the format of "DD-MM-YYYY XX:XX for mm minutes"
    // Second String is the actual event: in the format of "Event for mm minutes"
    Map<String, String> calendar_times = new HashMap<String, String>();
    // Same as calendar_times, but index and value are switched
    Map<String, String> calendar_events = new HashMap<String, String>();

    // Empty constructor
    Calendar() {}

    // Idea from https://www.almanac.com/how-find-day-week#:~:text=Take%20the%20last%20two%20digits,digits%20(discard%20any%20remainder).&text=Divide%20the%20sum%20by%207,the%20day%20of%20the%20week!
    private String dayOfWeek(int year, int month, int day) {
        int yearpart = year%100;
        int century = year/100;
        int weekday = (day + (((month+1)*26)/10) + yearpart + (yearpart/4) + (century/4) + (5*century))%7;
        switch(weekday) {
            case 0:
                return("Saturday");
            case 1:
                 return("Sunday");
            case 2:
                return("Monday");
            case 3:
                return("Tuesday");
            case 4:
                return("Wednesday");
            case 5:
                return("Thursday");
            case 6:
                return("Friday");
        }
        return null;
    }

    // TODO: Also make sure somewhere else that these are valid numbers
    private boolean validDateFormat(String date) {
        return date.matches("\\d{2}-\\d{2}-\\d{4}");
    }

    private boolean validTimeFormat(String time) {
        return time.matches("\\d{2}:\\d{2}");
    }

    // TODO: Optional to add more features (ex: repeats)
    // Requires date to be in DD-MM-YYYY format; time to be in XX:XX format
    public boolean addEvent(String date, String startTime, String duration, String eventName) {
        if(!validDateFormat(date) || !validTimeFormat(startTime))
            return false;
        String index = (date + " " + startTime);
        String event = (eventName + " for " + duration + " minutes");
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

    public String getEvent(String date, String startTime) {
        String index = (date + " " + startTime);
        if(calendar_times.containsKey(index)) {
            return calendar_times.get(index);
        }
        return null;
    }

    public String getTime(String eventName, String duration) {
        String event = (eventName + " for " + duration + " minutes");
        if(calendar_events.containsKey(event)) {
            return calendar_events.get(event);
        }
        return null;
    }

    // Date in DD-MM-YYYY format
    public String getDayOfWeek(String date) {
        String[] splitDate = date.split("-");
        int year = Integer. parseInt(splitDate[2]);
        int month = Integer. parseInt(splitDate[1]);
        int day = Integer. parseInt(splitDate[0]);
        return dayOfWeek(year, month, day);
    }
}
