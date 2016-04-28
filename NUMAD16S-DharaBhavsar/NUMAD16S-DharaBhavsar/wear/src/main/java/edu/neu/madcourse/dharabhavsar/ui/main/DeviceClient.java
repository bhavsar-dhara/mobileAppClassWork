package edu.neu.madcourse.dharabhavsar.ui.main;

import android.content.Context;
import android.hardware.Sensor;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DeviceClient {
    private static final String TAG = "DeviceClient";

    public static DeviceClient instance;
    private Context context;
    private int filterId = -1;
    private Map<Integer, Long> lastSensorData = new HashMap<>();
    private String filePath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "AnalysisData.csv";
    private List<String[]> listValues = new ArrayList<>();
    private boolean vibrating = false;
    private Vibrator v;

    private boolean nextBiteAllowed = true;

    public static DeviceClient getInstance(Context context) {
        if (instance == null) {
            instance = new DeviceClient(context.getApplicationContext());
        }
        return instance;
    }

    private DeviceClient(Context context) {
        this.context = context;
    }

    public boolean sendSensorData(final int sensorType, final int accuracy,
                                  final long timestamp, final float[] values) {

        if(vibrating) {
            return vibrating;
        }

        if(detectBite2(sensorType, values)){
            vibrate();
        }

        return vibrating;
    }

    public boolean sendSensorData2(final int sensorType, final int accuracy,
                                  final long timestamp, final float[] values) {

        if(vibrating) {
            return vibrating;
        }

        if(detectBite2(sensorType, values)) {
            Log.e(TAG, "BITE DETECTED --> VIBRATE SKIPPED");
            PreferenceManager.getDefaultSharedPreferences(context)
                    .edit().putBoolean(Constants.biteDetected, true).apply();
            nextBiteAllowed = PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean(Constants.nextBiteAllowed, true);
            vibrate();
            if(!nextBiteAllowed){
                Log.e(TAG, "BITE DETECTED --> Counter Running");
                vibrate();
                PreferenceManager.getDefaultSharedPreferences(context).edit()
                        .putString(Constants.mealText,
                        "Your eating speed has increased").apply();
            }
            return true;
        }
        return false;
    }

    private LinkedList<Float> yValues = new LinkedList<>();
    private LinkedList<Float> xValues = new LinkedList<>();
    private boolean handTurned = false;


    private boolean handReversed = false;
    private float zValue;

    private void checkIfWatchInversed(float[] values){
        float z = values[2];
        if(z < -3 && zValue < -3 && !handReversed){
            handReversed = true;
            Log.i(TAG, "Hand Reversed");
            new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        Thread.sleep(300);
                        handReversed = false;
                        Log.i(TAG, "Hand Corrected");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

            }.execute();
        }
        zValue = z;
    }

    private float xGyro;
    private boolean handRaised;

    private boolean detectBite2(int sensorType, float[] values) {

        //Log.e(TAG, "handTurned = " + handTurned + sensorType);
        if(sensorType == Sensor.TYPE_ACCELEROMETER){
            checkIfWatchInversed(values);

            if(handReversed){
                handTurned = false;
                handRaised = false;
            }

            float y = values[1];
            yValues.offer(y);
            xValues.offer(values[0]);
            if(yValues.size() > 3) {
                yValues.poll();
                xValues.poll();

                float yAvg = 0;
                float ySum = 0;
                float xAvg = 0;
                float xSum = 0;
/*
                for(Float yValue : yValues){
                    ySum += yValue;
                }*/

                for(int i=0; i< 3; i++){
                    ySum += yValues.get(i);
                    xSum += xValues.get(i);
                }

                yAvg = ySum/3;
                xAvg = xSum/3;

                if(handTurned) {
                    Log.e(TAG, "xAvg = " + xAvg + " & yAvg = " + yAvg);
                }

               if (handTurned && !handReversed && xAvg > -2 && xAvg < 2) {
                    handTurned = false;
                    handRaised = false;
                    Log.i(TAG, System.currentTimeMillis()+" HBite Detected");
                    return true;
                }
                else if(yAvg < -5 && Math.abs(xAvg) > 4) {
                    handTurned = true;
                }
            }
        }
        else{
            if(handTurned) {
                /*if(values[0] > 1 || values[0] < -1)
                    Log.i(TAG, "Gyro Values " + System.currentTimeMillis()
                            + " " + values[0]+"----------------------------------");
                else
                    Log.i(TAG, "Gyro Values " + System.currentTimeMillis()
                            + " " + values[0] + " " + values[1] + " " + values[2]);*/
            }

           /* if(xGyro < -1 && values[0] < -1){
                handRaised = true;
            }*/
            xGyro = values[0];
        }
        return false;
    }




    /**
     * Vibrates the device for 0.5 seconds
     */
    private void vibrate(){
        Log.i(TAG, "Yaaayyy.... Detected a bite");
        if(!vibrating) {
            vibrating = true;
//            Toast.makeText(context, "TEST Vibrate", Toast.LENGTH_SHORT).show();
            new CountDownTimer(1000, 1000) {

                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    vibrating = false;
                }
            }.start();
            v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            v.vibrate(500);
        }
    }
}
