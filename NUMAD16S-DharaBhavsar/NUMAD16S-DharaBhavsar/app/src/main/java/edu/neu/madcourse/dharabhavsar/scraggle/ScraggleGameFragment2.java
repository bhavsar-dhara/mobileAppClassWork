package edu.neu.madcourse.dharabhavsar.scraggle;

import android.app.Fragment;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

import edu.neu.madcourse.dharabhavsar.main.R;

/**
 * Created by Dhara on 2/26/2016.
 */
public class ScraggleGameFragment2 extends Fragment {
    static private int mLargeIdList[] = {R.id.wglarge1, R.id.wglarge2, R.id.wglarge3,
            R.id.wglarge4, R.id.wglarge5, R.id.wglarge6, R.id.wglarge7, R.id.wglarge8,
            R.id.wglarge9,};
    static private int mSmallIdList[] = {R.id.wgsmall1, R.id.wgsmall2, R.id.wgsmall3,
            R.id.wgsmall4, R.id.wgsmall5, R.id.wgsmall6, R.id.wgsmall7, R.id.wgsmall8,
            R.id.wgsmall9,};
    private int mLastLarge;
    private int mLastSmall;
    private Handler mHandler = new Handler();
    private ScraggleTile mEntireBoard = new ScraggleTile(this);
    private ScraggleTile mLargeTiles[] = new ScraggleTile[9];
    private ScraggleTile mSmallTiles[][] = new ScraggleTile[9][9];
    private int mSoundX, mSoundO, mSoundMiss, mSoundRewind;
    private SoundPool mSoundPool;
    private float mVolume = 1f;
    private Vibrator v;
    private long savedInterval;
    private int gameScore = 0;
    private Set<ScraggleTile> mNextMove = new HashSet<ScraggleTile>();
    private View mView;
    static private String[] wordMadeList = new String[9];
    private Set<ScraggleTile> mAvailable = new HashSet<ScraggleTile>();
    private String word = "";
    private String gameState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retain this fragment across configuration changes.
        setRetainInstance(true);
        Log.e("onCreate 2", "inside");
        initGame();

        mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        mSoundX = mSoundPool.load(getActivity(), R.raw.shnur_drum_freesound_org, 1);
        mSoundO = mSoundPool.load(getActivity(), R.raw.shnur_drum_freesound_org, 1);
        mSoundMiss = mSoundPool.load(getActivity(), R.raw.bertrof_game_sound_wrong_freesound_org, 1);
        mSoundRewind = mSoundPool.load(getActivity(), R.raw.shnur_drum_freesound_org, 1);
        v = (Vibrator) this.getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        Bundle b = this.getActivity().getIntent().getExtras();

        if (b != null) {
            gameState = b.getString("gameData");
//            Log.e("getExtras 2", gameState);
//            putState(gameState);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("onCreateView 2", "inside");
        View rootView =
                inflater.inflate(R.layout.large_board_scraggle, container, false);
        mView = rootView;
        initViews(rootView);
        putState(gameState);
        updateAllTiles();
        return rootView;
    }

    public void initGame() {
        Log.e("initGame 2", "inside");
        Log.d("Scraggle 2", "init game");
        mEntireBoard = new ScraggleTile(this);
        // Create all the tiles
        for (int large = 0; large < 9; large++) {
            mLargeTiles[large] = new ScraggleTile(this);
            for (int small = 0; small < 9; small++) {
                mSmallTiles[large][small] = new ScraggleTile(this);
            }
            mLargeTiles[large].setSubTiles(mSmallTiles[large]);
        }
        mEntireBoard.setSubTiles(mLargeTiles);

        // If the player moves first, set which spots are available
        mLastSmall = -1;
        mLastLarge = -1;
        setAvailableFromLastMove(mLastSmall);
//        setNextPossibleMoveFromLastMove(mLastSmall, mLastLarge);
    }

