package edu.neu.madcourse.dharabhavsar.dictionary;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import edu.neu.madcourse.dharabhavsar.main.R;

/**
 * Created by Dhara on 2/4/2016.
 */
public class MainFragmentDict extends Fragment {

    TextView textViewWordList;
    EditText editWordText;
    MediaPlayer mMediaPlayer;
    Trie trie;
    String result;
    byte[] b;

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

        // Method to show the list of words found from the provided word list
        textViewWordList = (TextView) rootView.findViewById(R.id.textViewWordList);
        textViewWordList.setMovementMethod(new ScrollingMovementMethod());
        editWordText = (EditText) rootView.findViewById(R.id.editWordText);

//        Reading from a file occurs in the AsyncTaskRunner
        editWordText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                String word = String.valueOf(editWordText.getText());

                byte[] byteWord = word.getBytes();
                int presence = indexOf(b, byteWord);

                if(presence >= 1) {
                    result.concat(word+"\\n");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0)
                    textViewWordList.setText("");
            }
        });

        // Method called out on clear button click
        Button clearBtn = (Button) rootView.findViewById(R.id.clearBtn);
        clearBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                mMediaPlayer = MediaPlayer.create(MainFragmentDict.this.getActivity(),
                        R.raw.short_ping_freesound_org);
                mMediaPlayer.setVolume(0.5f, 0.5f);
                mMediaPlayer.start();

                // Clear text when clicked
//                editWordText.setText("");
                editWordText.getText().clear();
                Log.v("MainFragmentDict", "onClick: "+editWordText.getText());
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

                MainFragmentDict.this.getActivity().finish();
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

//    getData()
    /** Create a string containing the state of the game. */
    public String getData() {
        StringBuilder builder = new StringBuilder();
        builder.append(editWordText);
        builder.append(",");
        builder.append(textViewWordList);
        builder.append(",");
        return builder.toString();
    }
//    putData()
    /** Restore the state of the game from the given string. */
    public void putData(String gameData) {
        String[] fields = gameData.split(",");
        int index = 0;
        editWordText.setText(fields[index++]);
        textViewWordList.setText(fields[index++]);
    }

//    private class KPM {
        /**
         * Search the data byte array for the first occurrence
         * of the byte array pattern.
         */
        public int indexOf(byte[] data, byte[] pattern) {
            int[] failure = computeFailure(pattern);

            int j = 0;

            for (int i = 0; i < data.length; i++) {
                while (j > 0 && pattern[j] != data[i]) {
                    j = failure[j - 1];
                }
                if (pattern[j] == data[i]) {
                    j++;
                }
                if (j == pattern.length) {
                    return i - pattern.length + 1;
                }
            }
            return -1;
        }

        /**
         * Computes the failure function using a boot-strapping process,
         * where the pattern is matched against itself.
         */
        private int[] computeFailure(byte[] pattern) {
            int[] failure = new int[pattern.length];

            int j = 0;
            for (int i = 1; i < pattern.length; i++) {
                while (j>0 && pattern[j] != pattern[i]) {
                    j = failure[j - 1];
                }
                if (pattern[j] == pattern[i]) {
                    j++;
                }
                failure[i] = j;
            }

            return failure;
        }
//    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Sleeping..."); // Calls onProgressUpdate()
            Log.e("ASYNC TASK", "starting");
            try {
                // Do your long operations here and return the result
//                String result;
                try {
                    Resources res = getResources();
                    InputStream in_s = res.openRawResource(R.raw.wordlist);

                    byte[] b = new byte[in_s.available()];
                    in_s.read(b);
                    result = new String(b);
                    String[] strings = result.split("\\n");
                    Log.e("INSERT", "inserting");
                    for (String s : strings) {
                        trie.insert(s);
                    }
                    Log.e("INSERT", "inserted");
                } catch (IOException e) {
                    Log.e("ERROR", "not inserted");
                }
                int time = Integer.parseInt(params[0]);
                // Sleeping for given time period
                Thread.sleep(time);
                resp = "Slept for " + time + " milliseconds";
            } catch (InterruptedException e) {
                e.printStackTrace();
                resp = e.getMessage();
            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            Log.e("ASYNC TASK", "finishing");
            return resp;
        }

        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            textViewWordList.setText(result);
        }

        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
        }

        @Override
        protected void onProgressUpdate(String... text) {
            textViewWordList.setText(text[0]);
        }
    }
}
