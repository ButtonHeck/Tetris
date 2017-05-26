package com.ilyaMalgin.tetris.models;

import com.ilyaMalgin.tetris.BlockSheet;
import com.ilyaMalgin.tetris.Game;
import com.ilyaMalgin.tetris.controllers.AudioController;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Shape {

    private static int spawnX = Game.GRID_WIDTH / 2 - 1;
    private int x, y; //offset of the shape on the global map
    private final ArrayList<Block> blocks;
    private final boolean rotatable;
    private volatile boolean moveEnded = false;
    private final int blockPattern;
    private final BufferedImage[] images;
    private static int imageSeed = 0;

    private static final Shape L_SHAPE = new Shape(true, 0, BlockSheet.L_SHAPE_IMAGES);
    private static final Shape T_SHAPE = new Shape(true, 1, BlockSheet.T_SHAPE_IMAGES);
    private static final Shape I_SHAPE = new Shape(true, 2, BlockSheet.I_SHAPE_IMAGES);
    private static final Shape CUBE_SHAPE = new Shape(false, 3, BlockSheet.CUBE_SHAPE_IMAGES);
    private static final Shape Z_SHAPE = new Shape(true, 4, BlockSheet.Z_SHAPE_IMAGES);
    private static final Shape L_MIRROR_SHAPE = new Shape(true, 5, BlockSheet.L_SHAPE_IMAGES);
    private static final Shape Z_MIRROR_SHAPE = new Shape(true, 6, BlockSheet.Z_SHAPE_IMAGES);

    private static final Shape[] shapes = new Shape[]{L_SHAPE, T_SHAPE, I_SHAPE, CUBE_SHAPE, Z_SHAPE, L_MIRROR_SHAPE, Z_MIRROR_SHAPE};

    private Shape(boolean rotatable, int blockPattern, BufferedImage[] images) {
        x = blockPattern <= 4 ? spawnX : spawnX + 1;
        y = 1;
        this.blocks = new ArrayList<>();
        this.rotatable = rotatable;
        this.blockPattern = blockPattern;
        this.images = images;
        Collections.addAll(blocks, BlockPatterns.getBlocks(blockPattern));
        blocks.forEach(e -> {
            e.setParent(this);
            e.setBlockImage(images[Math.abs(++imageSeed % 3)]);
            e.setGlobX(e.getGlobX() + x);
            e.setGlobY(e.getGlobY() + y);
        });
    }

    public boolean tryMove(int x, int y) {
        if (movementPossible(x, y)) {
            move(x, y);
            return true;
        } else
            return false;
    }

    private boolean movementPossible(int x, int y) {
        return !sideBorderReached(x) && !lowBorderReached(y);
    }

    private void move(int x, int y) {
        this.x += x;
        this.y += y;
        blocks.forEach(block -> {
            block.setGlobX(block.getGlobX() + x);
            block.setGlobY(block.getGlobY() + y);
        });
        Game.renewBricksMap();
        if (Game.bricksCollide()) {
            if (y != 0)
                moveEnded = true;
            move(-x, -y);
        }
    }

    private boolean sideBorderReached(int x) {
        return blocks.stream().anyMatch(block -> block.getGlobX() + x < 0 || block.getGlobX() + x > Game.GRID_WIDTH - 1);
    }

    private boolean lowBorderReached(int y) {
        if (blocks.stream().anyMatch(block -> block.getGlobY() < 0 || block.getGlobY() + y > Game.GRID_HEIGHT - 1)) {
            moveEnded = true;
            return true;
        }
        return false;
    }

    public void drop() {
        while (!Game.bricksCollide() && !lowBorderReached(1) && !moveEnded) {
            move(0, 1);
        }
        AudioController.drop();
    }

    public void detach(int y) {
        blocks.removeIf(block -> block.getGlobY() == y);
        Game.renewBricksMap();
        detachDrop(y);
    }

    private void detachDrop(int y) {
        for (int i = 0; i < blocks.size(); i++) {
            Block temp = blocks.get(i);
            if (temp.getGlobY() < y) {
                temp.setGlobY(temp.getGlobY() + 1);
            }
        }
        Game.renewBricksMap();
    }

    public void rotate(boolean left) {
        if (!rotatable || blocks.stream().anyMatch(block -> impossibleToRotate(block, left)))
            return;
        blocks.forEach(block -> {
            int dx = (left ? -block.getRelY() : block.getRelY()) - block.getRelX();
            int dy = (left ? block.getRelX() : -block.getRelX()) - block.getRelY();
            block.relocate(dx, dy);
        });
        Game.renewBricksMap();
        if (Game.bricksCollide()) { //undo previous rotation
            blocks.forEach(block -> {
                int dx = (left ? block.getRelY() : -block.getRelY()) - block.getRelX();
                int dy = (left ? -block.getRelX() : block.getRelX()) - block.getRelY();
                block.relocate(dx, dy);
            });
            return;
        }
        AudioController.rotate();
    }

    private boolean impossibleToRotate(Block block, boolean left) {
        int dx = (left ? -block.getRelY() : block.getRelY()) - block.getRelX();
        int dy = (left ? block.getRelX() : -block.getRelX()) - block.getRelY();
        return block.getGlobX() + dx < 0 || block.getGlobX() + dx > Game.GRID_WIDTH - 1
                || block.getGlobY() + dy < 0 || block.getGlobY() + dy > Game.GRID_HEIGHT - 1;
    }

    public void placeOnMap(ArrayList<Integer> map) {
        blocks.forEach(block ->
                map.set(block.getGlobY() * Game.GRID_WIDTH + block.getGlobX(),
                        map.get(block.getGlobY() * Game.GRID_WIDTH + block.getGlobX()) + 1));
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
