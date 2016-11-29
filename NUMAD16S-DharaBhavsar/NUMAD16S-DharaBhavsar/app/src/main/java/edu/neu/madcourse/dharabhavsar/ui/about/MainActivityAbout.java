package edu.neu.madcourse.dharabhavsar.ui.about;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import edu.neu.madcourse.dharabhavsar.ui.main.R;

/**
 * Created by Dhara on 1/22/2016.
 */
public class MainActivityAbout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_about);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.about_app_screen));
    }

}
