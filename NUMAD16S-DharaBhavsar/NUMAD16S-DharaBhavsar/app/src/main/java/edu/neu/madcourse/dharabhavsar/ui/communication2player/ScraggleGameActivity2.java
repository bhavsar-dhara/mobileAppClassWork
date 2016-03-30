package edu.neu.madcourse.dharabhavsar.ui.communication2player;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import edu.neu.madcourse.dharabhavsar.model.communication.GameData;
import edu.neu.madcourse.dharabhavsar.model.communication.UserData;
import edu.neu.madcourse.dharabhavsar.ui.main.R;
import edu.neu.madcourse.dharabhavsar.utils.Constants;
import edu.neu.madcourse.dharabhavsar.utils.firebaseconn.RemoteClient;
import edu.neu.madcourse.dharabhavsar.utils.gcmcomm.CommunicationMain;

public class ScraggleGameActivity2 extends Activity {
    private MediaPlayer mMediaPlayer;
    private Handler mHandler = new Handler();
    private ScraggleGameFragment2 mGameFragment;
    private ControlFragmentScraggle2 mControlFragment;
    private List<String> nineWords = new ArrayList<>();
    private TextView mTextField;
    private final int interval = 90000; //90 seconds ; 1 minute 30 seconds
    private long savedRemainingInterval = 0;
    private MyCount counter;
    private String resultStr = "";
    private String finalResult = "";
    private String result = "";
    private ArrayList<String> vocabList = new ArrayList<String>();
    private String insertedText = "";
    private AlertDialog mDialog;
    private Boolean resFlag = false;
    private TextView mScoreTextField;
    private int score = 0;
    private int score2 = 0;
    private boolean restore;
    private boolean isResumeFlag = false;
    private boolean isPhaseTwo = false;
    private String gameData = "";
    private Context appContext = this;
    private boolean isGameEnd = false;

    private static final String STATE_SCORE = "playerScore";
    private static final String STATE_LEVEL = "playerLevel";

    private UserData user;
    private RemoteClient mRemoteClient;
    private SharedPreferences prefs;
    private SharedPreferences prefs2;
    private UserData user2player;
    private String userKey;
    private String userId;
    private GameData gameDataFb;
    private char[][] gameLetter = new char[9][9];
    private CommunicationMain mCommMain = new CommunicationMain();
    private String gameKey;
    private String user2key;
    private HashMap<String, UserData> current2UserMap = new HashMap<String, UserData>();
    private UserData selected2PlayerData;
    private GameData retrievedGameData;

    /*private TextView xcordview;
    private TextView ycordview;
    private TextView buttonIndicator;
    private FrameLayout touchview;
    private static int defaultStates[];
    private Button mLastButton;
    private final static int[] STATE_PRESSED = {
            android.R.attr.state_pressed,
            android.R.attr.state_focused
                    | android.R.attr.state_enabled };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("GAMEActivity", "isPhaseTwo = " + isPhaseTwo);
        Bundle b = this.getIntent().getExtras();
        if (b != null) {
            isPhaseTwo = b.getBoolean("isTwoFlag");
            gameData = b.getString("gameData");
        }

        /*xcordview = (TextView) findViewById(R.id.textView4);
        ycordview = (TextView) findViewById(R.id.textView3);
        buttonIndicator = (TextView) findViewById(R.id.button_indicator);
        touchview = (FrameLayout) findViewById(R.id.frameLayout);

        touchview.setOnTouchListener(new View.OnTouchListener() {

            private boolean isInside = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int x = (int) event.getX();
                int y = (int) event.getY();

                Log.e("Touch testing", "X = " + x);
                Log.e("Touch testing", "Y = " + y);

                xcordview.setText(String.valueOf(x));
                ycordview.setText(String.valueOf(y));

                for (int i = 0; i < touchview.getChildCount(); i++) {
                    View current = touchview.getChildAt(i);
                    Log.e("Touch testing", "inside for");

                    if (current instanceof Button) {
                        Button b = (Button) current;
                        Log.e("Touch testing", "inside if");

                        if (!isPointWithin(x, y, b.getLeft(), b.getRight(), b.getTop(),
                                b.getBottom())) {
                            b.getBackground().setState(defaultStates);
                            Log.e("Touch testing", "inside if if");
                        }

                        if (isPointWithin(x, y, b.getLeft(), b.getRight(), b.getTop(),
                                b.getBottom())) {
                            Log.e("Touch testing", "inside fif if");
                            b.getBackground().setState(STATE_PRESSED);
                            if (b != mLastButton) {
                                Log.e("Touch testing", "inside if if if");
                                mLastButton = b;
                                buttonIndicator.setText(mLastButton.getText());
                            }
                        }

                    }
                }
                return true;
            }

        });*/

//        Log.e("ScraggleActivity", "isPhaseTwo = " + isPhaseTwo);
//        Log.e("ScraggleActivity", "savedRemainingInterval = " + savedRemainingInterval);

//         The below code didn't work for this activity
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        prefs = appContext.getSharedPreferences(RemoteClient.class.getSimpleName(), Context.MODE_PRIVATE);
        prefs2 = appContext.getSharedPreferences(CommunicationMain.class.getSimpleName(), Context.MODE_PRIVATE);
        mRemoteClient = new RemoteClient(appContext);

