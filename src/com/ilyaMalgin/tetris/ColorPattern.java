package com.ilyaMalgin.tetris;

import java.awt.*;

public class ColorPattern {

    public final Color main, darker, brighter;

    private ColorPattern(Color main, Color darker, Color brighter) {
        this.main = main;
        this.darker = darker;
        this.brighter = brighter;
    }

    public static final ColorPattern L_MODEL = new ColorPattern(new Color(210, 210, 50),
            new Color(150, 120, 0),
            new Color(240, 240, 140));

    public static final ColorPattern T_MODEL = new ColorPattern(new Color(250, 100, 100),
            new Color(150, 50, 50),
            new Color(240, 140, 140));

    public static final ColorPattern I_MODEL = new ColorPattern(new Color(100, 100, 250),
            new Color(50, 50, 150),
            new Color(100, 150, 250));

    public static final ColorPattern CUBE_MODEL = new ColorPattern(new Color(125, 140, 125),
            new Color(80, 90, 80),
            new Color(190, 210, 190));

    public static final ColorPattern Z_MODEL = new ColorPattern(new Color(100, 250, 100),
            new Color(50, 150, 50),
            new Color(140, 240, 140));
}
