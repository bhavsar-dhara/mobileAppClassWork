package edu.neu.madcourse.dharabhavsar.wear;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by Dhara on 4/3/2016.
 */
public class SensorDetector extends Activity{


    public void startStuff(){
        startService(new Intent(this, SensorService.class));
    }


}