        userKey = prefs.getString(Constants.USER_UNIQUE_KEY, "");
        Constants.USER_KEY = userKey;
        userId = prefs2.getString(Constants.PROPERTY_REG_ID, "");
        gameKey = prefs.getString(Constants.GAME_UNIQUE_KEY, "");
//        Constants.GAME_KEY = gameKey;

        setContentView(R.layout.activity_game_scraggle2);
        mGameFragment = (ScraggleGameFragment2) getFragmentManager()
                .findFragmentById(R.id.fragment_game_scraggle);
        mControlFragment = (ControlFragmentScraggle2) getFragmentManager()
                .findFragmentById(R.id.fragment_control_scraggle);
        mTextField = (TextView) findViewById(R.id.timerView);
        mScoreTextField = (TextView) findViewById(R.id.scoreView);

        restore = getIntent().getBooleanExtra(Constants.KEY_RESTORE, false);
        if (restore) {
            String gameData = getPreferences(MODE_PRIVATE)
                    .getString(Constants.PREF_RESTORE, null);
//            Log.e("ScraggleActivity", "gameData = " + gameData);
            if (gameData != null) {
                mGameFragment.putState(gameData);
            }
        }
        Log.e("Scraggle", "restore = " + restore);

//        mScoreTextField.setText("Score = " + String.valueOf(score));
        if (!isPhaseTwo) {
            mScoreTextField.setText("Score = " + String.valueOf(score));
        } else {
            mScoreTextField.setText("Score = " + String.valueOf(score + score2));
        }

//        Log.e("ScraggleActivity", "savedRemainingInterval = " + savedRemainingInterval);
        if (isPhaseTwo) {
            if (savedRemainingInterval < 2000) {
                Log.e("PhaseTwo Timer", "interval and gameEndFlag = " + String.valueOf(isGameEnd));
                counter = new MyCount(interval, 1000);
            } else {
//                Log.e("PhaseTwo Timer", "savedRemainingInterval");
                counter = new MyCount(savedRemainingInterval, 1000);
            }
            Log.e("onFinish phase2", "in prefs, userKey = " + userKey);
            if (userKey != null) {
                fetchPlayerDetailsCombat();
                fetchRandomPlayer2DetailsCombat();
                fetchGameDetailsCombat();
            }
        } else {
            if (savedRemainingInterval > 0) {
//                Log.e("PhaseOne Timer", "savedRemainingInterval");
                counter = new MyCount(savedRemainingInterval, 1000);
            } else {
//                Log.e("PhaseOne Timer", "interval");
                counter = new MyCount(interval, 1000);
            }
//            TODO initializing game data at the start of the game
            saveInitialGameDataP1Combat();
        }
    }

    static boolean isPointWithin(int x, int y, int x1, int x2, int y1, int y2) {
        return (x <= x2 && x >= x1 && y <= y2 && y >= y1);
    }

    public List<String> methodCallToAsyncTaskRunner() {
        try {
            if (!isPhaseTwo) {
                new AsyncTaskRunner().execute().get();
            }
//            Log.e("nineWords size", String.valueOf(nineWords.size()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return nineWords;
    }

    public void restartGame() {
        mGameFragment.restartGame();
    }

    public void startThinking() {
        View thinkView = findViewById(R.id.thinking_scraggle);
        thinkView.setVisibility(View.VISIBLE);
    }

    public void stopThinking() {
        if (!isPhaseTwo) {
            mScoreTextField.setText("Score = " + String.valueOf(score));
        } else {
            mScoreTextField.setText("Score = " + String.valueOf(score + score2));
        }
//        View thinkView = findViewById(R.id.thinking_scraggle);
//        thinkView.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("onResume", "inside resume");
        if (!isResumeFlag) {
            isResumeFlag = true;
            if (savedRemainingInterval > 0 && isResumeFlag) {
//                counter = new MyCount(savedRemainingInterval, 1000);
                counter.start();
            } else {
//                counter = new MyCount(interval, 1000);
                counter.start();
            }
        }
        mMediaPlayer = MediaPlayer.create(this, R.raw.erokia_timelift_rhodes_piano_freesound_org);
        mMediaPlayer.start();
        mMediaPlayer.setLooping(true);
//        Log.e("onResume", "score = " + score);
//        Log.e("onResume", "score2 = " + score2);
        if (!isPhaseTwo) {
            mScoreTextField.setText("Score = " + String.valueOf(score));
        } else {
            mScoreTextField.setText("Score = " + String.valueOf(score + score2));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("onPause", "inside pause");
        mHandler.removeCallbacks(null);
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
        counter.cancel();
        if (isResumeFlag) {
            isResumeFlag = false;
        }
//        if(!isGameEnd) {
        String gameData = mGameFragment.getState();
        getPreferences(MODE_PRIVATE).edit()
                .putString(Constants.PREF_RESTORE, gameData)
                .commit();
        Log.d("Scraggle", "state = " + gameData);
//        }
        // Get rid of the about dialog if it's still up
        if (mDialog != null)
            mDialog.dismiss();
    }

    public void toogleMute() {
//        Log.e("Mute TEST toogleMute", "inside func");
        if (mMediaPlayer.isPlaying()) {
//            Log.e("Mute TEST toogleMute", "inside for pause");
            mMediaPlayer.pause();
        } else {
//            Log.e("Mute TEST toogleMute", "inside for start");
            mMediaPlayer.start();
        }
    }

    //    http://stackoverflow.com/questions/9630398/how-can-i-pause-the-timer-in-android/9663508#9663508
    public class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            if (!isPhaseTwo) {
                Log.e("onFinish p1", String.valueOf(mGameFragment.getWordCount1()));
                if (mGameFragment.getWordCount1() > 1) {
                    Log.e("onFinish", "Done");
                    mTextField.setText("            DONE");
                    mGameFragment.disableLetterGrid();
                    AlertDialog.Builder builder = new AlertDialog.Builder(appContext);
//                AlertDialog.Builder builder = new AlertDialog.Builder(ScraggleGameActivity.this);
                    builder.setTitle(R.string.phase_change_title);
                    builder.setMessage(R.string.phase_change_text);
                    builder.setCancelable(false);
                    builder.setPositiveButton(R.string.ok_label,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // take to Phase 2
                                    isPhaseTwo = true;
                                    savedRemainingInterval = 0;
//                                mGameFragment.setIsPhaseTwo(true);
//                                startActivity(new Intent(ScraggleGameActivity.this, ScraggleGameActivity.class));
                                    Intent intent = new Intent(ScraggleGameActivity2.this, ScraggleGameActivity2.class);
                                    intent.putExtra("gameData", mGameFragment.getState());
                                    intent.putExtra("isTwoFlag", isPhaseTwo);
//                                intent.putExtra("isTwoFlagFrag", isPhaseTwo);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                    mDialog = builder.show();
                } else {
                    Log.e("onFinish", "Game Over Phase 1");
                    mTextField.setText("            GAME OVER");
                    AlertDialog.Builder builder = new AlertDialog.Builder(appContext);
//                AlertDialog.Builder builder = new AlertDialog.Builder(ScraggleGameActivity.this);
                    builder.setTitle(R.string.game_end_title);
                    builder.setMessage(String.format(getResources().getString(R.string.game_end_text), score));
                    builder.setCancelable(false);
                    builder.setPositiveButton(R.string.ok_label,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            });
                    mDialog = builder.show();
                }
            } else {
                Log.e("onFinish", "Game Over");
                mTextField.setText("            GAME OVER");
                mGameFragment.disableLetterGrid();
                mControlFragment.getView().setVisibility(View.INVISIBLE);
                gameData = null;
                Constants.GAME_KEY = gameKey;
                updatePlayer1DetailsOnFinishP1Combat();
                updatePlayer2DetailsOnFinishP1Combat();
                updateGameDetailsOnFinishP1Combat();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        TODO - remove TEST and add meaningful msg
                        mCommMain.sendCombatGameRequest("TEST", user2player.getUserId());
                    }
                }, 5000);
