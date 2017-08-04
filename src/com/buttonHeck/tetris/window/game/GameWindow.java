package com.buttonHeck.tetris.window.game;

import com.buttonHeck.tetris.handler.AudioHandler;
import com.buttonHeck.tetris.handler.KeyboardHandler;
import com.buttonHeck.tetris.model.Shape;
import com.buttonHeck.tetris.util.Messages;
import com.buttonHeck.tetris.util.Options;
import com.buttonHeck.tetris.window.menu.StartWindow;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;

public class GameWindow extends JFrame implements Runnable {

    public static final int BLOCK_SIZE = 40;
    private static int GRID_HEIGHT;
    private static int GRID_WIDTH;

    private Renderer renderer;
    private volatile boolean running;
    private boolean firstUpdateHappen;
    private boolean paused, gameOverScreenState;
    private Score score;
    private StartWindow startWindow;
    private Thread gameThread;
    private Canvas gameCanvas;
    private JLabel messageLabel = new JLabel(Messages.START);
    private KeyboardHandler keyboardController;

    //game map stuff
    private static final ArrayList<Integer> blocksMap = new ArrayList<>(GRID_HEIGHT * GRID_WIDTH);
    private static ArrayList<Shape> shapesOnBoard = new ArrayList<>();
    private Shape currentShape, nextShape;

    public GameWindow(StartWindow window) {
        this.startWindow = window;
        initializeWindowParameters();
        initializeCanvasRenderer();
        initializeLogicMaps();
        if (Options.isShowMessagesOn())
            initializeMessageLayout();
        allocateGameOnScreen();
        initializeInputListeners();
        gameThread = new Thread(this, "Game window thread");
        start();
    }

    private void initializeWindowParameters() {
        GRID_HEIGHT = Options.getColumns();
        GRID_WIDTH = Options.getRows();
    }

    private void initializeCanvasRenderer() {
        renderer = new Renderer(this, shapesOnBoard);
        gameCanvas = renderer.getCanvas();
    }

    private void initializeLogicMaps() {
        blocksMap.clear();
        for (int i = 0; i < GRID_HEIGHT * GRID_WIDTH; i++) {
            blocksMap.add(0);
        }
        if (!shapesOnBoard.isEmpty())
            shapesOnBoard.clear();
        Shape.setSpawnX(GRID_WIDTH / 2 - 1);
    }

    private void initializeMessageLayout() {
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        add(messageLabel);
        BorderLayout layout = new BorderLayout(0, 0);
        messageLabel.setBorder(new MatteBorder(2, 1, 1, 1, Color.BLACK));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        layout.addLayoutComponent(gameCanvas, BorderLayout.NORTH);
        layout.addLayoutComponent(messageLabel, BorderLayout.SOUTH);
        setLayout(layout);
    }

