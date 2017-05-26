package com.ilyaMalgin.tetris.controllers;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.openal.WaveData;

public class AudioController {

    private static Sound drop, rotate, move;
    private static Sound line1, line2, line3, line4;
    private static boolean soundsEnabled = true;
    private static int music;

    static {
        try {
            drop = new Sound(AudioController.class.getResource("/audio/drop.ogg"));
            rotate = new Sound(AudioController.class.getResource("/audio/rotate.ogg"));
            move = new Sound(AudioController.class.getResource("/audio/move.ogg"));
            line1 = new Sound(AudioController.class.getResource("/audio/line1.ogg"));
            line2 = new Sound(AudioController.class.getResource("/audio/line2.ogg"));
            line3 = new Sound(AudioController.class.getResource("/audio/line3.ogg"));
            line4 = new Sound(AudioController.class.getResource("/audio/line4.ogg"));

            WaveData musicData = WaveData.create(AudioController.class.getResource("/audio/music.wav"));
            int musicBuffer = AL10.alGenBuffers();
            AL10.alBufferData(musicBuffer, musicData.format, musicData.data, musicData.samplerate);
            musicData.dispose();
            music = AL10.alGenSources();
            AL10.alSourcei(music, AL10.AL_BUFFER, musicBuffer);
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
        AL10.alSourcei(music, AL10.AL_LOOPING, 1);
        AL10.alSourcePlay(music);
    }

    public static void pauseState(boolean paused) {
        AL10.alSourcef(music, AL10.AL_GAIN, paused ? 0.4f : 1.0f);
    }

    public static void stopMusic() {
        AL10.alSourceStop(music);
    }

    public static void setSoundsEnabled(boolean soundsEnabled) {
        AudioController.soundsEnabled = soundsEnabled;
    }

    public static void finish() {
        AudioController.stopMusic();
        AL.destroy();
    }
}
