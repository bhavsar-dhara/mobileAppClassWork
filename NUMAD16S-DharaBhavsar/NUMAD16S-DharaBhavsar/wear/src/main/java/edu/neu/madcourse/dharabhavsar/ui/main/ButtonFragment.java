package edu.neu.madcourse.dharabhavsar.ui.main;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by Anirudh on 4/16/2016.
 */
public class ButtonFragment extends Fragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private ImageButton mealButton;
    private TextView mealButtonText;
    private boolean mealStarted = false;

    private DeviceClient client;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //View view =  inflater.inflate(R.layout.fragment_button, container, false);
        View view =  inflater.inflate(R.layout.fragment_meal_button, container, false);

        mealButton = (ImageButton)view.findViewById(R.id.meal_button);
        mealButtonText = (TextView)view.findViewById(R.id.meal_text);

        mealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mealStarted = !mealStarted;
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(getActivity()).edit();
                editor.putBoolean(Constants.mealStarted, mealStarted).apply();
                updateDisplay();
            }
        });

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        mealStarted = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getBoolean(Constants.mealStarted, false);
        updateDisplay();
        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause(){
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private void updateDisplay(){
        if(mealStarted){
            mealButtonText.setText(R.string.eating_speed_text);
            mealButton.setBackground(getResources().getDrawable(R.drawable.finish_meal));
        }
        else{
            mealButtonText.setText(R.string.hand_notifier);
            mealButton.setBackground(getResources().getDrawable(R.drawable.begin_meal));
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equalsIgnoreCase(Constants.biteTime)) {
            long diff = Math.round(PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .getLong(key, 0)/1000);
            Log.i("Bite Time", "the difference is = "+diff);
            String t = String.valueOf(diff);
            mealButtonText.setText("Last bite duration is " + t + " ms");
        }
    }
}
