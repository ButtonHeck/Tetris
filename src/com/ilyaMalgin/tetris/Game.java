package com.ilyaMalgin.tetris;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Collections;

public class Game extends JFrame implements Runnable {
    //Application staff
    public static final int BLOCK_SIZE = 40, GRID_WIDTH = 8;
    public static int GRID_HEIGHT, SCREEN_WIDTH, SCREEN_HEIGHT;

    private volatile boolean running;
    private StartWindow startWindow;
    private Thread gameThread;
    private BufferStrategy bs;
    private Graphics g;
    private int[] pixels;
    private BufferedImage boardImage;
    private int boardColor = 0xFF8899AA;

    //logic staff
    private static final ArrayList<Integer> bricksMap = new ArrayList<>(GRID_HEIGHT * GRID_WIDTH);
    private static ArrayList<Shape> shapesOnBoard = new ArrayList<>();
    private Shape currentShape;

    public Game(StartWindow window) {
        this.startWindow = window;
        initializeWindowParameters();
        pixels = new int[SCREEN_HEIGHT * SCREEN_WIDTH];
        setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        setLocationRelativeTo(null);

        boardImage = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
        createBoardImageData();
        initializeBricksMap();
        shapesOnBoard.clear();

        debugMouseListener();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    stop();
                }
                if (currentShape.moveEnded() || !running)
                    return;
                if (e.getKeyCode() == KeyEvent.VK_D)
                    currentShape.tryMove(1, 0);
                else if (e.getKeyCode() == KeyEvent.VK_A)
                    currentShape.tryMove(-1, 0);
                else if (e.getKeyCode() == KeyEvent.VK_W)
                    currentShape.rotate(true);
                else if (e.getKeyCode() == KeyEvent.VK_S)
                    currentShape.rotate(false);
                else if (e.getKeyCode() == KeyEvent.VK_SPACE)
                    currentShape.drop();
                update();
                render();
            }
        });
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);

        System.out.println(getWidth() + "x" + getHeight());
        /*this shows proper stats as it should be,
        but in fact occasionally the actual size of the window is cut on the upper side*/

        gameThread = new Thread(this, "Main game thread");
        start();
    }

    private static void initializeWindowParameters() {
        GRID_HEIGHT = Options.getColumns();
        SCREEN_WIDTH = GRID_WIDTH * BLOCK_SIZE;
        SCREEN_HEIGHT = GRID_HEIGHT * BLOCK_SIZE;
    }

    public synchronized void start() {
        if (running)
            return;
        running = true;
        gameThread.start();
    }

    public synchronized void stop() {
        if (!running)
            return;
        running = false;
        try {
            if (Thread.currentThread() != gameThread) {
                dispose();
                gameThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //System.out.println(running);
            startWindow.setVisible(true);
            startWindow.setLocationRelativeTo(null);
        }
    }

    @Override
    public void run() {
        setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        spawn();
        double ups = Options.getSpeed() / 12;
        long last = System.nanoTime(), now;
        double delta = 0;
        double timePerUpdate = 1_000_000_000 / ups;
        while (running) {
            now = System.nanoTime();
            delta += (now - last) / timePerUpdate;
            last = now;
            if (delta >= 1) {
                update();
                render();
                if (!currentShape.moveEnded()) {
                    if (running)
                        currentShape.tryMove(0, 1);
                    else
                        dispose();
                }
                delta = 0;
            }
        }
    }

    private void spawn() {
        checkFullLines();
        Shape shape = Shape.getRandomShape();
        currentShape = shape;
        shapesOnBoard.add(shape);
        renewMap();
        if (bricksCollide()) {
            render();
            System.out.println("LOSE!");
            stop();
        }
    }

    private void checkFullLines() {
        int firstFullRow = 0, adjacentFullRows = 0;
        for (int i = GRID_HEIGHT - 1; i >= 0; --i) {
            if (!bricksMap.subList(GRID_WIDTH * i, GRID_WIDTH * (i + 1)).contains(0)) {
                if (adjacentFullRows == 0)
                    firstFullRow = i;
                ++adjacentFullRows;
                continue;
            }
            if (adjacentFullRows != 0)
                break;
        }
        if (adjacentFullRows != 0) {
            boardColor = 0xFF99AABB;
            createBoardImageData();
            removeFullLines(firstFullRow, adjacentFullRows);
        }
    }

    private void removeFullLines(int removableRow, int adjacentFullRows) {
        for (int i = 0; i < adjacentFullRows; ++i) {
            shapesOnBoard.forEach(shape -> shape.detach(removableRow));
        }
        renewMap();
    }

    public void update() {
        if (currentShape.moveEnded())
            spawn();
        renewMap();
    }

    public void render() {
        bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }
        g = bs.getDrawGraphics();
        g.drawImage(boardImage, 0, 0, null);
        for (int i = 0; i < shapesOnBoard.size(); i++) {
            shapesOnBoard.get(i).render(g);
        }
        boardColor = 0xFF8899AA;
        createBoardImageData();
        bs.show();
        g.dispose();
    }

    public static boolean bricksCollide() {
        return bricksMap.contains(2);
    }

    private void debugMouseListener() {
        addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println(e.getX() / BLOCK_SIZE + 1 + ":" + (e.getY() / BLOCK_SIZE + 1) + ", shapes on board:" + shapesOnBoard.size());
                /*for (int i = 0; i < bricksMap.size(); )
                    System.out.print(bricksMap.get(i) + (++i % GRID_WIDTH == 0 ? "\n" : ", "));*/
            }
        });
    }

    private void createBoardImageData() {
        int colorDelta = boardColor;
        int[] colorSquares = new int[GRID_HEIGHT * GRID_WIDTH];
        for (int i = 0; i < colorSquares.length; i++) {
            colorSquares[i] = colorDelta;
            colorDelta += 0x00_03_03_03;
        }
        pixels = ((DataBufferInt) boardImage.getRaster().getDataBuffer()).getData();
        int colorIndex = 0;
        for (int y = 0; y < SCREEN_HEIGHT; y++) {
            if (y % BLOCK_SIZE == 0) colorIndex += 10;
            for (int x = 0; x < SCREEN_WIDTH; x++) {
                pixels[x + y * SCREEN_WIDTH] = colorSquares[colorIndex];
                if (colorIndex < colorSquares.length && x % BLOCK_SIZE == 0) {
                    colorIndex++;
                }
            }
            colorIndex = 0;
        }
    }

    private void initializeBricksMap() {
        bricksMap.clear();
        for (int i = 0; i < GRID_HEIGHT * GRID_WIDTH; i++) {
            bricksMap.add(0);
        }
    }

    public static void renewMap() {
        Collections.fill(bricksMap, 0);
        synchronized (bricksMap) {
            shapesOnBoard.forEach(shape -> shape.placeOnMap(bricksMap));
        }
    }
}
