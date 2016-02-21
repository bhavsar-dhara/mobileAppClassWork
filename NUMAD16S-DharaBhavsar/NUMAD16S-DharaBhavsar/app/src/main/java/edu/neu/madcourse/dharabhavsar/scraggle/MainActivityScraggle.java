package edu.neu.madcourse.dharabhavsar.scraggle;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;

import edu.neu.madcourse.dharabhavsar.main.R;

public class MainActivityScraggle extends Activity {
    MediaPlayer mMediaPlayer;
    // ...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_scraggle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMediaPlayer = MediaPlayer.create(this, R.raw.snapper4298__credits2_freesound_org);
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
    }
}
