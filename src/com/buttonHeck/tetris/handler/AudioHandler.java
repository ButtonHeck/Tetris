package com.buttonHeck.tetris.handler;

import org.lwjgl.openal.AL;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

public class AudioHandler {

    private static Sound drop, rotate, move;
    private static Sound line1, line2, line3, line4;
    private static Music music;
    private static boolean soundsEnabled = true;

    static {
        try {
            drop = new Sound(AudioHandler.class.getResource("/audio/drop.ogg"));
            rotate = new Sound(AudioHandler.class.getResource("/audio/rotate.ogg"));
            move = new Sound(AudioHandler.class.getResource("/audio/move.ogg"));
            line1 = new Sound(AudioHandler.class.getResource("/audio/line1.ogg"));
            line2 = new Sound(AudioHandler.class.getResource("/audio/line2.ogg"));
            line3 = new Sound(AudioHandler.class.getResource("/audio/line3.ogg"));
            line4 = new Sound(AudioHandler.class.getResource("/audio/line4.ogg"));
            music = new Music(AudioHandler.class.getResource("/audio/music.ogg"));
        } catch (SlickException e) {
            e.printStackTrace();
        }
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

    public static void playMusic() {
        music.loop();
    }

    public static void pauseState(boolean paused) {
        music.setVolume(paused ? 0.4f : 1.0f);
    }

    public static void stopMusic() {
        music.stop();
    }

    public static void setSoundsEnabled(boolean soundsEnabled) {
        AudioHandler.soundsEnabled = soundsEnabled;
    }

    public static void finish() {
        AudioHandler.stopMusic();
        AL.destroy();
    }
}
