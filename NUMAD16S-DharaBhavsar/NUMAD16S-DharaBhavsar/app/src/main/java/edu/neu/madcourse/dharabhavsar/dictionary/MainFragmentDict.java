package edu.neu.madcourse.dharabhavsar.dictionary;

import android.app.Fragment;
import android.content.Intent;
import android.media.MediaPlayer;
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

        // Method to show the list of words found from the provided word list
        textViewWordList = (TextView) rootView.findViewById(R.id.textViewWordList);
        textViewWordList.setMovementMethod(new ScrollingMovementMethod());
//        Testing scroll
        /*textViewWordList.setText("xxx\n" +
                "rrr\n" +
                "rrr\n" +
                "eee\n" +
                "www\n" +
                "dddd\n" +
                "www\n" +
                "www\n" +
                "qqq\n" +
                "qqq\n" +
                "qqq\n" +
                "rrr\n" +
                "rrfr\n" +
                "rr\n");*/
        editWordText = (EditText) rootView.findViewById(R.id.editWordText);
//        Reading from a file

//        String result;
//        Map<String, String> map = new HashMap<String, String>();

//        try {
//            Resources res = getResources();
//            InputStream in_s = res.openRawResource(R.raw.wordlist);
//
//            byte[] b = new byte[in_s.available()];
//            in_s.read(b);
//            result = new String(b);
//            String[] strings = result.split("\\n");
//            Log.e("INSERT", "inserting");
//            for (String s: strings) {
//                trie.insert(s);
//            }
//            Log.e("INSERT", "inserted");

//            do {
//                if (new String(b).contains("\\n")) {
//                    String[] strings = new String(b).split("\\n");
////                    Log.e("EditTextBox-HashMap", "strings[0] = " + strings[0] + " strings[1] = " + strings[1]);
////                    map.put(strings[0], strings[0]);
//
//                }
//            } while (true);

//        } catch (IOException e) {
//            // e.printStackTrace();
//            Log.e("ERROR", "Error: can't show file.");
//        }

//        Log.e("RES TEST", String.valueOf(result.length()));

//        int i = result.length();
//        Map<String, String> map = new HashMap<String, String>();// it should be static - whereever you define
//            while (i != 0) {
//                if (result.contains("\\n")) {
//                    String[] strings = result.split("\\n");
//                    Log.e("EditTextBox-HashMap", "strings[0] = " + strings[0] + " strings[1] = " + strings[1]);
//                    map.put(strings[0], strings[0]);
//                }
//                i--;
//            }

//        Below code Results into App assassin
//        String[] str = result.split("\\n");
//
//        final ArrayList<String> words = new ArrayList<>(650000);
//        for (String element : str) {
//            words.add(element);
//        }


//        Log.e("RES TEST", String.valueOf(words.size()));

//        Try 3
//        FileInputStream fis;
//        final StringBuffer storedString = new StringBuffer();
//        Map<String, Integer> map = new HashMap<String, Integer>();
//
//        try {
//            Log.e("EditTextBox-HashMap", "xxx in try");
//            fis = fileContext.openFileInput("/libs/wordlist.txt");
//            DataInputStream dataIO = new DataInputStream(fis);
//            String strLine = null;
//
////            if ((strLine = dataIO.readLine()) != null) {
////                Log.e("EditTextBox-HashMap","strings[0] = "+storedString);
////                storedString.append(strLine);
////            }
//
//            while ((strLine = dataIO.readLine()) != null) {
//                if (strLine.contains("\n")) {
//                    String[] strings = strLine.split("\n");
//                    Log.e("EditTextBox-HashMap","strings[0] = "+strings[0]+" strings[1] = "+strings[1]);
//                    map.put(strings[0], Integer.parseInt(strings[1]));
//                }
//            }
//
//            dataIO.close();
//            fis.close();
//        }
//        catch  (Exception e) {
//            Log.e("EditTextBox-HashMap", "xxx in Exception : " + e.getMessage());
//            e.printStackTrace();
//        }

//          Try 1
        // Load the words from the resource file
