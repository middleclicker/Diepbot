package me.middleclicker.diepbot;

import java.util.ArrayList;

public class TestClass {
    public static int[] build = new int[]{0,0,0,7,7,7,7,5}; // Pure glass build
    public static String buildMode = "Prioritize Bullet Stats";
    public static void main(String[] args) {
        ArrayList<Integer> keystrokes = generateBuildKeystrokes(build, buildMode);
        System.out.println(keystrokes);
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
}
