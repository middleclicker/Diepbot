package me.middleclicker.diepbot;

import me.middleclicker.diepbot.util.Keyboard;
import org.javatuples.Triplet;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.utils.Converters;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static me.middleclicker.diepbot.util.Helpers.*;
import static org.opencv.imgproc.Imgproc.*;
import static org.opencv.imgproc.Imgproc.CHAIN_APPROX_SIMPLE;

public class Core {

    public static int statTracker = 0;
    public static boolean dead = false;
    public static Color[] teamColors = new Color[]{new Color(241,78,84), new Color(0,178,225), new Color(191,127,245), new Color(0,225,110)};
    public static ArrayList<Integer> keystrokes = generateBuildKeystrokes(Settings.build, Settings.buildMode);

    public static void sendUserMessages() {
        System.out.println("------------Starting bot------------");
        System.out.println("Current Mode: " + Settings.mode);
        System.out.println("Current Build: " + Arrays.toString(Settings.build));
        System.out.println("Current Build Mode: " + Settings.buildMode);
        System.out.println("Auto Respawn: " + Settings.autoRespawn);
        System.out.println("Tank Name: " + Settings.tankName);
        System.out.println("Auto Build: " + Settings.doBuild);
    }



    public static void doSmartAim(BufferedImage original) throws AWTException {
        HashMap<Point, Triplet<Double, Float, Integer>> processedHashMap = furtherProcessImage(original);

        if (!processedHashMap.isEmpty()) {
            Map.Entry<Point, Triplet<Double, Float, Integer>> maxEntry = processedHashMap.entrySet().iterator().next();
            for (Map.Entry<Point, Triplet<Double, Float, Integer>> entry : processedHashMap.entrySet()) {
                if (entry.getValue().getValue2() + entry.getValue().getValue1() - entry.getValue().getValue0() > entry.getValue().getValue2() + maxEntry.getValue().getValue1() - maxEntry.getValue().getValue0()) {
                    maxEntry = entry;
                }
            }

            new Robot().mouseMove(maxEntry.getKey().x, maxEntry.getKey().y+Settings.startingPoint.y);
        }
    }

