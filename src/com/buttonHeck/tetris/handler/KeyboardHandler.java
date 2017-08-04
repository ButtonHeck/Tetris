package com.buttonHeck.tetris.handler;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyboardHandler extends KeyAdapter {

    final private int ESCAPE = 0, SPACE = 1, PAUSE = 2, W = 3, S = 4, A = 5, D = 6, SOME_KEY_PRESSED = 7;

    private boolean keys[] = new boolean[8];

    @Override
    public void keyPressed(KeyEvent e) {
        keys[SOME_KEY_PRESSED] = true;
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            keys[ESCAPE] = true;
        if (e.getKeyCode() == KeyEvent.VK_SPACE)
            keys[SPACE] = true;
        if (e.getKeyCode() == KeyEvent.VK_P)
            keys[PAUSE] = true;
        if (e.getKeyCode() == KeyEvent.VK_W)
            keys[W] = true;
        if (e.getKeyCode() == KeyEvent.VK_S)
            keys[S] = true;
        if (e.getKeyCode() == KeyEvent.VK_A)
            keys[A] = true;
        if (e.getKeyCode() == KeyEvent.VK_D)
            keys[D] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys[SOME_KEY_PRESSED] = false;
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            keys[ESCAPE] = false;
        if (e.getKeyCode() == KeyEvent.VK_SPACE)
            keys[SPACE] = false;
        if (e.getKeyCode() == KeyEvent.VK_P)
            keys[PAUSE] = false;
        if (e.getKeyCode() == KeyEvent.VK_W)
            keys[W] = false;
        if (e.getKeyCode() == KeyEvent.VK_S)
            keys[S] = false;
        if (e.getKeyCode() == KeyEvent.VK_A)
            keys[A] = false;
        if (e.getKeyCode() == KeyEvent.VK_D)
            keys[D] = false;
    }

    public boolean escPressed() {
        boolean escPressed = keys[ESCAPE];
        keys[ESCAPE] = false;
        return escPressed;
    }

    public boolean dropPressed() {
        boolean dropPressed = keys[SPACE];
        keys[SPACE] = false;
        return dropPressed;
    }

    public boolean pausePressed() {
        boolean pausePressed = keys[PAUSE];
        keys[PAUSE] = false;
        return pausePressed;
    }

    public boolean rotateLPressed() {
        boolean rotateLPressed = keys[W];
        keys[W] = false;
        return rotateLPressed;
    }

    public boolean rotateRPressed() {
        boolean rotateRPressed = keys[S];
        keys[S] = false;
        return rotateRPressed;
    }

    public boolean leftPressed() {
        boolean leftPressed = keys[A];
        keys[A] = false;
        return leftPressed;
    }

    public boolean rightPressed() {
        boolean rightPressed = keys[D];
        keys[D] = false;
        return rightPressed;
    }

    public void markAllEventsHandled() {
        keys[SOME_KEY_PRESSED] = false;
    }

    public boolean allEventsHandled() {
        return !keys[SOME_KEY_PRESSED];
    }
}
