package me.middleclicker.diepbot;

import java.awt.*;

/*
Color List (RGB):
    Square: 255,232,105 Value: 10
    Triangle: 252,118,119 Value: 25
    Pentagon: 118,141,252 Value: 130
    Crasher: 241,119,221 Value: 15

    Stat Upgrade Checker: 108,150,240 or 102,144,234
    Respawn Checker: 173, 173, 173

    Team Colors:
        Red: 241,78,84
        Blue: 0,178,225
        Purple: 191,127,245
        Green: 0,225,110
*/

// User Settings, change accordingly
public class Settings {
    public static int width = 1919, height = 965;
    public static Point startingPoint = new Point(1, 73);

    // The variables below are relative to the screenshot taken by dimensions above, so use the Screenshot.java class to take a screenshot of that area then use paint to find the coordinates
    public static Point statUpgradeCheck = new Point(210, 831); // Use the coordinates for "Bullet Speed" bar, where the blue is at
    public static Point respawnCheck = new Point(1790, 20); // Use the coordinates for the upper part (grey) part of the Copy Part Link button]

    // Settings
    public static String mode = "4 Teams"; // Modes: "4 Teams"
    public static int[] build = new int[]{0,0,0,7,7,7,7,5}; // Pure glass build
    public static String buildMode = "Prioritize Bullet Stats"; // Bullet Mode
    public static boolean doBuild = false;

    // Very buggy features
    public static boolean autoRespawn = false; // Suggest turning this off :)
    public static String tankName = "Middleclicker"; // Only applies if auto respawn is enabled
}
