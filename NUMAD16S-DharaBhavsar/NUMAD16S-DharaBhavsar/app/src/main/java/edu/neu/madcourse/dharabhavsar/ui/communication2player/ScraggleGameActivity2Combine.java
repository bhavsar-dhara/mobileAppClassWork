package edu.neu.madcourse.dharabhavsar.ui.communication2player;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import edu.neu.madcourse.dharabhavsar.model.communication.GameData;
import edu.neu.madcourse.dharabhavsar.model.communication.UserData;
import edu.neu.madcourse.dharabhavsar.ui.dictionary.TrieLookup;
import edu.neu.madcourse.dharabhavsar.ui.main.R;
import edu.neu.madcourse.dharabhavsar.utils.Constants;
import edu.neu.madcourse.dharabhavsar.utils.firebaseconn.RemoteClient;
import edu.neu.madcourse.dharabhavsar.utils.gcmcomm.CommunicationMain;

public class ScraggleGameActivity2Combine extends Activity {
    public static final String KEY_RESTORE = "key_restore";
    public static final String PREF_RESTORE = "pref_restore";
    private MediaPlayer mMediaPlayer;
    private Handler mHandler = new Handler();
    private ScraggleGameFragment2Combine mGameFragment;
    private ControlFragmentScraggle2Combine mControlFragment;
    List<String> nineWords = new ArrayList<>();
    TextView mTextField;
    private final int interval = 90000; //90 seconds ; 1 minute 30 seconds
    long savedRemainingInterval = 0;
    MyCount counter;
    String resultStr = "";
    String finalResult = "";
    String result = "";
    ArrayList<String> vocabList = new ArrayList<String>();
    String insertedText = "";
    private AlertDialog mDialog;
    TrieLookup trie;
    Boolean resFlag = false;
    TextView mScoreTextField;
    int score = 0;
    int score2 = 0;
    boolean restore;
    boolean isResumeFlag = false;
    private boolean isPhaseTwo = false;
    private String gameData = "";
    private Context appContext = this;
    private boolean isGameEnd = false;

    static final String STATE_SCORE = "playerScore";
    static final String STATE_LEVEL = "playerLevel";

    private UserData user;
    private RemoteClient mRemoteClient;
    private SharedPreferences prefs;
    private SharedPreferences prefs2;
    private Timer timer;
    private TimerTask timerTask;
    private UserData user2player;
    private Timer timer2;
    private TimerTask timerTask2;
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
    private boolean isTeamPlayerSelected = false;
    private static HashMap<String, UserData> fireBaseAllUserList = new HashMap<String, UserData>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = this.getIntent().getExtras();
        if (b != null) {
            isPhaseTwo = b.getBoolean("isTwoFlag");
            gameData = b.getString("gameData");
        }

//         The below code didn't work for this activity
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        prefs = appContext.getSharedPreferences(RemoteClient.class.getSimpleName(), Context.MODE_PRIVATE);
        prefs2 = appContext.getSharedPreferences(CommunicationMain.class.getSimpleName(), Context.MODE_PRIVATE);
        mRemoteClient = new RemoteClient(appContext);

        userKey = prefs.getString(Constants.USER_UNIQUE_KEY, "");
        Constants.USER_KEY = userKey;
        userId = prefs2.getString(Constants.PROPERTY_REG_ID, "");
        fetchPlayerDetailsCombat();

        setContentView(R.layout.activity_game_scraggle2combine);
        mGameFragment = (ScraggleGameFragment2Combine) getFragmentManager()
                .findFragmentById(R.id.fragment_game_scraggle);
        mControlFragment = (ControlFragmentScraggle2Combine) getFragmentManager()
                .findFragmentById(R.id.fragment_control_scraggle);
        mTextField = (TextView) findViewById(R.id.timerView);
        mScoreTextField = (TextView) findViewById(R.id.scoreView);