//        InputStream in = MainFragmentDict.class.getResourceAsStream("/wordlist.txt");
//        InputStreamReader is = new InputStreamReader(in);    // bang
//        final ArrayList<String> words = new ArrayList<>(150000);
//        ArrayList<byte[]> wordsInt = new ArrayList<>(150000);
//        if (is != null) {
//            BufferedReader reader = new BufferedReader(is);
//            do {
//                String line = null;
//                try {
//                    line = reader.readLine();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                if (line == null) {
//                    break;
//                }
//                if (line.matches("[a-z]+")) {
//                    words.add(line);
//                    wordsInt.add(Alphabet.LOWERCASE.toInt(line));
//                }
//            } while (true);
//            try {
//                reader.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        FileInputStream fis = MainFragmentDict.class.
//        final TrieLookup trieLookup = new TrieLookup(words);

//        Try 2
//        try {
//            BufferedReader reader = new BufferedReader(new FileReader(new File("/lib/wordlist.txt")));
//            String line = null;
//            Map<String, Integer> map = new HashMap<String, Integer>();// it should be static - whereever you define
//            while ((line = reader.readLine()) != null) {
//                if (line.contains("\n")) {
//                    String[] strings = line.split("\n");
//                    Log.e("EditTextBox-HashMap","strings[0] = "+strings[0]+" strings[1] = "+strings[1]);
//                    map.put(strings[0], Integer.parseInt(strings[1]));
//                }
//            }
//        } catch (Exception e) {
//            Log.e("EditTextBox-HashMap","error = " );
//            e.printStackTrace();
//        }


        editWordText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                String word = String.valueOf(editWordText.getText());

                boolean flag = trie.search(word);
                Log.e("SEARCH", String.valueOf(flag));
//                Try 1
//                int r = (int) (Math.random() * words.size());
//                Log.e("EditTextBox","Checking word Trie with int[], r = "+r);
//                long start = System.currentTimeMillis();
//                int found = 0;
//                String word = String.valueOf(editWordText.getText());
//                    if (trieLookup.contains(word)) {
//                        found++;
//                    }
//                Log.e("EditTextBox","Completed check in "
//                        + (System.currentTimeMillis() - start) + " ms. Found " + found);
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

                // wrong implementation as it starts a new activity
//                Intent intent = new Intent(MainFragmentDict.this.getActivity(), MainActivity.class);5
//                startActivity(intent);
//                Method 2
//                super.onBackPressed();
//                Method 3
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

//    TODO
//    getData()
//    putData()

//    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
//
//        private String resp;
//
//        @Override
//        protected String doInBackground(String... params) {
//            publishProgress("Sleeping..."); // Calls onProgressUpdate()
//            try {
//                // Do your long operations here and return the result
////                String result;
//                try {
//                    Resources res = getResources();
//                    InputStream in_s = res.openRawResource(R.raw.wordlist);
//
//                    byte[] b = new byte[in_s.available()];
//                    in_s.read(b);
//                    result = new String(b);
//                    String[] strings = result.split("\\n");
//                    Log.e("INSERT", "inserting");
//                    for (String s : strings) {
//                        trie.insert(s);
//                    }
//                    Log.e("INSERT", "inserted");
//                } catch (IOException e) {
//                    Log.e("ERROR", "not inserted");
//                }
//                int time = Integer.parseInt(params[0]);
//                // Sleeping for given time period
//                Thread.sleep(time);
//                resp = "Slept for " + time + " milliseconds";
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//                resp = e.getMessage();
//            } catch (Exception e) {
//                e.printStackTrace();
//                resp = e.getMessage();
//            }
//            return resp;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            // execution of result of Long time consuming operation
//            textViewWordList.setText(result);
//        }
//
//        @Override
//        protected void onPreExecute() {
//            // Things to be done before execution of long running operation. For
//            // example showing ProgessDialog
//        }
//
//        @Override
//        protected void onProgressUpdate(String... text) {
//            textViewWordList.setText(text[0]);
//        }
//    }
}
