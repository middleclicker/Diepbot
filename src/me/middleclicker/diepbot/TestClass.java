package me.middleclicker.diepbot;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class TestClass {
    public static int width = 1919, height = 965;
    public static Point startingPoint = new Point(1, 73);
    public static void main(String[] args) throws AWTException, IOException, InterruptedException {
        Main.sleep(5);
        BufferedImage original = new Robot().createScreenCapture(new Rectangle(startingPoint, new Dimension(width, height)));
        Main.saveBufferedImage(original, "deathscreen", "testimages/", "png");
    }
}
