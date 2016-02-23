package edu.neu.madcourse.dharabhavsar.scraggle;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import edu.neu.madcourse.dharabhavsar.main.R;

public class ScraggleGameActivity extends Activity {
    public static final String KEY_RESTORE = "key_restore";
    public static final String PREF_RESTORE = "pref_restore";
    private MediaPlayer mMediaPlayer;
    private Handler mHandler = new Handler();
    private ScraggleGameFragment mGameFragment;
    List<String> nineWords = new ArrayList<>();
    TextView mTextField;
    long mStartTime;
    private final int interval = 90000; //90 seconds ; 1 minute 30 seconds
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//         The below code didn't work for this activity
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        setContentView(R.layout.activity_game_scraggle);
        mGameFragment = (ScraggleGameFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_game_scraggle);
        mTextField = (TextView) findViewById(R.id.textView4);
        boolean restore = getIntent().getBooleanExtra(KEY_RESTORE, false);
        if (restore) {
            String gameData = getPreferences(MODE_PRIVATE)
                    .getString(PREF_RESTORE, null);
            if (gameData != null) {
                mGameFragment.putState(gameData);
            }
        }
        Log.d("Scraggle", "restore = " + restore);

        mTextField = (TextView) findViewById(R.id.textView4);
        countDownTimer = new CountDownTimer(90000, 1000) {

            public void onTick(long millisUntilFinished) {
                mTextField.setText("Seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                mTextField.setText("done!");
            }
        }.start();
    }

    public List<String> methodCallToAsyncTaskRunner(){
        try {
            new AsyncTaskRunner().execute().get();
//            Log.e("nineWords size", String.valueOf(nineWords.size()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return nineWords;
    }

    public void restartGame() {
        mGameFragment.restartGame();
    }

    /*public void reportWinner(final ScraggleTile.Owner winner) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
        }
        builder.setMessage(getString(R.string.declare_winner, winner));
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok_label,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
        final Dialog dialog = builder.create();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMediaPlayer = MediaPlayer.create(ScraggleGameActivity.this,
                        winner == ScraggleTile.Owner.X ? R.raw.winner_sound_freesound_org
                                : winner == ScraggleTile.Owner.O ? R.raw.loser_sound_freesound_org
                                : R.raw.draw_sound_freesound_org
                );
                mMediaPlayer.start();
                dialog.show();
            }
        }, 500);

        // Reset the board to the initial position
        mGameFragment.initGame();
    }*/

    public void startThinking() {
        View thinkView = findViewById(R.id.thinking_scraggle);
        thinkView.setVisibility(View.VISIBLE);
    }

    public void stopThinking() {
        View thinkView = findViewById(R.id.thinking_scraggle);
        thinkView.setVisibility(View.GONE);
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
        /*if (mStartTime == 0L) {
            mStartTime = System.currentTimeMillis();
            mHandler.removeCallbacks(mUpdateTimeTask);
            mHandler.postDelayed(mUpdateTimeTask, 100);
        }*/
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
        String gameData = mGameFragment.getState();
        getPreferences(MODE_PRIVATE).edit()
                .putString(PREF_RESTORE, gameData)
                .commit();
        Log.d("Scraggle", "state = " + gameData);
    }

//    http://stackoverflow.com/questions/20588736/how-can-i-shuffle-the-letters-of-a-word
    private static String scramble( Random random, String inputString )
    {
        // Convert your string into a simple char array:
        char a[] = inputString.toCharArray();
        // Scramble the letters using the standard Fisher-Yates shuffle,
        for( int i=0 ; i<a.length-1 ; i++ )
        {
            int j = random.nextInt(a.length-1);
            // Swap letters
            char temp = a[i]; a[i] = a[j];  a[j] = temp;
        }
        return new String( a );
    }

    private class AsyncTaskRunner extends AsyncTask<Void, Void, List<String>> {
        @Override
        protected List<String> doInBackground(Void... params) {
            String word = null;
//            to handle or restrict word repetition
            HashSet<String> wordSet = new HashSet<>();
            try {
                while(wordSet.size() < 9) {
                    Random random = new Random();
                    char c = (char) (random.nextInt(26) + 'a');
                    Resources res = getResources();
                    InputStream in_s = null;

                    int resID = getResources().getIdentifier(String.valueOf(c), "raw", getPackageName());
                    in_s = res.openRawResource(resID);

                    byte[] b = new byte[in_s.available()];
                    in_s.read(b);
                    String result = new String(b);
                    String[] strings = result.split("\\n");

                    List<String> stringList = new ArrayList<>();
                    for (String s : strings) {
                        if (s.trim().length() == 9)
                            stringList.add(s.trim());
                    }
                    Random yourRandom = new Random();
                    int index = yourRandom.nextInt(stringList.size());
                    word = stringList.get(index);
                    Log.e("nineLetter", word + " " + String.valueOf(word.length()));
                    wordSet.add(word);
                }
            } catch (Exception e) {
                Log.e("fetchNineWords", "Exception occurred");
            }
            nineWords = new ArrayList<String>(wordSet);
//            Log.e("nineWords ", String.valueOf(nineWords.size()));

            /*ScraggleGameFragment fragment = (ScraggleGameFragment) getFragmentManager().findFragmentById(R.id.fragment_game_scraggle);
            fragment.setLettersOnBoard(nineWords);*/

            return nineWords;
        }
    }

    public void toogleMute() {
        if(mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
        else {
            mMediaPlayer.start();
//            mMediaPlayer.setLooping(true);
        }
    }

//    http://stackoverflow.com/questions/1877417/how-to-set-a-timer-in-android
    /*private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            final long start = mStartTime;
            long millis = SystemClock.uptimeMillis() - start;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds     = seconds % 60;

            if (seconds < 10) {
                mTextField.setText("" + minutes + ":0" + seconds);
            } else {
                mTextField.setText("" + minutes + ":" + seconds);
            }

            Log.e("TIMER TEST", "" + minutes + ":" + seconds);

            mHandler.postAtTime(this,
                    start + (((minutes * 60) + seconds + 1) * 1000));
        }
    };*/
}
