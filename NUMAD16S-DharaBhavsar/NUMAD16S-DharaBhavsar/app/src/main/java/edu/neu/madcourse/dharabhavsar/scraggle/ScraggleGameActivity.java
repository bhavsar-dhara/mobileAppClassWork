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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//         The below code didn't work for this activity
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        setContentView(R.layout.activity_game_scraggle);
        mGameFragment = (ScraggleGameFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_game_scraggle);
        mTextField = (TextView) findViewById(R.id.timerView);
        boolean restore = getIntent().getBooleanExtra(KEY_RESTORE, false);
        if (restore) {
            String gameData = getPreferences(MODE_PRIVATE)
                    .getString(PREF_RESTORE, null);
            if (gameData != null) {
                mGameFragment.putState(gameData);
            }
        }
        Log.d("Scraggle", "restore = " + restore);

        mTextField = (TextView) findViewById(R.id.timerView);
        counter = new MyCount(interval, 1000);
        counter.start();

        mScoreTextField = (TextView) findViewById(R.id.scoreView);
        mScoreTextField.setText("Score = " + String.valueOf(score));
    }

    public List<String> methodCallToAsyncTaskRunner() {
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
        if(savedRemainingInterval > 0) {
            counter = new MyCount(savedRemainingInterval, 1000);
            counter.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("Mute TEST Pause", "inside pause");
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
                    Log.e("nineLetter", word + " " + String.valueOf(word.length()));
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
        Log.e("Mute TEST toogleMute", "inside func");
        if (mMediaPlayer.isPlaying()) {
            Log.e("Mute TEST toogleMute", "inside for pause");
            mMediaPlayer.pause();
        } else {
            Log.e("Mute TEST toogleMute", "inside for start");
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
                            Intent intent = new Intent(ScraggleGameActivity.this, ScraggleGameActivity2.class);
                            intent.putExtra("gameData", mGameFragment.getState());
                            startActivity(intent);
                        }
                    });
            mDialog = builder.show();
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

//    METHOD to implement the dictionary word check - but I feel this is a wrong way to do it and
//    taking too much time
    public Boolean checkWordInDict(String str) {
        Log.e("DICT TEST", str);
        try {
            new AsyncTaskRunner3().execute(str).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return resFlag;
    }

    private class AsyncTaskRunner0 extends AsyncTask<String, Void, List<String>> {
        @Override
        protected List<String> doInBackground(String... params) {
            String word = params[0];
            try {
                try {
                    Log.e("Inside AS0","Inside AS0");

                    Resources res = getResources();
                    InputStream in_s = null;
                    String fileName = String.valueOf(word.charAt(0));
                    int resID = getResources().getIdentifier(fileName, "raw", getPackageName());
                    in_s = res.openRawResource(resID);

                    byte[] b = new byte[in_s.available()];
                    in_s.read(b);
                    result = new String(b);
                    String[] strings = result.split("\\n");
                    Log.e("INSERT", "inserting count = " + strings.length);
                    for (String s : strings) {
//                        vocabList.put(s, s);
                        vocabList.add(s);
                    }
//                    trie = new TrieLookup(Arrays.asList(strings));
                    Log.e("INSERT", "inserted");
                } catch (IOException e) {
                    Log.e("ERROR", "not inserted");
                }
            } catch (Exception e) {
//                Thread.interrupted();
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
                Log.e("AsyncTaskRunner2", "insertedText = " + insertedText);
//                Log.e("AsyncTaskRunner2", "vocabList = " + String.valueOf(vocabList.size()));

//                isThereInDict = trie.contains(resp);
//                isThereInDict = trie.contains(insertedText);

                for (String s : vocabList) {
                    if (insertedText.equals(s.trim())) {
                        resp = insertedText;
                    }
                }

                if(!resp.equals("")) {
                    isThereInDict = true;
                }

                Log.e("TEST HASHMAP resp", resp);

            } catch (Exception e) {
                Log.e("AsyncTaskRunner2", "Error encountered");
            }
            Log.e("AsyncTaskRunner2", "result = " + resp);
            return isThereInDict;
        }
    }

    //    method to be called in the asyncTask for the Word Game ever
    public Boolean searchWord(String str) {
        String result1 = "";
        String word = str;
        Log.e("searchWord WORD LEN", "afterTextChanged: " + word.length() + " word = " + word);
        if (word.length() >= 3) {
            try {
                new AsyncTaskRunner0().execute(word).get();
                resFlag = new AsyncTaskRunner2().execute(word).get();
                Log.e("searchWord", "resFlag = " + resFlag);
                /*resultStr = resultStr + word + "\n";
                List<String> list = Arrays.asList(resultStr.split("\n"));
                Set<String> uniqueWords = new HashSet<String>(list);
                finalResult = "";
                for (String s1 : uniqueWords) {
                    System.out.println(word + ": " + Collections.frequency(list, s1));
                    finalResult = finalResult + s1 + "\n";
                }*/
                Log.e("searchWord", finalResult);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            result1 = resultStr;
            Log.e("searchWord RESULT STR", "afterTextChanged: RESULT STRING = " + result1);
        } else {
            List<String> list = Arrays.asList(resultStr.split("\n"));
            Set<String> uniqueWords = new HashSet<String>(list);
            finalResult = "";
            for (String s1 : uniqueWords) {
                System.out.println(word + ": " + Collections.frequency(list, s1));
                finalResult = finalResult + s1 + "\n";
            }
            Log.e("searchWord", finalResult);
        }
        return resFlag;
    }

    private class AsyncTaskRunner3 extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String str = params[0];
            try {
                Log.e("AsyncTaskRunner3", "insertedText = " + str);

                searchWord(str);

                Log.e("TEST HASHMAP resp", str);
            } catch (Exception e) {
                Log.e("AsyncTaskRunner3", "Error encountered");
            }
            Log.e("AsyncTaskRunner3", "result = " + str);
            return null;
        }
    }

    public long getSavedRemainingInterval(){
        return this.savedRemainingInterval;
    }

    public void setSavedRemainingInterval(long savedInterval){
        savedRemainingInterval = savedInterval;
    }

    public int getScore(){
        return this.score;
    }

    public void setScore(int savedScore){
        score = savedScore;
    }
}
