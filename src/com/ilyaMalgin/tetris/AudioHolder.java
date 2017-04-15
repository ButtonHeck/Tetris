package com.ilyaMalgin.tetris;

import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

public class AudioHolder {

    public static Music music;
    private static Sound buttonClick, drop, rotate, move;
    private static Sound line1, line2, line3, line4;
    private static boolean soundsEnabled = true;

    static {
        try {
            music = new Music("res/audio/music.wav");
            buttonClick = new Sound("res/audio/buttonClick.wav");
            drop = new Sound("res/audio/drop.wav");
            rotate = new Sound("res/audio/rotate.wav");
            move = new Sound("res/audio/move.wav");

            line1 = new Sound("res/audio/line1.wav");
            line2 = new Sound("res/audio/line2.wav");
            line3 = new Sound("res/audio/line3.wav");
            line4 = new Sound("res/audio/line4.wav");
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    public static void buttonClick() {
        if (soundsEnabled)
            buttonClick.play();
    }

    public static void drop() {
        if (soundsEnabled)
            drop.play((float) Math.random() + 1, 1.0f);
    }

    public static void rotate() {
        if (soundsEnabled)
            rotate.play((float) Math.random() + 1, 1.0f);
    }

    public static void move() {
        if (soundsEnabled)
            move.play((float) Math.random() + 1, 1.0f);
    }

    public static void line(int line) {
        if (!soundsEnabled)
            return;
        switch (line) {
            case 1:
                line1.play();
                break;
            case 2:
                line2.play();
                break;
            case 3:
                line3.play();
                break;
            case 4:
                line4.play();
                break;
        }
    }

    public static boolean isSoundsEnabled() {
        return soundsEnabled;
    }

    public static void setSoundsEnabled(boolean soundsEnabled) {
        AudioHolder.soundsEnabled = soundsEnabled;
    }
}
