/*
 * This class gets newly created skills and adds them into to the existing CFG. 
 * Also responsible for generating different ways of asking the same question. 
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class cfg {

    private File file;
    private FileWriter writer;

    public static void main(String[] args) throws IOException {
        cfg test = new cfg();
        test.defaultCFG();
    }

    public cfg() throws IOException{
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")){
            file = new File("src/cfgSkills.txt");
        }
        else if (os.contains("os x")){
            file = new File("src/cfgSkills.txt");
        }   
        writer = new FileWriter(file, true); 
    }

    public void addToCFG(){

    }

    public void defaultCFG() throws IOException{
        writer.write("Rule <S> <ACTION>\n");
        writer.write("Rule <ACTION> <LOCATION> | <SCHEDULE>\n");
        writer.write("Rule <SCHEDULE> Which lectures are there <TIMEEXPRESSION> | <TIMEEXPRESSION> which lectu\n");
        writer.write("Rule <TIMEEXPRESSION> on <DAY> at <TIME> | at <TIME> on <DAY>\n"); 
        writer.write("Rule <TIME> 9 | 12\n"); 
        writer.write("Rule <LOCATION> Where is <ROOM> | How do <PRO> get to <ROOM> | Where is <ROOM> located\n"); 
        writer.write("Rule <PRO> I | you | he | she"); 
        writer.write("Rule <ROOM> DeepSpace | SpaceBox\n"); 
        writer.write("Rule <DAY> Monday | Tuesday | Wednesday | Thursday | Friday | Saturday | Sunday\n"); 
        writer.write("Action <SCHEDULE> * <DAY> Saturday There are no lectures on Saturday\n"); 
        writer.write("Action <SCHEDULE> * <DAY> Monday <TIME> 9 We start the week with math\n"); 
        writer.write("Action <SCHEDULE> * <DAY> Monday <TIME> 12 On Monday noon we have Theoratical Computer S\n"); 
        writer.write("Action <LOCATION> * <ROOM> DeepSpace DeepSpace is the first room after the entrance\n"); 
        writer.write("Action <LOCATION> * <ROOM> is in the first floor\n"); 
        writer.write("Action  I have no idea"); 
    }

    public void removeEmptyLines(){
        try {
            String current = "";
            BufferedReader readerDel1 = new BufferedReader(new FileReader(file));
            StringBuilder stringBuilder1 = new StringBuilder();
            while((current = readerDel1.readLine()) != null) {
                if(current.length() != 0){
                    stringBuilder1.append(current);
                    stringBuilder1.append(System.getProperty("line.separator"));
                }
            }
            readerDel1.close();
            FileWriter writerDel1 = new FileWriter(file);
            writerDel1.write(stringBuilder1.toString());
            writerDel1.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}