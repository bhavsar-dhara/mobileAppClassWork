package edu.neu.madcourse.dharabhavsar.scraggle;

import android.app.Fragment;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import edu.neu.madcourse.dharabhavsar.main.R;

public class ScraggleGameFragment extends Fragment {
    static private int mLargeIdList[] = {R.id.large1, R.id.large2, R.id.large3,
            R.id.large4, R.id.large5, R.id.large6, R.id.large7, R.id.large8,
            R.id.large9,};
    static private int mSmallIdList[] = {R.id.small1, R.id.small2, R.id.small3,
            R.id.small4, R.id.small5, R.id.small6, R.id.small7, R.id.small8,
            R.id.small9,};
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
    List<List<Integer>> resultList = new ArrayList<List<Integer>>();
    List<String> stringLst;
    Vibrator v;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retain this fragment across configuration changes.
        setRetainInstance(true);
        initGame();

//        Method call to the asyncTaskRunner
        stringLst = ((ScraggleGameActivity) getActivity()).methodCallToAsyncTaskRunner();
        setLettersOnBoard(stringLst);

        mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        mSoundX = mSoundPool.load(getActivity(), R.raw.shnur_drum_freesound_org, 1);
        mSoundO = mSoundPool.load(getActivity(), R.raw.shnur_drum_freesound_org, 1);
        mSoundMiss = mSoundPool.load(getActivity(), R.raw.shnur_drum_freesound_org, 1);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.large_board_scraggle, container, false);
        initViews(rootView);
        updateAllTiles();
        return rootView;
    }

    private void initViews(View rootView) {
        mEntireBoard.setView(rootView);
        for (int large = 0; large < 9; large++) {
            View outer = rootView.findViewById(mLargeIdList[large]);
            mLargeTiles[large].setView(outer);

            List<Integer> posnList = resultList.get(large);
            String str = stringLst.get(large);
//            Log.e("nineWords", str);

            /*for (int small = 0; small < 9; small++) {
                int i = posnList.get(small);
                Button innerText = (Button) outer.findViewById
                        (mSmallIdList[i]);
                innerText.setText(String.valueOf(str.charAt(small)));
            }*/

            for (int small = 0; small < 9; small++) {
                int i = posnList.get(small);
                Button innerText = (Button) outer.findViewById
                        (mSmallIdList[i]);
                innerText.setText(String.valueOf(str.charAt(small)));

                final ScraggleTile smallTileText = mSmallTiles[large][i];
                smallTileText.setInnerText(String.valueOf(str.charAt(small)));

                Button inner = (Button) outer.findViewById
                        (mSmallIdList[small]);
                final int fLarge = large;
                final int fSmall = small;
                final ScraggleTile smallTile = mSmallTiles[large][small];
                smallTile.setView(inner);
                // ...
                inner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        smallTile.animate();
                        // ...
                        if (isAvailable(smallTile)) {
                            ((ScraggleGameActivity) getActivity()).startThinking();
                            mSoundPool.play(mSoundX, mVolume, mVolume, 1, 0, 1f);
                            // Vibrate for 500 milliseconds
                            v.vibrate(500);
//                            makeMove(fLarge, fSmall);
                            think();
                        } else {
                            mSoundPool.play(mSoundMiss, mVolume, mVolume, 1, 0, 1f);
                        }
                    }
                });
                // ...
            }
        }
    }

    private void think() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getActivity() == null) return;

                ((ScraggleGameActivity) getActivity()).stopThinking();
            }
        }, 1000);
    }

    public void restartGame() {
        mSoundPool.play(mSoundRewind, mVolume, mVolume, 1, 0, 1f);
        // ...
        initGame();
        initViews(getView());
        updateAllTiles();
    }

    public void initGame() {
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
    }

    private void setAvailableFromLastMove(int small) {
        clearAvailable();
        // Make all the tiles at the destination available
        if (small != -1) {
            for (int dest = 0; dest < 9; dest++) {
                ScraggleTile tile = mSmallTiles[small][dest];
//                if (tile.getOwner() == ScraggleTile.Owner.NEITHER)
                    addAvailable(tile);
            }
        }
        // If there were none available, make all squares available
        if (mAvailable.isEmpty()) {
            setAllAvailable();
        }
    }

    private void setAllAvailable() {
        for (int large = 0; large < 9; large++) {
            for (int small = 0; small < 9; small++) {
                ScraggleTile tile = mSmallTiles[large][small];
//                if (tile.getOwner() == ScraggleTile.Owner.NEITHER)
                    addAvailable(tile);
            }
        }
    }

    private void updateAllTiles() {
//        mEntireBoard.updateDrawableState();
        for (int large = 0; large < 9; large++) {
//            mLargeTiles[large].updateDrawableState();
            for (int small = 0; small < 9; small++) {
//                mSmallTiles[large][small].updateDrawableState();
            }
        }
    }

    /**
     * Create a string containing the state of the game.
     */
    public String getState() {
        StringBuilder builder = new StringBuilder();
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
        }
        return builder.toString();
    }

    /**
     * Restore the state of the game from the given string.
     */
    public void putState(String gameData) {
        String[] fields = gameData.split(",");
        int index = 0;
        mLastLarge = Integer.parseInt(fields[index++]);
        mLastSmall = Integer.parseInt(fields[index++]);
        for (int large = 0; large < 9; large++) {
            for (int small = 0; small < 9; small++) {
                String innerText = (fields[index++]);
                mSmallTiles[large][small].setInnerText(innerText);
                Boolean isSelected = Boolean.valueOf(fields[index++]);
                mSmallTiles[large][small].setIsSelected(isSelected);
            }
        }
        setAvailableFromLastMove(mLastSmall);
        updateAllTiles();
    }

    public void setLettersOnBoard(List<String> stringList) {
        List<List<Integer>> myList = new ArrayList<List<Integer>>();
        myList.addAll(Arrays.asList(Arrays.asList(0, 1, 4, 6, 3, 7, 8, 5, 2),
                Arrays.asList(8, 4, 0, 3, 6, 7, 5, 2, 1),
                Arrays.asList(8, 7, 6, 3, 0, 1, 5, 4, 2),
                Arrays.asList(3, 7, 6, 4, 8, 5, 2, 1, 0),
                Arrays.asList(5, 4, 8, 7, 6, 3, 0, 1, 2),
                Arrays.asList(5, 7, 8, 4, 6, 3, 0, 2, 1),
                Arrays.asList(7, 8, 5, 4, 6, 3, 0, 2, 1),
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
//            Log.e("Random position", String.valueOf(rnd));
//            Log.e("Random position", myList.get(rnd).toString());
            resultList.add(myList.get(rnd));
        }
    }

    public List<Integer> getNeighbors(Integer i) {
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
}

