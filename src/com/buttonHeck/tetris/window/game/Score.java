package com.buttonHeck.tetris.window.game;

class Score {
    private int score, nextSpeedIncreaseScore;

    Score() {
        score = 0;
        nextSpeedIncreaseScore = 10;
    }

    int getScore() {
        return score;
    }

    void plus(int adjacentFullRows) {
        score += adjacentFullRows * adjacentFullRows;
    }

    boolean reachedIncreaseSpeed() {
        boolean reached = score >= nextSpeedIncreaseScore;
        if (reached)
            nextSpeedIncreaseScore += 10;
        return reached;
    }
}
