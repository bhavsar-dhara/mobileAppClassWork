package edu.neu.madcourse.dharabhavsar.utils.gcmcomm;

public class CommunicationConstants
{
    public static final String TAG = "GCM_Globals";
    public static final String GCM_SENDER_ID = "250764059657";
    public static final String BASE_URL = "https://android.googleapis.com/gcm/send";
    public static final String PREFS_NAME = "GCM_Communication";

    public static final String GCM_API_KEY = "AIzaSyDPLi-KX2qABaooTnfDmNwbyhjHyFAP1rk";
    public static final int SIMPLE_NOTIFICATION = 22;
    public static final long GCM_TIME_TO_LIVE = 60L * 60L * 24L * 7L * 4L; // 4 Weeks
    public static int mode = 0;

    public static String alertText = "";
    public static String titleText = "";
    public static String contentText = "";

    public static final String regAlertText = "Register Notification";
    public static final String unregAlertText = "Unregister Notification";
    public static final String msgAlertText = "Message Notification";
    public static final String combatAlertText = "Scraggle Combat Word Game";
    public static final String combineAlertText = "Scraggle Combine Word Game";

    public static String gameKey = "";
    public static String combineGameKey = "";

}