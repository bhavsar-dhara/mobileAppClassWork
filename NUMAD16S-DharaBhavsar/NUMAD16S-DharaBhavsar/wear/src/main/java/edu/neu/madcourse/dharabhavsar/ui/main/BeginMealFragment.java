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
public class BeginMealFragment extends Fragment {

    private CircularButton beginMealButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.begin_meal_fragment, container, false);

        beginMealButton = (CircularButton)view.findViewById(R.id.begin_meal_button);

        beginMealButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp));

        beginMealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }
}
