package edu.neu.madcourse.dharabhavsar.scraggle;

import android.app.Activity;
import android.os.Bundle;

import edu.neu.madcourse.dharabhavsar.main.R;

/**
 * Created by Dhara on 2/26/2016.
 */
public class ScraggleGameActivity2 extends Activity {

    private ScraggleGameFragment2 mGameFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_scraggle2);
        mGameFragment = (ScraggleGameFragment2) getFragmentManager()
                .findFragmentById(R.id.fragment_game_scraggle2);
    }


}