    /* while using Canvas object we have to set "resizable" parameter before packing,
       otherwise the output looking would be different on windows, linux, and mac.
       (on Windows the gameCanvas object would have additional empty space on the right and bottom sides of the screen)
    */
    private void allocateGameOnScreen() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setTitle("Cheeky bricky: Game");
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        gameCanvas.requestFocusInWindow();
    }

    private void initializeInputListeners() {
        keyboardController = new KeyboardHandler();
        gameCanvas.addKeyListener(keyboardController);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowDeactivated(WindowEvent e) {
                if (!paused)
                    pause();
            }
        });
    }

    private synchronized void start() {
        running = true;
        gameThread.start();
    }

    private void pause() {
        if (!running)
            return;
        paused = !paused;
        AudioHandler.pauseState(paused);
        renderer.createBoardImageData();
        renewScore(0);
    }

    private synchronized void stop() {
        gameOverScreenState = false;
        AudioHandler.pauseState(false);
        running = false;
        startWindow.setVisible(true);
        startWindow.setLocationRelativeTo(null);
    }

    @Override
    public void run() {
        score = new Score();
        spawnShape();
        loopWhileRunning();
        dispose();
    }

    private void loopWhileRunning() {
        Clock clock = new Clock();
        while (running || gameOverScreenState) {
            if (!firstUpdateHappen)
                renderer.renderBeforeFirstUpdate();
            clock.update();
            if (clock.secondsTicked() >= 1) {
                tickFrame();
                clock.tick();
            }
            handleKeyboardEvents();
            sleep(5);
        }
    }

    private void tickFrame() {
        if (running) {
            if (!paused)
                update();
            renderer.render(nextShape);
            if (running && !currentShape.moveEnded() && !paused)
                currentShape.tryMoveTo(0, 1);
            firstUpdateHappen = true;
        } else if (gameOverScreenState) {
            renderer.renderGameOverScreen(score.getScore());
        } else
            throw new IllegalStateException("Game is over, but the flag hasn't been set");
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void spawnShape() {
        checkFullLines();
        currentShape = nextShape == null ? Shape.getRandomShape() : nextShape;
        nextShape = Shape.getRandomShape();
        shapesOnBoard.add(currentShape);
        renewBlocksMap();
        if (blocksCollide()) { //if we have collision right after a shape was spawned -> gameOver
            renderer.render(nextShape);
            gameOverScreenState = true;
            running = false;
        }
    }

    private void checkFullLines() {
        int firstFullRow = 0, adjacentFullRows = 0;
        for (int i = GRID_HEIGHT - 1; i >= 0; --i) {
            if (!blocksMap.subList(GRID_WIDTH * i, GRID_WIDTH * (i + 1)).contains(0)) {
                if (adjacentFullRows == 0)
                    firstFullRow = i;
                ++adjacentFullRows;
                continue;
            }
            if (adjacentFullRows != 0)
                break;
        }
        if (adjacentFullRows != 0)
            playerHasFullLine(firstFullRow, adjacentFullRows);
    }

    private void playerHasFullLine(int firstFullRow, int adjacentFullRows) {
        renderer.createBoardImageDataColorChanged(adjacentFullRows);
        removeFullLines(firstFullRow, adjacentFullRows);
        renewScore(adjacentFullRows);
        feedbackToPlayer(adjacentFullRows);
        checkFullLines();
    }

    private void removeFullLines(int removableRow, int adjacentFullRows) {
        for (int i = 0; i < adjacentFullRows; ++i)
            shapesOnBoard.forEach(shape -> shape.detachAtRow(removableRow));
        renewBlocksMap();
    }

    private void renewScore(int adjacentFullRows) {
        score.plus(adjacentFullRows);
        if (Options.isSpeedIncreaseOn() && score.reachedIncreaseSpeed()) {
            Options.setSpeed(Options.getSpeed() + 1);
            Clock.renewTimePerUpdate();
        }
    }

    private void feedbackToPlayer(int adjacentFullRows) {
        if (Options.isShowMessagesOn())
            messageLabel.setText(Messages.getMessageFor(adjacentFullRows));
        AudioHandler.line(adjacentFullRows);
    }

    private void handleKeyboardEvents() {
        if (keyboardController.allEventsHandled())
            return;
        keyboardController.markAllEventsHandled();
        handleKeyboardGameStateEvents();
        if (currentShape.moveEnded() || !running || paused)
            return;
        handleKeyboardShapeMovementEvents();
        update();
        renderer.render(nextShape);
    }

    private void handleKeyboardGameStateEvents() {
        if (keyboardController.escPressed())
            stop();
        if (keyboardController.pausePressed()) {
            pause();
            if (running)
                renderer.render(nextShape);
        }
    }

    private void handleKeyboardShapeMovementEvents() {
        handleMoveEvents();
        handleRotateEvents();
    }

    private void handleMoveEvents() {
        if (keyboardController.rightPressed())
            if (currentShape.tryMoveTo(1, 0))
                AudioHandler.move();
        if (keyboardController.leftPressed())
            if (currentShape.tryMoveTo(-1, 0))
                AudioHandler.move();
        if (keyboardController.dropPressed())
            currentShape.drop();
    }

    private void handleRotateEvents() {
        if (keyboardController.rotateLPressed())
            currentShape.rotate(true);
        if (keyboardController.rotateRPressed())
            currentShape.rotate(false);
    }

    private void update() {
        if (currentShape.moveEnded())
            spawnShape();
        renewBlocksMap();
    }

    public static boolean blocksCollide() {
        return blocksMap.contains(2);
    }

    public static void renewBlocksMap() {
        Collections.fill(blocksMap, 0);
        shapesOnBoard.forEach(shape -> shape.placeOnMap(blocksMap));
    }

    //Getters

    boolean isPaused() {
        return paused;
    }

    int getScore() {
        return score.getScore();
    }

    public static int getGridHeight() {
        return GRID_HEIGHT;
    }

    public static int getGridWidth() {
        return GRID_WIDTH;
    }
}
