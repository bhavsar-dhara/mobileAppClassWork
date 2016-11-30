package edu.neu.madcourse.dharabhavsar.ui.main;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import edu.neu.madcourse.dharabhavsar.ui.about.MainActivityAbout;
import edu.neu.madcourse.dharabhavsar.ui.communication2player.MainActivityScraggle2;
import edu.neu.madcourse.dharabhavsar.ui.communication2player.MainActivityScraggle3;
import edu.neu.madcourse.dharabhavsar.ui.dictionary.MainActivityDict;
import edu.neu.madcourse.dharabhavsar.ui.project.WearableLaunchActivity;
import edu.neu.madcourse.dharabhavsar.ui.scraggle.MainActivityScraggle;
import edu.neu.madcourse.dharabhavsar.ui.ut3.MainActivityUt3;
import edu.neu.madcourse.dharabhavsar.utils.CommonUtils;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final String TAG = MainActivityFragment.class.getSimpleName();

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Method to make Quit button exit the application.
        Button btn1 = (Button) rootView.findViewById(R.id.quit_button);
        btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                CommonUtils.playSound(getContext(), R.raw.short_ping_freesound_org);
                getActivity().finish();

                Log.i(TAG, "clicked on quit_button");
//                Returning back to the Home Screen
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
            }
        });

        // Method to pop-up UT3 app
        Button btn2 = (Button) rootView.findViewById(R.id.tictactoe_button);
        btn2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommonUtils.playSound(getContext(), R.raw.short_ping_freesound_org);

                Log.i(TAG, "clicked on tictactoe_button");
                Intent intent = new Intent(getActivity(), MainActivityUt3.class);
                startActivity(intent);
            }
        });

        // Method to generate an error
        Button btn3 = (Button) rootView.findViewById(R.id.generate_error_button);
        btn3.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommonUtils.playSound(getContext(), R.raw.distillerystudio_error_03_freesound_org);

                Log.i(TAG, "clicked on generate_error_button");
//          App generates an error due to proper
                PackageManager pm = getActivity().getPackageManager();
                Intent intent = pm.getLaunchIntentForPackage("edu.neu.madcourse.dharabhavsar.ui.ut3.MainActivity");
                startActivity(intent);
            }
        });

//        Method to view About Details
        Button btn4 = (Button) rootView.findViewById(R.id.about_button);
        btn4.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommonUtils.playSound(getContext(), R.raw.short_ping_freesound_org);

                Log.i(TAG, "clicked on about_button");
                Intent intent = new Intent(getActivity(), MainActivityAbout.class);
                startActivity(intent);
            }
        });

        Button btn5 = (Button) rootView.findViewById(R.id.dictionary_button);
        btn5.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommonUtils.playSound(getContext(), R.raw.short_ping_freesound_org);

                Log.i(TAG, "clicked on dictionary_button");
                Intent intent = new Intent(getActivity(), MainActivityDict.class);
                startActivity(intent);
            }
        });

        Button btn6 = (Button) rootView.findViewById(R.id.wordgame_button);
        btn6.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommonUtils.playSound(getContext(), R.raw.short_ping_freesound_org);

                Log.i(TAG, "clicked on wordgame_button");
                Intent intent = new Intent(getActivity(), MainActivityScraggle.class);
                startActivity(intent);
            }
        });

        Button btn7 = (Button) rootView.findViewById(R.id.communication_button);
        btn7.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommonUtils.playSound(getContext(), R.raw.short_ping_freesound_org);

                Log.i(TAG, "clicked on communication_button");
                Intent intent = new Intent(getActivity(), MainActivityScraggle2.class);
//                Intent intent = new Intent(getActivity(), CommunicationMain.class);
                startActivity(intent);
            }
        });

        Button btn8 = (Button) rootView.findViewById(R.id.wordgame2player_button);
        btn8.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommonUtils.playSound(getContext(), R.raw.short_ping_freesound_org);

                Log.i(TAG, "clicked on wordgame2player_button");
                Intent intent = new Intent(getActivity(), MainActivityScraggle3.class);
                startActivity(intent);
            }
        });

        Button btn9 = (Button) rootView.findViewById(R.id.trickiestpart_button);
        btn9.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommonUtils.playSound(getContext(), R.raw.short_ping_freesound_org);

                Log.i(TAG, "clicked on trickiestpart_button");
                Intent intent = new Intent(getActivity(), WearableLaunchActivity.class);
                intent.putExtra("finalProject", false);
                startActivity(intent);
            }
        });

        Button btn10 = (Button) rootView.findViewById(R.id.finalproject_button);
        btn10.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommonUtils.playSound(getContext(), R.raw.short_ping_freesound_org);

                Log.i(TAG, "clicked on finalproject_button");
                Intent intent = new Intent(getActivity(), WearableLaunchActivity.class);
                intent.putExtra("finalProject", true);
                startActivity(intent);
            }
        });

        return rootView;
    }
}
