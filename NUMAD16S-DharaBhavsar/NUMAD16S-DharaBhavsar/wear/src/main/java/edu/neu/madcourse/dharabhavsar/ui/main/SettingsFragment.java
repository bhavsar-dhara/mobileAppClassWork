package edu.neu.madcourse.dharabhavsar.ui.main;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Dhara on 4/21/2016.
 */
public class SettingsFragment extends Fragment {

    private static final String TAG = "SettingsFragment";
    private static final int REQUEST_CODE = 1;
    private ImageButton leftButton;
    private TextView secondsText;
    private ImageButton rightButton;
    private CheckBox isManual;
    private SharedPreferences sp;
    private LinearLayout linearLayout;
    private TextView text2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.settings_layout, container, false);

        leftButton = (ImageButton)view.findViewById(R.id.decreaseButton);
        secondsText = (TextView) view.findViewById(R.id.editText);
        rightButton = (ImageButton)view.findViewById(R.id.increaseButton);
        isManual = (CheckBox)view.findViewById(R.id.checkBox);
        linearLayout = (LinearLayout)view.findViewById(R.id.setBiteInterval);
        text2 = (TextView)view.findViewById(R.id.textView2);

        sp = getActivity().getSharedPreferences(Constants.PREF_SHARED, Context.MODE_PRIVATE);

        initialScreen();

        isManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isManual.isChecked()) {
                    sp.edit().putBoolean(Constants.manualBiteInterval, true).apply();
                    linearLayout.setVisibility(View.VISIBLE);
                    text2.setVisibility(View.VISIBLE);
                    String noOfSeconds = "30";
                    if (sp.contains(Constants.manualDurationSet))
                        noOfSeconds = sp.getString(Constants.manualDurationSet, "30");
                    else
                        sp.edit().putString(Constants.manualDurationSet, noOfSeconds).apply();

                    secondsText.setText(noOfSeconds);
                    secondsText.setTag(secondsText.getKeyListener());
                    secondsText.setKeyListener(null);
                } else {
                    sp.edit().putBoolean(Constants.manualBiteInterval, false).apply();
                    linearLayout.setVisibility(View.GONE);
                    text2.setVisibility(View.GONE);
                }
            }
        });

        secondsText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(), CustomWearableList.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        /*leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int seconds = Integer.parseInt(secondsText.getText().toString());
                seconds--;
                secondsText.setText(String.valueOf(seconds));
                sp.edit().putString(Constants.manualDurationSet, String.valueOf(seconds)).apply();
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int seconds = Integer.parseInt(secondsText.getText().toString());
                seconds++;
                secondsText.setText(String.valueOf(seconds));
                sp.edit().putString(Constants.manualDurationSet, String.valueOf(seconds)).apply();
            }
        });*/

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

    private void initialScreen() {
        boolean isManualSettings = sp.getBoolean(Constants.manualBiteInterval, false);
        String noOfSeconds = sp.getString(Constants.manualDurationSet, getString(R.string._30));

        if(isManualSettings) {
            isManual.setChecked(true);
            linearLayout.setVisibility(View.VISIBLE);
            text2.setVisibility(View.VISIBLE);
            secondsText.setText(noOfSeconds);
        } else {
            isManual.setChecked(false);
            linearLayout.setVisibility(View.GONE);
            text2.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getActivity();
        if(requestCode == 1 && resultCode == Activity.RESULT_OK) {
            String result = data.getStringExtra("result");
//            Log.i(TAG, "result obtained = " + result);
            sp.edit().putString(Constants.manualDurationSet, result).apply();
            secondsText.setText(result);
        }
    }
}
