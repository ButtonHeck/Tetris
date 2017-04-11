package com.ilyaMalgin.tetris;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Collections;

public class Game extends JFrame implements Runnable {
    //Application stuff
    public static final int BLOCK_SIZE = 40;
    public static int GRID_HEIGHT, GRID_WIDTH, GAME_SCREEN_WIDTH, GAME_SCREEN_HEIGHT;

    private volatile boolean running;
    private boolean firstUpdateHappen, boardColorChanged, paused, showNext;
    private StartWindow startWindow;
    private Thread gameThread;
    private BufferStrategy bs;
    private Graphics g;
    private int[] pixels;
    private BufferedImage boardImage;
    private Canvas canvas;
    private int boardColor = 0xFF889999, score = 0;
    private JLabel scoreLabel = new JLabel("Speed: " + (int) Options.getSpeed() + ", Score: " + 0);

    //logic stuff
    private static final ArrayList<Integer> bricksMap = new ArrayList<>(GRID_HEIGHT * GRID_WIDTH);
    private static ArrayList<Shape> shapesOnBoard = new ArrayList<>();
    private Shape currentShape, nextShape;
    private KeyboardController keyboardController;

    public Game(StartWindow window) {
        this.startWindow = window;
        initializeWindowParameters();
        initializeCanvasParameters();
        initializeGraphicsData();
        initializeLogicMaps();
        setupLayoutAndScore();
        allocateGameOnScreen();
        initializeInputListeners();
        gameThread = new Thread(this, "Game window thread");
        start();
    }

    private static void initializeWindowParameters() {
        GRID_HEIGHT = Options.getColumns();
        GRID_WIDTH = Options.getRows();
        GAME_SCREEN_WIDTH = GRID_WIDTH * BLOCK_SIZE;
        GAME_SCREEN_HEIGHT = GRID_HEIGHT * BLOCK_SIZE;
    }

    private void initializeCanvasParameters() {
        canvas = new Canvas();
        showNext = Options.getShowNext();
        canvas.setPreferredSize(new Dimension(GAME_SCREEN_WIDTH + (showNext ? 200 : 0), GAME_SCREEN_HEIGHT));
        canvas.setMaximumSize(new Dimension(GAME_SCREEN_WIDTH + (showNext ? 200 : 0), GAME_SCREEN_HEIGHT));
        canvas.setMinimumSize(new Dimension(GAME_SCREEN_WIDTH + (showNext ? 200 : 0), GAME_SCREEN_HEIGHT));
        add(canvas);
    }

    private void initializeGraphicsData() {
        boardImage = new BufferedImage(GAME_SCREEN_WIDTH, GAME_SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
        pixels = new int[GAME_SCREEN_HEIGHT * GAME_SCREEN_WIDTH];
        createBoardImageData(boardColor);
    }

    private void initializeLogicMaps() {
        bricksMap.clear();
        for (int i = 0; i < GRID_HEIGHT * GRID_WIDTH; i++) {
            bricksMap.add(0);
        }
        if (!shapesOnBoard.isEmpty())
            shapesOnBoard.clear();
        Shape.setSpawnX(GRID_WIDTH / 2 - 1);
    }

    private void setupLayoutAndScore() {
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        add(scoreLabel);
        BorderLayout layout = new BorderLayout(0, 0);
        scoreLabel.setBorder(new MatteBorder(2, 1, 1, 1, Color.BLACK));
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        layout.addLayoutComponent(canvas, BorderLayout.NORTH);
        layout.addLayoutComponent(scoreLabel, BorderLayout.SOUTH);
        setLayout(layout);
    }

    private void allocateGameOnScreen() {
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);
        canvas.requestFocusInWindow();
    }

    private void initializeInputListeners() {
        keyboardController = new KeyboardController();
        canvas.addKeyListener(keyboardController);
    }

    public synchronized void start() {
        running = true;
        gameThread.start();
    }

