package group6;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class App extends Application {

    private static Controller controller;
    public static final String TEXTPATH = "src/main/java/group6/Python/Connection.txt";
    
    public static String name = "";

    @Override
    public void start(Stage stage) throws Exception {
        long start = System.currentTimeMillis();
        long end = System.currentTimeMillis();

        String directoryPath = "src/main/java/group6/Python/faces";
        String[] whitelist = getImageNames(directoryPath);

        boolean notfound=true;
        while(notfound) {
            end = System.currentTimeMillis();
   
            if((end-start)>=1000) {
                start = System.currentTimeMillis();

                File file = new File("src/main/java/group6/Python/Connection.txt");
                Scanner sc = new Scanner(file);
                String data = "";
                while (sc.hasNextLine()) {
                    data = sc.nextLine();
                }
                if(checkName(whitelist, data)) {
                    System.out.println("Person found! Running GUI...");
                    name = data;
                    notfound=false;

                } else if(data.equalsIgnoreCase("loading")){
                    System.out.println("Hold on tight, Python is loading & looking for a person...");
                } else {
                    System.out.println("No person not found! Looking for a person...");
                }
                sc.close();
            } 
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("App.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);        

        controller = loader.getController();

        Image image = new Image(getClass().getResourceAsStream("icon.png"));
        stage.getIcons().add(image);        
        stage.setScene(scene);
        stage.setTitle("Multi-Modal Digital Assistant");
        stage.show();
    }

    public static Controller getController() {
        return controller;
    }

    public boolean checkName(String[] whitelist, String name) {
        for(int i=0; i<whitelist.length; i++) {
            if(whitelist[i].equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static String[] getImageNames(String directoryPath) {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();
        
        List<String> whitelist = new ArrayList<>();
        
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    String fileName = file.getName();
                    if (isImageFile(fileName)) {
                        String imageName = getFileNameWithoutExtension(fileName);
                        whitelist.add(imageName);
                    }
                }
            }
        }

        whitelist.add("unknown");
        
        return whitelist.toArray(new String[0]);
    }
    
    private static boolean isImageFile(String fileName) {
        return fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".jpeg");
    }
    
    private static String getFileNameWithoutExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            return fileName.substring(0, dotIndex);
        }
        return fileName;
    }
}
