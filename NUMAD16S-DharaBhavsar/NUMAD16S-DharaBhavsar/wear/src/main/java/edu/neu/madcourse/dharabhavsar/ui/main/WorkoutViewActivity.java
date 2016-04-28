package edu.neu.madcourse.dharabhavsar.ui.main;

/**
 * Created by Dhara on 4/27/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class WorkoutViewActivity extends Activity {

    public static final int MAX_TIME = 30;      // 30s countdown timer
    public static final int START_TIME = 30;    // Countdown from 30 to zero
    private static CircularProgressDrawable mCircularProgressTimer;
    private ImageView mCircularImageView;

    private static final String TAG = "WorkoutViewActivity";

    private Sensor linearAccelero;

    private SensorManager mSensorManager;
    private SensorEventListener mSensorListener;
    private boolean bite = false;
    private TextView title;

    private DeviceClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer_notifier);

        Log.i(TAG, "In onCreate Method");

        // Get a reference of our ImageView layout component to be used
        // to display our circular progress timer.
        mCircularImageView = (ImageView) findViewById(R.id.imageview);
        title = (TextView)findViewById(R.id.title);

        // Create an instance of a drawable circular progress timer
        mCircularProgressTimer = new CircularProgressDrawable(START_TIME,
                MAX_TIME, CircularProgressDrawable.Order.DESCENDING);

        // Set a callback to update our circular progress timer
        mCircularProgressTimer.setCallback(mPieDrawableCallback);

        // Set a drawable object for our Imageview
        mCircularImageView.setImageDrawable(mCircularProgressTimer);

        mSensorManager = ((SensorManager) getSystemService(Context.SENSOR_SERVICE));
        linearAccelero = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        client = DeviceClient.getInstance(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "In onResume");
        startMeasurement();
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.i(TAG, "In onPause");
        stopMeasurement();
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

    protected void startMeasurement() {

        Log.i(TAG, "started Measurement");

        mSensorListener = new SensorEventListener() {
            @Override
            public void onAccuracyChanged(Sensor arg0, int arg1) {
            }

            @Override
            public void onSensorChanged(SensorEvent event) {

//                Log.i(TAG, "values are "+event.values);
                boolean check = client.sendSensorData2(event.sensor.getType(), event.accuracy,
                        event.timestamp, event.values);
                if(bite != check) {
                    bite = check;
                    mCircularProgressTimer.stop();
                    mCircularProgressTimer.restart();
                    mCircularProgressTimer.start();
                }
            }
        };

        mSensorManager.registerListener(mSensorListener,
                linearAccelero, SensorManager.SENSOR_DELAY_NORMAL);

        mCircularProgressTimer.start();
        title.setText(R.string.bite_detected);
    }

    private void stopMeasurement() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(mSensorListener);
            mSensorListener = null;
        }
        mCircularProgressTimer.stop();
    }
}