package edu.neu.madcourse.dharabhavsar.utils;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by Bhavsar, Dhara on 11/30/16.
 */

public class CommonUtils {

    private static MediaPlayer mMediaPlayer;

    public static void playSound(Context context, int soundID) {
        mMediaPlayer = MediaPlayer.create(context, soundID);
        mMediaPlayer.setVolume(0.5f, 0.5f);
        mMediaPlayer.start();
    }
}
