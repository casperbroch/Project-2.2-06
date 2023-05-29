package group6;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Motion {
    static Mat cap;
    static Mat initial;
    static Mat gray;
    static Mat nextCap;
    static Mat thresh;
    static List<MatOfPoint> contours;

    static VideoCapture webcam;

    // Class Initializer
    public Motion() {
        // Load library
        //System.loadLibrary("");
        System.load("/opencv_java470.dll");

        cap = new Mat();
        initial = new Mat();
        gray = new Mat();
        nextCap = new Mat();
        thresh = new Mat();
        contours = new ArrayList<MatOfPoint>();

        webcam = new VideoCapture();
    }


    /*
    Method to show a simple image.

    Input: Image in encoded Mat format
    Output: JFrame with JPEG of Mat image is shown

    * */
    public static void showFrame(Mat img) throws IOException {
        // Resize image
        Imgproc.resize(img, img, new Size(500, 500));
        // Holds encoded image data
        MatOfByte mob = new MatOfByte();
        // Converts to JPEG and encoded image data extracted and stored
        Imgcodecs.imencode(".jpg", img, mob);
        // Encoded image data extracted and stored
        byte[] byteArray = mob.toArray();
        // New empty buffer
        BufferedImage buff = null;
        // Wrapped in inpout stream
        InputStream in = new ByteArrayInputStream(byteArray);
        // Read input
        buff = ImageIO.read(in);

        // New JFrame
        JFrame frame = new JFrame();
        // Add panel with image to JFrame
        frame.getContentPane().add(new JLabel(new ImageIcon(buff)));
        frame.pack();
        frame.setVisible(true);

    }

    /*
    Method to detect motion. Method will run until motion is detected, then true will be returned and the home greeting for the bot can be presented.

    Input: Integer representing the pixel threshold of a contour
    Output: When threshold is reached, a boolean is returned

    */
    public static boolean isMotionDetected(int pixelthresh) throws IOException {
        //System.loadLibrary("opencv_java470");
        webcam.open(0); //open webcam

        // Video size 500x500 =250k pixels (good to know for threshhold)
        webcam.set(3, 500);
        webcam.set(4, 500);

        webcam.read(cap);
        //convert to grayscale and set the first frame
        Imgproc.cvtColor(cap, initial, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(initial, initial, new Size(21, 21), 0);

        while(true) {
            webcam.read(cap);
            // Make image black and white, easier to process
            Imgproc.cvtColor(cap, gray, Imgproc.COLOR_BGR2GRAY);
            // Add gaussian blur, this removes the detection of minute motions (contours are grouped larger)
            Imgproc.GaussianBlur(gray, gray, new Size(21, 21), 0);

            // Compute difference between first frame and current frame
            Core.absdiff(initial, gray, nextCap);
            Imgproc.threshold(nextCap, thresh, 25, 255, Imgproc.THRESH_BINARY);

            Imgproc.dilate(thresh, thresh, new Mat(), new Point(-1, -1), 2);

            //We collect all the contours in the image (representing motion) into an array
            Imgproc.findContours(thresh, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

            // Show the contours for demo/testing
            showFrame(thresh);

            // Check all contours and if any of them is too large, motion is detected
            for(int i=0; i < contours.size(); i++) {
                if(Imgproc.contourArea(contours.get(i)) > pixelthresh) {
                    // Kill webcam, return true
                    webcam.release();
                    return true;
                }
            }
        }
        //return false;
    }

    // MAIN FOR TESTING
    public static void main(String args[]) throws IOException {
        Motion m =new Motion();
        System.out.println(m.isMotionDetected(200000));
    }
}






























