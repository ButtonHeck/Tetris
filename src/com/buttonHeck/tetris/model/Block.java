package com.buttonHeck.tetris.model;

import com.buttonHeck.tetris.window.game.GameWindow;
import com.buttonHeck.tetris.window.game.Renderer;

import java.awt.*;
import java.awt.image.BufferedImage;

class Block {

    /*
    glob == global (coordinate of a block on map)
    rel == relative (coordinate of a block relative to the block with [0;0] coordinates which doesn't change coordinate
        when a shape is being turned)
     */
    private int globX, globY, relX, relY;
    private com.buttonHeck.tetris.model.Shape parent;
    private BufferedImage image;

    Block(int globalX, int globalY) {
        this.globX = globalX;
        this.globY = globalY;
        relX = globalX;
        relY = globalY;
    }

    void setBlockImage(BufferedImage image) {
        this.image = image;
    }

    void render(Graphics g, boolean isCurrent) {
        g.drawImage(image, isCurrent ?
                        globX * GameWindow.BLOCK_SIZE :
                        Renderer.getScreenWidth() + getXOffset(),
                globY * GameWindow.BLOCK_SIZE + (isCurrent ? 0 : 60),
                null);
    }

    private int getXOffset() {
        int xOffset = (relX + (parent.getBlockPattern() <= 4 ? 1 : 2)) * GameWindow.BLOCK_SIZE;
        xOffset += parent.getBlockPattern() == 1 ? 30 : 10;
        return xOffset;
    }

    void relocate(int dx, int dy) {
        globX += dx;
        globY += dy;
        relX += dx;
        relY += dy;
    }

    //Getters and Setters

    int getGlobX() {
        return globX;
    }

    void setGlobX(int globX) {
        this.globX = globX;
    }

    int getGlobY() {
        return globY;
    }

    void setGlobY(int globY) {
        this.globY = globY;
    }

    void setParent(com.buttonHeck.tetris.model.Shape parent) {
        this.parent = parent;
    }

    int getRelY() {
        return relY;
    }

    int getRelX() {
        return relX;
    }
}