//                Log.e("null game state 1", (gameData!=null?String.valueOf(gameData):"null"));
                AlertDialog.Builder builder = new AlertDialog.Builder(appContext);
//                AlertDialog.Builder builder = new AlertDialog.Builder(ScraggleGameActivity.this);
                builder.setTitle(R.string.game_end_title);
                builder.setMessage(String.format(getResources().getString(R.string.game_end_text), score + score2));
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.ok_label,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        });
                mDialog = builder.show();
                isGameEnd = true;
//                Log.e("null game state 2", (gameData!=null?String.valueOf(gameData):"null"));
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int secs = (int) (millisUntilFinished / 1000);
            int seconds = secs % 60;
            int minutes = secs / 60;
            String stringTime = String.format("%02d:%02d", minutes, seconds);
            if (stringTime.equals("00:05") || stringTime.equals("00:04") || stringTime.equals("00:03") ||
                    stringTime.equals("00:02") || stringTime.equals("00:01")) {
//                Log.e("onTick", "in animation");
//                mTextField.animator();
                Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.gametimer);
                mTextField.startAnimation(animation1);
                mTextField.setText("            Time: " + stringTime);
            } else {
//                Log.e("onTick", "no");
                mTextField.setText("            Time: " + stringTime);
            }
//            mTextField.setText("Seconds Remaining: " + millisUntilFinished / 1000);
            savedRemainingInterval = millisUntilFinished;
        }
    }

    protected void onPauseGame() {
        Log.e("onPauseGame", "inside pause");
        counter.cancel();
        mMediaPlayer.pause();
        mGameFragment.disableLetterGrid();
        isResumeFlag = false;
    }

    protected void onResumeGame() {
        Log.e("onResumeGame", "inside pause");
        counter = new MyCount(savedRemainingInterval, 1000);
        counter.start();
        mMediaPlayer.start();
        mGameFragment.enableLetterGrid();
        isResumeFlag = true;
    }

    protected void onQuitGame() {
        Log.e("onQuit", "inside pause");
        counter.cancel();
        mMediaPlayer.pause();
        isResumeFlag = false;
    }


    private class AsyncTaskRunner extends AsyncTask<Void, Void, List<String>> {
        @Override
        protected List<String> doInBackground(Void... params) {
            String word = null;
//            to handle or restrict word repetition
            HashSet<String> wordSet = new HashSet<>();
            try {
                while (wordSet.size() < 9) {
                    Random random = new Random();
                    char c = (char) (random.nextInt(26) + 'a');
                    InputStream in_s = null;

                    int resID = getResources().getIdentifier(String.valueOf(c), "raw", getPackageName());
                    in_s = getResources().openRawResource(resID);

                    byte[] b = new byte[in_s.available()];
                    in_s.read(b);
                    String result = new String(b);
                    String[] strings = result.split("\\n");

                    List<String> stringList = new ArrayList<>();
                    for (String s : strings) {
                        if (s.trim().length() == 9)
                            stringList.add(s.trim());
                    }
                    Random yourRandom = new Random();
                    int index = yourRandom.nextInt(stringList.size());
                    word = stringList.get(index);
//                    Log.e("nineLetter", word + " " + String.valueOf(word.length()));
                    wordSet.add(word);
                }
            } catch (Exception e) {
                Log.e("fetchNineWords", "Exception occurred");
            }
            nineWords = new ArrayList<String>(wordSet);
            return nineWords;
        }
    }

    private class AsyncTaskRunner0 extends AsyncTask<String, Void, List<String>> {
        @Override
        protected List<String> doInBackground(String... params) {
            String word = params[0];
            try {
                try {
//                    Log.e("Inside AS0", "Inside AS0");
                    InputStream in_s = null;
                    String fileName = String.valueOf(word.charAt(0));
                    int resID = getResources().getIdentifier(fileName, "raw", getPackageName());
                    in_s = getResources().openRawResource(resID);
                    byte[] b = new byte[in_s.available()];
                    in_s.read(b);
                    result = new String(b);
                    String[] strings = result.split("\\n");
                    for (String s : strings) {
                        vocabList.add(s);
                    }
//                    Log.e("INSERT", "inserted");
                } catch (IOException e) {
                    Log.e("ERROR", "not inserted");
                }
            } catch (Exception e) {
                Log.e("AsyncTaskRunner", "Exception occurred");
                Log.e("AsyncTaskRunner Error", e.getMessage().toString());
            }
            return vocabList;
        }
    }

    private class AsyncTaskRunner2 extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            insertedText = params[0];
            String resp = "";
            boolean isThereInDict = false;
            try {
//                Log.e("AsyncTaskRunner2", "insertedText = " + insertedText);
                for (String s : vocabList) {
                    if (insertedText.equals(s.trim())) {
                        resp = insertedText;
                    }
                }
                if (!resp.equals("")) {
                    isThereInDict = true;
                }
            } catch (Exception e) {
                Log.e("AsyncTaskRunner2", "Error encountered");
            }
