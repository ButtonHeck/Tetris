package com.buttonHeck.tetris.window.game;

import com.buttonHeck.tetris.util.Options;

class Clock {

    private long lastUpdateTime = System.nanoTime(), nowTime;
    private double delta;
    private static double timePerUpdate;

    Clock() {
        lastUpdateTime = System.nanoTime();
        delta = 0;
        renewTimePerUpdate();
    }

    static void renewTimePerUpdate() {
        timePerUpdate = 1_000_000_000 / (Options.getSpeed() / 12);
    }

    void update() {
        nowTime = System.nanoTime();
        delta += (nowTime - lastUpdateTime) / timePerUpdate;
        lastUpdateTime = nowTime;
    }

    double secondsTicked() {
        return delta;
    }

    void tick() {
        delta = 0;
    }
}
