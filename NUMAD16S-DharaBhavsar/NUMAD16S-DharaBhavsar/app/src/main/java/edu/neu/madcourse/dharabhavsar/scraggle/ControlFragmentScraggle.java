package edu.neu.madcourse.dharabhavsar.scraggle;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.neu.madcourse.dharabhavsar.main.R;

public class ControlFragmentScraggle extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.fragment_control_scraggle, container, false);
        final View pause = rootView.findViewById(R.id.button_pause);
        View quit = rootView.findViewById(R.id.button_quit);
        final View resume = rootView.findViewById(R.id.button_resume);
        /*View restart = rootView.findViewById(R.id.button_restart);*/

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO saving game state & hiding the game grid & stopping the countdown timer
                resume.setVisibility(View.VISIBLE);
                pause.setVisibility(View.INVISIBLE);
                ((ScraggleGameActivity) getActivity()).onPause();
            }
        });
        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO restore game state & game grid & start the countdown timer
                pause.setVisibility(View.VISIBLE);
                resume.setVisibility(View.INVISIBLE);
                ((ScraggleGameActivity) getActivity()).onResume();
            }
        });
        /*restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GameActivity) getActivity()).restartGame();
            }
        });*/
        return rootView;
    }

}
