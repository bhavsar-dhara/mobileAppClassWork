package edu.neu.madcourse.dharabhavsar.dictionary;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;

import edu.neu.madcourse.dharabhavsar.main.R;


/**
 * Created by Dhara on 2/4/2016.
 */
public class MainActivityDict extends Activity {

    public static final String KEY_RESTORE = "key_restore";
    public static final String PREF_RESTORE = "pref_restore";
    private Handler mHandler = new Handler();
    private MainFragmentDict mFragmentDict;
    DatabaseTable db = new DatabaseTable(this);
    public String resultStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dict);
        mFragmentDict = (MainFragmentDict) getFragmentManager()
                .findFragmentById(R.id.fragment_main_dict);
        boolean restore = getIntent().getBooleanExtra(KEY_RESTORE, false);
        if (restore) {
            String gameData = getPreferences(MODE_PRIVATE)
                    .getString(PREF_RESTORE, null);
            if (gameData != null) {
                mFragmentDict.putData(gameData);
            }
        }
        handleIntent(getIntent());
        Log.d("MainActivityDict", "restore = " + restore);
    }

    @Override
    protected void onNewIntent(Intent intent) {
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
//        TODO
        String gameData = mFragmentDict.getData();
        getPreferences(MODE_PRIVATE).edit()
                .putString(PREF_RESTORE, gameData)
                .commit();
        Log.d("MainActivityDict", "state = " + gameData);
    }
}
