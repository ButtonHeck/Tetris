package com.ilyaMalgin.tetris;

public class Options {

    private static int speed;
    private static int columns = 15;

    public static double getSpeed() {
        return speed;
    }

    public static void setSpeed(int speed) {
        Options.speed = speed;
    }

    public static int getColumns() {
        return columns;
    }

    public static void setColumns(int columns) {
        Options.columns = columns;
    }
}
