package edu.neu.madcourse.dharabhavsar.ui.main;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by Dhara on 4/21/2016.
 */
public class SettingsFragment extends Fragment
        implements SharedPreferences.OnSharedPreferenceChangeListener  {

    private ImageButton leftButton;
    private TextView secondsText;
    private ImageButton rightButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.settings_layout, container, false);

        leftButton = (ImageButton)view.findViewById(R.id.decrease_button);
        secondsText = (TextView)view.findViewById(R.id.editText);
        rightButton = (ImageButton)view.findViewById(R.id.increase_button);

        secondsText.setTag(secondsText.getKeyListener());
        secondsText.setKeyListener(null);

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int seconds = Integer.parseInt(secondsText.getText().toString());
                seconds--;
                secondsText.setText(String.valueOf(seconds));
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int seconds = Integer.parseInt(secondsText.getText().toString());
                seconds++;
                secondsText.setText(String.valueOf(seconds));
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }
}
