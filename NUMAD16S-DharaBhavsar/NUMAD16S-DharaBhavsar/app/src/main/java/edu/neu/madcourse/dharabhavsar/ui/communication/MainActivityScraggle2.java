package edu.neu.madcourse.dharabhavsar.ui.communication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

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
    // ...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main_scraggle2);
        context = getApplicationContext();
        mRemoteClient = new RemoteClient(context);

        Log.e(LOG_TAG, "in onCreate");

        regId = mCommObj.getRegistrationId(context);
        if (!regId.isEmpty()) {
//            retrieve the userData
            Log.e(LOG_TAG, "an existing user" + regId);
            mRemoteClient.fetchUserData("userData", regId);
        }
        else {
//            get and store the userData
            Log.e(LOG_TAG, "not an existing user");
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startGameDialog();
                }
            }, 2000);
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

    public void startGameDialog(){
        Log.e(LOG_TAG, "in startGameDialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivityScraggle2.this);

        builder.setTitle(getResources().getString(R.string.first_time_user));
        builder.setMessage(getResources().getString(R.string.whats_your_name));

        final EditText newName = new EditText(context);
        newName.setHint("Enter your name here...");

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
        newName.setFocusable(true);
//        requestfocus();
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(newName.getWindowToken(), 0);
        Log.e(LOG_TAG, "exiting startGameDialog");
        builder.setView(newName);
        /*mDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        mDialog.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);*/
        mDialog = builder.show();
    }
}
