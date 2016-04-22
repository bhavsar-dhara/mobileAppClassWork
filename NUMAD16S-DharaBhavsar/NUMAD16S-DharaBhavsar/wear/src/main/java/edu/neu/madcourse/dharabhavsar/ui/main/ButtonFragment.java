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

    private static final String TAG = "ButtonFragment";

    private ImageButton mealButton;
    private TextView mealButtonText;
    private TextView timerText;
    private boolean mealStarted = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //View view =  inflater.inflate(R.layout.fragment_button, container, false);
        View view =  inflater.inflate(R.layout.fragment_meal_button, container, false);

        mealButton = (ImageButton)view.findViewById(R.id.meal_button);
        mealButtonText = (TextView)view.findViewById(R.id.meal_text);
        timerText = (TextView)view.findViewById(R.id.timer_text);

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
            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                    .putString(Constants.mealText,
                            getString(R.string.eating_speed_text)).apply();
            mealButtonText.setText(R.string.eating_speed_text);
            mealButton.setBackground(getResources().getDrawable(R.drawable.finish_meal));
        }
        else{
            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                    .putString(Constants.mealText,
                            getString(R.string.hand_notifier)).apply();
            mealButtonText.setText(R.string.hand_notifier);
            timerText.setText("");
            mealButton.setBackground(getResources().getDrawable(R.drawable.begin_meal));
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equalsIgnoreCase(Constants.timerText)) {
            timerText.setText(PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .getString(Constants.timerText, "Timer Text"));
        }
        if(key.equalsIgnoreCase(Constants.mealText)) {
            mealButtonText.setText(PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .getString(Constants.mealText, "Meal Text"));
        }
    }
}
