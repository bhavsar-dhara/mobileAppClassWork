package edu.neu.madcourse.dharabhavsar.scraggle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
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
import java.util.Set;
import java.util.concurrent.ExecutionException;

import edu.neu.madcourse.dharabhavsar.dictionary.TrieLookup;
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
    String resultStr = "";
    String finalResult = "";
    String result = "";
    ArrayList<String> vocabList = new ArrayList<String>();
    String insertedText = "";
    TrieLookup trie;
    Boolean resFlag = false;
    TextView mScoreTextField;
    int score = 0;

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
//        Intent intent = new Intent();
//        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        this.finish();
        Intent i = new Intent(ScraggleGameActivity2.this, MainActivityScraggle.class);
        ScraggleGameActivity2.this.finish();
        startActivity(i);
    }

    public void startThinking() {
        View thinkView = findViewById(R.id.thinking_scraggle);
        thinkView.setVisibility(View.VISIBLE);
    }

    public void stopThinking() {
        View thinkView = findViewById(R.id.thinking_scraggle);
        thinkView.setVisibility(View.GONE);
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
