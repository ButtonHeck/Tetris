package com.ilyaMalgin.tetris.models;

import com.ilyaMalgin.tetris.Game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Block {

    private int globX, globY, relX, relY;
    private Shape parent;
    private BufferedImage image;

    public Block(int globalX, int globalY) {
        this.globX = globalX;
        this.globY = globalY;
        relX = globalX;
        relY = globalY;
    }

    public void setBlockImage(BufferedImage image) {
        this.image = image;
    }

    public void render(Graphics g, boolean isCurrent) {
        g.drawImage(image,
                isCurrent ? globX * Game.BLOCK_SIZE : Game.GAME_SCREEN_WIDTH + (relX + (parent.getBlockPattern() <= 4 ? 1 : 2)) * Game.BLOCK_SIZE + (parent.getBlockPattern() == 1 ? 30 : 10),
                globY * Game.BLOCK_SIZE + (isCurrent ? 0 : 60),
                null);
    }

    public void relocate(int dx, int dy) {
        globX += dx;
        globY += dy;
        relX += dx;
        relY += dy;
    }

    //Getters and Setters

    public int getGlobX() {
        return globX;
    }

    public void setGlobX(int globX) {
        this.globX = globX;
    }

    public int getGlobY() {
        return globY;
    }

    public void setGlobY(int globY) {
        this.globY = globY;
    }

    public void setParent(Shape parent) {
        this.parent = parent;
    }

    public int getRelY() {
        return relY;
    }

    public int getRelX() {
        return relX;
    }
}
