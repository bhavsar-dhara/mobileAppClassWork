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
    private boolean isChallengedGamePending;  // true if there is already pending game with the user
    private String challengedBy;
    private boolean isCombineGameRequest;   // true if there is a combine Play game request

    public UserData() {
    }

    public UserData(String userId, String userName, int userIndividualBestScore,
                    int userCombineBestScore, String teamPlayerName,
                    boolean isChallengedGamePending, String challengedBy,
                    boolean isCombineGameRequest) {
        this.userId = userId;
        this.userName = userName;
        this.userIndividualBestScore = userIndividualBestScore;
        this.userCombineBestScore = userCombineBestScore;
        this.teamPlayerName = teamPlayerName;
        this.isChallengedGamePending = isChallengedGamePending;
        this.challengedBy = challengedBy;
        this.isCombineGameRequest = isCombineGameRequest;
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
        return isChallengedGamePending;
    }

    public String getChallengedBy() {
        return challengedBy;
    }

    public boolean isCombineGameRequest() {
        return isCombineGameRequest;
    }
}