//            Log.e("AsyncTaskRunner2", "result = " + resp);
            return isThereInDict;
        }
    }

    //    method to be called in the asyncTask for the Word Game ever
    public Boolean searchWord(String str) {
        String result1 = "";
        String word = str;
//        Log.e("searchWord WORD LEN", "afterTextChanged: " + word.length() + " word = " + word);
        if (word.length() >= 3) {
            try {
                new AsyncTaskRunner0().execute(word).get();
                resFlag = new AsyncTaskRunner2().execute(word).get();
//                Log.e("searchWord", "resFlag = " + resFlag);
//                Log.e("searchWord", finalResult);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            result1 = resultStr;
//            Log.e("searchWord RESULT STR", "afterTextChanged: RESULT STRING = " + result1);
        } else {
            List<String> list = Arrays.asList(resultStr.split("\n"));
            Set<String> uniqueWords = new HashSet<String>(list);
            finalResult = "";
            for (String s1 : uniqueWords) {
                System.out.println(word + ": " + Collections.frequency(list, s1));
                finalResult = finalResult + s1 + "\n";
            }
//            Log.e("searchWord", "finalResult = " + finalResult);
        }
        return resFlag;
    }

    public long getSavedRemainingInterval() {
        return this.savedRemainingInterval;
    }

    public void setSavedRemainingInterval(long savedInterval) {
        savedRemainingInterval = savedInterval;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int savedScore) {
        score = savedScore;
    }

    public int getScore2() {
        return this.score2;
    }

    public void setScore2(int savedScore) {
        score2 = savedScore;
    }

    public boolean isRestore() {
        return this.restore;
    }

    public void setRestore(boolean savedRestore) {
        restore = savedRestore;
    }

    public boolean isPhaseTwo() {
        return this.isPhaseTwo;
    }

    public void setIsPhaseTwo(boolean isPhaseTwoFlag) {
        isPhaseTwo = isPhaseTwoFlag;
    }

    public boolean isGameEnd() {
        return this.isGameEnd;
    }

    public void setIsGameEnd(boolean isGameEndFlag) {
        isGameEnd = isGameEndFlag;
    }

    // TODO functions to save and update and fetch user and game data

    private void saveInitialGameDataP1Combat() {
        /*mRemoteClient.fetchUserData(Constants.USER_DATA, userKey);
        user = mRemoteClient.getUserData(userKey);*/
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gameLetter = mGameFragment.getGameLetterState();
                Log.e("remoteClient", "gamedata 1 = " + gameLetter.toString());
                gameDataFb = new GameData(0, 0, 0, 0, 0, userKey, "", gameLetter, true, false, false, false);
                mRemoteClient.saveGameData(gameDataFb);
            }
        }, 5000);
    }

    private void fetchPlayerDetailsCombat() {
        Log.e("remote", "userKey = " + userKey);
        mRemoteClient.fetchUserData(Constants.USER_DATA, userKey);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                user = mRemoteClient.getUserData(userKey);
                Log.e("remote", "username = " + user.getUserName());
            }
        }, 10000);
    }

    private void fetchRandomPlayer2DetailsCombat() {
        mRemoteClient.fetchRandomUsers(Constants.USER_DATA, userKey);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                current2UserMap = mRemoteClient.getFireBaseRandomUserData();
                if(current2UserMap.size() == 1) {
                    for (String key : current2UserMap.keySet()) {
                        user2key = key;
                        user2player = current2UserMap.get(key);
                    }
                }
//                user2key = mRemoteClient.getRandomUserKey();
//                user2player = mRemoteClient.getRandomUserData();
                Log.e("remote", "username 2 = " + user2player.getUserName());
            }
        }, 10000);
    }

    private void fetchGameDetailsCombat() {
        Log.e("remote", "gameKey = " + gameKey);
        mRemoteClient.fetchGameData(Constants.GAME_DATA, gameKey);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                retrievedGameData = mRemoteClient.getGameData(gameKey);
                Log.e("remote", "retrievedGameData = " + retrievedGameData.getPlayer1ID());
            }
        }, 10000);
    }

    private void updatePlayer1DetailsOnFinishP1Combat() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                String gameKey = prefs.getString(Constants.GAME_UNIQUE_KEY, "");
                UserData updatedUserData = new UserData(user.getUserId(), user.getUserName(),
                        ((user.getUserIndividualBestScore()>0 && user.getUserIndividualBestScore()>(score+score2))
                                ?user.getUserIndividualBestScore():score+score2),
                        user.getUserCombineBestScore(), user.getTeamPlayerName(), true,
                        user.getChallengedBy(), user.isCombineGameRequest(), score+score2,
                        user.getUserPendingCombineGameScore(), gameKey, user.getPendingCombineGameKey());
                mRemoteClient.updateUserData(updatedUserData);
                Log.e("remote", "username = " + user.getUserName());
            }
        }, 5000);
    }

    private void updateGameDetailsOnFinishP1Combat() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                GameData updatedGameData = new GameData(retrievedGameData.getScoreCombinePlay(),
                        score, score2, retrievedGameData.getP2Score1(),
                        retrievedGameData.getP2Score1(),
                        retrievedGameData.getPlayer1ID(), user2key,
                        retrievedGameData.getGameLetterState(),
                        retrievedGameData.isFirstCombatPlay(), true,
                        retrievedGameData.isCombinePlay(), retrievedGameData.isGameOver());
                mRemoteClient.updateGameData(updatedGameData);
                Log.e("remote", "retrievedGameData = " + retrievedGameData.getPlayer1ID());
            }
        }, 15000);
    }

    private void updatePlayer2DetailsOnFinishP1Combat() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                UserData updatedUserData = new UserData(user2player.getUserId(),
                        user2player.getUserName(),
                        user2player.getUserIndividualBestScore(),
                        user2player.getUserCombineBestScore(),
                        user2player.getTeamPlayerName(),
                        true,
                        user2player.getChallengedBy(),
                        user2player.isCombineGameRequest(),
                        user2player.getUserPendingIndividualGameScore(),
                        user2player.getUserPendingCombineGameScore(),
                        gameKey,
                        user2player.getPendingCombineGameKey());
                mRemoteClient.updateUser2Data(user2key, updatedUserData);
                Log.e("remote player2", "username2 = " + user2player.getUserName());
            }
        }, 15000);
    }
}
