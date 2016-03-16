package edu.neu.madcourse.dharabhavsar.ui.dictionary;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import edu.neu.madcourse.dharabhavsar.ui.main.R;


/**
 * Created by Dhara on 2/4/2016.
 */
public class MainActivityDict extends Activity {

    public static final String KEY_RESTORE = "key_restore";
    public static final String PREF_RESTORE = "pref_restore";
    private Handler mHandler = new Handler();
    String resultStr = "";
    String finalResult = "";
    TextView textViewWordList;
    EditText editWordText;
    MediaPlayer mMediaPlayer;
    String result = "";
    HashMap<String,String> vocabList = new HashMap<String, String>();
    String insertedText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dict);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        boolean restore = getIntent().getBooleanExtra(KEY_RESTORE, false);
        if (restore) {
            String gameData = getPreferences(MODE_PRIVATE)
                    .getString(PREF_RESTORE, null);
            if (gameData != null) {
                putData(gameData);
            }
        }

        // Method to show the list of words found from the provided word list
        textViewWordList = (TextView) findViewById(R.id.textViewWordList);
        textViewWordList.setMovementMethod(new ScrollingMovementMethod());
        editWordText = (EditText) findViewById(R.id.editWordText);

//        Reading from a file occurs in the AsyncTaskRunner
        editWordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String result1 = "";
                String word = String.valueOf(editWordText.getText());
                Log.e("WORD LENGTH Fragment", "afterTextChanged: " + word.length()+" word = " + word);
                if(word.length() >= 3) {
                    insertedText = word;
                    try {
                        new AsyncTaskRunner().execute(word).get();
                        String res = new AsyncTaskRunner2().execute().get();
                        Log.e("addTextChangedListener", "res = "+res);
//                        Log.e("addTextChangedListener", resp);
                        resultStr = resultStr + res + "\n";
                        List<String> list = Arrays.asList(resultStr.split("\n"));
                        Set<String> uniqueWords = new HashSet<String>(list);
                        finalResult = "";
                        for (String s1 : uniqueWords) {
                            System.out.println(word + ": " + Collections.frequency(list, s1));
                            finalResult = finalResult + s1 + "\n";
                        }
                        Log.e("addTextChangedListener", finalResult);
                        textViewWordList.setText(finalResult);
                        if(res != "") {
                            mMediaPlayer = MediaPlayer.create(MainActivityDict.this,
                                    R.raw.short_ping_freesound_org);
                            mMediaPlayer.setVolume(0.5f, 0.5f);
                            mMediaPlayer.start();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    result1 = resultStr;
                    Log.e("RESULT CONCAT Fragment", "afterTextChanged: RESULT STRING = " + result1);
                } else {
                    List<String> list = Arrays.asList(resultStr.split("\n"));
                    Set<String> uniqueWords = new HashSet<String>(list);
                    finalResult = "";
                    for (String s1 : uniqueWords) {
                        System.out.println(word + ": " + Collections.frequency(list, s1));
                        finalResult = finalResult + s1 + "\n";
                    }
                    Log.e("addTextChangedListener", finalResult);
                    textViewWordList.setText(finalResult);
//                    resultStr = "";
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0)
                    textViewWordList.setText("");
            }
        });

        // Method called out on clear button click
        Button clearBtn = (Button) findViewById(R.id.clearBtn);
        clearBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                mMediaPlayer = MediaPlayer.create(MainActivityDict.this,
                        R.raw.short_ping_freesound_org);
                mMediaPlayer.setVolume(0.5f, 0.5f);
                mMediaPlayer.start();

                // Clear text when clicked
//                editWordText.setText("");
                editWordText.getText().clear();
                Log.v("MainActivityDict", "onClick: " + editWordText.getText());
                textViewWordList.setText("");
                resultStr = "";
            }
        });

        // Method called out on return to menu button click
        Button returnToMenuBtn = (Button) findViewById(R.id.returnToMenuBtn);
        returnToMenuBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                mMediaPlayer = MediaPlayer.create(MainActivityDict.this,
                        R.raw.short_ping_freesound_org);
                mMediaPlayer.setVolume(0.5f, 0.5f);
                mMediaPlayer.start();

                MainActivityDict.this.finish();
            }
        });

        // Method called out on acknowledgements button click
        Button acknowledgementsBtn = (Button) findViewById(R.id.acknowledgementsBtn);
        acknowledgementsBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                mMediaPlayer = MediaPlayer.create(MainActivityDict.this,
                        R.raw.short_ping_freesound_org);
                mMediaPlayer.setVolume(0.5f, 0.5f);
                mMediaPlayer.start();

                Intent intent = new Intent(MainActivityDict.this, AcknowledgementMainActivity.class);
                startActivity(intent);
            }
        });
        Log.d("MainActivityDict", "restore = " + restore);
    }

    static{
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(null);
        String gameData = getData();
        getPreferences(MODE_PRIVATE).edit()
                .putString(PREF_RESTORE, gameData)
                .commit();
        Log.d("MainActivityDict", "state = " + gameData);
    }

    //    getData()
    /** Create a string containing the state of the game. */
    public String getData() {
        StringBuilder builder = new StringBuilder();
        builder.append(editWordText);
        builder.append(",");
        builder.append(textViewWordList);
        builder.append(",");
        return builder.toString();
    }
//    putData()
    /** Restore the state of the game from the given string. */
    public void putData(String gameData) {
        String[] fields = gameData.split(",");
        int index = 0;
        editWordText.setText(fields[index++]);
        textViewWordList.setText(fields[index++]);
    }

    private class AsyncTaskRunner extends AsyncTask<String, Void, HashMap> {
        @Override
        protected HashMap doInBackground(String... params) {
            String word = params[0];
                try {
                    try {
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
                            vocabList.put(s, s);
                        }
                        Log.e("INSERT", "inserted");
                    } catch (IOException e) {
                        Log.e("ERROR", "not inserted");
                    }
                } catch (Exception e) {
                    Thread.interrupted();
                    Log.e("AsyncTaskRunner", "Exception occurred");
                }
            return vocabList;
        }
    }

    private class AsyncTaskRunner2 extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
//            HashMap<String, String> wordList = params[0];
            String resp = "";
            try {
                Log.e("AsyncTaskRunner2", "insertedText = " + insertedText);
//                Log.e("AsyncTaskRunner2", "vocabList = " + String.valueOf(vocabList.size()));

                for (String s : vocabList.values()) {
                    if (insertedText.equals(s.trim())) {
                        resp = insertedText;
                    }
                }
                Log.e("TEST HASHMAP resp", resp);
            } catch (Exception e) {
                Log.e("AsyncTaskRunner2", "Error encountered");
            }
            Log.e("AsyncTaskRunner2", "result = " + resp);
            return resp;
        }
    }

}
