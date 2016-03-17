package edu.neu.madcourse.dharabhavsar.model.communication;

/**
 * Created by Dhara on 3/16/2016.
 */
public class UserData {
//    basically unique GCM registration key
    private String userId;
    private String userName;
    private int userLastScore;
    private int userBestScore;

    public UserData() {
    }

    public UserData(String userId, String userName, int userLastScore, int userBestScore) {
        this.userId = userId;
        this.userName = userName;
        this.userLastScore = userLastScore;
        this.userBestScore = userBestScore;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public int getUserLastScore() {
        return userLastScore;
    }

    public int getUserBestScore() {
        return userBestScore;
    }
}
