package edu.neu.madcourse.dharabhavsar.ui.communication2player;

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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import edu.neu.madcourse.dharabhavsar.R;
import edu.neu.madcourse.dharabhavsar.utils.gcmcomm.CommunicationConstants;

public class ScraggleGameFragment2Combine extends Fragment {
    static private int mLargeIdList[] = {R.id.wglarge1, R.id.wglarge2, R.id.wglarge3,
            R.id.wglarge4, R.id.wglarge5, R.id.wglarge6, R.id.wglarge7, R.id.wglarge8,
            R.id.wglarge9,};
    static private int mSmallIdList[] = {R.id.wgsmall1, R.id.wgsmall2, R.id.wgsmall3,
            R.id.wgsmall4, R.id.wgsmall5, R.id.wgsmall6, R.id.wgsmall7, R.id.wgsmall8,
            R.id.wgsmall9,};
    private String[] wordMadeList = new String[9];
    private Handler mHandler = new Handler();
    private ScraggleTile2 mEntireBoard = new ScraggleTile2(this);
    private ScraggleTile2 mLargeTiles[] = new ScraggleTile2[9];
    private ScraggleTile2 mSmallTiles[][] = new ScraggleTile2[9][9];
    private Set<ScraggleTile2> mAvailable = new HashSet<ScraggleTile2>();
    private int mSoundX, mSoundO, mSoundMiss, mSoundRewind;
    private SoundPool mSoundPool;
    private float mVolume = 1f;
    private int mLastLarge;
    private int mLastSmall;
    private List<List<Integer>> resultList = new ArrayList<List<Integer>>();
    private List<String> stringLst;
    private Vibrator v;
    private String word = "";
    private Set<ScraggleTile2> mNextMove = new HashSet<ScraggleTile2>();
    private View mView;
    private long savedInterval;
    private int gameScore = 0;
    private boolean isPhaseTwo = false;
    private char[][] gameLetterState = new char[9][9];
    private boolean[] isWord = new boolean[9];
    private int wordScore;
    private int[] wordScores = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    private String[] wordsMadePhase2 = new String[100];
    private String gameData = "";
    private String word2 = "";
    private String wordCheck2 = "";
    private int countWordFound = 0;
    private int gameScore2 = 0;

    private List<Integer> arrInt = new ArrayList<Integer>();
    int randomInt;

    private String[] boggledWords = new String[]{"", "", "", "", "", "", "", "", ""};
    private String[] boggledWordsRetrieved = new String[9];
    private String[] selectedWordsMade = new String[]{"", "", "", "", "", "", "", "", ""};

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
//        Log.e("ScraggleFragment", "isPhaseTwo = " + isPhaseTwo);

