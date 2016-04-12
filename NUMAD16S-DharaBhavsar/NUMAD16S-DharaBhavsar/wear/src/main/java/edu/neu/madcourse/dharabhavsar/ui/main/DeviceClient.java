package edu.neu.madcourse.dharabhavsar.ui.main;

import android.content.Context;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Vibrator;
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

    public static DeviceClient getInstance(Context context) {
        if (instance == null) {
            instance = new DeviceClient(context.getApplicationContext());
        }
        return instance;
    }

    private DeviceClient(Context context) {
        this.context = context;
    }

    public boolean sendSensorData(final int sensorType, final int accuracy, final long timestamp, final float[] values) {

        if(vibrating) {
            return vibrating;
        }
        /*long t = System.currentTimeMillis();

        long lastTimestamp = lastSensorData.get(sensorType) == null ? 0 : lastSensorData.get(sensorType);
        long timeAgo = t - lastTimestamp;

        if (lastTimestamp != 0) {
            if (filterId == sensorType && timeAgo < 100) {
                return vibrating;
            }

            if (filterId != sensorType && timeAgo < 3000) {
                return vibrating;
            }
        }

        lastSensorData.put(sensorType, t);*/


        /*if(sensorType == Sensor.TYPE_LINEAR_ACCELERATION ||
                sensorType == Sensor.TYPE_GYROSCOPE ||
                sensorType == Sensor.TYPE_GAME_ROTATION_VECTOR ||
                sensorType == Sensor.TYPE_ACCELEROMETER) {
            listValues.add(new String[]{Long.toString(timestamp),
                    String.valueOf(sensorType),
                    Float.toString(values[0]),
                    Float.toString(values[1]),
                    Float.toString(values[2])});
        }*/

        /*if(detectBite(sensorType, values)){
            vibrate();
        }*/
        if(detectBite2(values)){
            vibrate();
        }

        return vibrating;
    }

    private LinkedList<Float> yValues = new LinkedList<>();
    private boolean handRaised = false;


    private boolean handReversed = false;

    private void checkIfWatchInversed(float[] values){
        float z = values[2];
        if(z < 0 && !handReversed){
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
    }

    private boolean detectBite2(float[] values) {

        checkIfWatchInversed(values);

        if(handReversed){
            handRaised = false;
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

            Log.e(TAG, "yAvg = " + yAvg);

            if(yAvg < -5) {
                handRaised = true;
            } else if (yAvg > -2 && handRaised && !handReversed) {
                handRaised = false;
                Log.i(TAG, "HBite Detected");
                return true;
            }
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
