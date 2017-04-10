package com.ilyaMalgin.tetris;

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

    public void render(Graphics g) {
        g.drawImage(image, getGlobX() * Game.BLOCK_SIZE, getGlobY() * Game.BLOCK_SIZE, null);
    }

    public void relocate(int dx1, int dy1, int dx2, int dy2) {
        setGlobX(getGlobX() + dx1 + dx2);
        setGlobY(getGlobY() + dy1 + dy2);
        int newRelX = getRelX() + dx1 + dx2;
        int newRelY = getRelY() + dy1 + dy2;
        setRelX(newRelX);
        setRelY(newRelY);
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

    public Shape getParent() {
        return parent;
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

    public void setRelY(int relY) {
        this.relY = relY;
    }

    public void setRelX(int relX) {
        this.relX = relX;
    }
}
