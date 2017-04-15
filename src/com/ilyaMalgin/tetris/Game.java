package com.ilyaMalgin.tetris;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class Game extends JFrame implements Runnable {
    //Application stuff
    public static final int BLOCK_SIZE = 40;
    public static int GRID_HEIGHT, GRID_WIDTH, GAME_SCREEN_WIDTH, GAME_SCREEN_HEIGHT;
    private static final int SIDE_PART_SCREEN_WIDTH = 180;

    private volatile boolean running;
    private boolean firstUpdateHappen, boardColorChanged, paused, gameOverScreenState;
    private StartWindow startWindow;
    private Thread gameThread;
    private BufferStrategy bs;
    private Graphics g;
    private int[] pixels;
    private BufferedImage boardImage;
    private Canvas canvas;
    private int boardColor = 0xFF889999, score = 0, nextSpeedIncreaseScore = 10;
    private JLabel messageLabel = new JLabel(Messages.START);
    private double timePerUpdate;
    private Random messageRandomizer = new Random(System.currentTimeMillis());
    private KeyboardController keyboardController;

    //game map stuff
    private static final ArrayList<Integer> bricksMap = new ArrayList<>(GRID_HEIGHT * GRID_WIDTH);
    private static ArrayList<Shape> shapesOnBoard = new ArrayList<>();
    private Shape currentShape, nextShape;

    public Game(StartWindow window) {
        this.startWindow = window;
        initializeWindowParameters();
        initializeCanvasParameters();
        initializeGraphicsData();
        initializeLogicMaps();
        if (Options.isShowMessages())
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
        canvas.setPreferredSize(new Dimension(GAME_SCREEN_WIDTH + SIDE_PART_SCREEN_WIDTH, GAME_SCREEN_HEIGHT));
        canvas.setMaximumSize(new Dimension(GAME_SCREEN_WIDTH + SIDE_PART_SCREEN_WIDTH, GAME_SCREEN_HEIGHT));
        canvas.setMinimumSize(new Dimension(GAME_SCREEN_WIDTH + SIDE_PART_SCREEN_WIDTH, GAME_SCREEN_HEIGHT));
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
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        add(messageLabel);
        BorderLayout layout = new BorderLayout(0, 0);
        messageLabel.setBorder(new MatteBorder(2, 1, 1, 1, Color.BLACK));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        layout.addLayoutComponent(canvas, BorderLayout.NORTH);
        layout.addLayoutComponent(messageLabel, BorderLayout.SOUTH);
        setLayout(layout);
    }

    private void allocateGameOnScreen() {
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);
        setTitle("Cheeky bricky: Game");
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
        timePerUpdate = 1_000_000_000 / updatesPerSecond;
        while (running || gameOverScreenState) {
            if (!firstUpdateHappen) {
                renderBeforeFirstUpdate();
            }
            nowTime = System.nanoTime();
            delta += (nowTime - lastUpdateTime) / timePerUpdate;
            lastUpdateTime = nowTime;
            if (delta >= 1) {
                if (running) {
                    if (!paused)
                        update();
                    render();
                    if (running && !currentShape.moveEnded() && !paused)
                        currentShape.tryMove(0, 1);
                    delta = 0;
                    firstUpdateHappen = true;
                } else if (gameOverScreenState) {
                    renderGameOverScreen();
                } else
                    throw new IllegalStateException("Game is over, but the flag hasn't been set");
            }
            if (keyboardController.hasUnhandledEvents())
                handleKeyboardEvents();
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        setNewHiScore();
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
            gameOverScreenState = true;
            running = false;
        }
    }

    private void handleKeyboardEvents() {
        keyboardController.eventsHandled();
        if (keyboardController.escPressed()) {
            gameOverScreenState = false;
            AudioHolder.music.setVolume(1.0f);
            stop();
        }
        if (keyboardController.pausePressed()) {
            paused = !paused;
            AudioHolder.music.setVolume(paused ? 0.4f : 1.0f);
            createBoardImageData(paused ? 0x333333 : boardColor);
            renewScore(0);
            if (running)
                render();
        }
        if (currentShape.moveEnded() || !running || paused)
            return;
        if (keyboardController.rightPressed())
            if (currentShape.tryMove(1, 0))
                AudioHolder.move();
        if (keyboardController.leftPressed())
            if (currentShape.tryMove(-1, 0))
                AudioHolder.move();
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
            boardColor += adjacentFullRows * 0xFF0F0F0F;
            createBoardImageData(boardColor);
            removeFullLines(firstFullRow, adjacentFullRows);
            renewScore(adjacentFullRows);
            feedbackToPlayer(adjacentFullRows);
            /*
            Occasionally there are still some full lines after others been removed,
            that happens, for example, when we drop vertical I shape that removes the lowest
            2 rows and SHOULD remove the top one of her own top coordinate, BUT, if in the 3rd
            line there is a gap somewhere the algorithm should stop counting adjacentFullRows when
            check THAT EXACT row and remove only the lowest two. The same situation if a gap is
            in the 2nd row from below. Also this happens with L shape when the second row has a gap
            while 1st and 3rd haven't. Recursive invocation solves that in way to double check after
            the first removal.
             */
            checkFullLines();
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
        if (Options.isSpeedIncrease() && score >= nextSpeedIncreaseScore)
            blockFallSpeedIncrease();
    }

    private void feedbackToPlayer(int adjacentFullRows) {
        if (Options.isShowMessages()) {
            switch (adjacentFullRows) {
                case 1:
                    messageLabel.setText(Messages.L1[messageRandomizer.nextInt(Messages.L1.length)]);
                    break;
                case 2:
                    messageLabel.setText(Messages.L2[messageRandomizer.nextInt(Messages.L2.length)]);
                    break;
                case 3:
                    messageLabel.setText(Messages.L3[messageRandomizer.nextInt(Messages.L3.length)]);
                    break;
                case 4:
                    messageLabel.setText(Messages.L4[messageRandomizer.nextInt(Messages.L4.length)]);
                    break;
            }
        }
        AudioHolder.line(adjacentFullRows);
    }

    private void blockFallSpeedIncrease() {
        nextSpeedIncreaseScore += 10;
        Options.setSpeed(Options.getSpeed() + 1);
        timePerUpdate = 1_000_000_000 / (Options.getSpeed() / 12);
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
        renderSideParts();
        bs.show();
        g.dispose();
    }

    private void renderGameOverScreen() {
        g = bs.getDrawGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GAME_SCREEN_WIDTH + SIDE_PART_SCREEN_WIDTH, GAME_SCREEN_HEIGHT);
        g.setColor(Color.WHITE);

        g.setFont(new Font("Arial", Font.BOLD, 38));
        String messsage = "Game over";
        FontMetrics metrics = g.getFontMetrics();
        g.drawString(messsage, (GAME_SCREEN_WIDTH + SIDE_PART_SCREEN_WIDTH) / 2 - metrics.stringWidth(messsage) / 2, GAME_SCREEN_HEIGHT / 2 - 60);
        String scoreMessage = "" + score;
        g.drawString(scoreMessage, (GAME_SCREEN_WIDTH + SIDE_PART_SCREEN_WIDTH) / 2 - metrics.stringWidth(scoreMessage) / 2, GAME_SCREEN_HEIGHT / 2);

        g.setFont(new Font("Arial", Font.BOLD, 28));
        FontMetrics metrics2 = g.getFontMetrics();
        String escHint = "Press ESC to back to menu";
        g.drawString(escHint, (GAME_SCREEN_WIDTH + SIDE_PART_SCREEN_WIDTH) / 2 - metrics2.stringWidth(escHint) / 2, GAME_SCREEN_HEIGHT / 2 + 60);
        bs.show();
        g.dispose();
    }

    private void renderSideParts() {
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        if (Options.getShowNext()) {
            g.clearRect(GAME_SCREEN_WIDTH, 0, 200, GAME_SCREEN_HEIGHT);
            nextShape.render(g, false);
            g.drawLine(GAME_SCREEN_WIDTH, 0, GAME_SCREEN_WIDTH, GAME_SCREEN_HEIGHT);
            g.drawString("Next:", GAME_SCREEN_WIDTH + 60, 40);
        }
        g.drawString("Score " + score, GAME_SCREEN_WIDTH + 10, 240);
        g.drawString("Speed: " + Options.getSpeed(), GAME_SCREEN_WIDTH + 10, 270);
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
        shapesOnBoard.forEach(shape -> shape.placeOnMap(bricksMap));
    }

    private void setNewHiScore() {
        try (Scanner scanner = new Scanner(new File("res/hiscore.txt"))) {
            int currentHiScore = scanner.nextInt();
            if (score <= currentHiScore)
                return;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try (FileWriter writer = new FileWriter(new File("res/hiscore.txt"))) {
            writer.write(String.valueOf(score));
        } catch (IOException e) {
            e.printStackTrace();
        }
        startWindow.renewHiScore();
    }
}