    private void initViews(View rootView) {
        Log.e("initViews2", "inside" + rootView);
        mEntireBoard.setView(rootView);
        for (int large = 0; large < 9; large++) {
            View outer = rootView.findViewById(mLargeIdList[large]);
            mLargeTiles[large].setView(outer);
            for (int small = 0; small < 9; small++) {
                final Button inner = (Button) outer.findViewById
                        (mSmallIdList[small]);
                final int fLarge = large;
                final int fSmall = small;
                final ScraggleTile smallTile = mSmallTiles[large][small];
                smallTile.setView(inner);
                // ...
                inner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e("WordTEST2", "inOnCLick ::: inner text : " + smallTile.getInnerText()
                        + " isSelected " + smallTile.getIsSelected());

//                        smallTile.animate();
                        // ...
                        Log.e("WordTEST2", String.valueOf(isAvailable(smallTile)));
//                        Log.e("WordTEST NextMove", String.valueOf(isNextMove(smallTile)));
                        if (isAvailable(smallTile)) {
//                        if (isNextMove(smallTile)) {
                            ((ScraggleGameActivity2) getActivity()).startThinking();
//                            setNextPossibleMoveFromLastMove(mLastSmall, mLastLarge);
                            Log.e("WordTEST2", String.valueOf(smallTile.getIsSelected()));
                            if (!smallTile.getIsSelected()) {
                                Log.e("WordTEST2", "in isSel = false :: " + String.valueOf(smallTile.getInnerText()));
                                smallTile.setIsSelected(true);
                                Log.e("mLastLarge2", String.valueOf(mLastLarge));
                                Log.e("fLarge2", String.valueOf(fLarge));
                                makeWord(String.valueOf(smallTile.getInnerText()), fLarge);
                                inner.setBackgroundDrawable(getResources().getDrawable(R.drawable.tile_selected_scraggle));
                                gameScore += getScore(smallTile.getInnerText().charAt(0));
                            } else {
                                Log.e("WordTEST2", "in isSel = true");
                                smallTile.setIsSelected(false);
                                /*String str = removeLastChar(wordMadeList[fLarge],
                                        String.valueOf(smallTile.getIsSelected()).charAt(0));*/
                                word = removeLastChar(wordMadeList[fLarge]);
                                Log.e("wordRemoveTestTest2", word);
                                wordMadeList[fLarge] = word;
                                Log.e("wordRemoveTestTest2", wordMadeList[fLarge]);
                                Log.e("wordRemoveTestTes2t", word);
                                inner.setBackgroundDrawable(getResources().getDrawable(R.drawable.tile_not_selected_scraggle));
                                gameScore -= getScore(smallTile.getInnerText().charAt(0));
                            }
                            mSoundPool.play(mSoundX, mVolume, mVolume, 1, 0, 1f);
                            // Vibrate for 25 milliseconds
                            v.vibrate(25);
//                            makeMove(fLarge, fSmall);
                            think();
                        } else {
                            mSoundPool.play(mSoundMiss, mVolume, mVolume, 1, 0, 1f);
                        }
                        mLastLarge = fLarge;
                        mLastSmall = fSmall;
                    }
                });
                // ...
            }
        }
    }

    private void think() {
        Log.e("think 2", "inside");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getActivity() == null) return;
//                optimized by removing the unnecessary looping
//                for (int i = 0; i < 9; i++) {
                if (wordMadeList[mLastLarge] != null) {
                    if (wordMadeList[mLastLarge].length() > 2) {
                        Log.e("DICT TEST 2", wordMadeList[mLastLarge]);

                        Boolean isThereFlag = ((ScraggleGameActivity2) getActivity()).searchWord(wordMadeList[mLastLarge]);

                        if (isThereFlag) {
//                            Custom Toast on Successfully finding a Word
                            View customToastRoot = getActivity().getLayoutInflater().inflate(R.layout.mycustom_toast, null);

                            Toast customtoast = new Toast(getActivity().getApplicationContext());

                            customtoast.setView(customToastRoot);
                            customtoast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                            customtoast.setDuration(Toast.LENGTH_SHORT);
//                                Throws error
//                                customtoast.setText("Word Found in Small 3x3 Grid " + i);
                            customtoast.show();

                                /*Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                                        "message", Toast.LENGTH_SHORT);
                                toast.setText("Word Found in Small 3x3 Grid " + String.valueOf(i));
                                toast.setView(customToastRoot);
                                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                                toast.show();*/
                        }
                    }
                }
