package edu.neu.madcourse.dharabhavsar.ui.communication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;

import edu.neu.madcourse.dharabhavsar.RemoteClient;
import edu.neu.madcourse.dharabhavsar.gcmcomm.CommunicationMain;
import edu.neu.madcourse.dharabhavsar.model.communication.UserData;
import edu.neu.madcourse.dharabhavsar.ui.main.R;

public class MainActivityScraggle2 extends Activity {
    MediaPlayer mMediaPlayer;
    CommunicationMain mCommObj = new CommunicationMain();
    Context context;
    RemoteClient mRemoteClient;
    UserData mUserData;
    String LOG_TAG = "MainActivityScraggle2";
    String regId;
    private Handler mHandler = new Handler();
    private AlertDialog mDialog;
    private static final int TEXT_ID = 0;
    // ...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main_scraggle2);
        context = getApplicationContext();
        mRemoteClient = new RemoteClient(context);

        Log.e(LOG_TAG, "in onCreate");

        if(isNetworkAvailable(context)) {
            regId = mCommObj.getRegistrationId(context);
            if (!regId.isEmpty()) {
//            retrieve the userData
                Log.e(LOG_TAG, "an existing user" + regId);
                mRemoteClient.fetchUserData("userData", regId);
            } else {
//            get and store the userData
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

        Log.e(LOG_TAG, "exiting onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMediaPlayer = MediaPlayer.create(this, R.raw.erokia_piano_ambiance_1_freesound_org);
        mMediaPlayer.setVolume(0.5f, 0.5f);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
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
    }

    protected void onResumeActivity() {
        regId = mCommObj.getRegistrationId(context);
        if (!regId.isEmpty()) {
//            retrieve the userData
            Log.e(LOG_TAG, "an existing user" + regId);
            mRemoteClient.fetchUserData("userData", regId);
        } else {
//            get and store the userData
            Log.e(LOG_TAG, "not an existing user");
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startGameDialog();
                }
            }, 2000);
        }
    }

    public void startGameDialog(){
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
                            /*String userId = mCommObj.sendRegistrationIdToBackend(context);
                            Log.e(LOG_TAG, "User gcm reg id is : " + userId);
                            mUserData = new UserData(userId, userName, 0, 0);
                            mRemoteClient.saveUserData(mUserData);*/
                            mCommObj.sendRegistrationIdToBackend(context, userName);
                        } else {
                            newName.setError("Your name is required!");
                        }
                    }
                });
        Log.e(LOG_TAG, "exiting startGameDialog");

        mDialog = builder.show();
    }

    private static boolean isNetworkAvailable(Context context)
    {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null)
        {
            return false;
        } else
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
            {
                for (int i = 0; i < info.length; i++)
                {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void startReconnectNetDialog(){
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
                        if(isNetworkAvailable(context)) {
//                            TODO not a correct way to implement it - change it
                            onResumeActivity();
                        } else {
                            startReconnectNetDialog();
                        }
                    }
                });
        Log.e(LOG_TAG, "exiting startReconnectNetDialog");

        mDialog = builder.show();
    }
}
