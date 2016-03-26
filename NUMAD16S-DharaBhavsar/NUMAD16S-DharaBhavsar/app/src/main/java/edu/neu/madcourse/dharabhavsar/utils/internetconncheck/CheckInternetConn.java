package edu.neu.madcourse.dharabhavsar.utils.internetconncheck;

/**
 * Created by Dhara on 3/25/2016.
 */
public class CheckInternetConn {

    private static CheckInternetConn instance = null;

    public CheckInternetConn() {
//        TODO - Trying to make a singleton class
    }

    public static CheckInternetConn getInstance() {
        if (instance == null) {
            instance = new CheckInternetConn();
        }
        return instance;
    }
}