//                }
                ((ScraggleGameActivity2) getActivity()).stopThinking();
            }
        }, 1000);
    }

    /**
     * Create a string containing the state of the game.
     */
    public String getState() {
        Log.e("SAVING GAME STATE 2", "in");
        StringBuilder builder = new StringBuilder();
        builder.append(((ScraggleGameActivity2) this.getActivity()).getSavedRemainingInterval());
        builder.append(',');
        builder.append(((ScraggleGameActivity2) this.getActivity()).getScore());
        builder.append(',');
        builder.append(mLastLarge);
        builder.append(',');
        builder.append(mLastSmall);
        builder.append(',');
        for (int large = 0; large < 9; large++) {
            for (int small = 0; small < 9; small++) {
                builder.append(mSmallTiles[large][small].getInnerText());
                builder.append(',');
                builder.append(mSmallTiles[large][small].getIsSelected());
                builder.append(',');
            }
            builder.append(wordMadeList[large]);
            builder.append(',');
        }
        Log.e("SAVING GAME STATE 2", "out");
        return builder.toString();
    }

    /**
     * Restore the state of the game from the given string.
     */
    public void putState(String gameData) {
        Log.e("RESTORING GAME STATE 2", "in");
        String[] fields = gameData.split(",");
        int index = 0;
        savedInterval = Long.parseLong(fields[index++]);
        ((ScraggleGameActivity2) this.getActivity()).setSavedRemainingInterval(savedInterval);
        gameScore = Integer.parseInt(fields[index++]);
        ((ScraggleGameActivity2) this.getActivity()).setScore(gameScore);
        mLastLarge = Integer.parseInt(fields[index++]);
        mLastSmall = Integer.parseInt(fields[index++]);

        for (int large = 0; large < 9; large++) {
            for (int small = 0; small < 9; small++) {
                String innerText = (fields[index++]);
                mSmallTiles[large][small].setInnerText(innerText);
                Boolean isSelected = Boolean.valueOf(fields[index++]);
                mSmallTiles[large][small].setIsSelected(isSelected);
            }
            String wordMade = (fields[index++]);
            wordMadeList[large] = wordMade;
        }
        setAvailableFromLastMove(mLastSmall);
        updateAllTiles();

        putPrevLetters(mView);
        Log.e("RESTORING GAME STATE 2", "out");
    }

    private void putPrevLetters(View rootView) {
        Log.e("putPrevLetters 2", "inside");
        mEntireBoard.setView(rootView);
        for (int large = 0; large < 9; large++) {
            View outer = rootView.findViewById(mLargeIdList[large]);
            mLargeTiles[large].setView(outer);
//          to restore previous state on GUI
            for (int small = 0; small < 9; small++) {
//                to add letters of the words
                Button innerText = (Button) outer.findViewById
                        (mSmallIdList[small]);
                if (mSmallTiles[large][small].getIsSelected()) {
                    innerText.setText(String.valueOf(mSmallTiles[large][small].getInnerText()));
                    innerText.setBackgroundDrawable(getResources().getDrawable(R.drawable.tile_selected_scraggle));
                }
                else {
                    innerText.setBackgroundDrawable(getResources().getDrawable(R.drawable.tile_deselected_scraggle));
                }
            }
        }
    }

    private void clearAvailable() {
        mAvailable.clear();
    }

    private void addAvailable(ScraggleTile tile) {
//        tile.animate();
        mAvailable.add(tile);
    }

    public boolean isAvailable(ScraggleTile tile) {
        return mAvailable.contains(tile);
    }

    private void setAvailableFromLastMove(int small) {
        clearAvailable();
        // Make all the tiles at the destination available
        if (small != -1) {
            for (int dest = 0; dest < 9; dest++) {
                ScraggleTile tile = mSmallTiles[small][dest];
//                if (tile.getOwner() == ScraggleTile.Owner.NEITHER)
                if(tile.getIsSelected())
                    addAvailable(tile);
            }
        }
        // If there were none available, make all squares available
        if (mAvailable.isEmpty()) {
            setAllAvailable();
        }
    }

    private void setAll() {
        for (int large = 0; large < 9; large++) {
            for (int small = 0; small < 9; small++) {
                ScraggleTile tile = mSmallTiles[large][small];
                if(!tile.getIsSelected())
                    addAvailable(tile);
            }
        }
    }

    private void setAllAvailable() {
        for (int large = 0; large < 9; large++) {
            for (int small = 0; small < 9; small++) {
                ScraggleTile tile = mSmallTiles[large][small];
                if(tile.getIsSelected())
                    addAvailable(tile);
            }
        }
    }

    private void updateAllTiles() {
        mEntireBoard.updateDrawableState();
        for (int large = 0; large < 9; large++) {
            mLargeTiles[large].updateDrawableState();
            for (int small = 0; small < 9; small++) {
                mSmallTiles[large][small].updateDrawableState();
            }
        }
    }

    private String removeLastChar(String str) {
        Log.e("removeLastChar 2", "in method call" + String.valueOf(str.length()));
        if (str.length() > 0) {
            str = str.substring(0, str.length()-1);
        }
        Log.e("removeLastChar 2", "exiting method call" + String.valueOf(str.length()));
        return str;
    }

    private int getScore(char c) {
        int score = 0;
        if(c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u' || c == 'n' ||
                c == 'l' || c == 'r' || c == 's' || c == 't'){
            score = 1;
        } else if(c == 'd' || c == 'g') {
            score = 2;
        } else if(c == 'b' || c == 'c' || c == 'm' || c == 'p') {
            score = 3;
        } else if(c == 'f' || c == 'h' || c == 'v' || c == 'w' || c == 'y') {
            score = 4;
        } else if(c == 'k') {
            score = 5;
        } else if(c == 'j' || c == 'x') {
            score = 8;
        } else if(c == 'q' || c == 'z') {
            score = 10;
        }
        return score;
    }

    private Void makeWord(String str, int i) {
//        word = word.concat(String.valueOf(smallTile.getInnerText()));
        Log.e("wordAddTestTest2 in 1", str);
        if (i == mLastLarge) {
            word = word.concat(str);
            Log.e("wordAddTestTest2 out 1", word);
            if (wordMadeList[i] != null && wordMadeList[i] != "") {
                if (word.length() == 1) {
                    word = wordMadeList[i].concat(word);
                    Log.e("wordAddTestTest2 out 3", word);
                }
                wordMadeList[i] = word;
            } else {
                wordMadeList[i] = word;
            }
            Log.e("wordAddTestTest2 out 2", wordMadeList[i]);
        } else {
            word = "".concat(str);
            Log.e("wordAddTestTest2 out 1", word);
            if (wordMadeList[i] != null && wordMadeList[i] != "") {
                word = wordMadeList[i].concat(word);
                wordMadeList[i] = word;
            } else {
                wordMadeList[i] = word;
            }
            Log.e("wordAddTestTest2 out 2", wordMadeList[i]);
        }
        return null;
    }
}
