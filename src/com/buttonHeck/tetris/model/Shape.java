package com.buttonHeck.tetris.model;

import com.buttonHeck.tetris.handler.ImageHandler;
import com.buttonHeck.tetris.window.game.GameWindow;
import com.buttonHeck.tetris.handler.AudioHandler;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Shape {

    private static int spawnX = GameWindow.getGridWidth() / 2 - 1;
    private int gridX, gridY;
    private ArrayList<Block> blocks;
    private final boolean rotatable;
    private volatile boolean moveEnded = false;
    private final int blockPattern;
    private final BufferedImage[] images;
    private static int imageSeed = 0;

    private static final Shape L_SHAPE = new Shape(true, 0, ImageHandler.L_SHAPE_IMAGES);
    private static final Shape T_SHAPE = new Shape(true, 1, ImageHandler.T_SHAPE_IMAGES);
    private static final Shape I_SHAPE = new Shape(true, 2, ImageHandler.I_SHAPE_IMAGES);
    private static final Shape CUBE_SHAPE = new Shape(false, 3, ImageHandler.CUBE_SHAPE_IMAGES);
    private static final Shape Z_SHAPE = new Shape(true, 4, ImageHandler.Z_SHAPE_IMAGES);
    private static final Shape L_MIRROR_SHAPE = new Shape(true, 5, ImageHandler.L_SHAPE_IMAGES);
    private static final Shape Z_MIRROR_SHAPE = new Shape(true, 6, ImageHandler.Z_SHAPE_IMAGES);

    private static final Shape[] shapes = new Shape[]
            {L_SHAPE, T_SHAPE, I_SHAPE, CUBE_SHAPE, Z_SHAPE, L_MIRROR_SHAPE, Z_MIRROR_SHAPE};

    private Shape(boolean rotatable, int blockPattern, BufferedImage[] images) {
        gridX = blockPattern <= 4 ? spawnX : spawnX + 1;
        gridY = 1;
        this.rotatable = rotatable;
        this.blockPattern = blockPattern;
        this.images = images;
        initializeComponentBlocks(blockPattern, images);
    }

    private void initializeComponentBlocks(int blockPattern, BufferedImage[] images) {
        blocks = new ArrayList<>();
        Collections.addAll(blocks, BlockPatterns.getBlocks(blockPattern));
        blocks.forEach(e -> {
            e.setParent(this);
            e.setBlockImage(images[Math.abs(++imageSeed % 3)]);
            e.setGlobX(e.getGlobX() + gridX);
            e.setGlobY(e.getGlobY() + gridY);
        });
    }

    public boolean tryMoveTo(int x, int y) {
        if (movementPossibleTo(x, y)) {
            moveTo(x, y);
            return true;
        } else
            return false;
    }

    private boolean movementPossibleTo(int x, int y) {
        return !sideBorderReached(x) && !lowBorderReached(y);
    }

    private boolean sideBorderReached(int x) {
        return blocks.stream().anyMatch(block -> block.getGlobX() + x < 0
                || block.getGlobX() + x > GameWindow.getGridWidth() - 1);
    }

    private boolean lowBorderReached(int y) {
        if (blocks.stream().anyMatch(block -> block.getGlobY() < 0
                || block.getGlobY() + y > GameWindow.getGridHeight() - 1)) {
            moveEnded = true;
            return true;
        }
        return false;
    }

    private void moveTo(int x, int y) {
        gridX += x;
        gridY += y;
        blocks.forEach(block -> {
            block.setGlobX(block.getGlobX() + x);
            block.setGlobY(block.getGlobY() + y);
        });
        GameWindow.renewBlocksMap();
        if (GameWindow.blocksCollide()) {
            if (y != 0)
                moveEnded = true;
            moveTo(-x, -y);
        }
    }

    public void drop() {
        while (!GameWindow.blocksCollide() && !lowBorderReached(1) && !moveEnded) {
            moveTo(0, 1);
        }
        AudioHandler.drop();
    }

    public void detachAtRow(int y) {
        blocks.removeIf(block -> block.getGlobY() == y);
        dropSurvivedBlocksHigherThan(y);
    }

    private void dropSurvivedBlocksHigherThan(int y) {
        for (int i = 0; i < blocks.size(); i++) {
            Block temp = blocks.get(i);
            if (temp.getGlobY() < y) {
                temp.setGlobY(temp.getGlobY() + 1);
            }
        }
    }

    public void rotate(boolean left) {
        if (!rotatable || blocks.stream().anyMatch(block -> impossibleToRotate(block, left)))
            return;
        blocks.forEach(block -> block.relocate(dX(block, left, false), dY(block, left, false)));
        GameWindow.renewBlocksMap();
        if (GameWindow.blocksCollide()) { //undo previous rotation
            blocks.forEach(block -> block.relocate(dX(block, left, true), dY(block, left, true)));
            return;
        }
        AudioHandler.rotate();
    }

    private boolean impossibleToRotate(Block block, boolean left) {
        int dx = dX(block, left, false);
        int dy = dY(block, left, false);
        return block.getGlobX() + dx < 0 || block.getGlobX() + dx > GameWindow.getGridWidth() - 1
                || block.getGlobY() + dy < 0 || block.getGlobY() + dy > GameWindow.getGridHeight() - 1;
    }

    private int dX(Block block, boolean left, boolean reverse) {
        return (left ? -block.getRelY() : block.getRelY()) * (reverse ? -1 : 1) - block.getRelX();
    }

    private int dY(Block block, boolean left, boolean reverse) {
        return (left ? block.getRelX() : -block.getRelX()) * (reverse ? -1 : 1) - block.getRelY();
    }

    public void placeOnMap(ArrayList<Integer> map) {
        blocks.forEach(block ->
                map.set(block.getGlobY() * GameWindow.getGridWidth() + block.getGlobX(),
                        map.get(block.getGlobY() * GameWindow.getGridWidth() + block.getGlobX()) + 1));
    }

    public void render(Graphics g, boolean isCurrent) {
        for (int i = 0; i < blocks.size(); i++) {
            blocks.get(i).render(g, isCurrent);
        }
    }

    private Shape copy() {
        return new Shape(this.rotatable, this.blockPattern, this.images);
    }

    public static Shape getRandomShape() {
        return shapes[new Random().nextInt(Shape.shapes.length)].copy();
    }

    public boolean moveEnded() {
        return moveEnded;
    }

    public static void setSpawnX(int spawnX) {
        Shape.spawnX = spawnX;
    }

    int getBlockPattern() {
        return blockPattern;
    }
}
