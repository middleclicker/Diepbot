package me.middleclicker.diepbot.util;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static org.opencv.highgui.HighGui.imshow;
import static org.opencv.highgui.HighGui.waitKey;

public class Helpers {
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

    public static void saveBufferedImage(BufferedImage bufferedImage, String name, String path, String fileExtension) throws IOException {
        File outputfile = new File(path + name+"."+fileExtension);
        ImageIO.write(bufferedImage, fileExtension, outputfile);
    }

    public static void sleep(long time) throws InterruptedException {
        TimeUnit.SECONDS.sleep(time);
    }
}
