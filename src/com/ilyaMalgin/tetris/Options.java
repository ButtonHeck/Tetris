package com.ilyaMalgin.tetris;

public class Options {

    private static int speed;
    private static int columns = 14;
    private static int rows = 8;
    private static boolean showNext = true;

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

    public static void setRows(int rows) {
        Options.rows = rows;
    }

    public static int getRows() {
        return rows;
    }

    public static void setShowNext(boolean showNext) {
        Options.showNext = showNext;
    }

    public static boolean getShowNext() {
        return showNext;
    }
}
