package edu.neu.madcourse.dharabhavsar.scraggle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import edu.neu.madcourse.dharabhavsar.main.R;

/**
 * Created by Dhara on 2/26/2016.
 */
public class ScraggleGameActivity2 extends Activity {

    public static final String KEY_RESTORE = "key_restore";
    public static final String PREF_RESTORE = "pref_restore";
    private ScraggleGameFragment2 mGameFragment;
    private MediaPlayer mMediaPlayer;
    private Handler mHandler = new Handler();
    TextView mTextField;
    private AlertDialog mDialog;
    private final int interval = 90000; //90 seconds ; 1 minute 30 seconds
    long savedRemainingInterval;
    MyCount counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_scraggle2);
        mGameFragment = (ScraggleGameFragment2) getFragmentManager()
                .findFragmentById(R.id.fragment_game_scraggle2);
        mTextField = (TextView) findViewById(R.id.textView4);
        counter = new MyCount(interval, 1000);
        counter.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMediaPlayer = MediaPlayer.create(this, R.raw.erokia_timelift_rhodes_piano_freesound_org);
        mMediaPlayer.start();
        mMediaPlayer.setLooping(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(null);
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
        String gameData = mGameFragment.getState();
        getPreferences(MODE_PRIVATE).edit()
                .putString(PREF_RESTORE, gameData)
                .commit();
        Log.d("Scraggle", "state = " + gameData);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        this.finish();
    }

    public void toogleMute() {
        Log.e("Mute TEST toogleMute", "inside func");
        if (mMediaPlayer.isPlaying()) {
            Log.e("Mute TEST toogleMute", "inside for pause");
            mMediaPlayer.pause();
        } else {
            Log.e("Mute TEST toogleMute", "inside for start");
            mMediaPlayer.start();
        }
    }

    public class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            mTextField.setText("Game Over");
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mTextField.setText("Seconds Remaining: " + millisUntilFinished / 1000);
            savedRemainingInterval = millisUntilFinished;
        }
    }

    protected void onPauseGame() {
        counter.cancel();
        mMediaPlayer.pause();
//        Log.e("Timer TEST Pause", String.valueOf(savedRemainingInterval));
    }

    protected void onResumeGame() {
        counter = new MyCount(savedRemainingInterval, 1000);
        counter.start();
        mMediaPlayer.start();
//        Log.e("Timer TEST Resume", String.valueOf(savedRemainingInterval));
    }

}