    public static void doBuild(BufferedImage original) throws AWTException {
        if (dead) {
            statTracker = 0;
            return;
        }

        int  clr   = original.getRGB(Settings.statUpgradeCheck.x, Settings.statUpgradeCheck.y);
        int  red   = (clr & 0x00ff0000) >> 16;
        int  green = (clr & 0x0000ff00) >> 8;
        int  blue  =  clr & 0x000000ff;
        // System.out.println("R:" + red + " G:" + green + " B:" + blue);
        if (((red == 108 && green == 150 && blue == 240) || (red == 102 && green == 144 && blue == 234)) && !keystrokes.isEmpty() && statTracker < 33) {
            // System.out.println("Detected!");
            int keyToPress = keystrokes.get(statTracker)+1;
            Keyboard keyboard = new Keyboard();
            System.out.println(keyToPress);
            keyboard.type((char)(keyToPress+'0'));
            statTracker++;
        }
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

    public static java.util.List<java.util.List<org.opencv.core.Point>> processImage(Mat image) {
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
        java.util.List<java.util.List<org.opencv.core.Point>> listoflistsofpoints = new ArrayList<>();
        for (int i = 0; i < contours.size(); i++) {
            List<org.opencv.core.Point> tempList = new ArrayList<>();
            Converters.Mat_to_vector_Point(contours.get(i), tempList);
            listoflistsofpoints.add(i, tempList);
        }

        return listoflistsofpoints;
    }

    public static HashMap<Point, Triplet<Double, Float, Integer>> furtherProcessImage(BufferedImage original) {
        BufferedImage square = new BufferedImage(Settings.width, Settings.height, TYPE_INT_RGB);
        BufferedImage triangle = new BufferedImage(Settings.width, Settings.height, TYPE_INT_RGB);
        BufferedImage pentagon = new BufferedImage(Settings.width, Settings.height, TYPE_INT_RGB);
        BufferedImage crasher = new BufferedImage(Settings.width, Settings.height, TYPE_INT_RGB);
        BufferedImage player = new BufferedImage(Settings.width, Settings.height, TYPE_INT_RGB);

        int selfColorInt = teamColors[2].getRGB();
        int  selfred   = (selfColorInt & 0x00ff0000) >> 16;
        int  selfgreen = (selfColorInt & 0x0000ff00) >> 8;
        int  selfblue  =  selfColorInt & 0x000000ff;


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
                } else if (red == 241 && green == 119 && blue == 221) {
                    crasher.setRGB(x, y, convertToRGB(118, 141, 252));
                }

                for (int i = 0; i < 4; i++) {
                    if (selfred == teamColors[i].getRed() && selfblue == teamColors[i].getBlue() && selfgreen == teamColors[i].getGreen()) continue;
                    if (teamColors[i].getRGB() == clr) {
                        // System.out.println("Player detected at " + x + ", " + y + " with color " + (i+1));
                        player.setRGB(x, y, teamColors[i].getRGB());
                    }
                }
            }
        }

        HashMap<Point, Triplet<Double, Float, Integer>> squareHashmap = calcPossibleAimPoints(processImage(img2Mat(square)), 1, 1, false);
        HashMap<Point, Triplet<Double, Float, Integer>> triangleHashmap = calcPossibleAimPoints(processImage(img2Mat(triangle)), 2.5f, 1, false);
        HashMap<Point, Triplet<Double, Float, Integer>> pentagonHashmap = calcPossibleAimPoints(processImage(img2Mat(pentagon)), 13, 1, false);
        HashMap<Point, Triplet<Double, Float, Integer>> crasherHashmap = calcPossibleAimPoints(processImage(img2Mat(crasher)), 13, 2, false);
        HashMap<Point, Triplet<Double, Float, Integer>> playerHashmap = calcPossibleAimPoints(processImage(img2Mat(player)), 100, 10, true);

        squareHashmap.putAll(triangleHashmap);
        squareHashmap.putAll(pentagonHashmap);
        squareHashmap.putAll(crasherHashmap);
        squareHashmap.putAll(playerHashmap);

        return squareHashmap;
    }

    public static HashMap<Point, Triplet<Double, Float, Integer>> calcPossibleAimPoints(java.util.List<java.util.List<org.opencv.core.Point>> listoflistsofpoints, float value, int priority, boolean isPlayerMap) {
        //     Location   Distance     Value  Priority
        HashMap<Point, Triplet<Double, Float, Integer>> possibleAimPoints = new HashMap<>();

        // Process each contour (to find the center)
        for (java.util.List<org.opencv.core.Point> listoflistsofpoint : listoflistsofpoints) {
            MatOfPoint mop = new MatOfPoint();
            mop.fromList(listoflistsofpoint);
            Moments moments = Imgproc.moments(mop);

            Point centroid = new Point();
            centroid.x = (int) (moments.get_m10() / moments.get_m00());
            centroid.y = (int) (moments.get_m01() / moments.get_m00());

            if (isPlayerMap) {
                if (moments.m00 < 1470) {
                    continue;
                }
            }

            // System.out.println(area + " Priority: " + priority);

            possibleAimPoints.put(new Point(centroid.x, centroid.y), new Triplet(calculateDistance(new Point(Settings.width / 2, Settings.height / 2), new Point(centroid.x, centroid.y)), value, priority));
        }

        return possibleAimPoints;
    }

    public static void processDeath(BufferedImage original) throws AWTException {
        int  clr   = original.getRGB(Settings.respawnCheck.x, Settings.respawnCheck.y);
        int  red   = (clr & 0x00ff0000) >> 16;
        int  green = (clr & 0x0000ff00) >> 8;
        int  blue  =  clr & 0x000000ff;

        if (red == 173 && green == 173 && blue == 173) {
            dead = true;
            if (Settings.autoRespawn) {
                Keyboard keyboard = new Keyboard();
                keyboard.type('\n'); // Enter
                keyboard.type(Settings.tankName);
            }
        } else {
            dead = false;
        }
    }

}
