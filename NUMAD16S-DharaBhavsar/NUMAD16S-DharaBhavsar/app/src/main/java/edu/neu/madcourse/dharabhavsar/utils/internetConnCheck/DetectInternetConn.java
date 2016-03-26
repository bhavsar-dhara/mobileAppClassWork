package edu.neu.madcourse.dharabhavsar.utils.internetConnCheck;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by Dhara on 3/19/2016.
 */
public class DetectInternetConn {

    private Context context;

    public DetectInternetConn(Context context){
        this.context = context;
    }

    public boolean isNetworkAvailable() {
        Log.e("DetectInternetConn", "in isNetworkAvailable");
        ConnectivityManager connectivity = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
