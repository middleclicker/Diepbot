package me.middleclicker.diepaimbot.user;

import me.middleclicker.diepaimbot.Settings;
import me.middleclicker.diepaimbot.util.Helpers;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Screenshot {
    // Run this class to take a screenshot. Gives you 5 seconds to prepare.
    public static int width = Settings.width, height = Settings.height;
    public static Point startingPoint = new Point(1, 73);
    public static void main(String[] args) throws AWTException, IOException, InterruptedException {
        Helpers.sleep(5000);
        BufferedImage original = new Robot().createScreenCapture(new Rectangle(startingPoint, new Dimension(width, height)));
        Helpers.saveBufferedImage(original, "screenshot", "testimages/", "png");
    }
}
