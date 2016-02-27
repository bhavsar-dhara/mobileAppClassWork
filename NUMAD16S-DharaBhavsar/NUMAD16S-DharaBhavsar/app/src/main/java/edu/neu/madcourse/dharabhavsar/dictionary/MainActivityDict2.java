package edu.neu.madcourse.dharabhavsar.dictionary;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
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

/**
 * Created by Dhara on 2/27/2016.
 */
public class MainActivityDict2 extends Activity {
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
    Boolean isWordGameFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main_dict);
//        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            isWordGameFlag = bundle.getBoolean("isWordGameFlag");
            String message = bundle.getString("message");
            if (isWordGameFlag) {
                try {
                    new AsyncTaskRunner3().execute(message).get();
                    Intent output = new Intent();
                    output.putExtra("UNIQUE_WORD_LIST_STR", finalResult);
                    setResult(RESULT_OK, output);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }

        Log.d("MainActivityDict", "restore = " + finalResult);
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

    //    method to be called in the asyncTask for the Word Game ever
    private void searchWord(String str) {
        String result1 = "";
        String word = str;
        Log.e("searchWord WORD LEN", "afterTextChanged: " + word.length()+" word = " + word);
        if(word.length() >= 3) {
            insertedText = word;
            try {
                new AsyncTaskRunner().execute(word).get();
                String res = new AsyncTaskRunner2().execute().get();
                Log.e("searchWord", "res = "+res);
                resultStr = resultStr + res + "\n";
                List<String> list = Arrays.asList(resultStr.split("\n"));
                Set<String> uniqueWords = new HashSet<String>(list);
                finalResult = "";
                for (String s1 : uniqueWords) {
                    System.out.println(word + ": " + Collections.frequency(list, s1));
                    finalResult = finalResult + s1 + "\n";
                }
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

}
