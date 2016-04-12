package edu.neu.madcourse.dharabhavsar.wear;

import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import static android.util.FloatMath.cos;
import static android.util.FloatMath.sin;
import static android.util.FloatMath.sqrt;

/**
 * Created by Dhara on 4/11/2016.
 */
public class CalcGyroRotation extends SensorActivity {
    // Create a constant to convert nanoseconds to seconds.
    private static final float NS2S = 1.0f / 1000000000.0f;
    private final static double EPSILON = 0.00001;
    private final float[] deltaRotationVector = new float[4];
    private float timestamp;


}
