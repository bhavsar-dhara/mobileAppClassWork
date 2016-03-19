package edu.neu.madcourse.dharabhavsar;

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
        /*if(intent.getExtras()!=null) {
            NetworkInfo ni=(NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
            if(ni!=null && ni.getState()==NetworkInfo.State.CONNECTED) {
                // we're connected
            }
        }
        // we're not connected*/
    }
}
