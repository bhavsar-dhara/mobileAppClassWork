package edu.neu.madcourse.dharabhavsar.model.communication;

/**
 * Created by Dhara on 3/16/2016.
 *
 * POJO class to store and retrieve data from Firebase
 */
public class GameData {
    private int scoreCombinePlay;
    private int p1Score1;
    private int p1Score2;
    private int p2Score1;
    private int p2Score2;
    private String player1ID;
    private String player2ID;
    private char[][] gameLetterState;
    private boolean firstCombatPlay;        // ???
    private boolean secondCombatPlay;       // ???
    private boolean combinePlay;            // true if it is a combine play
    private boolean gameOver;               // true if both the players have played it

    public GameData() {
    }

    public GameData(int scoreCombinePlay, int score1P1, int score2P1, int score1P2, int score2P2,
                    String player1ID, String player2ID, char[][] gameLetterState,
                    boolean isFirstCombatPlay, boolean isSecondCombatPlay, boolean isCombinePlay,
                    boolean isGameOver) {
        this.scoreCombinePlay = scoreCombinePlay;
        this.p1Score1 = score1P1;
        this.p1Score2 = score2P1;
        this.p2Score1 = score1P2;
        this.p2Score2 = score2P2;
        this.player1ID = player1ID;
        this.player2ID = player2ID;
        this.gameLetterState = gameLetterState;
        this.firstCombatPlay = isFirstCombatPlay;
        this.secondCombatPlay = isSecondCombatPlay;
        this.combinePlay = isCombinePlay;
        this.gameOver = isGameOver;
    }

    public int getScoreCombinePlay() {
        return scoreCombinePlay;
    }

    public int getP1Score1() {
        return p1Score1;
    }

    public int getP1Score2() {
        return p1Score2;
    }

    public int getP2Score1() {
        return p2Score1;
    }

    public int getP2Score2() {
        return p2Score2;
    }

    public String getPlayer1ID() {
        return player1ID;
    }

    public String getPlayer2ID() {
        return player2ID;
    }

    public char[][] getGameLetterState() {
        return gameLetterState;
    }

    public boolean isFirstCombatPlay() {
        return firstCombatPlay;
    }

    public boolean isSecondCombatPlay() {
        return secondCombatPlay;
    }

    public boolean isCombinePlay() {
        return combinePlay;
    }

    public boolean isGameOver() {
        return gameOver;
    }
}
