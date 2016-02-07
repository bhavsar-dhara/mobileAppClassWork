package edu.neu.madcourse.dharabhavsar.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import edu.neu.madcourse.dharabhavsar.dictionary.Trie;

public class MainActivity extends AppCompatActivity {
//public class MainActivity extends Application {

    Trie trie;
    String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle(getResources().getString(R.string.title_name));
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}
