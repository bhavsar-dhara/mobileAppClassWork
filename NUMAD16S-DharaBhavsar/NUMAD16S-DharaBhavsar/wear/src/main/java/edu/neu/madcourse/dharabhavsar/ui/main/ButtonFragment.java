package edu.neu.madcourse.dharabhavsar.ui.main;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.CircularButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Anirudh on 4/16/2016.
 */
public class ButtonFragment extends Fragment {

    private CircularButton mealButton;
    private TextView mealButtonText;
    private boolean mealStarted = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_button, container, false);

        mealButton = (CircularButton)view.findViewById(R.id.fragment_button);
        mealButtonText = (TextView)view.findViewById(R.id.fragment_button_text);

        mealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSharedPreferences("mealPrefs", Context.MODE_PRIVATE).edit().putBoolean("mealStarted", !mealStarted).apply();
                Intent intent = new Intent(getActivity(), SensorActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    public void onResume(){
        super.onResume();
        mealStarted = getActivity().getSharedPreferences("mealPrefs", Context.MODE_PRIVATE).getBoolean("mealStarted", false);
        if(mealStarted){
            mealButtonText.setText(R.string.finish_meal);
            mealButton.setImageResource(R.drawable.ic_stop_white_24dp);
        }
        else{
            mealButtonText.setText(R.string.start_meal);
            mealButton.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        }
    }
}
