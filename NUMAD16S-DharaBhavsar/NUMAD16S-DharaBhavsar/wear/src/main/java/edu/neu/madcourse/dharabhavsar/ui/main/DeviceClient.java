package edu.neu.madcourse.dharabhavsar.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
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

        if(detectBite2(sensorType, values) && nextBiteAllowed){
            Log.e(TAG, "BITE DETECTED --> VIBRATE SKIPPED");
            int biteCount = PreferenceManager.getDefaultSharedPreferences(context)
                    .getInt(Constants.biteCount, 0);
            if(biteCount > 5) {
                nextBiteAllowed = false;
                SharedPreferences sp = PreferenceManager
                        .getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean(Constants.nextBiteAllowed, nextBiteAllowed).apply();
            }
            return true;
        }
        /*if(!nextBiteAllowed) {

        }*/
//        nextBiteAllowed = false;
        return false;
    }

    private LinkedList<Float> yValues = new LinkedList<>();
    private boolean handTurned = false;


    private boolean handReversed = false;
    private float zValue;

    private void checkIfWatchInversed(float[] values){
        float z = values[2];
        if(z < -2 && zValue < -2 && !handReversed){
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
    private boolean handLowered;

    private boolean detectBite2(int sensorType, float[] values) {

        //Log.e(TAG, "handTurned = " + handTurned + sensorType);
        if(sensorType == Sensor.TYPE_ACCELEROMETER){
            checkIfWatchInversed(values);

            if(handReversed){
                handTurned = false;
                handRaised = false;
                handLowered = false;
            }

            float y = values[1];
            yValues.offer(y);
            if(yValues.size() > 3) {
                yValues.poll();

                float yAvg = 0;
                float ySum = 0;

                for(Float yValue : yValues){
                    ySum += yValue;
                }

                yAvg = ySum/3;

                //Log.e(TAG, "yAvg = " + yAvg);

                if(yAvg < -5) {
                    handTurned = true;
                } else if (yAvg > -2 && handTurned && handRaised && !handReversed && handLowered) {
                    handTurned = false;
                    handRaised = false;
                    Log.i(TAG, System.currentTimeMillis()+" HBite Detected");
                    return true;
                }
            }
        }
        else{
            if(handTurned) {
                if(values[0] > 1 || values[0] < -1)
                    Log.i(TAG, "Gyro Values " + System.currentTimeMillis()
                            + " " + values[0]+"----------------------------------");
                else
                    Log.i(TAG, "Gyro Values " + System.currentTimeMillis()
                            + " " + values[0] + " " + values[1] + " " + values[2]);
            }

            if(xGyro < -1 && values[0] < -1){
                handRaised = true;
            }
            else if(xGyro > 1 && values[0] > 0.5 && handRaised){
                handLowered = true;
            }

            xGyro = values[0];
        }


        return false;
    }
    /**
     * Clears the file create for logging data
     */
    protected void clearFile(){
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params){
                try{
                    CSVWriter writer;
                    writer = new CSVWriter(new FileWriter(filePath , false), '\t');
                    writer.writeNext(new String[]{"Date","Sensor2","x","y","z"});
                    writer.close();
                }
                catch (IOException e){
                    Log.e(TAG, "IOException while clearing the file "+e.getMessage());
                }

                return null;
            }
        }.execute();
    }


    /**
     * Writes data to file and saves it
     */
    protected void writeToFile(){

        new AsyncTask<List<String[]>, Void, Void>(){

            @Override
            protected Void doInBackground(List<String[]>... params){
                try{
                    Log.i("FileName","The file name is - "+filePath);
                    File f = new File(filePath);
                    CSVWriter writer;
                    if(f.exists() && !f.isDirectory()){
                        writer = new CSVWriter(new FileWriter(filePath , true), '\t');
                    }
                    else {
                        writer = new CSVWriter(new FileWriter(filePath), '\t');
                    }
                    for(String[] value : params[0])
                        writer.writeNext(value);
                    writer.close();
                } catch (IOException e) {
                    Log.e(TAG, "IOException while writing the file "+e.getMessage());
                    e.printStackTrace();
                } catch (Exception e){
                    Log.e(TAG, "Exception while writing the file "+e.getMessage());
                }
                return null;
            }
        }.execute(listValues);
    }

    /**
     * Vibrates the device for 0.5 seconds
     */
    private void vibrate(){
        if(!vibrating) {
            vibrating = true;
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
