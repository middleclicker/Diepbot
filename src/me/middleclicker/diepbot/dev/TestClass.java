package me.middleclicker.diepbot.dev;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TestClass {
    public static int width = 1919, height = 965;
    public static Point startingPoint = new Point(1, 73);
    public static void main(String[] args) throws AWTException, IOException, InterruptedException {
        BufferedImage square = ImageIO.read(new File("testimages/image.png"));
    }
}
