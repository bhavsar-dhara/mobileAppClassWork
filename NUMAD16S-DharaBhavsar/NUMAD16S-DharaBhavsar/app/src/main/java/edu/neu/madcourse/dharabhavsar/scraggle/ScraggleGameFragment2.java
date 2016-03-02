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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retain this fragment across configuration changes.
        setRetainInstance(true);
        initGame();

        mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        mSoundX = mSoundPool.load(getActivity(), R.raw.shnur_drum_freesound_org, 1);
        mSoundO = mSoundPool.load(getActivity(), R.raw.shnur_drum_freesound_org, 1);
        mSoundMiss = mSoundPool.load(getActivity(), R.raw.bertrof_game_sound_wrong_freesound_org, 1);
        mSoundRewind = mSoundPool.load(getActivity(), R.raw.shnur_drum_freesound_org, 1);
        v = (Vibrator) this.getActivity().getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.large_board_scraggle, container, false);

        return rootView;
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
//        setAvailableFromLastMove(mLastSmall);
//        setNextPossibleMoveFromLastMove(mLastSmall, mLastLarge);
    }

    /**
     * Create a string containing the state of the game.
     */
    public String getState() {
        Log.e("SAVING GAME STATE", "in");
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
//        setAvailableFromLastMove(mLastSmall);
//        updateAllTiles();
        Log.e("RESTORING GAME STATE", "out");
    }
}