        if (!((ScraggleGameActivity2Combine) this.getActivity()).isRestore()) {
//        Method call to the asyncTaskRunner
            stringLst = ((ScraggleGameActivity2Combine) getActivity()).methodCallToAsyncTaskRunner();
            setLettersOnBoard();
//            setLettersOnBoggleBoard();
        }
        if (((ScraggleGameActivity2Combine) getActivity()).isPlayer2()) {
            Log.e("RetrieveDATAP2", CommunicationConstants.combineGameKey);
            ((ScraggleGameActivity2Combine) getActivity()).
                    fetchGameWordDetailsCombat(CommunicationConstants.combineGameKey);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    boggledWordsRetrieved = ((ScraggleGameActivity2Combine) getActivity()).
                            getRetrievedGameData().getBoggledWords();
                    Log.e("RetrieveDATAP2", String.valueOf(boggledWordsRetrieved.length));
                }
            }, 3000);
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

    private void addAvailable(ScraggleTile2 tile) {
        tile.animateCombine();
        mAvailable.add(tile);
    }

    public boolean isAvailable(ScraggleTile2 tile) {
        return mAvailable.contains(tile);
    }

    private void clearNextMove() {
//        Log.e("WordTEST", "Clear All");
        mNextMove.clear();
    }

    private void addNextMove(ScraggleTile2 tile) {
//        tile.animateCombine();
        mNextMove.add(tile);
    }

    public boolean isNextMove(ScraggleTile2 tile) {
        return mNextMove.contains(tile);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("onCreateView", "inside");
//        Log.e("onCreateView", "inside : " + gameData);
        final View rootView =
                inflater.inflate(R.layout.large_board_scraggle2, container, false);
        mView = rootView;
        initViews(rootView);
        if (gameData != "" && gameData != null) {
            putState(gameData);
        } else if (((ScraggleGameActivity2Combine) getActivity()).isPlayer2()) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    initAddAsyncGameLetters(rootView);
                }
            }, 7000);
        } else {
            initAddLetters(rootView);
        }
        updateAllTiles();
        return rootView;
    }

    private void initAddAsyncGameLetters(View rootView) {
        Log.e("initAddAsyncGameLetters", "inside");
        mEntireBoard.setView(rootView);
        String str;
//        boggledWordsRetrieved = Arrays.copyOf(fireBaseGameData.getBoggledWords(), 9);
        for (int large = 0; large < 9; large++) {
            View outer = rootView.findViewById(mLargeIdList[large]);
            mLargeTiles[large].setView(outer);
            str = boggledWordsRetrieved[large];
            for (int small = 0; small < 9; small++) {
                Button innerText = (Button) outer.findViewById
                        (mSmallIdList[small]);
                final ScraggleTile2 smallTileText = mSmallTiles[large][small];
//                smallTileText.setView(innerText);
                smallTileText.setInnerText(String.valueOf(str.charAt(small)));
//                str = String.valueOf(retrievedWordList[large][small]);
//                Log.e("initAddAsyncGameLetters", "inside : " + smallTileText.getInnerText());
                innerText.setText(String.valueOf(str.charAt(small)));
                if(innerText.isEnabled())
                    innerText.setBackgroundDrawable(getResources().getDrawable
                        (R.drawable.tile_not_selected_scraggle));
                else
                    innerText.setBackgroundDrawable(getResources().getDrawable
                            (R.drawable.tile_deselected_scraggle));
            }
        }
    }

    private void initAddLetters(View rootView) {
//        Log.e("initAddLetters", "inside : " + isPhaseTwo);
//        Log.e("initAddLetters", "inside : " + ((ScraggleGameActivity2Combine) getActivity()).isPhoneShaked());
        mEntireBoard.setView(rootView);
        String str;
        for (int large = 0; large < 9; large++) {
            View outer = rootView.findViewById(mLargeIdList[large]);
            mLargeTiles[large].setView(outer);
//            to add letters of the words
            List<Integer> posnList = resultList.get(large);
            str = stringLst.get(large);
//            Log.e("nineWords", str);
            for (int small = 0; small < 9; small++) {
                if (!isPhaseTwo) {
                    int i = posnList.get(small);
//                    Log.e("nineWords ", i + " = " + str);
                    Button innerText = (Button) outer.findViewById
                            (mSmallIdList[i]);
                    innerText.setText(String.valueOf(str.charAt(small)));
                    final ScraggleTile2 smallTileText = mSmallTiles[large][i];
                    gameLetterState[large][i] = str.charAt(small);
                    smallTileText.setInnerText(String.valueOf(str.charAt(small)));
                }
                /*if (((ScraggleGameActivity2Combine) getActivity()).isPhoneShaked()) {
                    int i = posnList.get(small);
//                    Log.e("nineWords ", i + " = " + str);
                    Button innerText = (Button) outer.findViewById
                            (mSmallIdList[i]);
                    final ScraggleTile2 smallTileText = mSmallTiles[large][i];
                    smallTileText.setView(innerText);
                    innerText.setText(String.valueOf(str.charAt(small)));
                    innerText.setBackgroundDrawable(getResources().getDrawable
                            (R.drawable.tile_not_selected_scraggle));
                    gameLetterState[large][i] = str.charAt(small);
                    smallTileText.setInnerText(String.valueOf(str.charAt(small)));
                    smallTileText.setIsSelected(false);
                    if (!isPhaseTwo) {
                        gameScore = 0;
                    } else {
                        gameScore2 = 0;
                    }
                }*/
            }
            for (int small = 0; small < 9; small++) {
                boggledWords[large] += gameLetterState[large][small];
            }
            /*if (((ScraggleGameActivity2Combine) getActivity()).isPhoneShaked()) {
                wordScores = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
                if(isPhaseTwo) {
                    wordsMadePhase2 = new String[100];
                } else {
                    wordMadeList = new String[wordMadeList.length];
                    mLastLarge = -1;
                    mLastSmall = -1;
                }
            }*/
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
                if (((ScraggleGameActivity2Combine) getActivity()).isPlayer2()) {
                    if(large % 2 == 0) {
//                        Log.e("RetrieveDATAP2", CommunicationConstants.combineGameKey);
                        inner.setEnabled(false);
                        inner.setBackgroundDrawable(getResources().getDrawable
                                (R.drawable.tile_deselected_scraggle));
                    }
                } else {
                    if(large % 2 != 0) {
//                        Log.e("RetrieveDATAP2", "Player1");
                        inner.setEnabled(false);
                        inner.setBackgroundDrawable(getResources().getDrawable
                                (R.drawable.tile_deselected_scraggle));
                    }
                }
                final int fLarge = large;
                final int fSmall = small;
                final ScraggleTile2 smallTile = mSmallTiles[large][small];
                smallTile.setView(inner);
                // ...
                inner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Log.e("WordTEST", "inOnCLick");

                        smallTile.animateCombine();
                        // ...
//                        Log.e("think", "isPhaseTwo = " + isPhaseTwo);
//                        Log.e("WordTEST", String.valueOf(isAvailable(smallTile)));
//                        Log.e("WordTEST NextMove", String.valueOf(isNextMove(smallTile)));
                        if (!isPhaseTwo) {
//                        if (isAvailable(smallTile)) {
                            if (isNextMove(smallTile)) {
//                                ((ScraggleGameActivity2Combine) getActivity()).startThinking();
//                                Log.e("WordTEST", String.valueOf(smallTile.getIsSelected()));
                                if (!smallTile.getIsSelected()) {
//                                    Log.e("WordTEST", "in isSel = false :: " + String.valueOf(smallTile.getInnerText()));
                                    smallTile.setIsSelected(true);
//                                    ...
                                    selectedWordsMade[fLarge] += fSmall;
//                                    Log.e("mLastLarge", String.valueOf(mLastLarge));
//                                    Log.e("fLarge", String.valueOf(fLarge));
                                    makeWord(String.valueOf(smallTile.getInnerText()), fLarge);
                                    inner.setBackgroundDrawable(getResources().getDrawable(R.drawable.tile_selected_scraggle));
//                                gameScore += getScore(smallTile.getInnerText().charAt(0));
                                    // any polling mechanism can be used
                                    /*if(!((ScraggleGameActivity2Combine) getActivity()).isPlayer2())
                                        ((ScraggleGameActivity2Combine) getActivity())
                                                .startTimer1(Constants.COMBINE_GAME_KEY);
                                    else
                                        ((ScraggleGameActivity2Combine) getActivity())
                                                .startTimer1(CommunicationConstants.combineGameKey);*/
                                } else {
//                                    Log.e("WordTEST", "in isSel = true");
                                    smallTile.setIsSelected(false);
                                /*String str = removeLastChar(wordMadeList[fLarge],
                                        String.valueOf(smallTile.getIsSelected()).charAt(0));*/
                                    word = removeLastChar(wordMadeList[fLarge]);
//                                    Log.e("wordRemoveTestTest", word);
                                    wordMadeList[fLarge] = word;
//                                    Log.e("wordRemoveTestTest", wordMadeList[fLarge]);
//                                    Log.e("wordRemoveTestTest", word);
                                    inner.setBackgroundDrawable(getResources().getDrawable(R.drawable.tile_not_selected_scraggle));
//                                gameScore -= getScore(smallTile.getInnerText().charAt(0));
                                }
                                mSoundPool.play(mSoundX, mVolume, mVolume, 1, 0, 1f);
                                // Vibrate for 25 milliseconds
                                v.vibrate(25);
                                think();
                            } else {
                                mSoundPool.play(mSoundMiss, mVolume, mVolume, 1, 0, 1f);
                            }
                            mLastLarge = fLarge;
                            mLastSmall = fSmall;

                            String[] setLetters = ((ScraggleGameActivity2Combine) getActivity())
                                    .getRetrievedGameData().getLettersSelectedCombine();



                        } else {
                            Log.e("initViews", "inside PhaseTwo code");
//                            ((ScraggleGameActivity2Combine) getActivity()).startThinking();
//                            Log.e("initViews", "mLastLarge = " + mLastLarge);
//                            Log.e("initViews", "fLarge = " + fLarge);
                            if (mLastLarge != fLarge) {
                                if (!smallTile.getIsBlank()) {
//                                    Log.e("initViews", "inside is selected code");
                                    smallTile.setIsBlank(true);
//                                wordsMadePhase2 =
                                    wordCheck2 = makeWord(String.valueOf(smallTile.getInnerText()));
                                    inner.setBackgroundDrawable(getResources().getDrawable(R.drawable.tile_selected_scraggle));
                                    mSoundPool.play(mSoundX, mVolume, mVolume, 1, 0, 1f);
                                    v.vibrate(25);
                                    think();
                                }
                            } else {
                                ((ScraggleGameActivity2Combine) getActivity()).stopThinking();
                                mSoundPool.play(mSoundMiss, mVolume, mVolume, 1, 0, 1f);
                            }
                            mLastLarge = fLarge;
                            mLastSmall = fSmall;
                        }
                    }
                });
                // ...
                inner.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (!isPhaseTwo) {
                            Log.e("Long PRESS largeGrid", String.valueOf(fLarge));
                            isWord[fLarge] = false;
//                            Log.e("Long PRESS isWord", String.valueOf(isWord[fLarge]));
                            word = "";
                            selectedWordsMade[fLarge] = "";
                            wordMadeList[fLarge] = "";
//                        Log.e("Long PRESS word ", wordMadeList[fLarge]);
                            wordScores[fLarge] = 0;
//                            Log.e("Long PRESS wordScore", String.valueOf(wordScores[fLarge]));
                            setAllNextMoves();
                            /*if(!((ScraggleGameActivity2Combine) getActivity()).isPlayer2())
                                ((ScraggleGameActivity2Combine) getActivity())
                                        .startTimer1(Constants.COMBINE_GAME_KEY);
                            else
                                ((ScraggleGameActivity2Combine) getActivity())
                                        .startTimer1(CommunicationConstants.combineGameKey);*/
                            for (int small = 0; small < 9; small++) {
                                final Button inner = (Button) outer.findViewById
                                        (mSmallIdList[small]);
                                final ScraggleTile2 smallTile = mSmallTiles[fLarge][small];
                                smallTile.setView(inner);
                                inner.setBackgroundDrawable(getResources().getDrawable(R.drawable.tile_not_selected_scraggle));
                                smallTile.animateCombine();
                                smallTile.setIsSelected(false);
                            }
                            v.vibrate(50);
                            calcGameScore();
                            ((ScraggleGameActivity2Combine) getActivity()).setScore(gameScore);
                        } else {
                            Log.e("Long PRESS 2", String.valueOf(fLarge));
                            refreshBoard(mView);
                            v.vibrate(50);
                            calcGameScore2();
                        }
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
//                Log.e("think", "isPhaseTwo = " + isPhaseTwo);
                if (!isPhaseTwo) {
                    setNextPossibleMoveFromLastMove(mLastSmall, mLastLarge);
                    if (getActivity() == null) return;
                    if (wordMadeList[mLastLarge] != null) {
//                        Log.e("DICT TEST", wordMadeList[mLastLarge]);
                        if (wordMadeList[mLastLarge].length() > 2) {
                            Boolean isThereFlag = ((ScraggleGameActivity2Combine) getActivity()).searchWord(wordMadeList[mLastLarge]);

                            if (isThereFlag) {
//                            Custom Toast on Successfully finding a Word
                                View customToastRoot = getActivity().getLayoutInflater().inflate(R.layout.mycustom_toast,
                                        (ViewGroup) getActivity().findViewById(R.id.toast_layout_root));

                                TextView text = (TextView) customToastRoot.findViewById(R.id.textView1);
                                String str = String.format(getResources().getString(R.string.custom_toast_text),
                                        wordMadeList[mLastLarge]);
//                                Log.e("WordFound", str);
                                text.setText(str);

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
                            ((ScraggleGameActivity2Combine) getActivity()).setScore(gameScore);
                        }
                    }
//                }
                    ((ScraggleGameActivity2Combine) getActivity()).stopThinking();
                } else {
                    if (getActivity() == null) return;
//                    Log.e("think", "length = " + wordCheck2.length());
                    if (wordCheck2.length() >= 3) {
                        if (!Arrays.asList(wordsMadePhase2).contains(wordCheck2)) {
                            Boolean isThereFlag = ((ScraggleGameActivity2Combine) getActivity()).searchWord(wordCheck2);
                            Log.e("think 2", "isThereFlag = " + isThereFlag);
                            if (isThereFlag) {
                                wordsMadePhase2[countWordFound] = wordCheck2;
                                View customToastRoot = getActivity().getLayoutInflater().inflate(R.layout.mycustom_toast,
                                        (ViewGroup) getActivity().findViewById(R.id.toast_layout_root));

                                TextView text = (TextView) customToastRoot.findViewById(R.id.textView1);
                                String str = String.format(getResources().getString(R.string.custom_toast_text),
                                        wordCheck2);
//                                Log.e("WordFound", str);
                                text.setText(str);

                                Toast customtoast = new Toast(getActivity().getApplicationContext());
                                customtoast.setView(customToastRoot);
                                customtoast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                                customtoast.setDuration(Toast.LENGTH_SHORT);
                                customtoast.show();
                                countWordFound++;
                                refreshBoard(mView);
                                calcGameScore2();
                                ((ScraggleGameActivity2Combine) getActivity()).setScore2(gameScore);
                            }
                        }
                    }
                    ((ScraggleGameActivity2Combine) getActivity()).stopThinking();
                }
            }
        }, 1000);
    }

    private void calcGameScore() {
        gameScore = 0;
        for (int i = 0; i < 9; i++) {
            if (isWord[i]) {
//                Log.e("SCORES", String.valueOf(i)+" : "+String.valueOf(wordScores[i]));
                gameScore += wordScores[i];
//                Log.e("Game SCORES", String.valueOf(gameScore));
            }
        }
//        Log.e("DICT TEST Score", String.valueOf(gameScore));
    }

    private void calcGameScore2() {
        gameScore = 0;
        for (int cnt = 0; cnt < countWordFound; cnt++) {
            wordScore = 0;
            for (int i = 0; i < wordsMadePhase2[cnt].length(); i++) {
//                Log.e("SCORES 2", "word letter : " + String.valueOf(wordsMadePhase2[cnt].charAt(i)));
                wordScore += getScore(wordsMadePhase2[cnt].charAt(i));
            }
//            Log.e("SCORES 2", String.valueOf(cnt) + " : " + String.valueOf(wordScore));
            gameScore += wordScore;
//            Log.e("Game SCORES 2", String.valueOf(gameScore));
        }
//        Log.e("DICT TEST Score 2", String.valueOf(gameScore));
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
        mEntireBoard = new ScraggleTile2(this);
        // Create all the tiles
        for (int large = 0; large < 9; large++) {
            mLargeTiles[large] = new ScraggleTile2(this);
            for (int small = 0; small < 9; small++) {
                mSmallTiles[large][small] = new ScraggleTile2(this);
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
                ScraggleTile2 tile = mSmallTiles[dest][small];
//                if (tile.getOwner() == ScraggleTile2.Owner.NEITHER)
                if (!tile.getIsSelected())
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
//        Log.e("WordTEST", "small: " + String.valueOf(small) + " large: " + String.valueOf(large));
        // Make all the neighboring tiles at the destination available
        if (small != -1 && large != -1) {
//            ScraggleTile2 tile = mSmallTiles[large][small];
//            mNextMove.clear();
//            if(tile.getIsSelected()) {
            List<Integer> intList = getNeighbors(small);
//                Log.e("WordTEST neighbors : ", String.valueOf(intList.size()));
            for (int i = 0; i < intList.size(); i++) {
                int j = intList.get(i);
//                    Log.e("WordTEST neighbor : ", String.valueOf(j));
                ScraggleTile2 neighborTile = mSmallTiles[large][j];
                if (!neighborTile.getIsSelected()) {
                    addNextMove(neighborTile);
                }
            }
            for (int j = 0; j < 9; j++) {
//                Log.e("WordTEST", "j: " + String.valueOf(j) + " large: " + String.valueOf(large));
                for (int k = 0; k < 9; k++) {
                    if (j != large) {
                        ScraggleTile2 neighborTile = mSmallTiles[j][k];
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
                ScraggleTile2 tile = mSmallTiles[large][small];
                addNextMove(tile);
            }
        }
    }

    private void setAllAvailable() {
        for (int large = 0; large < 9; large++) {
            for (int small = 0; small < 9; small++) {
                ScraggleTile2 tile = mSmallTiles[large][small];
                if (!tile.getIsSelected())
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
        builder.append(((ScraggleGameActivity2Combine) this.getActivity()).getSavedRemainingInterval());
        builder.append(',');
        builder.append(((ScraggleGameActivity2Combine) this.getActivity()).getScore());
        builder.append(',');
        builder.append(((ScraggleGameActivity2Combine) this.getActivity()).getScore2());
        builder.append(',');
        builder.append(((ScraggleGameActivity2Combine) this.getActivity()).isPhaseTwo());
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
//        if(isPhaseTwo) {
        builder.append(wordCheck2);
        builder.append(',');
//        Log.e("SAVING GAME STATE", "count = " + String.valueOf(countWordFound));
        builder.append(countWordFound);
        builder.append(',');
        for (int i = 0; i < countWordFound; i++) {
            if (wordsMadePhase2[i] != null && wordsMadePhase2[i] != "") {
                builder.append(wordsMadePhase2[i]);
                builder.append(',');
            }
        }
//        }
//        Log.e("SAVING GAME STATE", "isGameEndFlag = " +
//                String.valueOf(((ScraggleGameActivity2Combine) this.getActivity()).isGameEnd()));
        builder.append(((ScraggleGameActivity2Combine) this.getActivity()).isGameEnd());
        builder.append(',');
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
        ((ScraggleGameActivity2Combine) this.getActivity()).setSavedRemainingInterval(savedInterval);
        gameScore = Integer.parseInt(fields[index++]);
        ((ScraggleGameActivity2Combine) this.getActivity()).setScore(gameScore);
        gameScore2 = Integer.parseInt(fields[index++]);
        ((ScraggleGameActivity2Combine) this.getActivity()).setScore2(gameScore2);
        isPhaseTwo = Boolean.parseBoolean(fields[index++]);
//        Log.e("putSTATE", "isPhaseTwo = " + isPhaseTwo);
        ((ScraggleGameActivity2Combine) this.getActivity()).setIsPhaseTwo(isPhaseTwo);
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
//        if(isPhaseTwo) {
        wordCheck2 = (fields[index++]);
        countWordFound = Integer.valueOf(fields[index++]);
        for (int i = 0; i < countWordFound; i++) {
            wordsMadePhase2[i] = (fields[index++]);
        }
//        }
        boolean isGameEndFlag = Boolean.parseBoolean(fields[index++]);
        ((ScraggleGameActivity2Combine) this.getActivity()).setIsGameEnd(isGameEndFlag);
        setAvailableFromLastMove(mLastSmall);
        updateAllTiles();
        putPrevLetters(mView);
        Log.e("RESTORING GAME STATE", "out");
    }

    private void putPrevLetters(View rootView) {
        Log.e("putPrevLetters", "inside");
//        Log.e("putPrevLetters", "isPhaseTwo = " + isPhaseTwo);
        mEntireBoard.setView(rootView);
        for (int large = 0; large < 9; large++) {
//            Log.e("putPrevLetters", "isWord[large] = " + isWord[large]);
            View outer = rootView.findViewById(mLargeIdList[large]);
            mLargeTiles[large].setView(outer);
//          to restore previous state on GUI
            for (int small = 0; small < 9; small++) {
//                to add letters of the words
                Button innerText = (Button) outer.findViewById
                        (mSmallIdList[small]);
                if (isPhaseTwo) {
                    if (mSmallTiles[large][small].getIsSelected()) {
                        if (isWord[large]) {
//                            Log.e("putPrevLetters", "inside word true");
                            innerText.setText(String.valueOf(mSmallTiles[large][small].getInnerText()));
                            innerText.setBackgroundDrawable(getResources().getDrawable(R.drawable.tile_not_selected_scraggle));
                        } else {
//                            Log.e("putPrevLetters", "inside word false");
                            innerText.setText("");
                            innerText.setBackgroundDrawable(getResources().getDrawable(R.drawable.tile_deselected_scraggle));
                            innerText.setEnabled(false);
                        }
                    } else {
                        innerText.setText("");
//                        Log.e("putPrevLetters", "inside letter not selected");
                        innerText.setBackgroundDrawable(getResources().getDrawable(R.drawable.tile_deselected_scraggle));
                        innerText.setEnabled(false);
                    }
                } else {
                    innerText.setText(String.valueOf(gameLetterState[large][small]));
                    mSmallTiles[large][small].setInnerText(String.valueOf(gameLetterState[large][small]));
                    if (mSmallTiles[large][small].getIsSelected()) {
                        innerText.setBackgroundDrawable(getResources().getDrawable(R.drawable.tile_selected_scraggle));
                    } else {
                        innerText.setBackgroundDrawable(getResources().getDrawable(R.drawable.tile_not_selected_scraggle));
                    }
                }
            }
        }
    }

    private void refreshBoard(View rootView) {
        Log.e("refreshBoard", "inside");
        mEntireBoard.setView(rootView);
        for (int large = 0; large < 9; large++) {
//            Log.e("refreshBoard", "isWord[large] = " + isWord[large]);
            View outer = rootView.findViewById(mLargeIdList[large]);
            mLargeTiles[large].setView(outer);
//          to restore previous state on GUI
            for (int small = 0; small < 9; small++) {
//                to add letters of the words
                Button innerText = (Button) outer.findViewById
                        (mSmallIdList[small]);
//                if (isPhaseTwo) {
                if (mSmallTiles[large][small].getIsSelected()) {
                    if (isWord[large]) {
//                            Log.e("putPrevLetters", "inside word true");
                        mSmallTiles[large][small].setIsBlank(false);
                        wordCheck2 = "";
                        word2 = "";
                        mLastLarge = -1;
                        innerText.setText(String.valueOf(mSmallTiles[large][small].getInnerText()));
                        innerText.setBackgroundDrawable(getResources().getDrawable(R.drawable.tile_not_selected_scraggle));
                    } else {
//                            Log.e("putPrevLetters", "inside word false");
                        innerText.setText("");
                        innerText.setBackgroundDrawable(getResources().getDrawable(R.drawable.tile_deselected_scraggle));
                        innerText.setEnabled(false);
                    }
                } else {
                    innerText.setText("");
//                        Log.e("putPrevLetters", "inside letter not selected");
                    innerText.setBackgroundDrawable(getResources().getDrawable(R.drawable.tile_deselected_scraggle));
                    innerText.setEnabled(false);
                }
//                }
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

    private void setLettersOnBoggleBoard() {

        /*int[] arr = {0,1,2,3,4,5,6,7,8};
        List arrInt = new ArrayList(9);
        arrInt = Arrays.asList(arr);
        Vector v = new Vector(arrInt);*/

        randomInt = (int) Math.random()*9;
        arrInt.add(randomInt);
//        List neighborLst = getNeighbors(randomInt);
        shuffleLetterBoggleStyle(randomInt);

        /*while(neighborLst.size() > 0) {
            int rnd = new Random().nextInt(neighborLst.size());
            int newInt = (int) neighborLst.get(rnd);
        }

        for(int i = 0; i < 9; i++) {
            neighborLst = getNeighbors(randomInt);
            int rnd = new Random().nextInt(neighborLst.size());
            int newInt = (int) neighborLst.get(rnd);
            if (!arrInt.contains(newInt))
                arrInt.add(newInt);
        }*/
    }

    private List shuffleLetterBoggleStyle(int i) {
//        if(arrInt.size() < 10) {
            Log.e("test boggle shuffle", "array element " + i);
            List neighborLst = getNeighbors(i);
            /*int rnd = new Random().nextInt(neighborLst.size());
            int newInt = (int) neighborLst.get(rnd);
            if (!arrInt.contains(newInt)) {
                arrInt.add(newInt);
                shuffleLetterBoggleStyle(newInt);
            } else {
                shuffleLetterBoggleStyle(randomInt);
            }*/
            if (neighborLst.size() > 0 && arrInt.size() < 10) {
                int rnd = new Random().nextInt(neighborLst.size());
                int newInt = (int) neighborLst.get(rnd);
                if (!arrInt.contains(newInt)) {
                    arrInt.add(newInt);
                    shuffleLetterBoggleStyle(newInt);
                } else {
                    shuffleLetterBoggleStyle(i);
                }
            }
            shuffleLetterBoggleStyle(randomInt);
//        }
        return arrInt;
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

    private String removeLastChar(String str) {
        Log.e("removeLastChar", "in method call" + String.valueOf(str.length()));
        if (str.length() > 0) {
            str = str.substring(0, str.length() - 1);
        }
        Log.e("removeLastChar", "exiting method call" + String.valueOf(str.length()));
        return str;
    }

    private int getScore(char c) {
        int score = 0;
        if (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u' || c == 'n' ||
                c == 'l' || c == 'r' || c == 's' || c == 't') {
            score = 1;
        } else if (c == 'd' || c == 'g') {
            score = 2;
        } else if (c == 'b' || c == 'c' || c == 'm' || c == 'p') {
            score = 3;
        } else if (c == 'f' || c == 'h' || c == 'v' || c == 'w' || c == 'y') {
            score = 4;
        } else if (c == 'k') {
            score = 5;
        } else if (c == 'j' || c == 'x') {
            score = 8;
        } else if (c == 'q' || c == 'z') {
            score = 10;
        }
        return score;
    }

    private Void makeWord(String str, int i) {
//        word = word.concat(String.valueOf(smallTile.getInnerText()));
//        Log.e("wordAddTestTest in 1", str);
        if (i == mLastLarge) {
            word = word.concat(str);
//            Log.e("wordAddTestTest out 1", word);
            if (wordMadeList[i] != null && wordMadeList[i] != "" && !wordMadeList[i].equals("null")) {
                if (word.length() == 1) {
                    word = wordMadeList[i].concat(word);
//                    Log.e("wordAddTestTest out 3", word);
                }
                wordMadeList[i] = word;
            } else {
                wordMadeList[i] = word;
            }
//            Log.e("wordAddTestTest out 2", wordMadeList[i]);
        } else {
            word = "".concat(str);
//            Log.e("wordAddTestTest out 1", word);
            if (wordMadeList[i] != null && wordMadeList[i] != "" && !wordMadeList[i].equals("null")) {
                word = wordMadeList[i].concat(word);
                wordMadeList[i] = word;
            } else {
                wordMadeList[i] = word;
            }
//            Log.e("wordAddTestTest out 2", wordMadeList[i]);
        }
        return null;
    }

    private String makeWord(String str) {
//        Log.e("wordAddTestTest p2", str);
        word2 = word2.concat(str);
//        Log.e("wordAddTestTest p2", word2);
        return word2;
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

    protected int getWordCount1() {
        int count = 0;
        for(int i = 0; i < wordMadeList.length; i++) {
            if(wordMadeList[i] != null && !wordMadeList[i].equals("") && !wordMadeList[i].equals("null")) {
                Log.e("getWordCount1", String.valueOf(i) + " : " +wordMadeList[i]);
                count++;
            }
            Log.e("getWordCount1", String.valueOf(count));
        }
        return count;
    }

    public char[][] getGameLetterState() {
        return gameLetterState;
    }

    public void reshuffleLettersOnShake(){
        resultList.clear();
        setLettersOnBoard();
        initAddLetters(mView);
    }

    public String[] getBoggledWords() {
        return boggledWords;
    }

    public String[] getBoggledWordsRetrieved() {
        return boggledWordsRetrieved;
    }

    public String[] getSelectedWordsMade() {
        return selectedWordsMade;
    }
}

