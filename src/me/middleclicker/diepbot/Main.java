package me.middleclicker.diepbot;

import org.javatuples.Pair;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.utils.Converters;

import javax.imageio.ImageIO;
import java.awt.Point;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.opencv.highgui.HighGui.imshow;
import static org.opencv.highgui.HighGui.waitKey;
import static org.opencv.imgproc.Imgproc.*;

/*
Square: 255,232,105 Value: 10
Triangle: 252,118,119 Value: 25
Pentagon: 118,141,252 Value: 130
*/
public class Main {
    // Change to what suites your needs
    public static int width = 1919, height = 965;
    public static Point startingPoint = new Point(1, 73);

    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
    public static void main(String[] args) {
        System.out.println("------------Starting bot--------------");
        while (true) {
            try {
                // BufferedImage square = ImageIO.read(new File("testimages/trisquaretest.png"));
                BufferedImage square = new Robot().createScreenCapture(new Rectangle(startingPoint, new Dimension(width, height)));
                BufferedImage triangle = copyImage(square);
                BufferedImage pentagon = copyImage(square);
                for (int y = 0; y < square.getHeight(); y++) {
                    for (int x = 0; x < square.getWidth(); x++) {
                        int  clr   = square.getRGB(x, y);
                        int  red   = (clr & 0x00ff0000) >> 16;
                        int  green = (clr & 0x0000ff00) >> 8;
                        int  blue  =  clr & 0x000000ff;

                        if (red == 255 && green == 232 && blue == 105) {
                            triangle.setRGB(x, y, convertToRGB(0, 0, 0));
                            pentagon.setRGB(x, y, convertToRGB(0, 0, 0));
                        } else if (red == 252 && green == 118 && blue == 119) {
                            square.setRGB(x, y, convertToRGB(0, 0, 0));
                            pentagon.setRGB(x, y, convertToRGB(0, 0, 0));
                        } else if (red == 118 && green == 141 && blue == 252) {
                            square.setRGB(x, y, convertToRGB(0, 0, 0));
                            triangle.setRGB(x, y, convertToRGB(0, 0, 0));
                        } else {
                            square.setRGB(x, y, convertToRGB(0, 0, 0));
                            triangle.setRGB(x, y, convertToRGB(0, 0, 0));
                            pentagon.setRGB(x, y, convertToRGB(0, 0, 0));
                        }
                    }
                }

                HashMap<Point, Pair<Double, Float>> squareHashmap = calcPossibleAimPoints(processImage(img2Mat(square)), 1);
                HashMap<Point, Pair<Double, Float>> triangleHashmap = calcPossibleAimPoints(processImage(img2Mat(triangle)), 2.5f);
                HashMap<Point, Pair<Double, Float>> pentagonHashmap = calcPossibleAimPoints(processImage(img2Mat(pentagon)), 13);

                // Combine all 3 hash maps
                squareHashmap.putAll(triangleHashmap);
                squareHashmap.putAll(pentagonHashmap);

                if (!squareHashmap.isEmpty()) {
                    Map.Entry<Point, Pair<Double, Float>> maxEntry = squareHashmap.entrySet().iterator().next();
                    for (Map.Entry<Point, Pair<Double, Float>> entry : squareHashmap.entrySet()) {
                        if (entry.getValue().getValue1() - entry.getValue().getValue0() > maxEntry.getValue().getValue1() - maxEntry.getValue().getValue0()) {
                            maxEntry = entry;
                        }
                    }

                    new Robot().mouseMove(maxEntry.getKey().x, maxEntry.getKey().y+startingPoint.y);
                    System.out.println(maxEntry);
                }

            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }

    public static HashMap<Point, Pair<Double, Float>> calcPossibleAimPoints(List<List<org.opencv.core.Point>> listoflistsofpoints, float value) {
        //     Location   Distance Value
        HashMap<Point, Pair<Double, Float>> possibleAimPoints = new HashMap<>();

        // Process each contour (to find the center)
        for (int i = 0; i < listoflistsofpoints.size(); i++) {
            MatOfPoint mop = new MatOfPoint();
            mop.fromList(listoflistsofpoints.get(i));
            Moments moments = Imgproc.moments(mop);

            Point centroid = new Point();
            centroid.x = (int) (moments.get_m10() / moments.get_m00());
            centroid.y = (int) (moments.get_m01() / moments.get_m00());

            possibleAimPoints.put(new Point(centroid.x, centroid.y), new Pair(calculateDistance(new Point(width/2, height/2), new Point(centroid.x, centroid.y)), value));
        }

        return possibleAimPoints;
    }

    public static List<List<org.opencv.core.Point>> processImage(Mat image) {
        Mat gray = new Mat();
        Mat blurred = new Mat();

        // Processing the image
        cvtColor(image, gray, COLOR_BGR2GRAY);
        GaussianBlur(gray, blurred, new Size(5, 5), 0);

        // Finding contours
        ArrayList<MatOfPoint> contours = new ArrayList<>();
        Mat heirarchey = new Mat();
        findContours(blurred, contours, heirarchey, RETR_TREE, CHAIN_APPROX_SIMPLE);

        // Extract the points
        List<List<org.opencv.core.Point>> listoflistsofpoints = new ArrayList<>();
        for (int i = 0; i < contours.size(); i++) {
            List<org.opencv.core.Point> tempList = new ArrayList<>();
            Converters.Mat_to_vector_Point(contours.get(i), tempList);
            listoflistsofpoints.add(i, tempList);
        }

        return listoflistsofpoints;
    }

    public static Mat img2Mat(BufferedImage image) {
        image = convertTo3ByteBGRType(image);
        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
        mat.put(0, 0, data);
        return mat;
    }

    private static BufferedImage convertTo3ByteBGRType(BufferedImage image) {
        BufferedImage convertedImage = new BufferedImage(image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_3BYTE_BGR);
        convertedImage.getGraphics().drawImage(image, 0, 0, null);
        return convertedImage;
    }

    public static BufferedImage imageToBufferedImage(Image im) {
        BufferedImage bi = new BufferedImage
                (im.getWidth(null),im.getHeight(null),BufferedImage.TYPE_INT_RGB);
        Graphics bg = bi.getGraphics();
        bg.drawImage(im, 0, 0, null);
        bg.dispose();
        return bi;
    }

    public static int convertToRGB(int r, int g, int b) {
        return ((r&0x0ff)<<16)|((g&0x0ff)<<8)|(b&0x0ff);
    }

    public static void showImageOpencv(String name, Mat img) {
        imshow(name, img);
        waitKey();
    }

    public static BufferedImage copyImage(BufferedImage source){
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }

    public static double calculateDistance(Point from, Point to) {
        return Math.sqrt((from.x-to.x)*(from.x-to.x) + (from.y-to.y)*(from.y-to.y));
    }
}
