public class Test {

    public static void main(String[] args) {
        Calendar calendar = new Calendar();
        String someDate = "11-04-2002";
        String someStartTime = "00:00";
        String someDuration = "1000";
        String someEvent = "Birthday";
        calendar.addEvent(someDate, someStartTime, someDuration, someEvent);
        System.out.println(calendar.getTime(someEvent, someDuration));

    }
}
