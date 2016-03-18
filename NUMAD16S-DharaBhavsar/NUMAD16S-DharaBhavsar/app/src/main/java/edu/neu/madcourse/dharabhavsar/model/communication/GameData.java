package edu.neu.madcourse.dharabhavsar.model.communication;

/**
 * Created by Dhara on 3/16/2016.
 *
 * POJO class to store and retrieve data from Firebase
 */
public class GameData {
    private int scoreCombinePlay;
    private int score1P1;
    private int score2P1;
    private int score1P2;
    private int score2P2;
    private String player1ID;
    private String player2ID;
    private char[][] gameLetterState;
    private boolean isFirstCombatPlay;
    private boolean isSecondCombatPlay;
    private boolean isCombinePlay;

    public GameData() {
    }

    public GameData(int scoreCombinePlay, int score1P1, int score2P1, int score1P2, int score2P2,
                    String player1ID, String player2ID, char[][] gameLetterState,
                    boolean isFirstCombatPlay, boolean isSecondCombatPlay, boolean isCombinePlay) {
        this.scoreCombinePlay = scoreCombinePlay;
        this.score1P1 = score1P1;
        this.score2P1 = score2P1;
        this.score1P2 = score1P2;
        this.score2P2 = score2P2;
        this.player1ID = player1ID;
        this.player2ID = player2ID;
        this.gameLetterState = gameLetterState;
        this.isFirstCombatPlay = isFirstCombatPlay;
        this.isSecondCombatPlay = isSecondCombatPlay;
        this.isCombinePlay = isCombinePlay;
    }

    public int getScoreCombinePlay() {
        return scoreCombinePlay;
    }

    public int getScore1P1() {
        return score1P1;
    }

    public int getScore2P1() {
        return score2P1;
    }

    public int getScore1P2() {
        return score1P2;
    }

    public int getScore2P2() {
        return score2P2;
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
        return isFirstCombatPlay;
    }

    public boolean isSecondCombatPlay() {
        return isSecondCombatPlay;
    }

    public boolean isCombinePlay() {
        return isCombinePlay;
    }
}
