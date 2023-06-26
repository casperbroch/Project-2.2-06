package group6;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/*
    Class to detect the presence of an open hand using OpenCV convex hull generation and defect detection.

    The idea is that if we map a convex hull over a hand, and check for defects, we will get 6 defects this means an open hand is present in the frame.
    Other objects such as a face or partial hand will not give this amount of defects.
*/

public class HandDetection {

    ////Color settings for detecting hand contour against background (skin colour bounds)
    static final int upperR =0;
    static final int upperG =20;
    static final int upperB =70;

    static final int lowerR =20;
    static final int lowerG =255;
    static final int lowerB =255;


    /*
        Method runs until a hand is found in the frame, at which the true boolean will be returned.
        To find a hand the algorithm checks the largest contour for defects, and if the defects equal 6, a hand is found.
     */
    public static boolean checkForHand() throws IOException{
        System.load("C:/Users/schre/OneDrive/Desktop/opencv/build/java/x64/opencv_java470.dll");
        
        Mat webcamImage = new Mat();
        VideoCapture webcam = new VideoCapture(0);
        webcam.set(3, 500);
        webcam.set(4, 500);

        if (!webcam.isOpened()) {
            System.out.println("No webcam");
        }

        while (true) {
            if (webcam.read(webcamImage)) {
                //showFrame(webcamImage);
                Imgproc.cvtColor(webcamImage, webcamImage, Imgproc.COLOR_BGR2RGB);

                Mat skinMask = returnThreshold(webcamImage);
                MatOfPoint handContour = getHandContour(skinMask);

                if (handContour != null) {
                    MatOfInt hullIndices = new MatOfInt();
                    Imgproc.convexHull(handContour, hullIndices);
                    MatOfInt4 defects = new MatOfInt4();
                    Imgproc.convexityDefects(handContour, hullIndices, defects);

                    int numDefects = (int) defects.total();

                    if (numDefects ==6){
                        //return true;
                    }
                    for (int i = 0; i < numDefects; i++) {
                        double[] defect = defects.get(i, 0);
                        double depth = defect[3] / 256.0;

                        if (depth > 20) {
                            Point start = new Point(handContour.get((int) defect[0], 0));
                            Point end = new Point(handContour.get((int) defect[1], 0));
                            Point far = new Point(handContour.get((int) defect[2], 0));

                            Imgproc.line(webcamImage, start, end, new Scalar(0, 255, 0), 2);
                            Imgproc.circle(webcamImage, far, 4, new Scalar(0, 0, 255), -1);
                        }
                    }

                    Imgproc.drawContours(webcamImage, List.of(handContour), 0, new Scalar(255, 0, 0), 2);
                }

                
                showFrame(webcamImage);
                //System.out.println("sasa");
            }
        }
    }

    /*
    Class to show each webcam frame in a JFrame for testing
     */
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
        Returns the threshold for a hand using the color settings
    */
    private static Mat returnThreshold(Mat frame) {
        Mat hsvImage = new Mat();
        Imgproc.cvtColor(frame, hsvImage, Imgproc.COLOR_RGB2HSV);

        Scalar lowerBound = new Scalar(lowerR, lowerG, lowerB);
        Scalar upperBound = new Scalar(upperR, upperG, upperB);

        // Scalar lowerBound = new Scalar(0, 0,0);
        // Scalar upperBound = new Scalar(0,0,0);
        Mat skinMask = new Mat();
        Core.inRange(hsvImage, lowerBound, upperBound, skinMask);

        //Apply gaussian blur
        Mat blurred = new Mat();
        Imgproc.GaussianBlur(skinMask, blurred, new Size(5, 5), 0);

        //Create threshhold
        Mat threshold = new Mat();
        Imgproc.threshold(blurred, threshold, 0, 255, Imgproc.THRESH_BINARY);

        return threshold;
    }

    /*
        Gets the largest contour which may represent a hand
     */
    private static MatOfPoint getHandContour(Mat threshold) {
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(threshold, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        int maxContourArea = 0;
        int maxContourIdx = -1;
        for (int i = 0; i < contours.size(); i++) {
            double contourArea = Imgproc.contourArea(contours.get(i));
            if (contourArea > maxContourArea) {
                maxContourArea = (int) contourArea;
                maxContourIdx = i;
            }
        }

        if (maxContourIdx >= 0) {
            return contours.get(maxContourIdx);
        }

        return null;
    }

////////////////////////
////MAIN FOR TESTING////
////////////////////////

    public static void main(String[] args) throws IOException {
        System.out.println(checkForHand());
    }


}

