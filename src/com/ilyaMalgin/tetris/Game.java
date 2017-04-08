package com.ilyaMalgin.tetris;

import javax.swing.*;
import javax.swing.border.MatteBorder;
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
    //Application stuff
    public static final int BLOCK_SIZE = 40;
    public static int GRID_HEIGHT, GRID_WIDTH, SCREEN_WIDTH, SCREEN_HEIGHT;

    private volatile boolean running;
    private boolean firstUpdateHappen, boardColorChanged, paused;
    private StartWindow startWindow;
    private Thread gameThread;
    private BufferStrategy bs;
    private Graphics g;
    private int[] pixels;
    private BufferedImage boardImage;
    private Canvas canvas;
    private int boardColor = 0xFF8899AA, score = 0;
    private JLabel scoreLabel = new JLabel("Score: " + 0);

    //logic stuff
    private static final ArrayList<Integer> bricksMap = new ArrayList<>(GRID_HEIGHT * GRID_WIDTH);
    private static ArrayList<Shape> shapesOnBoard = new ArrayList<>();
    private Shape currentShape;
    private boolean keys[] = new boolean[8]; //7 for keys, 8 is a flag that any event was occured

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
        SCREEN_WIDTH = GRID_WIDTH * BLOCK_SIZE;
        SCREEN_HEIGHT = GRID_HEIGHT * BLOCK_SIZE;
    }

    private void initializeCanvasParameters() {
        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        canvas.setMaximumSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        canvas.setMinimumSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        add(canvas);
    }

    private void initializeGraphicsData() {
        boardImage = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
        pixels = new int[SCREEN_HEIGHT * SCREEN_WIDTH];
        createBoardImageData(boardColor);
    }

    private void initializeLogicMaps() {
        bricksMap.clear();
        for (int i = 0; i < GRID_HEIGHT * GRID_WIDTH; i++) {
            bricksMap.add(0);
        }
        if (!shapesOnBoard.isEmpty())
            shapesOnBoard.clear();
        Shape.SPAWN_X = GRID_WIDTH / 2 - 1;
    }

    private void setupLayoutAndScore() {
        scoreLabel.setFont(new Font("Monospaced", Font.PLAIN, 22));
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
        debugMouseListener();
        canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                keys[7] = true;
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    keys[0] = true;
                if (e.getKeyCode() == KeyEvent.VK_SPACE)
                    keys[1] = true;
                if (e.getKeyCode() == KeyEvent.VK_P)
                    keys[2] = true;
                if (e.getKeyCode() == KeyEvent.VK_W)
                    keys[3] = true;
                if (e.getKeyCode() == KeyEvent.VK_S)
                    keys[4] = true;
                if (e.getKeyCode() == KeyEvent.VK_A)
                    keys[5] = true;
                if (e.getKeyCode() == KeyEvent.VK_D)
                    keys[6] = true;
            }

            @Override
            public void keyReleased(KeyEvent e) {
                keys[7] = false;
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    keys[0] = false;
                if (e.getKeyCode() == KeyEvent.VK_SPACE)
                    keys[1] = false;
                if (e.getKeyCode() == KeyEvent.VK_P)
                    keys[2] = false;
                if (e.getKeyCode() == KeyEvent.VK_W)
                    keys[3] = false;
                if (e.getKeyCode() == KeyEvent.VK_S)
                    keys[4] = false;
                if (e.getKeyCode() == KeyEvent.VK_A)
                    keys[5] = false;
                if (e.getKeyCode() == KeyEvent.VK_D)
                    keys[6] = false;
            }
        });
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
            startWindow.setVisible(true);
            startWindow.setLocationRelativeTo(null);
        }
    }

    @Override
    public void run() {
        spawn();
        double ups = Options.getSpeed() / 12;
        long last = System.nanoTime(), now;
        double delta = 0;
        double timePerUpdate = 1_000_000_000 / ups;
        while (running) {
            if (!firstUpdateHappen) {
                renderBeforeFirstUpdate();
            }
            now = System.nanoTime();
            delta += (now - last) / timePerUpdate;
            last = now;
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
            //if there is an unhandled keyboard signal handle it, otherwise no need to get in there
            if (keys[7])
                handleKeyboardEvents();
        }
        dispose();
    }

    private void spawn() {
        checkFullLines();
        Shape shape = Shape.getRandomShape();
        currentShape = shape;
        shapesOnBoard.add(shape);
        renewBricksMap();
        if (bricksCollide()) {
            render();
            System.out.println("LOSE!");
            stop();
        }
    }

    private void handleKeyboardEvents() {
        keys[7] = false;
        if (keys[0]) {
            keys[0] = false;
            stop();
        }
        if (keys[2]) {
            keys[2] = false;
            paused = !paused;
            createBoardImageData(paused ? 0x777777 : boardColor);
            renewScore(0);
            if (running)
                render();
        }
        if (currentShape.moveEnded() || !running || paused)
            return;
        if (keys[6]) {
            keys[6] = false;
            currentShape.tryMove(1, 0);
        } else if (keys[5]) {
            keys[5] = false;
            currentShape.tryMove(-1, 0);
        } else if (keys[3]) {
            keys[3] = false;
            currentShape.rotate(true);
        } else if (keys[4]) {
            keys[4] = false;
            currentShape.rotate(false);
        } else if (keys[1]) {
            keys[1] = false;
            currentShape.drop();
        }
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
        scoreLabel.setText("Score: " + score + (paused ? " (paused)" : ""));
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
            shapesOnBoard.get(i).render(g);
        }
        if (boardColorChanged) {
            boardColor = 0xFF8899AA;
            createBoardImageData(boardColor);
            boardColorChanged = false;
        }
        bs.show();
        g.dispose();
    }

    private void createBoardImageData(int boardColor) {
        int colorDelta = boardColor;
        int[] colorSquares = new int[GRID_HEIGHT * GRID_WIDTH];
        for (int i = 0; i < colorSquares.length; i++) {
            colorSquares[i] = colorDelta;
            colorDelta += 0x00_02_02_02;
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

    private void debugMouseListener() {
        canvas.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println(e.getX() / BLOCK_SIZE + 1 + ":" + (e.getY() / BLOCK_SIZE + 1) + ", shapes on board:" + shapesOnBoard.size());
                /*for (int i = 0; i < bricksMap.size(); )
                    System.out.print(bricksMap.get(i) + (++i % GRID_WIDTH == 0 ? "\n" : ", "));*/
            }
        });
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
