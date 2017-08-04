package com.buttonHeck.tetris.window.game;

import com.buttonHeck.tetris.model.Shape;
import com.buttonHeck.tetris.util.Options;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;

import static com.buttonHeck.tetris.window.game.GameWindow.BLOCK_SIZE;

public class Renderer {

    private static final int SIDE_PART_SCREEN_WIDTH = 180;
    private static int SCREEN_WIDTH, SCREEN_HEIGHT;
    private static int GRID_WIDTH, GRID_HEIGHT;

    private GameWindow gameWindow;
    private Canvas gameCanvas;
    private BufferStrategy bs;
    private Graphics graphics;
    private BufferedImage boardImage;
    private int boardColor;
    private int[] pixels;
    private boolean boardColorChanged;
    private ArrayList<Shape> shapesOnBoard;

    Renderer(GameWindow window, ArrayList<Shape> shapes) {
        this.gameWindow = window;
        shapesOnBoard = shapes;
        initializeGameWindowParameters();
        initializeCanvasParameters();
        initializeGraphicsData();
    }

    private void initializeGameWindowParameters() {
        GRID_WIDTH = GameWindow.getGridWidth();
        GRID_HEIGHT = GameWindow.getGridHeight();
        SCREEN_WIDTH = GRID_WIDTH * BLOCK_SIZE;
        SCREEN_HEIGHT = GRID_HEIGHT * BLOCK_SIZE;
    }

    private void initializeCanvasParameters() {
        gameCanvas = new Canvas();
        gameCanvas.setPreferredSize(new Dimension(SCREEN_WIDTH + SIDE_PART_SCREEN_WIDTH, SCREEN_HEIGHT));
        gameCanvas.setMaximumSize(new Dimension(SCREEN_WIDTH + SIDE_PART_SCREEN_WIDTH, SCREEN_HEIGHT));
        gameCanvas.setMinimumSize(new Dimension(SCREEN_WIDTH + SIDE_PART_SCREEN_WIDTH, SCREEN_HEIGHT));
        gameWindow.add(gameCanvas);
    }

    private void initializeGraphicsData() {
        boardImage = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
        pixels = new int[SCREEN_HEIGHT * SCREEN_WIDTH];
        createBoardImageData();
    }

    void createBoardImageData() {
        boardColor = 0xFF889999;
        createBoardImageData(boardColor);
        boardColorChanged = false;
    }

    private void createBoardImageData(int boardColor) {
        if (gameWindow.isPaused())
            boardColor = 0x333333;
        int initialColor = boardColor;
        int[] colors = createGradientData(initialColor);
        paintGradientImage(colors);
    }

    private int[] createGradientData(int initialColor) {
        int[] colors = new int[GRID_HEIGHT * GRID_WIDTH];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = initialColor;
            initialColor += 0x00_01_01_02;
        }
        return colors;
    }

    private void paintGradientImage(int[] colors) {
        pixels = ((DataBufferInt) boardImage.getRaster().getDataBuffer()).getData();
        int colorIndex = 0;
        for (int y = 0; y < SCREEN_HEIGHT; y++) {
            if (y % BLOCK_SIZE == 0) colorIndex += 10;
            for (int x = 0; x < SCREEN_WIDTH; x++) {
                pixels[x + y * SCREEN_WIDTH] = colors[colorIndex];
                if (colorIndex < colors.length && x % BLOCK_SIZE == 0) {
                    colorIndex++;
                }
            }
            colorIndex = 0;
        }
    }

    void createBoardImageDataColorChanged(int adjacentFullRows) {
        boardColorChanged = true;
        boardColor += adjacentFullRows * 0xFF080808;
        createBoardImageData(boardColor);
    }

    void renderBeforeFirstUpdate() {
        if (initializeBufferStrategy())
            renderBeforeFirstUpdate();
        graphics = bs.getDrawGraphics();
        graphics.drawImage(boardImage, 0, 0, null);
        graphics.drawLine(SCREEN_WIDTH, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        bs.show();
        graphics.dispose();
    }

    void render(Shape nextShape) {
        if (initializeBufferStrategy())
            return;
        graphics = bs.getDrawGraphics();
        graphics.drawImage(boardImage, 0, 0, null);
        renderGameObjects();
        renderSidePart(nextShape);
        if (boardColorChanged)
            createBoardImageData();
        bs.show();
        graphics.dispose();
    }

    private boolean initializeBufferStrategy() {
        bs = gameCanvas.getBufferStrategy();
        if (bs == null) {
            gameCanvas.createBufferStrategy(2);
            return true;
        }
        return false;
    }

    private void renderGameObjects() {
        for (int i = 0; i < shapesOnBoard.size(); i++)
            shapesOnBoard.get(i).render(graphics, true);
    }

    private void renderSidePart(Shape nextShape) {
        graphics.setFont(new Font("Arial", Font.PLAIN, 20));
        graphics.clearRect(SCREEN_WIDTH, 0, 200, SCREEN_HEIGHT);
        if (Options.getShowNext()) {
            renderNextShape(nextShape);
        }
        graphics.drawLine(SCREEN_WIDTH, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        graphics.drawString("Score " + gameWindow.getScore(), SCREEN_WIDTH + 10, 240);
        graphics.drawString("Speed: " + Options.getSpeed(), SCREEN_WIDTH + 10, 270);
    }

    private void renderNextShape(Shape nextShape) {
        nextShape.render(graphics, false);
        graphics.drawString("Next:", SCREEN_WIDTH + 60, 40);
    }

    void renderGameOverScreen(int score) {
        drawBlackScreen();
        graphics.setColor(Color.WHITE);
        drawGameOverMessage(score);
        drawEscapeHint();
        bs.show();
        graphics.dispose();
    }

    private void drawBlackScreen() {
        graphics = bs.getDrawGraphics();
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, SCREEN_WIDTH + SIDE_PART_SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    private void drawGameOverMessage(int score) {
        graphics.setFont(new Font("Arial", Font.BOLD, 38));
        String gameOverMessage = "Game over";
        FontMetrics metrics = graphics.getFontMetrics();
        graphics.drawString(gameOverMessage, (SCREEN_WIDTH + SIDE_PART_SCREEN_WIDTH) / 2 - metrics.stringWidth(gameOverMessage) / 2, SCREEN_HEIGHT / 2 - 60);
        String scoreMessage = "" + score;
        graphics.drawString(scoreMessage, (SCREEN_WIDTH + SIDE_PART_SCREEN_WIDTH) / 2 - metrics.stringWidth(scoreMessage) / 2, SCREEN_HEIGHT / 2);
    }

    private void drawEscapeHint() {
        graphics.setFont(new Font("Arial", Font.BOLD, 28));
        FontMetrics metrics2 = graphics.getFontMetrics();
        String escHint = "Press ESC to back to menu";
        graphics.drawString(escHint, (SCREEN_WIDTH + SIDE_PART_SCREEN_WIDTH) / 2 - metrics2.stringWidth(escHint) / 2, SCREEN_HEIGHT / 2 + 60);
    }

    //Getters

    Canvas getCanvas() {
        return gameCanvas;
    }

    public static int getScreenWidth() {
        return SCREEN_WIDTH;
    }
}
