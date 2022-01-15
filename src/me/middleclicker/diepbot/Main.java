package me.middleclicker.diepbot;

import me.middleclicker.diepbot.util.Helpers.ThreadManager;
import org.opencv.core.Core;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

import static me.middleclicker.diepbot.Core.*;

public class Main {

    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
    public static void main(String[] args) throws AWTException, IOException {
        sendUserMessages();

        ThreadManager threadManager = new ThreadManager();

        while (true) {
            // BufferedImage original = ImageIO.read(new File("testimages/screenshot.png"));
            BufferedImage original = new Robot().createScreenCapture(new Rectangle(Settings.startingPoint, new Dimension(Settings.width, Settings.height)));

            doSmartAim(original);

            ExecutorService buildThread = threadManager.getThreadByName("buildThread");
            buildThread.execute(() -> {
                try {
                    if (Settings.doBuild) {
                        doBuild(original);
                    }
                } catch (AWTException e) {
                    e.printStackTrace();
                }
            });

            ExecutorService deathThread = threadManager.getThreadByName("deathThread");
            deathThread.execute(() -> {
                try {
                    processDeath(original);
                } catch (AWTException e) {
                    e.printStackTrace();
                }
            });
        }
    }

}
