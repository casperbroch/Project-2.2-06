// package group6;

// import com.google.api.client.auth.oauth2.Credential;
// import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
// import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
// import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
// import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
// import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
// import com.google.api.client.http.javanet.NetHttpTransport;
// import com.google.api.client.json.JsonFactory;
// import com.google.api.client.json.gson.GsonFactory;
// import com.google.api.client.util.DateTime;
// import com.google.api.client.util.store.FileDataStoreFactory;
// import com.google.api.services.calendar.Calendar;
// import com.google.api.services.calendar.CalendarScopes;
// import com.google.api.services.calendar.model.Event;
// import com.google.api.services.calendar.model.EventDateTime;
// import com.google.api.services.calendar.model.Events;

// import java.io.*;
// import java.security.GeneralSecurityException;
// import java.util.ArrayList;
// import java.util.Collections;
// import java.util.List;

// /* Class to connect with a users Google account and perform Google Calendar actions */
// public class CalendarConnection {
//     private static String insert1;
//     private static String insert2;
//     private static String insert3;
//     private static String insert4;
//     private static String insert5;

//     //State of insertion
//     private static int calInsertState;
//     //State of event fetching
//     private boolean calFetchState;
//     //Whether insert is clean or not
//     private static boolean insertIsClean;


//     private static final String APPLICATION_NAME = "Google Calendar";

//     private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

//     private static final String TOKENS_DIRECTORY_PATH = "tokens";

//     private NetHttpTransport HTTP_TRANSPORT;
//     static Calendar service;


//     private static final List<String> SCOPES =
//             Collections.singletonList(CalendarScopes.CALENDAR);
//     private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

//     public CalendarConnection() throws GeneralSecurityException, IOException{
//         insertIsClean =false;
//         calInsertState =0;
//         calFetchState =false;

//         // Build a new authorized API client service.
//         HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//         service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
//                 .setApplicationName(APPLICATION_NAME)
//                 .build();

//     }


//     private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
//             throws IOException {
//         // Load client secrets.
//         InputStream in = CalendarConnection.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
//         if (in == null) {
//             throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
//         }
//         GoogleClientSecrets clientSecrets =
//                 GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

//         // Build flow and trigger user authorization request.
//         GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
//                 HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
//                 .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
//                 .setAccessType("offline")
//                 .build();
//         LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
//         Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
//         //returns an authorized Credential object.
//         return credential;
//     }


//     public ArrayList<String> getNext10Events() throws IOException{
//         ArrayList<String> empty =new ArrayList<String>();
//         // List the next 10 events from the primary calendar.
//         DateTime now = new DateTime(System.currentTimeMillis());
//         Events events = service.events().list("primary")
//                 .setMaxResults(10)
//                 .setTimeMin(now)
//                 .setOrderBy("startTime")
//                 .setSingleEvents(true)
//                 .execute();
//         List<Event> items = events.getItems();
//         if (items.isEmpty()) {

//         } else {
//             for (Event event : items) {
//                 DateTime start = event.getStart().getDateTime();
//                 if (start == null) {
//                     start = event.getStart().getDate();
//                 }

//                 String s = event.getDescription() + " on " + event.getStart().getDate();
//                 String datetime = String.valueOf(event.getStart().getDateTime());
//                 String e =event.getSummary()+" on "+datetime.substring(0,10);
//                 empty.add(e);
//                 //empty.add(String.format("%s (%s)\n", event.getSummary(), start));

//             }
//         }
//         return empty;

//     }

//     public String getEvent(String search) throws IOException {
//         DateTime now = new DateTime(System.currentTimeMillis());
//         Events events = service.events().list("primary")
//                 .setMaxResults(100)
//                 .setTimeMin(now)
//                 .setOrderBy("startTime")
//                 .setSingleEvents(true)
//                 .execute();


//         List<Event> items = events.getItems();

//         for (Event event : items) {

//             if(event.getSummary().equalsIgnoreCase(search)){
//                 String description ="No description";
//                 String location ="No location";

//                 if(event.getDescription()!=null){
//                     description =event.getDescription();
//                 }
//                 if(event.getLocation()!=null){
//                     location =event.getLocation();
//                 }
//                 String datetime = String.valueOf(event.getStart().getDateTime());

//                 //2015-05-28T12:01:00-04:00
//                 return event.getSummary()+" - " +description+", on "+ datetime.substring(0,10)+" @ "+datetime.substring(11,16) +" by location "+location;
//             }
//         }


//         return "No event with that description found...";

//     }


