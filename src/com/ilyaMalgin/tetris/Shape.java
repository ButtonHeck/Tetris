package com.ilyaMalgin.tetris;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

public class Shape {

    public static final int SPAWN_X = 3, SPAWN_Y = 1;
    private int x, y;
    private Color color;
    private ArrayList<Block> blocks;
    private boolean rotatable;
    private volatile boolean moveEnded = false;
    private int blockPattern;
    private ColorPattern colorPattern;

    private static final Shape L_SHAPE = new Shape(ColorPattern.L_MODEL, true, 0);
    private static final Shape T_SHAPE = new Shape(ColorPattern.T_MODEL, true, 1);
    private static final Shape I_SHAPE = new Shape(ColorPattern.I_MODEL, true, 2);
    private static final Shape CUBE_SHAPE = new Shape(ColorPattern.CUBE_MODEL, false, 3);
    private static final Shape Z_SHAPE = new Shape(ColorPattern.Z_MODEL, true, 4);
    private static final Shape L_MIRROR_SHAPE = new Shape(ColorPattern.L_MODEL, true, 5);
    private static final Shape Z_MIRROR_SHAPE = new Shape(ColorPattern.Z_MODEL, true, 6);

    private static final Shape[] shapes = new Shape[]{L_SHAPE, T_SHAPE, I_SHAPE, CUBE_SHAPE, Z_SHAPE, L_MIRROR_SHAPE, Z_MIRROR_SHAPE};

    private Shape(ColorPattern pattern, boolean rotatable, int blockPattern) {
        this.x = SPAWN_X;
        this.y = SPAWN_Y;
        this.colorPattern = pattern;
        this.blocks = new ArrayList<>();
        this.rotatable = rotatable;
        this.blockPattern = blockPattern;
        Collections.addAll(blocks, BlockPatterns.getBlocks(blockPattern));
        blocks.forEach(e -> {
            e.setParent(this);
            e.setColorModel(colorPattern);
            e.setGlobX(e.getGlobX() + this.x);
            e.setGlobY(e.getGlobY() + this.y);
        });
    }

    public void tryMove(int x, int y) {
        if (movementPossible(x, y))
            move(x, y);
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

    public boolean sideBorderReached(int x) {
        return blocks.stream().anyMatch(block -> block.getGlobX() + x < 0 || block.getGlobX() + x > Game.GRID_WIDTH - 1);
    }

    public boolean lowBorderReached(int y) {
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
    }

    public void detach(int y) {
        Iterator<Block> it = blocks.iterator();
        while(it.hasNext()) {
            if (it.next().getGlobY() == y) {
                it.remove();
            }
        }
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
            int dx1 = left ? -block.getRelY() : block.getRelY();
            int dy1 = -block.getRelY();
            int dx2 = -block.getRelX();
            int dy2 = left ? block.getRelX() : -block.getRelX();
            block.relocate(dx1, dy1, dx2, dy2);
        });
        Game.renewBricksMap();
        if (Game.bricksCollide()) {
            //System.out.println("Rotate incorrect");
            blocks.forEach(block -> {
                int dx1 = left ? block.getRelY() : -block.getRelY();
                int dy1 = -block.getRelY();
                int dx2 = -block.getRelX();
                int dy2 = left ? -block.getRelX() : block.getRelX();
                block.relocate(dx1, dy1, dx2, dy2);
            });
        }
    }

    private boolean impossibleToRotate(Block block, boolean left) {
        int dx1 = left ? -block.getRelY() : block.getRelY();
        int dy1 = -block.getRelY();
        int dx2 = -block.getRelX();
        int dy2 = left ? block.getRelX() : -block.getRelX();
        if (block.getGlobX() + dx1 + dx2 < 0 || block.getGlobX() + dx1 + dx2 > Game.GRID_WIDTH - 1
                || block.getGlobY() + dy1 + dy2 < 0 || block.getGlobY() + dy1 + dy2 > Game.GRID_HEIGHT - 1) {
            return true;
        }
        return false;
    }

    public void placeOnMap(ArrayList<Integer> map) {
        blocks.forEach(block ->
                map.set(block.getGlobY() * Game.GRID_WIDTH + block.getGlobX(),
                        map.get(block.getGlobY() * Game.GRID_WIDTH + block.getGlobX()) + 1)
        );
    }

    public void render(Graphics g) {
        for (int i = 0; i < blocks.size(); i++) {
            blocks.get(i).render(g);
        }
    }

    public Shape copy() {
        return new Shape(this.colorPattern, this.rotatable, this.blockPattern);
    }

    public static Shape getRandomShape() {
        return shapes[new Random().nextInt(7)].copy();
    }

    //Getters and Setters

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public ArrayList<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(ArrayList<Block> blocks) {
        this.blocks = blocks;
    }

    public boolean moveEnded() {
        return moveEnded;
    }
}
