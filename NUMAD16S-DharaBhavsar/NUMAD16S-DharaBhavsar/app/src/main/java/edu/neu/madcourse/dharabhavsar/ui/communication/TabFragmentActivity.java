package edu.neu.madcourse.dharabhavsar.ui.communication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.widget.TabHost;

import edu.neu.madcourse.dharabhavsar.ui.main.R;

/**
 * Created by Dhara on 3/17/2016.
 */
public class TabFragmentActivity extends FragmentActivity {
    private FragmentTabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.score_board_tab_scraggle2);
        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator("Combat Play"),
                Tab1Fragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator("Combine Play"),
                Tab2Fragment.class, null);

        /*TabHost.TabSpec tab1 = mTabHost.newTabSpec("First Tab");
        TabHost.TabSpec tab2 = mTabHost.newTabSpec("Second Tab");

        // Set the Tab name and Activity
        // that will be opened when particular Tab will be selected
        tab1.setIndicator("Combat Play");
        tab1.setContent(new Intent(this, Tab1Fragment.class));

        tab2.setIndicator("Combine Play");
        tab2.setContent(new Intent(this, Tab2Fragment.class));

        *//** Add the tabs  to the TabHost to display. *//*
        mTabHost.addTab(tab1);
        mTabHost.addTab(tab2);*/
    }
}
