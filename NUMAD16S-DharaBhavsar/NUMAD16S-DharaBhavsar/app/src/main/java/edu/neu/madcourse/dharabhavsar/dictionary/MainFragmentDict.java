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
//    AutoCompleteTextView acTextViewWordList;
    EditText editWordText;
    MediaPlayer mMediaPlayer;
    Trie trie;
    String result = "";
    byte[] b;
    MainActivityDict mainActDict = new MainActivityDict();
//    DatabaseTable db = new DatabaseTable(MainFragmentDict.this.getActivity());

    public MainFragmentDict() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =
                inflater.inflate(R.layout.fragment_main_dict, container, false);

        // Method to show the list of words found from the provided word list
        textViewWordList = (TextView) rootView.findViewById(R.id.textViewWordList);
//        acTextViewWordList = (AutoCompleteTextView) rootView.findViewById(R.id.textViewWordList);
        textViewWordList.setMovementMethod(new ScrollingMovementMethod());
        editWordText = (EditText) rootView.findViewById(R.id.editWordText);

//        Reading from a file occurs in the AsyncTaskRunner
        editWordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String word = String.valueOf(editWordText.getText());
                Log.e("WORD LENGTH Fragment", "afterTextChanged: " + word.length());
//                    Intent intent = Intent.getIntent(word);
//                for word.length() >= 3
                if(word.length() >= 3) {
//                    if (trie.searchNode(word) == null) {
////                        Do nothing
//                    } else {
//                        result.concat(String.valueOf(trie.searchNode(word)));
//                    }
                    Intent i = new Intent(MainFragmentDict.this.getActivity(), MainFragmentDict.class);
                    mainActDict.onNewIntent(i);
                    result = mainActDict.resultStr;
                    Log.e("RESULT CONCAT Fragment", "afterTextChanged: " + result);
                }
//                Intent intent = Intent.getIntent(word);
//                if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
//                    String query = intent.getStringExtra(SearchManager.QUERY);
//                    Cursor c = db.getWordMatches(query, null);
//                    //process Cursor and display results
//                    c.moveToFirst();
//                    result = c.getString(0);
//                }

//                String word = String.valueOf(editWordText.getText());
//                byte[] byteWord = word.getBytes();
//                int presence = indexOf(b, byteWord);
//
//                if(presence >= 1) {
//                    result.concat(word+"\\n");
//                }
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
