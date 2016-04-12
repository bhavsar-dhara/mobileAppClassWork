package edu.neu.madcourse.dharabhavsar.wear;

import android.content.Context;
import android.hardware.Sensor;
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
    private SensorActivity sensorActivity = new SensorActivity();

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


        if(sensorType == Sensor.TYPE_LINEAR_ACCELERATION ||
                sensorType == Sensor.TYPE_GYROSCOPE ||
                sensorType == Sensor.TYPE_GAME_ROTATION_VECTOR ||
                sensorType == Sensor.TYPE_ACCELEROMETER) {
            listValues.add(new String[]{Long.toString(timestamp),
                    String.valueOf(sensorType),
                    Float.toString(values[0]),
                    Float.toString(values[1]),
                    Float.toString(values[2])});
        }

        if(detectBite(sensorType, values)){
            vibrate();
        }

        if(sensorType == Sensor.TYPE_ACCELEROMETER){
            detectStuff(sensorType, values[0], values[1], values[2]);
        }
        return vibrating;
    }

    List<Long> accel = new ArrayList<>();
    LinkedList<Long> gyro = new LinkedList<>();

    private boolean detectBite(int sensorType, float[] values){

        int aCount = 0;
        long currTime = System.currentTimeMillis();

        float absSum = Math.abs(values[0]) + Math.abs(values[1]) + Math.abs(values[2]);

        if(sensorType == Sensor.TYPE_LINEAR_ACCELERATION){
            if(absSum > 1){
                accel.add(currTime);
                Log.i(TAG,"Accelero crossed 1 "+currTime);
            }
            else{
                aCount = accel.size();

                if(aCount < 6 && aCount != 0 && gyro.size() != 0){

                    long sum = 0;
                    for(Long value : accel){
                        sum += value;
                    }
                    long aAvg = sum/accel.size();
                    sum = 0;
                    for(Long value : gyro){
                        sum += value;
                    }
                    long gAvg = sum/gyro.size();
                    Log.i(TAG, "And the average is "+Math.abs(aAvg - gAvg));
                    if(Math.abs(aAvg - gAvg) < 100){
                        return true;
                    }
                }
                accel.clear();
            }
        }
        else{

            if(absSum > 5){
                Log.i(TAG,"Gyro crossed 1 "+currTime+" "+values[0]+" "+values[1]+" "+values[2]);
                gyro.offer(currTime);
            }
            else{
                while(currTime - (gyro.peek() != null ? gyro.peek() : currTime) > 100){
                    if(!gyro.isEmpty())
                        gyro.poll();
                }
            }
        }
        return false;
    }

    private long maxAccelroUpTime = 0;
    private long maxAccelroDownTime = 0;
    private long lastAccelroUpTime = 0;
    private long lastAccelroDownTime = 0;
    private float lastAccelroValue = 0;
    private int accelroCountUp = 0;
    private int accelroCountDown = 0;
    private float maxAccelroValue = 0;

    private long maxGyroUpTime = 0;
    private long maxGyroDownTime = 0;
    private long lastGyroUpTime = 0;
    private long lastGyroDownTime = 0;
    private float lastGyroValue = 0;
    private int gyroCountUp = 0;
    private int gyroCountDown = 0;
    private float maxGyroValue = 0;

    private void detectBite2(int sensorType, float[] values){

        long currTime = System.currentTimeMillis();

        float valueCheck = values[0] * values[0] + values[1] * values[1] + values[2] * values[2];

        if(sensorType == Sensor.TYPE_LINEAR_ACCELERATION){
            if(valueCheck > lastAccelroValue){
                if(maxAccelroUpTime < (currTime - lastAccelroUpTime)){
                    maxAccelroUpTime = currTime - lastAccelroUpTime;
                    accelroCountUp = 0;
                    maxAccelroValue = valueCheck;
                }
                else{
                    accelroCountUp++;
                }
                lastAccelroDownTime = currTime;
            }
            else{
                if(maxAccelroDownTime < (currTime - lastAccelroDownTime)){
                    maxAccelroDownTime = currTime - lastAccelroDownTime;
                    accelroCountDown = 0;
                }
                else{
                    accelroCountDown++;
                }

                lastAccelroUpTime = currTime;
            }
            lastAccelroValue = valueCheck;
        }
        if(accelroCountUp > 10){
            Log.i(TAG, "Max accelroUp Value "+maxAccelroUpTime+" with max value as "+maxAccelroValue);
        }
        if(accelroCountDown > 10){
            Log.i(TAG, "Max accelroDown Value "+maxAccelroDownTime);
        }

        //For Gyro
        if(sensorType == Sensor.TYPE_GYROSCOPE){
            if(valueCheck > lastGyroValue){
                if(maxGyroUpTime < (currTime - lastGyroUpTime)){
                    maxGyroUpTime = currTime - lastGyroUpTime;
                    gyroCountUp = 0;
                    maxGyroValue = valueCheck;
                }
                else{
                    gyroCountUp++;
                }
                lastGyroDownTime = currTime;
            }
            else{
                if(maxGyroDownTime < (currTime - lastGyroDownTime)){
                    maxGyroDownTime = currTime - lastGyroDownTime;
                    gyroCountDown = 0;
                }
                else{
                    gyroCountDown++;
                }

                lastGyroUpTime = currTime;
            }
            lastGyroValue = valueCheck;
        }
        if(gyroCountUp > 10){
            Log.i(TAG, "Max GyroUp Value "+maxGyroUpTime+" with max value as "+maxGyroValue);
        }
        if(gyroCountDown > 10){
            Log.i(TAG, "Max GyroDown Value "+maxGyroDownTime);
        }

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

    private boolean speedCheck = false;
    private int count = 0;
    private float gx = 0;
    private float gy = 0;
    private float gz = 0;
    private float lx = 0;
    private float ly = 0;
    private float lz = 0;
    private final float alpha = 0.8f;
    private int correctSpeed = 0;
    private LinkedList<Long> dataList = new LinkedList<>();
    private Map<Long, Float> dataMap = new HashMap<>();

    private void detectStuff(long timestamp, float x, float y, float z){

        if(true)
            return;
        if(vibrating)
            return;

        gx = alpha * gx + (1 - alpha) * x;
        gy = alpha * gy + (1 - alpha) * y;
        gz = alpha * gz + (1 - alpha) * z;

        lx = x - gx;
        ly = y - gy;
        lz = z - gz;

        float value = (float)Math.sqrt(lx*lx+ly*ly+lz*lz);

        long lastTS = dataList.peek() == null ? 0 : dataList.peek();
        long MAX_INTERVAL = 100000000; // 0.1second
        if(timestamp - lastTS > MAX_INTERVAL){
            dataList.poll();
            dataMap.remove(lastTS);
        }
        dataList.offer(timestamp);
        dataMap.put(timestamp, value);

        float diff = value - (dataMap.get(dataList.peek()) == null ? 0 : dataMap.get(dataList.peek()));
        if(diff > 1){
            //Log.i("TAG", "ACCELERATING   "+diff);
            Bite.biteCheck(1, timestamp);
        }
        else if(diff < -1){
            //Log.i("TAG", "DECELERATING   "+diff);
            Bite.biteCheck(2, timestamp);
        }
        else{
            Bite.biteCheck(3, timestamp);
        }
        if(Bite.isValidBite()){
            vibrate();
        }

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
                    sensorActivity.restartGyroscope();
                    sensorActivity.restartAccelerometer();
                    vibrating = false;
                }
            }.start();
            v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            v.vibrate(500);
        }
    }
}

