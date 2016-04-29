package edu.neu.madcourse.dharabhavsar.ui.main;

/**
 * Created by Dhara on 4/27/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class WorkoutViewActivity extends Activity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final int MAX_TIME = 40;      // 30s countdown timer
    private static CircularProgressDrawable mCircularProgressTimer;
    private ImageView mCircularImageView;

    private static final String TAG = "WorkoutViewActivity";

    private Sensor linearAccelero;

    private SensorManager mSensorManager;
    private SensorEventListener mSensorListener;
    private boolean bite = false;
    private TextView title;

    private DeviceClient client;

    private SharedPreferences sharedPreferences;
    private boolean isManual;
    private int biteInterval;
    private int biteCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer_notifier);

        Log.i(TAG, "In onCreate Method");

        sharedPreferences = getSharedPreferences(Constants.PREF_SHARED, MODE_PRIVATE);
        // Get a reference of our ImageView layout component to be used
        // to display our circular progress timer.
        mCircularImageView = (ImageView) findViewById(R.id.imageview);
        title = (TextView)findViewById(R.id.title);

        isManual = sharedPreferences.getBoolean(Constants.manualBiteInterval, false);
        if(isManual) {
            biteInterval = Integer.parseInt(sharedPreferences.getString(Constants.manualDurationSet,
                    "0"));
            // Create an instance of a drawable circular progress timer
            mCircularProgressTimer = new CircularProgressDrawable(0,
                    biteInterval, CircularProgressDrawable.Order.DESCENDING, true);
            sharedPreferences.edit().putInt(Constants.SUGGESTED_BITE_INTERVAL,
                    biteInterval).apply();
            PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .putString(Constants.mealText, "").apply();
            // if timer runs out then show this message or normal text for manual set bite duration
            title.setText(R.string.take_bite);
        }
        else{
            // Create an instance of a drawable circular progress timer
            mCircularProgressTimer = new CircularProgressDrawable(MAX_TIME,
                    MAX_TIME, CircularProgressDrawable.Order.DESCENDING, false);
            PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .putString(Constants.mealText,
                            this.getString(R.string.eating_speed_text)).apply();
            // if detecting speed then show this message
            title.setText(R.string.eating_speed_text);
        }

        // Set a callback to update our circular progress timer
        mCircularProgressTimer.setCallback(mPieDrawableCallback);

        // Set a drawable object for our Imageview
        mCircularImageView.setImageDrawable(mCircularProgressTimer);

        mSensorManager = ((SensorManager) getSystemService(Context.SENSOR_SERVICE));
        linearAccelero = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        client = DeviceClient.getInstance(this);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Constants.MEAL_BITES, 0);
        editor.putLong(Constants.MEAL_START_TIME, System.currentTimeMillis()).apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "In onResume");
        startMeasurement();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.i(TAG, "In onPause");
        stopMeasurement();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private Drawable.Callback mPieDrawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            // Redraw our image with updated progress timer
            mCircularImageView.setImageDrawable(who);
        }

        // Empty placeholder
        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
        }

        // Empty placeholder
        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
        }
    };

    private List<Integer> biteTimeList = new ArrayList<>();
    private int getAvgBiteSize() {

        int biteInterval = 0;

        long maxDiff = (biteTimeList.get(3) - biteTimeList.get(0))/3;

        // TODO: Add logic for doing some stuff here
        if (maxDiff/1000 >= 25) {
            biteInterval = 30;
        } else if(maxDiff < 25 && maxDiff >= 20) {
            biteInterval = 25;
        } else if(maxDiff < 20 && maxDiff >= 15) {
            biteInterval = 20;
        } else if(maxDiff < 15) {
            biteInterval = 15;
        }

        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putString(Constants.mealText, "").apply();

        return biteInterval;
    }

    private boolean timerZero = false;

    protected void startMeasurement() {

        Log.i(TAG, "started Measurement");

        mSensorListener = new SensorEventListener() {
            @Override
            public void onAccuracyChanged(Sensor arg0, int arg1) {
            }

            @Override
            public void onSensorChanged(SensorEvent event) {

                if(!timerZero && mCircularProgressTimer.getValue() == 0){
                    timerZero = true;
                    PreferenceManager.getDefaultSharedPreferences(WorkoutViewActivity.this)
                            .edit()
                            .putString(Constants.mealText,
                                    WorkoutViewActivity.this.getString(R.string.take_bite))
                            .apply();
                }

                boolean check = client.sendSensorData3(event.sensor.getType(), event.values,
                        mCircularProgressTimer.getValue(), biteCount, isManual);
                if(bite != check) {
                    bite = check;
                    if(check) {
                        timerZero = false;
                        biteCount++;
                        Log.i(TAG, "The bite count is " + biteCount);
                        //int bite = sharedPreferences.getInt(Constants.MEAL_BITES, 0);
                        sharedPreferences.edit().putInt(Constants.MEAL_BITES, biteCount).apply();

                        if (!isManual && biteCount < 5) {
                            biteTimeList.add(MAX_TIME - mCircularProgressTimer.getValue());
                            if (biteCount == 4) {
                                biteInterval = getAvgBiteSize();
                                sharedPreferences.edit().putInt(Constants.SUGGESTED_BITE_INTERVAL,
                                        biteInterval).apply();
                                mCircularProgressTimer.stop();
                                mCircularProgressTimer = new CircularProgressDrawable(biteInterval,
                                        biteInterval, CircularProgressDrawable.Order.DESCENDING, true);
                                // Set a drawable object for our Imageview
                                mCircularImageView.setImageDrawable(mCircularProgressTimer);
                                PreferenceManager.getDefaultSharedPreferences
                                        (WorkoutViewActivity.this).edit()
                                        .putString(Constants.mealText, "").apply();
                                // if timer runs out then show this message
                                title.setText("");
                            }
                        }
                        mCircularProgressTimer.stop();
                        mCircularProgressTimer.restart();
                        mCircularProgressTimer.start();
                    }
                }
            }
        };

        mSensorManager.registerListener(mSensorListener,
                linearAccelero, SensorManager.SENSOR_DELAY_NORMAL);

        if(!isManual)
            mCircularProgressTimer.start();
    }

    private void stopMeasurement() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(mSensorListener);
            mSensorListener = null;
        }
        mCircularProgressTimer.stop();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equalsIgnoreCase(Constants.mealText)) {
            Log.i(TAG, "Preferences Changed. Value is " + key);
            String text = sharedPreferences.getString(Constants.mealText, "defValue");
            title.setText(text);
        }
    }
}