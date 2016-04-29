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
import android.os.CountDownTimer;
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
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "MainActivity";

    private Sensor gyro;
    private Sensor linearAccelero;

    private SensorManager mSensorManager;
    private SensorEventListener mSensorListener;
    private boolean bite = false;

    private int gyroCount = 0;
    private float[] gyroCheck = new float[3];

    private DeviceClient client;
    private int biteCount = 0;
    private List<Long> biteTimeList = new ArrayList<>();
    private long biteInterval = 0;

    public long savedRemainingInterval = 0;
    private MyCount counter;

    private boolean manualSettings = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NotificationBuilder.update(this);
        finish();
        if(true)
            return;

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

    private void getAvgBiteSize() {
        long maxDiff = (biteTimeList.get(3) - biteTimeList.get(0))/3;

        if (maxDiff/1000 >= 25) {
            biteInterval = 30;
        } else if(maxDiff/1000 < 25 && maxDiff/1000 >= 20) {
            biteInterval = 25;
        } else if(maxDiff/1000 < 20 && maxDiff/1000 >= 15) {
            biteInterval = 20;
        } else if(maxDiff/1000 < 15) {
            biteInterval = 15;
        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constants.mealText, "");
        editor.putBoolean(Constants.nextBiteAllowed, false).apply();

        if(counter != null)
            counter.cancel();
        counter = new MyCount(biteInterval * 1000, 1000);
        counter.start();
    }

    protected void startMeasurement() {
        /*NotificationBuilder.update(this);
        finish();*/
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
                            Log.e(TAG, "Unregister Gyroscope");
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
                    if(check) {
                        SharedPreferences sp = PreferenceManager
                                .getDefaultSharedPreferences(MainActivity.this);
                        boolean isManualSettings =
                                sp.getBoolean(Constants.manualBiteInterval, false);
                        if(!isManualSettings) {
                            biteCount++;
                            if (biteCount == 5) {
                                getAvgBiteSize();
                            } else if (biteCount < 5) {
                                biteTimeList.add(System.currentTimeMillis());
                            }
                        } else {
                            biteInterval = Long.parseLong(sp
                                    .getString(Constants.manualDurationSet, "30"));
                            if(counter != null)
                                counter.cancel();
                            counter = new MyCount(biteInterval * 1000, 1000);
                            counter.start();
                        }
                    }
                    bite = check;
                }
            }
        };

        mSensorManager.registerListener(mSensorListener,
                gyro, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(mSensorListener,
                linearAccelero, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void stopMeasurement() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(mSensorListener);
            mSensorListener = null;
        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        if(!sp.contains(Constants.noTutorial))
            editor.putBoolean(Constants.noTutorial, true);
        editor.putString(Constants.timerText, "").apply();
        if(counter != null)
            counter.cancel();
        biteCount = 0;
    }

    private boolean mealStarted;

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(Constants.mealStarted)) {
            Log.i(TAG, "Preferences Changed. Value is " + key);
            mealStarted = sharedPreferences.getBoolean(key, false);
            if(mealStarted) {
                startMeasurement();
            }
            else {
                stopMeasurement();
            }
        }
        else if(key.equalsIgnoreCase(Constants.biteDetected)) {
            Log.i(TAG, "Preferences Changed2. Value is " + key);
            if (sharedPreferences.getBoolean(key, false)) {
                if(!manualSettings && biteCount > 4) {
                    if (counter != null) {
                        counter.cancel();
                    }
                    counter = new MyCount(biteInterval * 1000, 1000);
                    counter.start();
                }
                sharedPreferences.edit().putBoolean(key, false).apply();
            }
        }
        else if(key.equalsIgnoreCase(Constants.manualBiteInterval)){
            manualSettings = sharedPreferences.getBoolean(key, manualSettings);
            if(counter != null)
                counter.cancel();
            sharedPreferences.edit().putBoolean(Constants.nextBiteAllowed, true).apply();
            sharedPreferences.edit().putString(Constants.timerText, "").apply();
            mealStarted = sharedPreferences.getBoolean(Constants.mealStarted, false);
            if(!mealStarted){
                sharedPreferences.edit().putString(Constants.mealText,
                        getString(R.string.hand_notifier)).apply();
            }
            if(manualSettings) {
                biteInterval = Long.parseLong(sharedPreferences
                        .getString(Constants.manualDurationSet, "30"));
            } else {
                biteCount = 0;
                if(mealStarted) {
                    sharedPreferences.edit().putString(Constants.mealText,
                            getString(R.string.eating_speed_text)).apply();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "Preferences Resume");
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "Preferences Paused");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        sp.unregisterOnSharedPreferenceChangeListener(this);
        editor.remove(Constants.mealText);
        editor.remove(Constants.nextBiteAllowed);
        editor.remove(Constants.biteDetected);
        editor.remove(Constants.timerText);
        editor.remove(Constants.mealStarted).apply();
        stopMeasurement();
        if(counter != null)
            counter.cancel();
    }

    public class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            SharedPreferences.Editor editor = PreferenceManager
                    .getDefaultSharedPreferences(MainActivity.this).edit();
            editor.putString(Constants.mealText, "");
            editor.putString(Constants.timerText, "Take your Next Bite");
            editor.putBoolean(Constants.nextBiteAllowed, true).apply();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(Constants.nextBiteAllowed, false).apply();
            int secs = (int) (millisUntilFinished / 1000);
            int seconds = secs % 60;
            int minutes = secs / 60;
            String stringTime = String.format("%02d:%02d", minutes, seconds);
            String timerText = "Next bite in " + stringTime + " seconds";
            editor.putString(Constants.timerText, timerText).apply();
            savedRemainingInterval = millisUntilFinished;
        }
    }
}
