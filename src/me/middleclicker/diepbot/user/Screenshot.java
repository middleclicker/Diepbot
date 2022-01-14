package me.middleclicker.diepbot.user;

import me.middleclicker.diepbot.Main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Screenshot {
    // Run this class to take a screenshot. Gives you 5 seconds to prepare.
    public static int width = Main.width, height = Main.height;
    public static Point startingPoint = new Point(1, 73);
    public static void main(String[] args) throws AWTException, IOException, InterruptedException {
        Main.sleep(5);
        BufferedImage original = new Robot().createScreenCapture(new Rectangle(startingPoint, new Dimension(width, height)));
        Main.saveBufferedImage(original, "screenshot", "", "png");
    }
}
