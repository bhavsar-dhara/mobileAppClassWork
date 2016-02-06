package edu.neu.madcourse.dharabhavsar.dictionary;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
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

import edu.neu.madcourse.dharabhavsar.main.R;


/**
 * Created by Dhara on 2/4/2016.
 */
public class MainActivityDict extends Activity {

    public static final String KEY_RESTORE = "key_restore";
    public static final String PREF_RESTORE = "pref_restore";
    private Handler mHandler = new Handler();
    DatabaseTable db = new DatabaseTable(this);
    public String resultStr = "";
    TextView textViewWordList;
    EditText editWordText;
    MediaPlayer mMediaPlayer;
    String result = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dict);
//        Intent intent = new Intent(this, MainActivityDict.class);
//        startActivity(intent);
        boolean restore = getIntent().getBooleanExtra(KEY_RESTORE, false);
        if (restore) {
            String gameData = getPreferences(MODE_PRIVATE)
                    .getString(PREF_RESTORE, null);
            if (gameData != null) {
                putData(gameData);
            }
        }
        Log.e("WORD LENGTH intent 2", "afterTextChanged: " + getIntent().getAction());
//        handleIntent(getIntent());

        // Method to show the list of words found from the provided word list
        textViewWordList = (TextView) findViewById(R.id.textViewWordList);
        textViewWordList.setMovementMethod(new ScrollingMovementMethod());
        editWordText = (EditText) findViewById(R.id.editWordText);

//        Reading from a file occurs in the AsyncTaskRunner
        editWordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String word = String.valueOf(editWordText.getText());
                Log.e("WORD LENGTH Fragment", "afterTextChanged: " + word.length());
                if(word.length() >= 3) {
                    Log.e("WORD LENGTH Search", "afterTextChanged: ");
                    Intent i = new Intent(MainActivityDict.this, MainActivityDict.class);
                    Log.e("WORD LENGTH intent 1", "afterTextChanged: " + i.getAction());
                    onNewIntent(i);
                    Log.e("WORD LENGTH intent 2", "afterTextChanged: " + getIntent().getAction());
                    onNewIntent(getIntent());
                    result = resultStr;
                    Log.e("RESULT CONCAT Fragment", "afterTextChanged: " + result);
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

    @Override
    protected void onNewIntent(Intent intent) {
//        setIntent(intent);
        handleIntent(intent);
    }

    private String handleIntent(Intent intent) {
        String result1 = "";
        Log.e("SEARCH", "starting search : Intent action : "+intent.getAction());
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            Log.e("HANDLE SEARCH", "handleIntent: "+ SearchManager.QUERY );
            String query = intent.getStringExtra(SearchManager.QUERY);
            Cursor c = db.getWordMatches(query, null);
            //process Cursor and display results
            c.moveToFirst();
            Log.e("SEARCH 2", "handleIntent: " + c.getString(0) + c.getString(1));
            result1 = c.getString(0);
            Log.e("SEARCH 3", result1);
        }
        Log.e("RESULT CONCAT", "afterTextChanged: " + result1);
        resultStr.concat(result1);
        return resultStr;
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
}
