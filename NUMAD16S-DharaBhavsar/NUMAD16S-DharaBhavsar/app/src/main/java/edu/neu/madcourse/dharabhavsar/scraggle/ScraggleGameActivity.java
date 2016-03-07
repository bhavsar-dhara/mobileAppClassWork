package edu.neu.madcourse.dharabhavsar.scraggle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import edu.neu.madcourse.dharabhavsar.dictionary.TrieLookup;
import edu.neu.madcourse.dharabhavsar.main.R;

public class ScraggleGameActivity extends Activity {
    public static final String KEY_RESTORE = "key_restore";
    public static final String PREF_RESTORE = "pref_restore";
    private MediaPlayer mMediaPlayer;
    private Handler mHandler = new Handler();
    private ScraggleGameFragment mGameFragment;
    List<String> nineWords = new ArrayList<>();
    TextView mTextField;
    private final int interval = 90000; //90 seconds ; 1 minute 30 seconds
    long savedRemainingInterval = 0;
    MyCount counter;
    String resultStr = "";
    String finalResult = "";
    String result = "";
    ArrayList<String> vocabList = new ArrayList<String>();
    String insertedText = "";
    private AlertDialog mDialog;
    TrieLookup trie;
    Boolean resFlag = false;
    TextView mScoreTextField;
    int score = 0;
    int score2 = 0;
    boolean restore;
    boolean isResumeFlag = false;
    private boolean isPhaseTwo = false;
    private String gameData = "";

    static final String STATE_SCORE = "playerScore";
    static final String STATE_LEVEL = "playerLevel";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check whether we're recreating a previously destroyed instance
        /*if (savedInstanceState != null) {
            // Restore value of members from saved state
//            score = savedInstanceState.getInt(STATE_SCORE);
            isPhaseTwo = savedInstanceState.getBoolean(STATE_LEVEL);
        } else {
            // Probably initialize members with default values for a new instance
        }
        Log.e("GAMEActivity", "isPhaseTwo = " + isPhaseTwo);*/
        Bundle b = this.getIntent().getExtras();
        if (b != null) {
            isPhaseTwo = b.getBoolean("isTwoFlag");
            gameData = b.getString("gameData");
        }
        Log.e("ScraggleActivity", "isPhaseTwo = " + isPhaseTwo);
        Log.e("ScraggleActivity", "savedRemainingInterval = " + savedRemainingInterval);

//         The below code didn't work for this activity
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        setContentView(R.layout.activity_game_scraggle);
        mGameFragment = (ScraggleGameFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_game_scraggle);
        mTextField = (TextView) findViewById(R.id.timerView);
        restore = getIntent().getBooleanExtra(KEY_RESTORE, false);
        if (restore) {
            String gameData = getPreferences(MODE_PRIVATE)
                    .getString(PREF_RESTORE, null);
//            Log.e("ScraggleActivity", "gameData = " + gameData);
            if (gameData != null) {
                mGameFragment.putState(gameData);
            }
        }
        Log.e("Scraggle", "restore = " + restore);

