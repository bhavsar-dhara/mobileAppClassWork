/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.neu.madcourse.dharabhavsar.ui.main;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.GridViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.WindowInsets;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity
        implements SharedPreferences.OnSharedPreferenceChangeListener{

    private static final String TAG = "MainActivity";

    private Sensor gyro;
    private Sensor linearAccelero;

    private SensorManager mSensorManager;
    private SensorEventListener mSensorListener;
    private boolean bite = false;

    private int gyroCount = 0;

    private float[] gyroCheck = new float[3];

    private DeviceClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Resources res = getResources();
        final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
        Log.i(TAG, "Trying to apply insets");
        pager.setOnApplyWindowInsetsListener(new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                // Adjust page margins:
                //   A little extra horizontal spacing between pages looks a bit
                //   less crowded on a round display.
                final boolean round = insets.isRound();
                int rowMargin = res.getDimensionPixelOffset(R.dimen.page_row_margin);
                int colMargin = res.getDimensionPixelOffset(round ?
                        R.dimen.page_column_margin_round : R.dimen.page_column_margin);
                pager.setPageMargins(rowMargin, colMargin);

                Log.i(TAG, "The page margins and round is "+round+" "+ rowMargin+" "+  colMargin);

                // GridViewPager relies on insets to properly handle
                // layout for round displays. They must be explicitly
                // applied since this listener has taken them over.
                pager.onApplyWindowInsets(insets);
                return insets;
            }
        });
        pager.setAdapter(new EaterPagerAdapter(this, getFragmentManager()));
        pager.setBackgroundColor(getResources().getColor(R.color.primary));
        DotsPageIndicator dotsPageIndicator = (DotsPageIndicator) findViewById(R.id.page_indicator);
        dotsPageIndicator.setPager(pager);

        mSensorManager = ((SensorManager) getSystemService(Context.SENSOR_SERVICE));
        gyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        linearAccelero = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        client = DeviceClient.getInstance(this);
    }

    private int biteCount = 0;

    private void updatePreferences() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        long currTime = System.currentTimeMillis();
        long biteDuration = currTime - sp.getLong(Constants.currTime,0);
        SharedPreferences.Editor editor = sp.edit();
        if(biteDuration != currTime)
            editor.putLong(Constants.biteTime, biteDuration);
        editor.putLong(Constants.currTime, currTime).apply();
    }

    private List<Long> biteTimeList = new ArrayList<>();

    private void getAvgBiteSize() {
        Log.e(TAG, "getAvgBiteSize: 0 = " +  biteTimeList.get(0));
        Log.e(TAG, "getAvgBiteSize: 1 = " +  biteTimeList.get(1));
        Log.e(TAG, "getAvgBiteSize: 2 = " +  biteTimeList.get(2));
        Log.e(TAG, "getAvgBiteSize: 3 = " +  biteTimeList.get(3));

        long diff3 = biteTimeList.get(3) - biteTimeList.get(2);
        long diff2 = biteTimeList.get(2) - biteTimeList.get(1);
        long diff1 = biteTimeList.get(1) - biteTimeList.get(0);
        Log.e(TAG, "getAvgBiteSize: diff1 = " +  diff1);
        Log.e(TAG, "getAvgBiteSize: diff2 = " +  diff2);
        Log.e(TAG, "getAvgBiteSize: diff3 = " +  diff3);

        long maxDiff = (biteTimeList.get(3) - biteTimeList.get(0))/3;
        Log.e(TAG, "getAvgBiteSize: maxDiff = " +  maxDiff);

        long diffAvg = (diff1 + diff2 + diff3)/3;
        Log.e(TAG, "getAvgBiteSize: diffAvg = " +  diffAvg);

        long biteInterval = 0;

        if (maxDiff/1000 >= 25) {
            biteInterval = 30;
        } else if(maxDiff/1000 < 25 && maxDiff/1000 >= 20) {
            biteInterval = 25;
        } else if(maxDiff/1000 < 20 && maxDiff/1000 >= 15) {
            biteInterval = 20;
        } else if(maxDiff/1000 < 15) {
            biteInterval = 15;
        }
        Log.e(TAG, "getAvgBiteSize: biteInterval = " +  biteInterval);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(Constants.biteInterval, biteInterval);
    }

    protected void startMeasurement() {
        mSensorListener = new SensorEventListener() {
            @Override
            public void onAccuracyChanged(Sensor arg0, int arg1) {
            }

            @Override
            public void onSensorChanged(SensorEvent event) {

                if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                    if(event.values[0] == gyroCheck[0] &&
                            event.values[1] == gyroCheck[1] &&
                            event.values[2] == gyroCheck[2]) {
                        gyroCount++;
                        if(gyroCount > 20 && mSensorManager != null) {
                            mSensorManager.unregisterListener(mSensorListener, gyro);
                            Log.e(TAG, "Unregistering Gyroscope");
                        }
                        return;
                    }
                    else {
                        gyroCount = 0;
                        gyroCheck[0] = event.values[0];
                        gyroCheck[1] = event.values[1];
                        gyroCheck[2] = event.values[2];
                    }
                }
                boolean check = client.sendSensorData2(event.sensor.getType(), event.accuracy,
                        event.timestamp, event.values);
                if(bite != check) {
                    //updateBiteDisplay(check);
                    if(check) {
                        biteCount++;
                        if(biteCount > 4) {
                            getAvgBiteSize();
                            updatePreferences();
                        }
                        else {
                            biteTimeList.add(System.currentTimeMillis());
                        }
                    }
                    bite = check;
                }
            }
        };

        mSensorManager.registerListener(mSensorListener,
                gyro, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(mSensorListener,
                linearAccelero, SensorManager.SENSOR_DELAY_UI);
    }

    private void stopMeasurement() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(mSensorListener);
            mSensorListener = null;
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(Constants.mealStarted)) {
            Log.i(TAG, "Preferecnes Changed. Value is " + key);
            boolean mealStarted = sharedPreferences.getBoolean(key, false);
            if(mealStarted) {
                startMeasurement();
            }
            else {
                stopMeasurement();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "Preferecnes Resume");
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "Preferecnes Paused");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.unregisterOnSharedPreferenceChangeListener(this);
        sp.edit().putBoolean(Constants.mealStarted, false).apply();
        stopMeasurement();
    }
}
