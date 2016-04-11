package edu.neu.madcourse.dharabhavsar.wear;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


public class SensorActivity extends Activity implements SensorEventListener {

    private static final String TAG = "SensorActivity";

    private final static int SENS_ACCELEROMETER = Sensor.TYPE_ACCELEROMETER;
    private final static int SENS_GYROSCOPE = Sensor.TYPE_GYROSCOPE;
    private final static int SENS_GRAVITY = Sensor.TYPE_GRAVITY;
    private final static int SENS_LINEAR_ACCELERATION = Sensor.TYPE_LINEAR_ACCELERATION;
    private final static int SENS_ROTATION_VECTOR = Sensor.TYPE_ROTATION_VECTOR;

    private SensorManager mSensorManager;
    private TextView displayData;
    private boolean bite = false;

    private float[] gyroCheck = new float[3];

    private DeviceClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        displayData = (TextView)findViewById(R.id.counter);

        client = DeviceClient.getInstance(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopMeasurement();
        client.writeToFile();
    }

    @Override
    public void onResume(){
        super.onResume();
        startMeasurement();
        client.clearFile();
    }

    protected void startMeasurement() {
        mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));

        Sensor accelerometerSensor = mSensorManager.getDefaultSensor(SENS_ACCELEROMETER);
        Sensor gravitySensor = mSensorManager.getDefaultSensor(SENS_GRAVITY);
        Sensor gyroscopeSensor = mSensorManager.getDefaultSensor(SENS_GYROSCOPE);
        Sensor linearAccelerationSensor = mSensorManager.getDefaultSensor(SENS_LINEAR_ACCELERATION);
        Sensor rotationVectorSensor = mSensorManager.getDefaultSensor(SENS_ROTATION_VECTOR);

        // Register the listener
        if (mSensorManager != null) {
            if (accelerometerSensor != null) {
                mSensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
            } else {
                Log.w(TAG, "No Accelerometer found");
            }

            if (gravitySensor != null) {
                mSensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.w(TAG, "No Gravity Sensor");
            }

            if (gyroscopeSensor != null) {
                mSensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.w(TAG, "No Gyroscope Sensor found");
            }

            if (linearAccelerationSensor != null) {
                mSensorManager.registerListener(this, linearAccelerationSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No Linear Acceleration Sensor found");
            }

            if (rotationVectorSensor != null) {
                mSensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No Rotation Vector Sensor found");
            }

        }
    }

    private void stopMeasurement() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        boolean found = client.sendSensorData(event.sensor.getType(), event.accuracy, event.timestamp, event.values);

        //Check if display needs to be changed
        if(found != bite)
            updateBiteDisplay(found);
        bite = found;

        //Check if gyro is giving same values
        if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
            if(event.values[0] == gyroCheck[0] &&
                    event.values[1] == gyroCheck[1] &&
                    event.values[2] == gyroCheck[2]){
                restartGyroscope();
            }
            else{
                gyroCheck[0] = event.values[0];
                gyroCheck[1] = event.values[1];
                gyroCheck[2] = event.values[2];
            }
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void updateBiteDisplay(boolean bite){
        if(bite){
            displayData.setText("Bite Detected");
            displayData.setBackgroundColor(getResources().getColor(R.color.green));
        }
        else{
            displayData.setText("Take a Bite");
            displayData.setBackgroundColor(getResources().getColor(R.color.white));
        }
    }

    /**
     * Restarts the gyroscope
     */
    private void restartGyroscope(){
        Log.i(TAG,"Restarting the Gyroscope");
        try {
            Sensor gyroscopeSensor = mSensorManager.getDefaultSensor(SENS_GYROSCOPE);
            mSensorManager.unregisterListener(this, gyroscopeSensor);
            mSensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        catch(Exception e){
            Log.e(TAG, "Error while restarting the Gyroscope - "+e.getMessage());
        }
    }
}
