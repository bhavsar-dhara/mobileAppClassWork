package edu.neu.madcourse.dharabhavsar.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.wearable.view.CircularButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Anirudh on 4/28/2016.
 */
public class FinishMealFragment extends Fragment {

    private CircularButton finishMealButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.finish_meal_fragment, container, false);

        finishMealButton = (CircularButton)view.findViewById(R.id.finish_meal_button);

        finishMealButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop_white_24dp));

        finishMealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Finish the meal and show the stats
            }
        });

        return view;
    }
}