//     public ArrayList<String> getEventsOnDate(String date) throws IOException {
//         DateTime now = new DateTime(System.currentTimeMillis());
//         Events events = service.events().list("primary")
//                 .setMaxResults(100)
//                 .setTimeMin(now)
//                 .setOrderBy("startTime")
//                 .setSingleEvents(true)
//                 .execute();


//         List<Event> items = events.getItems();

//         ArrayList<String> eventsOnDate =new ArrayList<>();

//         try{
//             for (Event event : items) {

//                 String dateTime = String.valueOf(event.getStart().getDateTime());
//                 String eventdate = dateTime.substring(0,10);

//                 if(eventdate.equals(date)){
//                     eventsOnDate.add(event.getSummary()+" on "+ date);
//                     //2015-05-28T12:01:00-04:00
//                     //return event.getSummary()+" on "+ datetime.substring(0,10)+" @ "+datetime.substring(11,16) +" by location "+location;
//                 }
//             }
//             return eventsOnDate;

//         }catch(Exception e){
//             eventsOnDate.add("Formatting was wrong, please try again");
//             return eventsOnDate;
//         }

//     }


//     public boolean deleteEvent(String delete) throws IOException {
//         //this.service.events().delete("primary", eventId).execute();

//         DateTime now = new DateTime(System.currentTimeMillis());
//         Events events = service.events().list("primary")
//                 .setMaxResults(100)
//                 .setTimeMin(now)
//                 .setOrderBy("startTime")
//                 .setSingleEvents(true)
//                 .execute();


//         List<Event> items = events.getItems();

//         if (items.isEmpty()) {
//         } else {
//             for (Event event : items) {

//                 if(event.getSummary().equalsIgnoreCase(delete)){
//                     String description ="No description";

//                     this.service.events().delete("primary", event.getId()).execute();
//                     return true;
//                 }
//             }
//         }
//         return false;
//     }

//     public static String insertEvent() throws IOException{

//         try{
//             if(insertIsClean){
//                 Event event = new Event()
//                         .setSummary(insert1)
//                         .setDescription(insert2);

//                 DateTime startDateTime = new DateTime(insert3+"T"+insert4+":00+02:00");
//                 EventDateTime start = new EventDateTime()
//                         .setDateTime(startDateTime)
//                         .setTimeZone("Europe/Amsterdam");
//                 event.setStart(start);

//                 DateTime endDateTime = new DateTime(insert3+"T"+insert5+":00+02:00");
//                 EventDateTime end = new EventDateTime()
//                         .setDateTime(endDateTime)
//                         .setTimeZone("Europe/Amsterdam");
//                 event.setEnd(end);

//                 String calendarId = "primary";
//                 event = service.events().insert(calendarId, event).execute();
//                 return "Event successfully created!\nName: "+insert1 +"\nDescription: "+insert2 +"\nDate: "+insert3+"\nFrom "+insert4+" till "+insert5+"\nLink: "+event.getHtmlLink();
//             }else{
//                 return "Please try again, formatting was wrong";
//             }
//         }catch (Exception e){
//             return "Please try again, formatting was wrong";
//         }


//     }


//     public void DeleteToken() {
//         File file = new File("tokens\\StoredCredential");

//         try{
//             file.delete();
//         }catch (Exception e){
//             e.printStackTrace();
//         }

//     }

//     public boolean validDate(String date){
//         return true;
//     }


//     public boolean setInsert1(String insert1) {
//         this.insert1 = insert1;
//         return true;
//     }

//     public boolean setInsert2(String insert2) {
//         this.insert2 = insert2;
//         return true;
//     }

//     public boolean setInsert3(String insert3) {
//         this.insert3 = insert3;
//         return true;
//     }

//     public boolean setInsert4(String insert4) {
//         this.insert4 = insert4;
//         return true;
//     }

//     public boolean setInsert5(String insert5) {
//         this.insert5 = insert5;
//         insertIsClean =true;
//         return true;
//     }

//     public void incrementInsertCalState() {
//         if(calInsertState==5){
//             this.calInsertState =0;
//         }
//         this.calInsertState++;
//     }

//     public static int getCalInsertState() {
//         return calInsertState;
//     }

//     public boolean isCalFetchState() {
//         return calFetchState;
//     }

//     public void setCalFetchState(boolean calFetchState) {
//         this.calFetchState = calFetchState;
//     }


//     public static void main(String... args) throws IOException, GeneralSecurityException {
//         CalendarConnection cal =new CalendarConnection();
//         ArrayList<String> e =cal.getNext10Events();
//         //cal.insertEvent();
//         cal.DeleteToken();
//     }
// }