        mTextField = (TextView) findViewById(R.id.timerView);
        if(isPhaseTwo) {
            if(savedRemainingInterval < 2000) {
//                Log.e("ScraggleActivity", "interval = " + interval);
                counter = new MyCount(interval, 1000);
            } else {
                counter = new MyCount(savedRemainingInterval, 1000);
            }
        } else {
            if (savedRemainingInterval > 0) {
                counter = new MyCount(savedRemainingInterval, 1000);
            } else {
                counter = new MyCount(interval, 1000);
            }
        }
        counter.start();
        mScoreTextField = (TextView) findViewById(R.id.scoreView);
        mScoreTextField.setText("Score = " + String.valueOf(score));
    }

    /*@Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.e("onSaveInstanceState", "isPhaseTwo = " + isPhaseTwo);
        // Save the user's current game state
//        savedInstanceState.putInt(STATE_SCORE, score);
        savedInstanceState.putBoolean(STATE_LEVEL, isPhaseTwo);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }*/

    public List<String> methodCallToAsyncTaskRunner() {
        try {
            if(!isPhaseTwo) {
                new AsyncTaskRunner().execute().get();
            }
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

    public void startThinking() {
        View thinkView = findViewById(R.id.thinking_scraggle);
        thinkView.setVisibility(View.VISIBLE);
    }

    public void stopThinking() {
        if(!isPhaseTwo) {
            mScoreTextField.setText("Score = " + String.valueOf(score));
        } else {
            mScoreTextField.setText("Score = " + String.valueOf(score+score2));
        }
        View thinkView = findViewById(R.id.thinking_scraggle);
        thinkView.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("onResume", "inside resume");
        mMediaPlayer = MediaPlayer.create(this, R.raw.erokia_timelift_rhodes_piano_freesound_org);
        mMediaPlayer.start();
        mMediaPlayer.setLooping(true);
        if (savedRemainingInterval > 0 && isResumeFlag) {
            counter = new MyCount(savedRemainingInterval, 1000);
            counter.start();
        }
        if(!isPhaseTwo) {
            mScoreTextField.setText("Score = " + String.valueOf(score));
        } else {
            mScoreTextField.setText("Score = " + String.valueOf(score + score2));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("onPause", "inside pause");
        mHandler.removeCallbacks(null);
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
        counter.cancel();
        String gameData = mGameFragment.getState();
        getPreferences(MODE_PRIVATE).edit()
                .putString(PREF_RESTORE, gameData)
                .commit();
        Log.d("Scraggle", "state = " + gameData);
    }

    private class AsyncTaskRunner extends AsyncTask<Void, Void, List<String>> {
        @Override
        protected List<String> doInBackground(Void... params) {
            String word = null;
//            to handle or restrict word repetition
            HashSet<String> wordSet = new HashSet<>();
            try {
                while (wordSet.size() < 9) {
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
//                    Log.e("nineLetter", word + " " + String.valueOf(word.length()));
                    wordSet.add(word);
                }
            } catch (Exception e) {
                Log.e("fetchNineWords", "Exception occurred");
            }
            nineWords = new ArrayList<String>(wordSet);
            return nineWords;
        }
    }

    public void toogleMute() {
//        Log.e("Mute TEST toogleMute", "inside func");
        if (mMediaPlayer.isPlaying()) {
//            Log.e("Mute TEST toogleMute", "inside for pause");
            mMediaPlayer.pause();
        } else {
//            Log.e("Mute TEST toogleMute", "inside for start");
            mMediaPlayer.start();
        }
    }

    //    http://stackoverflow.com/questions/9630398/how-can-i-pause-the-timer-in-android/9663508#9663508
    public class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            if(!isPhaseTwo) {
                mTextField.setText("DONE");
//            TODO method to disable the whole grid and go to phase 2
//            mGameFragment.disableLetterGrid();
                AlertDialog.Builder builder = new AlertDialog.Builder(ScraggleGameActivity.this);
                builder.setTitle(R.string.phase_change_title);
                builder.setMessage(R.string.phase_change_text);
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.ok_label,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // take to Phase 2
                                isPhaseTwo = true;
//                                mGameFragment.setIsPhaseTwo(true);
//                                startActivity(new Intent(ScraggleGameActivity.this, ScraggleGameActivity.class));
                                Intent intent = new Intent(ScraggleGameActivity.this, ScraggleGameActivity.class);
                                intent.putExtra("gameData", mGameFragment.getState());
                                intent.putExtra("isTwoFlag", isPhaseTwo);
//                                intent.putExtra("isTwoFlagFrag", isPhaseTwo);
                                startActivity(intent);
                                finish();
                            }
                        });
                mDialog = builder.show();
            } else {
                mTextField.setText("Game Over");
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mTextField.setText("Seconds Remaining: " + millisUntilFinished / 1000);
            savedRemainingInterval = millisUntilFinished;
        }
    }

    protected void onPauseGame() {
        Log.e("onPauseGame", "inside pause");
        counter.cancel();
        mMediaPlayer.pause();
        mGameFragment.disableLetterGrid();
        isResumeFlag = false;
    }

    protected void onResumeGame() {
        Log.e("onResumeGame", "inside pause");
        counter = new MyCount(savedRemainingInterval, 1000);
        counter.start();
        mMediaPlayer.start();
        mGameFragment.enableLetterGrid();
        isResumeFlag = true;
    }

    protected void onQuitGame() {
        Log.e("onQuit", "inside pause");
        isResumeFlag = false;
        String gameData = mGameFragment.getState();
        getPreferences(MODE_PRIVATE).edit()
                .putString(PREF_RESTORE, gameData)
                .commit();
    }

    private class AsyncTaskRunner0 extends AsyncTask<String, Void, List<String>> {
        @Override
        protected List<String> doInBackground(String... params) {
            String word = params[0];
            try {
                try {
//                    Log.e("Inside AS0", "Inside AS0");
                    Resources res = getResources();
                    InputStream in_s = null;
                    String fileName = String.valueOf(word.charAt(0));
                    int resID = getResources().getIdentifier(fileName, "raw", getPackageName());
                    in_s = res.openRawResource(resID);
                    byte[] b = new byte[in_s.available()];
                    in_s.read(b);
                    result = new String(b);
                    String[] strings = result.split("\\n");
//                    Log.e("INSERT", "inserting count = " + strings.length);
                    for (String s : strings) {
                        vocabList.add(s);
                    }
//                    Log.e("INSERT", "inserted");
                } catch (IOException e) {
                    Log.e("ERROR", "not inserted");
                }
            } catch (Exception e) {
                Log.e("AsyncTaskRunner", "Exception occurred");
                Log.e("AsyncTaskRunner Error", e.getMessage().toString());
            }
            return vocabList;
        }
    }

    private class AsyncTaskRunner2 extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            insertedText = params[0];
            String resp = "";
            boolean isThereInDict = false;
            try {
//                Log.e("AsyncTaskRunner2", "insertedText = " + insertedText);
                for (String s : vocabList) {
                    if (insertedText.equals(s.trim())) {
                        resp = insertedText;
                    }
                }
                if (!resp.equals("")) {
                    isThereInDict = true;
                }
//                Log.e("TEST HASHMAP resp", resp);
            } catch (Exception e) {
                Log.e("AsyncTaskRunner2", "Error encountered");
            }
