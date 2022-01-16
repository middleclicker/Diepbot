package me.middleclicker.diepaimbot;

import me.middleclicker.diepaimbot.util.Helpers;
import me.middleclicker.diepaimbot.util.Helpers.ThreadManager;
import org.opencv.core.Core;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

import static me.middleclicker.diepaimbot.Core.*;

public class Main {

    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
    public static void main(String[] args) throws AWTException, IOException, InterruptedException {
        sendUserMessages();
        Helpers.sleep(5000);
        doBuild();

        ThreadManager threadManager = new ThreadManager();

        while (true) {
            // BufferedImage original = ImageIO.read(new File("testimages/upgradeCheck.png"));
            BufferedImage original = new Robot().createScreenCapture(new Rectangle(Settings.startingPoint, new Dimension(Settings.width, Settings.height)));

            doSmartAim(original);

            ExecutorService upgradeThread = threadManager.getThreadByName("upgradeThread");
            upgradeThread.execute(() -> {
                try {
                    if (Settings.doTankUpgrade) {
                        doUpgradeTank(original);
                    }
                } catch (AWTException | InterruptedException e) {
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
