package edu.neu.madcourse.dharabhavsar.ui.main;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
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

    private DeviceClient client;

    public long savedRemainingInterval = 0;
    private MyCount counter;

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

        client = DeviceClient.getInstance(this.getActivity());

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
        if(counter != null)
            counter.start();
    }

    @Override
    public void onPause(){
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .unregisterOnSharedPreferenceChangeListener(this);
        if(counter != null)
            counter.cancel();
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
        if(key.equalsIgnoreCase(Constants.nextBiteAllowed)) {
            long biteCount = PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .getInt(Constants.biteCount, 0);
            Log.e(TAG, "biteCount = " + biteCount);
            if(biteCount > 4) {
                final boolean isAllowed = PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .getBoolean(key, true);
                if (!isAllowed) {
                    final long interval = Math.round(PreferenceManager.getDefaultSharedPreferences(getActivity())
                            .getLong(Constants.biteInterval, 0) / 1000);
//                    client.counter = new DeviceClient.MyCount(interval, 1000);
                    /*new CountDownTimer(interval, 1000) {

                        @Override
                        public void onTick(long l) {
                            int secs = (int) (interval / 1000);
                            int seconds = secs % 60;
                            int minutes = secs / 60;
                            String stringTime = String.format("%02d:%02d", minutes, seconds);
                            Log.e(TAG, "stringTime = " + stringTime);
                            timerText.setText("Next bite in " + stringTime + " seconds");
                        }

                        @Override
                        public void onFinish() {
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(getActivity()).edit();
                            editor.putBoolean(Constants.nextBiteAllowed, true).apply();
                        }
                    }.start();*/
                    counter = new MyCount(interval, 1000);
                }
            }
        }
    }

    public class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            SharedPreferences.Editor editor = PreferenceManager
                    .getDefaultSharedPreferences(getActivity()).edit();
            editor.putBoolean(Constants.nextBiteAllowed, true).apply();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int secs = (int) (millisUntilFinished);
            int seconds = secs % 60;
            int minutes = secs / 60;
            String stringTime = String.format("%02d:%02d", minutes, seconds);
            Log.e(TAG, "stringTime = " + stringTime);
            timerText.setText("Next bite in " + stringTime + " seconds");
            savedRemainingInterval = millisUntilFinished;
        }
    }
}
