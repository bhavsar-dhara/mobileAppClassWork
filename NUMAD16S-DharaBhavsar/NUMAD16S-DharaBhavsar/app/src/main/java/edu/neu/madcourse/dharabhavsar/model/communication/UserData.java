package edu.neu.madcourse.dharabhavsar.model.communication;

/**
 * Created by Dhara on 3/16/2016.
 */
public class UserData {
//    basically unique GCM registration key
    private String userId;
    private String userName;
    private int userIndividualBestScore;
    private int userCombineBestScore;
    private String teamPlayerName;      // name of the team player with whom the best score for
                                        // combine game was scored
    private boolean challengedGamePending;  // true if there is already pending game with the user
    private String challengedBy;
    private boolean combineGameRequest;   // true if there is a combine Play game request
    private int userPendingIndividualGameScore;
    private int userPendingCombineGameScore;
    private String pendingCombatGameKey;
    private String pendingCombineGameKey;

    public UserData() {
    }

    public UserData(String userId, String userName, int userIndividualBestScore,
                    int userCombineBestScore, String teamPlayerName, boolean challengedGamePending,
                    String challengedBy, boolean combineGameRequest,
                    int userPendingIndividualGameScore, int userPendingCombineGameScore,
                    String pendingCombatGameKey, String pendingCombineGameKey) {
        this.userId = userId;
        this.userName = userName;
        this.userIndividualBestScore = userIndividualBestScore;
        this.userCombineBestScore = userCombineBestScore;
        this.teamPlayerName = teamPlayerName;
        this.challengedGamePending = challengedGamePending;
        this.challengedBy = challengedBy;
        this.combineGameRequest = combineGameRequest;
        this.userPendingIndividualGameScore = userPendingIndividualGameScore;
        this.userPendingCombineGameScore = userPendingCombineGameScore;
        this.pendingCombatGameKey = pendingCombatGameKey;
        this.pendingCombineGameKey = pendingCombineGameKey;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public int getUserCombineBestScore() {
        return userCombineBestScore;
    }

    public int getUserIndividualBestScore() {
        return userIndividualBestScore;
    }

    public String getTeamPlayerName() {
        return teamPlayerName;
    }

    public boolean isChallengedGamePending() {
        return challengedGamePending;
    }

    public String getChallengedBy() {
        return challengedBy;
    }

    public boolean isCombineGameRequest() {
        return combineGameRequest;
    }

    public int getUserPendingIndividualGameScore() {
        return userPendingIndividualGameScore;
    }

    public int getUserPendingCombineGameScore() {
        return userPendingCombineGameScore;
    }

    public String getPendingCombatGameKey() {
        return pendingCombatGameKey;
    }

    public String getPendingCombineGameKey() {
        return pendingCombineGameKey;
    }
}
