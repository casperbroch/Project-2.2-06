package com.mda.cnn;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class face2data {
    public static void main(String[] args) throws IOException {

        String directoryPath = "C:/Users/caspe/Desktop/scaledfaces";
        BufferedWriter writer = new BufferedWriter(new FileWriter("core\\src\\main\\java\\com\\mda\\cnn\\facedata.csv"));

        File directory = new File(directoryPath);
        File[] files = directory.listFiles();
        int iteration = 0;

        int colsize=0;

        if(files != null) {
            for(File file:files) {
                if(file.isFile()) {
                    // GET WIDTH AND HEIGHT OF IMAGE
                    BufferedImage img = ImageIO.read(file);   

                    int width = img.getWidth();
                    int height = img.getHeight();

                    if(width*height != 25600) {
                        System.out.println("problem");
                        continue;
                    }
            
                    int[][] imgdata = new int[width][height];
            
                    for(int y=0; y<height; y++) {
                        for(int x=0; x<width; x++) {
                            imgdata[x][y]= img.getRGB(x, y)& 0xFF;
                        }
                    }


                    for (int i = 0; i < imgdata[0].length; i++) {
                        for (int j = 0; j < imgdata.length; j++) {
                            writer.append(Integer.toString(imgdata[j][i]));
                            writer.append(";");
                        }
                    }            
                    writer.append("1");
                    writer.newLine();

                }

                iteration++;
                if(iteration%100==0) {
                    System.out.println(iteration+ " of face data");
                }
            }
        }      

        System.out.println("converted & written faces data");


        directoryPath = "C:/Users/caspe/Desktop/scalednfaces";
        directory = new File(directoryPath);
        files = directory.listFiles();

        if(files != null) {
            for(File file:files) {
                if(file.isFile()) {
                    // GET WIDTH AND HEIGHT OF IMAGE
                    BufferedImage img = ImageIO.read(file);   

                    int width = img.getWidth();
                    int height = img.getHeight();
            
                    int[][] imgdata = new int[width][height];

                    if(width*height != 25600) {
                        System.out.println("problem");
                        continue;
                    }
            
                    for(int y=0; y<height; y++) {
                        for(int x=0; x<width; x++) {
                            imgdata[x][y]= img.getRGB(x, y)& 0xFF;
                        }
                    }


                    
                    for (int i = 0; i < imgdata[0].length; i++) {
                        for (int j = 0; j < imgdata.length; j++) {
                            writer.append(Integer.toString(imgdata[j][i]));
                            writer.append(";");
                        }
                    }            
                    writer.append("0");
                    writer.newLine();

                }
                iteration++;
                if(iteration%100==0) {
                    System.out.println(iteration+ " of face non-data");
                }
            }
        }     
        
        writer.flush();
        writer.close();

        System.out.println("converted & written non faces data");
    }
}