    public synchronized void stop() {
        running = false;
        try {
            if (Thread.currentThread() != gameThread) {
                dispose();
                gameThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            startWindow.setVisible(true);
            startWindow.setLocationRelativeTo(null);
        }
    }

    @Override
    public void run() {
        spawn();
        final double updatesPerSecond = Options.getSpeed() / 12;
        long lastUpdateTime = System.nanoTime(), nowTime;
        double delta = 0;
        double timePerUpdate = 1_000_000_000 / updatesPerSecond;
        while (running) {
            if (!firstUpdateHappen) {
                renderBeforeFirstUpdate();
            }
            nowTime = System.nanoTime();
            delta += (nowTime - lastUpdateTime) / timePerUpdate;
            lastUpdateTime = nowTime;
            if (delta >= 1) {
                if (!paused)
                    update();
                render();
                if (!currentShape.moveEnded() && !paused) {
                    if (running)
                        currentShape.tryMove(0, 1);
                    else
                        dispose();
                }
                delta = 0;
                firstUpdateHappen = true;
            }
            if (keyboardController.hasUnhandledEvents())
                handleKeyboardEvents();
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        dispose();
    }

    private void spawn() {
        checkFullLines();
        currentShape = nextShape == null ? Shape.getRandomShape() : nextShape;
        nextShape = Shape.getRandomShape();
        shapesOnBoard.add(currentShape);
        renewBricksMap();
        if (bricksCollide()) {
            render();
            System.out.println("LOSE!");
            stop();
        }
    }

    private void handleKeyboardEvents() {
        keyboardController.eventsHandled();
        if (keyboardController.escPressed())
            stop();
        if (keyboardController.pausePressed()) {
            paused = !paused;
            createBoardImageData(paused ? 0x333333 : boardColor);
            renewScore(0);
            if (running)
                render();
        }
        if (currentShape.moveEnded() || !running || paused)
            return;
        if (keyboardController.rightPressed())
            currentShape.tryMove(1, 0);
        if (keyboardController.leftPressed())
            currentShape.tryMove(-1, 0);
        if (keyboardController.rotateLPressed())
            currentShape.rotate(true);
        if (keyboardController.rotateRPressed())
            currentShape.rotate(false);
        if (keyboardController.dropPressed())
            currentShape.drop();
        update();
        render();
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
            boardColorChanged = true;
            boardColor += adjacentFullRows * 0xFF080808;
            createBoardImageData(boardColor);
            removeFullLines(firstFullRow, adjacentFullRows);
            renewScore(adjacentFullRows);
        }
    }

    private void removeFullLines(int removableRow, int adjacentFullRows) {
        for (int i = 0; i < adjacentFullRows; ++i) {
            shapesOnBoard.forEach(shape -> shape.detach(removableRow));
        }
        renewBricksMap();
    }

    private void renewScore(int adjacentFullRows) {
        score += adjacentFullRows * adjacentFullRows;
        scoreLabel.setText("Speed: " + (int) Options.getSpeed() + ", Score: " + score + (paused ? " (paused)" : ""));
    }

    public void update() {
        if (currentShape.moveEnded())
            spawn();
        renewBricksMap();
    }

    private void renderBeforeFirstUpdate() {
        bs = canvas.getBufferStrategy();
        if (bs == null) {
            canvas.createBufferStrategy(2);
            renderBeforeFirstUpdate();
        }
        g = bs.getDrawGraphics();
        g.drawImage(boardImage, 0, 0, null);
        bs.show();
        g.dispose();
    }

    public void render() {
        bs = canvas.getBufferStrategy();
        if (bs == null) {
            canvas.createBufferStrategy(2);
            return;
        }
        g = bs.getDrawGraphics();
        g.drawImage(boardImage, 0, 0, null);
        for (int i = 0; i < shapesOnBoard.size(); i++) {
            shapesOnBoard.get(i).render(g, true);
        }
        if (boardColorChanged) {
            boardColor = 0xFF889999;
            createBoardImageData(boardColor);
            boardColorChanged = false;
        }
        if (showNext)
            renderSideParts();
        bs.show();
        g.dispose();
    }

    private void renderSideParts() {
        g.clearRect(GAME_SCREEN_WIDTH, 0, 200, GAME_SCREEN_HEIGHT);
        nextShape.render(g, false);
        g.drawLine(GAME_SCREEN_WIDTH, 0, GAME_SCREEN_WIDTH, GAME_SCREEN_HEIGHT);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.drawString("Next:", GAME_SCREEN_WIDTH + 70, 40);
    }

    private void createBoardImageData(int boardColor) {
        int colorDelta = boardColor;
        int[] colorSquares = new int[GRID_HEIGHT * GRID_WIDTH];
        for (int i = 0; i < colorSquares.length; i++) {
            colorSquares[i] = colorDelta;
            colorDelta += 0x00_01_01_02;
        }
        pixels = ((DataBufferInt) boardImage.getRaster().getDataBuffer()).getData();
        int colorIndex = 0;
        for (int y = 0; y < GAME_SCREEN_HEIGHT; y++) {
            if (y % BLOCK_SIZE == 0) colorIndex += 10;
            for (int x = 0; x < GAME_SCREEN_WIDTH; x++) {
                pixels[x + y * GAME_SCREEN_WIDTH] = colorSquares[colorIndex];
                if (colorIndex < colorSquares.length && x % BLOCK_SIZE == 0) {
                    colorIndex++;
                }
            }
            colorIndex = 0;
        }
    }

    public static boolean bricksCollide() {
        return bricksMap.contains(2);
    }

    public static void renewBricksMap() {
        Collections.fill(bricksMap, 0);
        synchronized (bricksMap) {
            shapesOnBoard.forEach(shape -> shape.placeOnMap(bricksMap));
        }
    }
}