//            Log.e("AsyncTaskRunner2", "result = " + resp);
            return isThereInDict;
        }
    }

    //    method to be called in the asyncTask for the Word Game ever
    public Boolean searchWord(String str) {
        String result1 = "";
        String word = str;
//        Log.e("searchWord WORD LEN", "afterTextChanged: " + word.length() + " word = " + word);
        if (word.length() >= 3) {
            try {
                new AsyncTaskRunner0().execute(word).get();
                resFlag = new AsyncTaskRunner2().execute(word).get();
//                Log.e("searchWord", "resFlag = " + resFlag);
//                Log.e("searchWord", finalResult);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            result1 = resultStr;
//            Log.e("searchWord RESULT STR", "afterTextChanged: RESULT STRING = " + result1);
        } else {
            List<String> list = Arrays.asList(resultStr.split("\n"));
            Set<String> uniqueWords = new HashSet<String>(list);
            finalResult = "";
            for (String s1 : uniqueWords) {
                System.out.println(word + ": " + Collections.frequency(list, s1));
                finalResult = finalResult + s1 + "\n";
            }
//            Log.e("searchWord", finalResult);
        }
        return resFlag;
    }

    public long getSavedRemainingInterval() {
        return this.savedRemainingInterval;
    }

    public void setSavedRemainingInterval(long savedInterval) {
        savedRemainingInterval = savedInterval;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int savedScore) {
        score = savedScore;
    }

    public int getScore2() {
        return this.score2;
    }

    public void setScore2(int savedScore) {
        score2 = savedScore;
    }

    public boolean isRestore() {
        return this.restore;
    }

    public void setRestore(boolean savedRestore) {
        restore = savedRestore;
    }

    public boolean isPhaseTwo() {
        return this.isPhaseTwo;
    }

    public void setIsPhaseTwo(boolean isPhaseTwoFlag) {
        isPhaseTwo = isPhaseTwoFlag;
    }
}
