package edu.neu.madcourse.dharabhavsar.ui.communication2player;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;

import edu.neu.madcourse.dharabhavsar.model.communication.UserData;
import edu.neu.madcourse.dharabhavsar.R;
import edu.neu.madcourse.dharabhavsar.utils.Constants;
import edu.neu.madcourse.dharabhavsar.utils.firebaseconn.RemoteClient;
import edu.neu.madcourse.dharabhavsar.utils.gcmcomm.CommunicationMain;
import edu.neu.madcourse.dharabhavsar.utils.internetconncheck.DetectInternetConn;

public class MainActivityScraggle2 extends Activity {
    private MediaPlayer mMediaPlayer;
    private CommunicationMain mCommObj = new CommunicationMain();
    private Context context;
    private RemoteClient mRemoteClient;
    private UserData mUserData;
    private String LOG_TAG = "MainActivityScraggle2";
    private String regId;
    private Handler mHandler = new Handler();
    private AlertDialog mDialog;
    private static final int TEXT_ID = 0;

    private DetectInternetConn dic;
    private static final boolean DBG = true;
    private SharedPreferences prefs;
    // ...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main_scraggle2);
        context = getApplicationContext();
        mRemoteClient = new RemoteClient(context);
        dic = new DetectInternetConn(getApplicationContext());
        prefs = context.getSharedPreferences(RemoteClient.class.getSimpleName(), Context.MODE_PRIVATE);

        if(DBG)
            Log.e(LOG_TAG, "in onCreate");

        if(dic.isNetworkAvailable()) {
            regId = mCommObj.getRegistrationId(context);
            if (!regId.isEmpty()) {
//            retrieve the userData
                if(DBG)
                    Log.e(LOG_TAG, "an existing user " + regId);
                String userKey = prefs.getString(Constants.USER_UNIQUE_KEY, "");
                Log.e(LOG_TAG, "an existing user key " + userKey);
            } else {
//            get and store the userData
                if(DBG)
                    Log.e(LOG_TAG, "not an existing user");
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startGameDialog();
                    }
                }, 2000);
            }
        } else {
            Log.e(LOG_TAG, "No Internet Connection");
            startReconnectNetDialog();
        }

        if(DBG)
            Log.e(LOG_TAG, "exiting onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMediaPlayer = MediaPlayer.create(this, R.raw.erokia_piano_ambiance_1_freesound_org);
        mMediaPlayer.setVolume(0.5f, 0.5f);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
//        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
//        registerReceiver(DetectInternetConnBroadcastReceiver.networkReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
        // Get rid of the about dialog if it's still up
        if (mDialog != null)
            mDialog.dismiss();
//        unregisterReceiver(networkReceiver);
    }

    public void startGameDialog(){
        if(DBG)
            Log.e(LOG_TAG, "in startGameDialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivityScraggle2.this,
                AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);

        builder.setTitle(getResources().getString(R.string.first_time_user));
        builder.setMessage(getResources().getString(R.string.whats_your_name));

        final EditText newName = new EditText(context);
        newName.setId(TEXT_ID);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        newName.setLayoutParams(lp);
        newName.setHint(getResources().getString(R.string.hint_name));
        newName.setInputType(InputType.TYPE_CLASS_TEXT);
        newName.setText("");
        newName.setTextColor(getResources().getColor(R.color.blue_color));
        builder.setView(newName);

        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok_label,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!TextUtils.isEmpty(newName.getText().toString().trim())) {
                            String userName = newName.getText().toString().trim();
                            mCommObj.sendRegistrationIdToBackend(context, userName);
                        } else {
                            newName.setError("Your name is required!");
                        }
                    }
                });
        if(DBG)
            Log.e(LOG_TAG, "exiting startGameDialog");

        mDialog = builder.show();
    }

    public void startReconnectNetDialog(){
        if(DBG)
            Log.e(LOG_TAG, "in startReconnectNetDialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivityScraggle2.this,
                AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);

        builder.setTitle(getResources().getString(R.string.net_lost_title));
        builder.setMessage(getResources().getString(R.string.net_lost_text));

        builder.setCancelable(false);
        builder.setPositiveButton(R.string.reconnect_label,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (dic.isNetworkAvailable()) {
//                            TODO changed it - testing left
                            Intent intent = new Intent(MainActivityScraggle2.this, MainActivityScraggle2.class);
                            startActivity(intent);
                            finish();
                        } else {
                            startReconnectNetDialog();
                        }
                    }
                });
        if(DBG)
            Log.e(LOG_TAG, "exiting startReconnectNetDialog");

        mDialog = builder.show();
    }
}
