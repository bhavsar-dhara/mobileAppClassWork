package edu.neu.madcourse.dharabhavsar.utils.internetconncheck;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Dhara on 3/19/2016.
 */
public class DetectInternetConnBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("dicBroadcastReceiver", "in onReceive");
        DetectInternetConn dic = new DetectInternetConn(context);
        dic.isNetworkAvailable();
    }
}
