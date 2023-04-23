package com.mda;

import java.io.File;
import java.io.IOException;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import com.github.sarxos.webcam.WebcamResolution;

// Tutorial by Genuine Coder on YouTube

public class UserDetection {
    static int[][] img_old;
    static int[][] img_new;
    public static void main(String[] args) throws InterruptedException {
        Webcam webcam = Webcam.getDefault();

        webcam.addWebcamListener(new WebcamListener() {

            @Override
            public void webcamOpen(WebcamEvent we) {
                System.out.println("Webcam Opened");
            }

            @Override
            public void webcamClosed(WebcamEvent we) {
                System.out.println("Webcam Closed");

            }

            @Override
            public void webcamDisposed(WebcamEvent we) {
                System.out.println("Webcam Disposed");

            }

            @Override
            public void webcamImageObtained(WebcamEvent we) {
                System.out.println("Webcam Image Obtained");

            }
            
        });

        // Getting maximum dimensions supported by camera
        // for(Dimension supportedSize: webcam.getViewSizes()) {
        //     System.out.println(supportedSize.toString());
        // }

        webcam.setViewSize(new Dimension(640, 480));
        webcam.setViewSize(WebcamResolution.VGA.getSize());

        webcam.open();

        try {
            while(true) {
                // Takes an image every second
                ImageIO.write(webcam.getImage(), "PNG", new File("core/src/main/java/com/mda/Images/input_new.png"));
                BufferedImage image = ImageIO.read(new File("core/src/main/java/com/mda/Images/input_new.png"));
                // Replacing previous image with new image
                if(img_new != null) {
                    img_old = img_new;
                }
                img_new = new int[640][480];
                for(int i = 0; i < img_new.length; i++) {
                    for(int j = 0; j < img_new[0].length; j++) {
                        img_new[i][j] = image.getRGB(i, j);
                    }
                }
                Thread.sleep(1000); // Can remove depending on processing speed
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Next: https://www.youtube.com/watch?v=RkzfFGP60fw&list=PLhs1urmduZ28_IFafEsXNq3fjdqXLfpuL&index=3
    }
}
