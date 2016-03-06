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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import edu.neu.madcourse.dharabhavsar.main.R;

public class ScraggleGameFragment extends Fragment {
    static private int mLargeIdList[] = {R.id.wglarge1, R.id.wglarge2, R.id.wglarge3,
            R.id.wglarge4, R.id.wglarge5, R.id.wglarge6, R.id.wglarge7, R.id.wglarge8,
            R.id.wglarge9,};
    static private int mSmallIdList[] = {R.id.wgsmall1, R.id.wgsmall2, R.id.wgsmall3,
            R.id.wgsmall4, R.id.wgsmall5, R.id.wgsmall6, R.id.wgsmall7, R.id.wgsmall8,
            R.id.wgsmall9,};
    private String[] wordMadeList = new String[9];
    private Handler mHandler = new Handler();
    private ScraggleTile mEntireBoard = new ScraggleTile(this);
    private ScraggleTile mLargeTiles[] = new ScraggleTile[9];
    private ScraggleTile mSmallTiles[][] = new ScraggleTile[9][9];
    private Set<ScraggleTile> mAvailable = new HashSet<ScraggleTile>();
    private int mSoundX, mSoundO, mSoundMiss, mSoundRewind;
    private SoundPool mSoundPool;
    private float mVolume = 1f;
    private int mLastLarge;
    private int mLastSmall;
    private List<List<Integer>> resultList = new ArrayList<List<Integer>>();
    private List<String> stringLst;
    private Vibrator v;
    private String word = "";
    private Set<ScraggleTile> mNextMove = new HashSet<ScraggleTile>();
    private View mView;
    private long savedInterval;
    private int gameScore = 0;
    private boolean isPhaseTwo = false;
    private char[][] gameLetterState = new char[9][9];
    private boolean[] isWord = new boolean[9];
    private int wordScore;
    private int[] wordScores = {0,0,0,0,0,0,0,0,0};
    private String[] wordsMadePhase2 = new String[100];
    private String gameData = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retain this fragment across configuration changes.
        setRetainInstance(true);
        Log.e("onCreate", "inside");
        initGame();

        Bundle b = this.getActivity().getIntent().getExtras();

        if (b != null) {
            isPhaseTwo = b.getBoolean("isTwoFlag");
            gameData = b.getString("gameData");
        }
        Log.e("ScraggleFragment", "isPhaseTwo = " + isPhaseTwo);

        if(!((ScraggleGameActivity) this.getActivity()).isRestore()) {
//        Method call to the asyncTaskRunner
            stringLst = ((ScraggleGameActivity) getActivity()).methodCallToAsyncTaskRunner();
            setLettersOnBoard();
        }

        mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        mSoundX = mSoundPool.load(getActivity(), R.raw.shnur_drum_freesound_org, 1);
        mSoundO = mSoundPool.load(getActivity(), R.raw.shnur_drum_freesound_org, 1);
        mSoundMiss = mSoundPool.load(getActivity(), R.raw.bertrof_game_sound_wrong_freesound_org, 1);
        mSoundRewind = mSoundPool.load(getActivity(), R.raw.shnur_drum_freesound_org, 1);
        v = (Vibrator) this.getActivity().getSystemService(Context.VIBRATOR_SERVICE);
    }

    private void clearAvailable() {
        mAvailable.clear();
    }

    private void addAvailable(ScraggleTile tile) {
        tile.animate();
        mAvailable.add(tile);
    }

    public boolean isAvailable(ScraggleTile tile) {
        return mAvailable.contains(tile);
    }

    private void clearNextMove() {
//        Log.e("WordTEST", "Clear All");
        mNextMove.clear();
    }

    private void addNextMove(ScraggleTile tile) {
        tile.animate();
        mNextMove.add(tile);
    }

    public boolean isNextMove(ScraggleTile tile) {
        return mNextMove.contains(tile);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("onCreateView", "inside");
        View rootView =
                inflater.inflate(R.layout.large_board_scraggle, container, false);
        mView = rootView;
        initViews(rootView);
        if (gameData != "" && gameData != null) {
            putState(gameData);
        } else {
            initAddLetters(rootView);
        }
        updateAllTiles();
        return rootView;
    }

    private void initAddLetters(View rootView) {
        Log.e("initAddLetters", "inside");
        mEntireBoard.setView(rootView);
        for (int large = 0; large < 9; large++) {
            View outer = rootView.findViewById(mLargeIdList[large]);
            mLargeTiles[large].setView(outer);
//            to add letters of the words
            List<Integer> posnList = resultList.get(large);
            String str = stringLst.get(large);
//            Log.e("nineWords", str);
            for (int small = 0; small < 9; small++) {
                if(!isPhaseTwo) {
//                to add letters of the words
                    int i = posnList.get(small);
                    Button innerText = (Button) outer.findViewById
                            (mSmallIdList[i]);
                    innerText.setText(String.valueOf(str.charAt(small)));
                    final ScraggleTile smallTileText = mSmallTiles[large][i];
                    gameLetterState[large][i] = str.charAt(small);
//                if(smallTileText.getInnerText != null) {
                    smallTileText.setInnerText(String.valueOf(str.charAt(small)));
//                to add letters of the words
//                }
                } else {
                    Log.e("Phase-2", "inside initAddLetters");
                    Button innerText = (Button) outer.findViewById
                            (mSmallIdList[small]);
                    final ScraggleTile smallTileText = mSmallTiles[large][small];
                    if(smallTileText.getIsSelected() && isWord[large]) {
                        innerText.setText(String.valueOf(gameLetterState[large][small]));
                        smallTileText.setInnerText(String.valueOf(gameLetterState[large][small]));
                    }
                }
            }
        }
    }

    private void initViews(View rootView) {
        Log.e("initViews", "inside");
        mEntireBoard.setView(rootView);
        for (int large = 0; large < 9; large++) {
            final View outer = rootView.findViewById(mLargeIdList[large]);
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
                        Log.e("WordTEST", "inOnCLick");

                        smallTile.animate();
                        // ...
                        Log.e("WordTEST", String.valueOf(isAvailable(smallTile)));
                        Log.e("WordTEST NextMove", String.valueOf(isNextMove(smallTile)));
//                        if (isAvailable(smallTile)) {
                        if (isNextMove(smallTile)) {
                            ((ScraggleGameActivity) getActivity()).startThinking();
                            Log.e("WordTEST", String.valueOf(smallTile.getIsSelected()));
                            if (!smallTile.getIsSelected()) {
                                Log.e("WordTEST", "in isSel = false :: " + String.valueOf(smallTile.getInnerText()));
                                smallTile.setIsSelected(true);
                                Log.e("mLastLarge", String.valueOf(mLastLarge));
                                Log.e("fLarge", String.valueOf(fLarge));
                                makeWord(String.valueOf(smallTile.getInnerText()), fLarge);
                                inner.setBackgroundDrawable(getResources().getDrawable(R.drawable.tile_selected_scraggle));
//                                gameScore += getScore(smallTile.getInnerText().charAt(0));
                            } else {
                                Log.e("WordTEST", "in isSel = true");
                                smallTile.setIsSelected(false);
                                /*String str = removeLastChar(wordMadeList[fLarge],
                                        String.valueOf(smallTile.getIsSelected()).charAt(0));*/
                                word = removeLastChar(wordMadeList[fLarge]);
                                Log.e("wordRemoveTestTest", word);
                                wordMadeList[fLarge] = word;
                                Log.e("wordRemoveTestTest", wordMadeList[fLarge]);
                                Log.e("wordRemoveTestTest", word);
                                inner.setBackgroundDrawable(getResources().getDrawable(R.drawable.tile_not_selected_scraggle));
//                                gameScore -= getScore(smallTile.getInnerText().charAt(0));
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
                inner.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        Log.e("Long PRESS largeGrid", String.valueOf(fLarge));
                        isWord[fLarge] = false;
                        Log.e("Long PRESS isWord", String.valueOf(isWord[fLarge]));
                        word = "";
                        wordMadeList[fLarge] = "";
//                        Log.e("Long PRESS word ", wordMadeList[fLarge]);
                        wordScores[fLarge] = 0;
                        Log.e("Long PRESS wordScore", String.valueOf(wordScores[fLarge]));
                        setAllNextMoves();
                        for (int small = 0; small < 9; small++) {
                            final Button inner = (Button) outer.findViewById
                                    (mSmallIdList[small]);
                            final ScraggleTile smallTile = mSmallTiles[fLarge][small];
                            smallTile.setView(inner);
                            inner.setBackgroundDrawable(getResources().getDrawable(R.drawable.tile_not_selected_scraggle));
                            smallTile.animate();
                            smallTile.setIsSelected(false);
                        }
                        v.vibrate(50);
                        calcGameScore();
                        return true;
                    }
                });
            }
        }
    }

    private void think() {
        Log.e("think", "inside");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setNextPossibleMoveFromLastMove(mLastSmall, mLastLarge);
                if (getActivity() == null) return;
                if (wordMadeList[mLastLarge] != null) {
                    Log.e("DICT TEST", wordMadeList[mLastLarge]);
                    if (wordMadeList[mLastLarge].length() > 2) {
                        Boolean isThereFlag = ((ScraggleGameActivity) getActivity()).searchWord(wordMadeList[mLastLarge]);

                        if (isThereFlag) {
//                            Custom Toast on Successfully finding a Word
                            View customToastRoot = getActivity().getLayoutInflater().inflate(R.layout.mycustom_toast, null);

                            Toast customtoast = new Toast(getActivity().getApplicationContext());

                            customtoast.setView(customToastRoot);
                            customtoast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                            customtoast.setDuration(Toast.LENGTH_SHORT);
                            customtoast.show();

                            wordScore = 0;
                            for (int i = 0; i < wordMadeList[mLastLarge].length(); i++) {
                                wordScore += getScore(wordMadeList[mLastLarge].charAt(i));
                            }
                            wordScores[mLastLarge] = wordScore;
                        } else {
                            wordScores[mLastLarge] = 0;
                        }
                        isWord[mLastLarge] = isThereFlag;

                        calcGameScore();
                    }
                }
//                }
                ((ScraggleGameActivity) getActivity()).setScore(gameScore);
                ((ScraggleGameActivity) getActivity()).stopThinking();
            }
        }, 1000);
    }

    private void calcGameScore() {
        gameScore = 0;
        for(int i = 0; i < 9; i++) {
            if(isWord[i]) {
                Log.e("SCORES", String.valueOf(i)+" : "+String.valueOf(wordScores[i]));
                gameScore += wordScores[i];
                Log.e("Game SCORES", String.valueOf(gameScore));
            }
        }
        Log.e("DICT TEST Score", String.valueOf(gameScore));
    }

    public void restartGame() {
        Log.e("restartGAme", "inside");
        mSoundPool.play(mSoundRewind, mVolume, mVolume, 1, 0, 1f);
        // ...
        initGame();
        initViews(getView());
        updateAllTiles();
    }

    public void initGame() {
        Log.e("initGame", "inside");
        Log.d("Scraggle", "init game");
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
        setNextPossibleMoveFromLastMove(mLastSmall, mLastLarge);
    }

    private void setAvailableFromLastMove(int small) {
        clearAvailable();
        // Make all the tiles at the destination available
        if (small != -1) {
            for (int dest = 0; dest < 9; dest++) {
                ScraggleTile tile = mSmallTiles[dest][small];
//                if (tile.getOwner() == ScraggleTile.Owner.NEITHER)
                if(!tile.getIsSelected())
                    addAvailable(tile);
            }
        }
        // If there were none available, make all squares available
        if (mAvailable.isEmpty()) {
            setAllAvailable();
        }
    }

