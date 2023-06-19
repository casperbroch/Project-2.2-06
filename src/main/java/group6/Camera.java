package group6;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.util.ImageUtils;

public class Camera {
    
    public void takePicture(String name) {
        Webcam webcam = Webcam.getDefault();
        
        webcam.open();

        BufferedImage image = webcam.getImage();
        String formatted = "src/main/java/group6/Python/faces/"+name+".jpg";
        try {
            ImageIO.write(image, ImageUtils.FORMAT_JPG, new File(formatted));
        } catch (IOException e) {
            e.printStackTrace();
        }
        webcam.close();
    }

}
