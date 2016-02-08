package edu.neu.madcourse.dharabhavsar.dictionary;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import edu.neu.madcourse.dharabhavsar.main.R;


/**
 * Created by Dhara on 2/4/2016.
 */
public class MainActivityDict extends Activity {

    public static final String KEY_RESTORE = "key_restore";
    public static final String PREF_RESTORE = "pref_restore";
    private Handler mHandler = new Handler();
    String resultStr = "";
    TextView textViewWordList;
    EditText editWordText;
    MediaPlayer mMediaPlayer;
    String result = "";
    HashMap<String,String> vocabList = new HashMap<String, String>();
    String insertedText = "";
    String resp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dict);

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
                        new AsyncTaskRunner2().execute().get();
                        Log.e("addTextChangedListener", resp);
                        resultStr=resultStr+resp+"\n";
                       // resultStr=resultStr+"\n";
                        Log.e("addTextChangedListener", resultStr);
                        textViewWordList.setText(resultStr);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    result1 = resultStr;
                  Log.e("RESULT CONCAT Fragment", "afterTextChanged: RESULT STRING = " + result1);
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

                        switch (word.charAt(0)) {
                            case 'a':
                                in_s = res.openRawResource(R.raw.a);
                                break;
                            case 'b':
                                in_s = res.openRawResource(R.raw.b_wordlist);
                                break;
                            case 'c':
                                in_s = res.openRawResource(R.raw.c_wordlist);
                                break;
                            case 'd':
                                in_s = res.openRawResource(R.raw.d_wordlist);
                                break;
                            case 'e':
                                in_s = res.openRawResource(R.raw.e_wordlist);
                                break;
                            case 'f':
                                in_s = res.openRawResource(R.raw.f_wordlist);
                                break;
                            case 'g':
                                in_s = res.openRawResource(R.raw.g_wordlist);
                                break;
                            case 'h':
                                in_s = res.openRawResource(R.raw.h_wordlist);
                                break;
                            case 'i':
                                in_s = res.openRawResource(R.raw.i_wordlist);
                                break;
                            case 'j':
                                in_s = res.openRawResource(R.raw.j_wordlist);
                                break;
                            case 'k':
                                in_s = res.openRawResource(R.raw.k_wordlist);
                                break;
                            case 'l':
                                in_s = res.openRawResource(R.raw.l_wordlist);
                                break;
                            case 'm':
                                in_s = res.openRawResource(R.raw.m_wordlist);
                                break;
                            case 'n':
                                in_s = res.openRawResource(R.raw.n_wordlist);
                                break;
                            case 'o':
                                in_s = res.openRawResource(R.raw.o_wordlist);
                                break;
                            case 'p':
                                in_s = res.openRawResource(R.raw.p_wordlist);
                                break;
                            case 'q':
                                in_s = res.openRawResource(R.raw.q_wordlist);
                                break;
                            case 'r':
                                in_s = res.openRawResource(R.raw.r_wordlist);
                                break;
                            case 's':
                                in_s = res.openRawResource(R.raw.s_wordlist);
                                break;
                            case 't':
                                in_s = res.openRawResource(R.raw.t_wordlist);
                                break;
                            case 'u':
                                in_s = res.openRawResource(R.raw.u_wordlist);
                                break;
                            case 'v':
                                in_s = res.openRawResource(R.raw.v_wordlist);
                                break;
                            case 'w':
                                in_s = res.openRawResource(R.raw.w_wordlist);
                                break;
                            case 'x':
                                in_s = res.openRawResource(R.raw.x_wordlist);
                                break;
                            case 'y':
                                in_s = res.openRawResource(R.raw.y_wordlist);
                                break;
                            case 'z':
                                in_s = res.openRawResource(R.raw.z_wordlist);
                                break;
                        }
//                        in_s = res.openRawResource(R.raw.wordlist);

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
            try {
//                Log.e("AsyncTaskRunner2", "insertedText = " + insertedText);
//                Log.e("AsyncTaskRunner2", "vocabList = " + String.valueOf(vocabList.size()));

                for (String s : vocabList.values()) {
                    if (insertedText.equalsIgnoreCase(s.trim())) {
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
