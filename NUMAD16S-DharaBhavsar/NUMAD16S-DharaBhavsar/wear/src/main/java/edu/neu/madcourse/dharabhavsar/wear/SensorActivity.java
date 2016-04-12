package edu.neu.madcourse.dharabhavsar.wear;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;


public class SensorActivity extends Activity {

    private static final String TAG = "SensorActivity";

    private final static int SENS_ACCELEROMETER = Sensor.TYPE_ACCELEROMETER;
    private final static int SENS_GYROSCOPE = Sensor.TYPE_GYROSCOPE;
    private final static int SENS_GRAVITY = Sensor.TYPE_GRAVITY;
    private final static int SENS_LINEAR_ACCELERATION = Sensor.TYPE_LINEAR_ACCELERATION;
    private final static int SENS_ROTATION_VECTOR = Sensor.TYPE_ROTATION_VECTOR;

    private Sensor gyro;
    private Sensor linearAccelero;

    private SensorManager mSensorManager;
    private SensorEventListener mSensorListener;
    private TextView displayData;
    private boolean bite = false;

    private static final float NS2S = 1.0f / 1000000000.0f;
    private final static double EPSILON = 0.00001;
    private final float[] deltaRotationVector = new float[4];
    private float timestamp;

    private int gyroCount = 0;

    private float[] gyroCheck = new float[3];

    private DeviceClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                displayData = (TextView)findViewById(R.id.counter);
            }
        });

        mSensorManager = ((SensorManager) getSystemService(Context.SENSOR_SERVICE));
        gyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        linearAccelero = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

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
        mSensorListener = new SensorEventListener() {
            @Override
            public void onAccuracyChanged(Sensor arg0, int arg1) {
            }

            @Override
            public void onSensorChanged(SensorEvent event) {

                if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
                    if(event.values[0] == gyroCheck[0] &&
                            event.values[1] == gyroCheck[1] &&
                            event.values[2] == gyroCheck[2] &&
                            gyroCount++ > 6){
                        restartGyroscope();
                    }
                    else{
                        gyroCount = 0;
                        gyroCheck[0] = event.values[0];
                        gyroCheck[1] = event.values[1];
                        gyroCheck[2] = event.values[2];
                    }
                    //calcGyroRotation(event);
                }
                boolean check = client.sendSensorData(event.sensor.getType(), event.accuracy, event.timestamp, event.values);
                if(bite != check){
                    updateBiteDisplay(check);
                    bite = check;
                }
            }
        };

        mSensorManager.registerListener(mSensorListener,
                gyro, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(mSensorListener,
                linearAccelero, SensorManager.SENSOR_DELAY_GAME);
        /*mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_UI);
*/
        /*
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


        }*/
    }

    private void stopMeasurement() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(mSensorListener);
        }
    }
/*
    public void calcGyroRotation(SensorEvent event) {
        // This timestep's delta rotation to be multiplied by the current rotation
        // after computing it from the gyro sample data.
        if (timestamp != 0) {
            final float dT = (event.timestamp - timestamp) * NS2S;
            // Axis of the rotation sample, not normalized yet.
            float axisX = event.values[0];
            float axisY = event.values[1];
            float axisZ = event.values[2];

            // Calculate the angular speed of the sample
            float omegaMagnitude = sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);

            // Normalize the rotation vector if it's big enough to get the axis
            // (that is, EPSILON should represent your maximum allowable margin of error)
            if (omegaMagnitude > EPSILON) {
                axisX /= omegaMagnitude;
                axisY /= omegaMagnitude;
                axisZ /= omegaMagnitude;
            }

            // Integrate around this axis with the angular speed by the timestep
            // in order to get a delta rotation from this sample over the timestep
            // We will convert this axis-angle representation of the delta rotation
            // into a quaternion before turning it into the rotation matrix.
            float thetaOverTwo = omegaMagnitude * dT / 2.0f;
            float sinThetaOverTwo = sin(thetaOverTwo);
            float cosThetaOverTwo = cos(thetaOverTwo);
            deltaRotationVector[0] = sinThetaOverTwo * axisX;
            deltaRotationVector[1] = sinThetaOverTwo * axisY;
            deltaRotationVector[2] = sinThetaOverTwo * axisZ;
            deltaRotationVector[3] = cosThetaOverTwo;
        }
        timestamp = event.timestamp;
        float[] deltaRotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
        // User code should concatenate the delta rotation we computed with the current rotation
        // in order to get the updated rotation.
        // rotationCurrent = rotationCurrent * deltaRotationMatrix;
    }*/

    private void updateBiteDisplay(boolean bite){
        if(bite){
            displayData.setText(R.string.bite_detected);
            displayData.setBackgroundColor(getResources().getColor(R.color.green));
        }
        else{
            displayData.setText(R.string.take_bite);
            displayData.setBackgroundColor(getResources().getColor(R.color.white));
        }
    }

    /**
     * Restarts the gyroscope
     */
    public void restartGyroscope(){

        Log.i(TAG,"Restarting the Gyroscope");
        try {
            mSensorManager.unregisterListener(mSensorListener, gyro);
            mSensorManager.registerListener(mSensorListener, gyro, SensorManager.SENSOR_DELAY_UI);
        }
        catch(Exception e){
            Log.e(TAG, "Error while restarting the Gyroscope - "+e.getMessage());
        }
    }

    /**
     * Restarts the Linear Accelerometer
     */
    public void restartAccelerometer(){

        Log.i(TAG,"Restarting the Linear Accelerometer");
        try {
            mSensorManager.unregisterListener(mSensorListener, linearAccelero);
            mSensorManager.registerListener(mSensorListener, linearAccelero, SensorManager.SENSOR_DELAY_GAME);
        }
        catch(Exception e){
            Log.e(TAG, "Error while restarting the Linear Accelerometer - "+e.getMessage());
        }
    }
}