        restore = getIntent().getBooleanExtra(KEY_RESTORE, false);
        if (restore) {
            String gameData = getPreferences(MODE_PRIVATE)
                    .getString(PREF_RESTORE, null);
//            Log.e("ScraggleActivity", "gameData = " + gameData);
            if (gameData != null) {
                mGameFragment.putState(gameData);
            }
        }
        Log.e("Scraggle", "restore = " + restore);
        saveInitialGameDataP1Combine();
        gameKey = prefs.getString(Constants.COMBINE_GAME_UNIQUE_KEY, "");
        Constants.COMBINE_GAME_KEY = gameKey;
        mGameFragment.disableLetterGrid();

        if(!isTeamPlayerSelected) {
//            TODO
            startUserListDialog();
        } else {
            mScoreTextField.setText("Score = " + String.valueOf(score));
            if (savedRemainingInterval > 0) {
    //                Log.e("PhaseOne Timer", "savedRemainingInterval");
                counter = new MyCount(savedRemainingInterval, 1000);
            } else {
    //                Log.e("PhaseOne Timer", "interval");
                counter = new MyCount(interval, 1000);
            }
        }
    }

    public List<String> methodCallToAsyncTaskRunner() {
        try {
            new AsyncTaskRunner().execute().get();
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
        mScoreTextField.setText("Score = " + String.valueOf(score));
        View thinkView = findViewById(R.id.thinking_scraggle);
        thinkView.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("onResume", "inside resume");
        if (!isResumeFlag) {
            isResumeFlag = true;
            if(counter != null) {
                if (savedRemainingInterval > 0 && isResumeFlag) {
//                counter = new MyCount(savedRemainingInterval, 1000);
                    counter.start();
                } else {
//                counter = new MyCount(interval, 1000);
                    counter.start();
                }
            }
        }
        mMediaPlayer = MediaPlayer.create(this, R.raw.erokia_timelift_rhodes_piano_freesound_org);
        mMediaPlayer.start();
        mMediaPlayer.setLooping(true);
//        Log.e("onResume", "score = " + score);
//        Log.e("onResume", "score2 = " + score2);
        mScoreTextField.setText("Score = " + String.valueOf(score));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("onPause", "inside pause");
        mHandler.removeCallbacks(null);
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
        if(counter != null) {
            counter.cancel();
        }
        if (isResumeFlag) {
            isResumeFlag = false;
        }
//        if(!isGameEnd) {
        String gameData = mGameFragment.getState();
        getPreferences(MODE_PRIVATE).edit()
                .putString(PREF_RESTORE, gameData)
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
            Log.e("onFinish p1", String.valueOf(mGameFragment.getWordCount1()));
            if (mGameFragment.getWordCount1() > 1) {
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
        if(counter != null) {
            counter.cancel();
        }
        mMediaPlayer.pause();
        mGameFragment.disableLetterGrid();
        isResumeFlag = false;
    }

    protected void onResumeGame() {
        Log.e("onResumeGame", "inside pause");
        if(counter != null) {
            counter = new MyCount(savedRemainingInterval, 1000);
            counter.start();
        }
        mMediaPlayer.start();
        mGameFragment.enableLetterGrid();
        isResumeFlag = true;
    }

    protected void onQuitGame() {
        Log.e("onQuit", "inside pause");
        if(counter != null) {
            counter.cancel();
        }
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

    private void animator() {
        Animator anim = AnimatorInflater.loadAnimator(this,
                R.animator.gametimer);
        if (mTextField != null) {
            anim.setTarget(mTextField);
            anim.start();
        }
    }

    public boolean isGameEnd() {
        return this.isGameEnd;
    }

    public void setIsGameEnd(boolean isGameEndFlag) {
        isGameEnd = isGameEndFlag;
    }

    public void startTimer1(String key) {
        timer = new Timer();
        initializeTimerTask1(key);
        timer.schedule(timerTask, 5000, 1000);
    }

    public void startTimer2(String key) {
        timer2 = new Timer();
        initializeTimerTask2();
        timer2.schedule(timerTask2, 5000, 1000);
    }

    public void stopTimerTask1() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void stopTimerTask2() {
        if (timer2 != null) {
            timer2.cancel();
            timer2 = null;
        }
    }

    public void initializeTimerTask1(final String key) {
        timerTask = new TimerTask() {
            public void run() {
                Log.e("GameActivity2", "isDataFetched >>>> " + mRemoteClient.isDataFetched());
                if (mRemoteClient.isDataFetched()) {
                    mHandler.post(new Runnable() {
                        public void run() {
                            user = mRemoteClient.getUserData(key);
                            Log.e("GameActivity2", "Value >>>> " + user);
                            Toast.makeText(ScraggleGameActivity2Combine.this, "Value 1  "
                                            + user.getUserName(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    stopTimerTask1();
                }
            }
        };
    }

    public void initializeTimerTask2() {
        timerTask2 = new TimerTask() {
            public void run() {
                Log.e("GameActivity2", "isDataFetched >>>> " + mRemoteClient.isDataFetched());
                if (mRemoteClient.isDataFetched()) {
                    mHandler.post(new Runnable() {
                        public void run() {
                            current2UserMap = mRemoteClient.getFireBaseRandomUserData();
                            Log.e("GameActivity2", "Value2 >>>> " + user);
                            Toast.makeText(ScraggleGameActivity2Combine.this, "Value 2  "
                                            + user2player.getUserName(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    stopTimerTask2();
                }
            }
        };
    }

    private void startUserListDialog() {
        Log.e("COMBINE GAME DIALOG", "showing dialog!");
        fireBaseAllUserList = mRemoteClient.fetchAllUsers(Constants.USER_DATA, userKey);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String[] keys = new String[fireBaseAllUserList.size()];
                UserData[] values = new UserData[fireBaseAllUserList.size()];
                final String[] userNameList = new String[fireBaseAllUserList.size()];
                final String[] userIdList = new String[fireBaseAllUserList.size()];
                int index = 0;
                for (Map.Entry<String, UserData> mapEntry : fireBaseAllUserList.entrySet()) {
                    keys[index] = mapEntry.getKey();
                    values[index] = mapEntry.getValue();
                    userNameList[index] = mapEntry.getValue().getUserName();
                    userIdList[index] = mapEntry.getValue().getUserId();
                    index++;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        ScraggleGameActivity2Combine.this,
                        AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                builder.setTitle(R.string.select_team_mate)
                        .setItems(userNameList, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, final int which) {
                                // TODO - calling the game activity and getting it's gameKey
                                // and then passing this push notification
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mCommMain.sendCombineGameRequest("Play along with " +
                                                user.getUserName(), userIdList[which]);

                                            /*updateP1DetailsOnP2SelectionCombine();
                                            updateGameDetailsOnP2Combine();*/
                                    }
                                }, 3000);
                                isTeamPlayerSelected = true;
                                mGameFragment.enableLetterGrid();
                            }
                        });
                builder.create().show();
            }
        }, 5000);
    }

    private void saveInitialGameDataP1Combine() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gameLetter = mGameFragment.getGameLetterState();
                Log.e("remoteClient", "gamedata 1 = " + gameLetter.toString());
                gameDataFb = new GameData(0, 0, 0, 0, 0, userKey, "", gameLetter, false, false, true, false);
                mRemoteClient.saveGameData(gameDataFb);
            }
        }, 3000);
    }

    private void updateP1DetailsOnP2SelectionCombine() {
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

    private void updateGameDetailsOnP2Combine() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                GameData updatedGameData = new GameData(retrievedGameData.getScoreCombinePlay(),
                        retrievedGameData.getP1Score1(),
                        retrievedGameData.getP1Score2(),
                        retrievedGameData.getP2Score1(),
                        retrievedGameData.getP2Score1(),
                        retrievedGameData.getPlayer1ID(),
                        user2key,
                        retrievedGameData.getGameLetterState(),
                        retrievedGameData.isFirstCombatPlay(),
                        retrievedGameData.isSecondCombatPlay(),
                        retrievedGameData.isCombinePlay(),
                        retrievedGameData.isGameOver());
                mRemoteClient.updateGameData(updatedGameData);
                Log.e("remote", "retrievedGameData = " + retrievedGameData.getPlayer1ID());
            }
        }, 15000);
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
}