//    method to set the next possible moves - selecting only the adjacent tiles
//    of the selected tile as available
    private void setNextPossibleMoveFromLastMove(int small, int large) {
        clearNextMove();
        Log.e("WordTEST", "small: " + String.valueOf(small) + " large: " + String.valueOf(large));
        // Make all the neighboring tiles at the destination available
        if (small != -1 && large != -1) {
//            ScraggleTile tile = mSmallTiles[large][small];
//            mNextMove.clear();
//            if(tile.getIsSelected()) {
                List<Integer> intList = getNeighbors(small);
                Log.e("WordTEST neighbors : ", String.valueOf(intList.size()));
                for (int i = 0; i < intList.size(); i++) {
                    int j = intList.get(i);
                    Log.e("WordTEST neighbor : ", String.valueOf(j));
                    ScraggleTile neighborTile = mSmallTiles[large][j];
                    if(!neighborTile.getIsSelected()) {
                        addNextMove(neighborTile);
                    }
                }
            for(int j = 0; j < 9; j++) {
                Log.e("WordTEST", "j: " + String.valueOf(j) + " large: " + String.valueOf(large));
                for (int k = 0; k < 9; k++) {
                    if (j != large) {
                        ScraggleTile neighborTile = mSmallTiles[j][k];
                        addNextMove(neighborTile);
                    } else {
                        continue;
                    }
                }
            }
//            }
        }

        // If there were none available, make all squares available
        if (mNextMove.isEmpty()) {
//            Log.e("WordTEST", "Set All initially");
            setAllNextMoves();
        }
    }

    private void setAllNextMoves() {
        for (int large = 0; large < 9; large++) {
            for (int small = 0; small < 9; small++) {
                ScraggleTile tile = mSmallTiles[large][small];
                    addNextMove(tile);
            }
        }
    }

    private void setAllAvailable() {
        for (int large = 0; large < 9; large++) {
            for (int small = 0; small < 9; small++) {
                ScraggleTile tile = mSmallTiles[large][small];
                if(!tile.getIsSelected())
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

    /**
     * Create a string containing the state of the game.
     */
    public String getState() {
        Log.e("SAVING GAME STATE", "in");
        StringBuilder builder = new StringBuilder();
        builder.append(((ScraggleGameActivity) this.getActivity()).getSavedRemainingInterval());
        builder.append(',');
        builder.append(((ScraggleGameActivity) this.getActivity()).getScore());
        builder.append(',');
        builder.append(isPhaseTwo);
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
                builder.append(gameLetterState[large][small]);
                builder.append(',');
            }
            builder.append(wordMadeList[large]);
            builder.append(',');
            builder.append(isWord[large]);
            builder.append(',');
        }
        Log.e("SAVING GAME STATE", "out");
        return builder.toString();
    }

    /**
     * Restore the state of the game from the given string.
     */
    public void putState(String gameData) {
        Log.e("RESTORING GAME STATE", "in");
        String[] fields = gameData.split(",");
        int index = 0;
        savedInterval = Long.parseLong(fields[index++]);
        ((ScraggleGameActivity) this.getActivity()).setSavedRemainingInterval(savedInterval);
        gameScore = Integer.parseInt(fields[index++]);
        ((ScraggleGameActivity) this.getActivity()).setScore(gameScore);
        isPhaseTwo = Boolean.parseBoolean(fields[index++]);
        ((ScraggleGameActivity) this.getActivity()).setIsPhaseTwo(isPhaseTwo);
        mLastLarge = Integer.parseInt(fields[index++]);
        mLastSmall = Integer.parseInt(fields[index++]);
        for (int large = 0; large < 9; large++) {
            for (int small = 0; small < 9; small++) {
                String innerText = (fields[index++]);
                mSmallTiles[large][small].setInnerText(innerText);
                Boolean isSelected = Boolean.valueOf(fields[index++]);
                mSmallTiles[large][small].setIsSelected(isSelected);
                char charLetter = fields[index++].charAt(0);
                gameLetterState[large][small] = charLetter;
            }
            String wordMade = (fields[index++]);
            wordMadeList[large] = wordMade;
            Boolean isWordFlag = Boolean.valueOf(fields[index++]);
            isWord[large] = isWordFlag;
        }
        setAvailableFromLastMove(mLastSmall);
        updateAllTiles();
        putPrevLetters(mView);
        Log.e("RESTORING GAME STATE", "out");
    }

    private void putPrevLetters(View rootView) {
        Log.e("putPrevLetters", "inside");
        mEntireBoard.setView(rootView);
        for (int large = 0; large < 9; large++) {
            View outer = rootView.findViewById(mLargeIdList[large]);
            mLargeTiles[large].setView(outer);
//          to restore previous state on GUI
            for (int small = 0; small < 9; small++) {
//                to add letters of the words
                Button innerText = (Button) outer.findViewById
                        (mSmallIdList[small]);
                innerText.setText(String.valueOf(mSmallTiles[large][small].getInnerText()));
                if (mSmallTiles[large][small].getIsSelected())
                    innerText.setBackgroundDrawable(getResources().getDrawable(R.drawable.tile_selected_scraggle));
                else
                    innerText.setBackgroundDrawable(getResources().getDrawable(R.drawable.tile_not_selected_scraggle));
            }
        }
    }

    private void setLettersOnBoard() {
        List<List<Integer>> myList = new ArrayList<List<Integer>>();
        myList.addAll(Arrays.asList(Arrays.asList(0, 1, 4, 6, 3, 7, 8, 5, 2),
                Arrays.asList(8, 4, 0, 3, 6, 7, 5, 2, 1),
                Arrays.asList(8, 7, 6, 3, 0, 1, 5, 4, 2),
                Arrays.asList(3, 7, 6, 4, 8, 5, 2, 1, 0),
                Arrays.asList(5, 4, 8, 7, 6, 3, 0, 1, 2),
                Arrays.asList(5, 7, 8, 4, 6, 3, 0, 1, 2),
                Arrays.asList(7, 8, 5, 4, 6, 3, 0, 1, 2),
                Arrays.asList(6, 7, 5, 8, 4, 2, 1, 0, 3),
                Arrays.asList(7, 6, 4, 8, 5, 2, 1, 0, 3),
                Arrays.asList(3, 6, 4, 0, 1, 2, 5, 8, 7),
                Arrays.asList(4, 3, 0, 1, 2, 5, 8, 7, 6),
                Arrays.asList(2, 5, 7, 8, 4, 1, 0, 3, 6),
                Arrays.asList(1, 0, 4, 3, 6, 7, 8, 5, 2),
                Arrays.asList(2, 4, 6, 3, 0, 1, 5, 8, 7),
                Arrays.asList(5, 1, 2, 4, 8, 7, 6, 3, 0),
                Arrays.asList(8, 4, 2, 5, 7, 6, 3, 0, 1),
                Arrays.asList(2, 4, 6, 7, 8, 5, 1, 0, 3),
                Arrays.asList(0, 4, 8, 7, 6, 3, 1, 2, 5)));

        for (int i = 0; i < 9; i++) {
            int rnd = new Random().nextInt(myList.size());
            resultList.add(myList.get(rnd));
        }
    }

    private List<Integer> getNeighbors(Integer i) {
        List<Integer> intList = new ArrayList<Integer>();
        switch (i) {
            case 0:
                intList.addAll(Arrays.asList(1, 3, 4));
                break;
            case 1:
                intList.addAll(Arrays.asList(0, 2, 3, 4, 5));
                break;
            case 2:
                intList.addAll(Arrays.asList(1, 4, 5));
                break;
            case 3:
                intList.addAll(Arrays.asList(0, 1, 4, 6, 7));
                break;
            case 4:
                intList.addAll(Arrays.asList(0, 1, 2, 3, 5, 6, 7, 8));
                break;
            case 5:
                intList.addAll(Arrays.asList(1, 2, 4, 7, 8));
                break;
            case 6:
                intList.addAll(Arrays.asList(3, 4, 7));
                break;
            case 7:
                intList.addAll(Arrays.asList(3, 4, 5, 6, 8));
                break;
            case 8:
                intList.addAll(Arrays.asList(4, 5, 7));
                break;
        }
        return intList;
    }

    private String removeLastChar(String str, char chr) {
        if (str.length() > 0 && str.charAt(str.length()-1) == chr) {
            str = str.substring(0, str.length()-1);
        } else {
            Toast.makeText(getActivity(), "Error! Please delete the last selected letter only",
                    Toast.LENGTH_LONG).show();
        }
        return str;
    }

    private String removeLastChar(String str) {
        Log.e("removeLastChar", "in method call" + String.valueOf(str.length()));
        if (str.length() > 0) {
            str = str.substring(0, str.length()-1);
        }
        Log.e("removeLastChar", "exiting method call" + String.valueOf(str.length()));
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
        Log.e("wordAddTestTest in 1", str);
        if (i == mLastLarge) {
            word = word.concat(str);
            Log.e("wordAddTestTest out 1", word);
            if (wordMadeList[i] != null && wordMadeList[i] != "") {
                if (word.length() == 1) {
                    word = wordMadeList[i].concat(word);
                    Log.e("wordAddTestTest out 3", word);
                }
                wordMadeList[i] = word;
            } else {
                wordMadeList[i] = word;
            }
            Log.e("wordAddTestTest out 2", wordMadeList[i]);
        } else {
            word = "".concat(str);
            Log.e("wordAddTestTest out 1", word);
            if (wordMadeList[i] != null && wordMadeList[i] != "") {
                word = wordMadeList[i].concat(word);
                wordMadeList[i] = word;
            } else {
                wordMadeList[i] = word;
            }
            Log.e("wordAddTestTest out 2", wordMadeList[i]);
        }
        return null;
    }

    protected void disableLetterGrid() {
        mEntireBoard.getView().setVisibility(View.INVISIBLE);
    }

    protected void enableLetterGrid() {
        mEntireBoard.getView().setVisibility(View.VISIBLE);
    }

    public boolean isPhaseTwo() {
        return isPhaseTwo;
    }

    public void setIsPhaseTwo(boolean isPhaseTwo) {
        this.isPhaseTwo = isPhaseTwo;
    }
}