class Bite{
    private static int upAcc = 0;
    private static int upDec = 0;
    private static int upCon = 0;
    private static int downAcc = 0;
    private static int downDec = 0;
    private static int downCon = 0;
    private static boolean goingUp = true;
    private static boolean validBite = false;
    private static long accTimeStamp = 0;
    private static boolean accCorrect = false;

    public static boolean isValidBite(){
        toStrings();
        if(validBite){
            resetValues();
            return true;
        }
        else
            return false;
    }

    private static void accelration(long timestamp){
        if(goingUp){
            if(upAcc == 0){
                accTimeStamp = timestamp;
            }
            else{
                accCorrect = (timestamp - accTimeStamp) > 100000000 && (timestamp - accTimeStamp) < 300000000;
            }
            upAcc++;
        }
        else{
            if(downAcc == 0){
                accTimeStamp = timestamp;
            }
            else{
                accCorrect = (timestamp - accTimeStamp) > 100000000 && (timestamp - accTimeStamp) < 300000000;
            }
            downAcc++;
        }
    }

    private static void decelration(){
        if(!accCorrect){
            resetValues();
        }
        else if(goingUp) {
            upDec++;
        }
        else{
            downDec++;
        }
    }

    public static void biteCheck(int motion, long timestamp){
        if(motion == 1){
            accelration(timestamp);
        }
        else if(motion == 2){
            decelration();
            validUpMovementComplete();
        }
        else{
            if(!accCorrect){
                resetValues();
            }
            if(goingUp) {
                if(upAcc > 1)
                    upCon++;
            }
            else {
                if(downAcc > 1)
                    downCon++;
            }
        }
    }


    private static void validUpMovementComplete(){
        if(upAcc > 15 || downAcc > 15){
            resetValues();
            return;
        }
        if(goingUp){
            goingUp = !(accCorrect && upCon > 4 && upDec > 4);
        }
        else{
            validBite = accCorrect && downCon > 4 && downDec > 4;
        }
    }

    private static void resetValues(){
        upAcc = 0;
        upDec = 0;
        upCon = 0;
        downAcc = 0;
        downDec = 0;
        downCon = 0;
        goingUp = true;
        validBite = false;
        accTimeStamp = 0;
        accCorrect = false;
    }

    private static void toStrings(){
        Log.i("Bite",""+accCorrect+goingUp+" - "+accCorrect+" up"+upAcc+" "+upDec+" "+upCon+" down"+downAcc+" "+downDec+" "+downCon);
    }
}

