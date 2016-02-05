package edu.neu.madcourse.dharabhavsar.dictionary;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import edu.neu.madcourse.dharabhavsar.main.MainActivity;
import edu.neu.madcourse.dharabhavsar.main.R;

/**
 * Created by Dhara on 2/4/2016.
 */
public class MainFragmentDict extends Fragment {

    MediaPlayer mMediaPlayer;

    public MainFragmentDict() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =
                inflater.inflate(R.layout.fragment_main_dict, container, false);

//        MainFragmentDict.this.getActivity().setContentView(R.layout.fragment_main_dict);

        final TextView textViewWordList;
        final EditText editWordText;

        // Method to show the list of words found from the provided word list
        textViewWordList = (TextView) rootView.findViewById(R.id.textViewWordList);
        textViewWordList.setText("xxx");
        editWordText = (EditText) rootView.findViewById(R.id.editWordText);

        // Method called out on clear button click
        Button clearBtn = (Button) rootView.findViewById(R.id.clearBtn);
        clearBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                mMediaPlayer = MediaPlayer.create(MainFragmentDict.this.getActivity(),
                        R.raw.short_ping_freesound_org);
                mMediaPlayer.setVolume(0.5f, 0.5f);
                mMediaPlayer.start();

//                editWordText.setText("");
//                editWordText.getText().clear();

                // Clear text when clicked
                editWordText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editWordText.setText("");
                    }
                });
                textViewWordList.setText("");
            }
        });

        // Method called out on return to menu button click
        Button returnToMenuBtn = (Button) rootView.findViewById(R.id.returnToMenuBtn);
        returnToMenuBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                mMediaPlayer = MediaPlayer.create(MainFragmentDict.this.getActivity(),
                        R.raw.short_ping_freesound_org);
                mMediaPlayer.setVolume(0.5f, 0.5f);
                mMediaPlayer.start();

                Intent intent = new Intent(MainFragmentDict.this.getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        // Method called out on acknowledgements button click
        Button acknowledgementsBtn = (Button) rootView.findViewById(R.id.acknowledgementsBtn);
        acknowledgementsBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                mMediaPlayer = MediaPlayer.create(MainFragmentDict.this.getActivity(),
                        R.raw.short_ping_freesound_org);
                mMediaPlayer.setVolume(0.5f, 0.5f);
                mMediaPlayer.start();

                Intent intent = new Intent(MainFragmentDict.this.getActivity(), AcknowledgementMainActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }
}
