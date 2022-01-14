package me.middleclicker.diepbot;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.javatuples.Pair;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.utils.Converters;

import javax.imageio.ImageIO;
import java.awt.Point;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static org.opencv.highgui.HighGui.imshow;
import static org.opencv.highgui.HighGui.waitKey;
import static org.opencv.imgproc.Imgproc.*;

/*
Color List:
    Square: 255,232,105 Value: 10
    Triangle: 252,118,119 Value: 25
    Pentagon: 118,141,252 Value: 130

    Stat Upgrade Checker: 238,182,144
*/

public class Main {

    // Dependent on display size and other settings
    public static int width = 1919, height = 965;
    public static Point startingPoint = new Point(1, 73);
    public static Point statUpgradeCheck = new Point(209, 754);

    public static String mode = "4 Teams"; // Modes: "4 Teams"
    public static int[] build = new int[]{0,0,0,7,7,7,7,5}; // Pure glass build
    public static String buildMode = "Prioritize Bullet Stats";

    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
    public static void main(String[] args) throws AWTException, IOException, InterruptedException {
        sendUserMessages();

        int statTracker = 0;
        ArrayList<Integer> keystrokes = generateBuildKeystrokes(build, buildMode);

        while (true) {
            // BufferedImage square = ImageIO.read(new File("testimages/trisquaretest.png"));
            BufferedImage original = new Robot().createScreenCapture(new Rectangle(startingPoint, new Dimension(width, height)));

            int  clr   = original.getRGB(statUpgradeCheck.x, statUpgradeCheck.y);
            int  red   = (clr & 0x00ff0000) >> 16;
            int  green = (clr & 0x0000ff00) >> 8;
            int  blue  =  clr & 0x000000ff;
            // System.out.println("R:" + red + " G:" + green + " B:" + blue);
            if (red == 238 && green == 182 && blue == 144 && !keystrokes.isEmpty() && statTracker < 33) {
                // System.out.println("Detected!");
                int keyToPress = keystrokes.get(statTracker)+1;
                Keyboard keyboard = new Keyboard();
                System.out.println(keyToPress);
                keyboard.type((char)(keyToPress+'0'));
                statTracker++;
            }

            doSmartAim(original);
        }
    }

    public static void sendUserMessages() {
        System.out.println("------------Starting bot------------");
        System.out.println("Current Mode: " + mode);
        System.out.println("Current Build: " + Arrays.toString(build));
        System.out.println("Current Build Mode: " + buildMode);
    }

    public static ArrayList<Integer> generateBuildKeystrokes(int[] build, String buildMode) {
        ArrayList<Integer> buildOrder = new ArrayList<>();
        if (buildMode.equals("Prioritize Bullet Stats")) {
            boolean isEmptyBullets = false;
            while (!isEmptyBullets) {
                for (int i = 6; i >= 3; i--) {
                    if (build[i] > 0) {
                        buildOrder.add(i);
                        build[i]--;
                    }
                }
                if (build[3] == 0 && build[4] == 0 && build[5] == 0 && build[6] == 0) {
                    isEmptyBullets = true;
                }
            }

            boolean isEmptyWhole = false;
            while (!isEmptyWhole) {
                for (int i = 7; i >= 0; i--) {
                    if (build[i] > 0) {
                        buildOrder.add(i);
                        build[i]--;
                    }
                }
                if (build[0] == 0 && build[1] == 0 && build[2] == 0 && build[7] == 0) {
                    isEmptyWhole = true;
                }
            }
        }

        return buildOrder;
    }

    public static void doSmartAim(BufferedImage original) throws AWTException {
        HashMap<Point, Pair<Double, Float>> processedHashMap = furtherProcessImage(original);

        if (!processedHashMap.isEmpty()) {
            Map.Entry<Point, Pair<Double, Float>> maxEntry = processedHashMap.entrySet().iterator().next();
            for (Map.Entry<Point, Pair<Double, Float>> entry : processedHashMap.entrySet()) {
                if (entry.getValue().getValue1() - entry.getValue().getValue0() > maxEntry.getValue().getValue1() - maxEntry.getValue().getValue0()) {
                    maxEntry = entry;
                }
            }

            new Robot().mouseMove(maxEntry.getKey().x, maxEntry.getKey().y+startingPoint.y);
        }
    }

    public static HashMap<Point, Pair<Double, Float>> furtherProcessImage(BufferedImage original) {
        BufferedImage square = new BufferedImage(width, height, TYPE_INT_RGB);
        BufferedImage triangle = new BufferedImage(width, height, TYPE_INT_RGB);
        BufferedImage pentagon = new BufferedImage(width, height, TYPE_INT_RGB);
        for (int y = 0; y < original.getHeight(); y++) {
            for (int x = 0; x < original.getWidth(); x++) {
                int  clr   = original.getRGB(x, y);
                int  red   = (clr & 0x00ff0000) >> 16;
                int  green = (clr & 0x0000ff00) >> 8;
                int  blue  =  clr & 0x000000ff;

                if (red == 255 && green == 232 && blue == 105) {
                    square.setRGB(x, y, convertToRGB(255, 232, 105));
                } else if (red == 252 && green == 118 && blue == 119) {
                    triangle.setRGB(x, y, convertToRGB(252, 118, 119));
                } else if (red == 118 && green == 141 && blue == 252) {
                    pentagon.setRGB(x, y, convertToRGB(118, 141, 252));
                }
            }
        }

        HashMap<Point, Pair<Double, Float>> squareHashmap = calcPossibleAimPoints(processImage(img2Mat(square)), 1);
        HashMap<Point, Pair<Double, Float>> triangleHashmap = calcPossibleAimPoints(processImage(img2Mat(triangle)), 2.5f);
        HashMap<Point, Pair<Double, Float>> pentagonHashmap = calcPossibleAimPoints(processImage(img2Mat(pentagon)), 13);

        squareHashmap.putAll(triangleHashmap);
        squareHashmap.putAll(pentagonHashmap);

        return squareHashmap;
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
                (im.getWidth(null),im.getHeight(null), TYPE_INT_RGB);
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

    // Multithreading maybe?
    public static class ThreadManager {

        private final HashMap<String, ExecutorService> threadMap = new HashMap<String, ExecutorService>();

        private ExecutorService findOrSpawnThread(String threadID) {

            if (!threadMap.containsKey(threadID)) {
                BasicThreadFactory factory = new BasicThreadFactory.Builder()
                        .namingPattern(threadID + "-%d")
                        .daemon(true)
                        .priority(Thread.NORM_PRIORITY)
                        .build();
                threadMap.put(threadID, Executors.newSingleThreadExecutor(factory));
            }

            return threadMap.get(threadID);
        }

        public ExecutorService getThreadByName(String name) {
            return findOrSpawnThread(name);
        }
    }


}
