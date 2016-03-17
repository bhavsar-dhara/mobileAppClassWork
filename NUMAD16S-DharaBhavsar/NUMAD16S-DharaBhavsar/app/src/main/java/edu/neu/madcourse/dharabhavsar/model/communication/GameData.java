package edu.neu.madcourse.dharabhavsar.model.communication;

/**
 * Created by Dhara on 3/16/2016.
 *
 * POJO class to store and retrieve data from Firebase
 */
public class GameData {
    private int gameTime;
    private int score1;
    private int score2;
    private boolean isPhaseTwo;
    private int mLastLarge;
    private int mLastSmall;
    private boolean isGameEnd;

    public GameData() {
    }

    public GameData(int gameTime, int score1, int score2, boolean isPhaseTwo, int mLastLarge,
                    int mLastSmall, boolean isGameEnd) {
        this.gameTime = gameTime;
        this.score1 = score1;
        this.score2 = score2;
        this.isPhaseTwo = isPhaseTwo;
        this.mLastLarge = mLastLarge;
        this.mLastSmall = mLastSmall;
        this.isGameEnd = isGameEnd;
    }

    public int getGameTime() {
        return gameTime;
    }

    public int getScore1() {
        return score1;
    }

    public int getScore2() {
        return score2;
    }

    public boolean isPhaseTwo() {
        return isPhaseTwo;
    }

    public int getmLastLarge() {
        return mLastLarge;
    }

    public int getmLastSmall() {
        return mLastSmall;
    }

    public boolean isGameEnd() {
        return isGameEnd;
    }
}
