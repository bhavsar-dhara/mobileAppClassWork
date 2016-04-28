package edu.neu.madcourse.dharabhavsar.ui.main;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.wearable.view.CircularButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Anirudh on 4/28/2016.
 */
public class MealStatusFragment extends Fragment {

    private ImageView mealStatus;
    private static CircularProgressDrawable mCircularProgressTimer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.begin_meal_fragment, container, false);

        mealStatus = (ImageView) view.findViewById(R.id.meal_status);

        mealStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp));

        // Create an instance of a drawable circular progress timer
        /*mCircularProgressTimer = new CircularProgressDrawable(START_TIME,
                MAX_TIME, CircularProgressDrawable.Order.DESCENDING);

        // Set a callback to update our circular progress timer
        mCircularProgressTimer.setCallback(mPieDrawableCallback);

        // Set a drawable object for our Imageview
        mCircularImageView.setImageDrawable(mCircularProgressTimer);

        mSensorManager = ((SensorManager) getSystemService(Context.SENSOR_SERVICE));
        linearAccelero = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        client = DeviceClient.getInstance(this);
*/
        return view;
    }
}
