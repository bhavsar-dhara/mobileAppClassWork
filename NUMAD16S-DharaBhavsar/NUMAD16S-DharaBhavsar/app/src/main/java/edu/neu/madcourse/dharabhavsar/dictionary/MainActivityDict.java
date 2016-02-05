package edu.neu.madcourse.dharabhavsar.dictionary;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;

import edu.neu.madcourse.dharabhavsar.main.R;

/**
 * Created by Dhara on 2/4/2016.
 */
public class MainActivityDict extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dict);
    }

    static{
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
}
