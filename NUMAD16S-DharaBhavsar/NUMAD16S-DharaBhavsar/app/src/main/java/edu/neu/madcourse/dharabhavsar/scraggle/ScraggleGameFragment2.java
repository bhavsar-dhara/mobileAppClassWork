package edu.neu.madcourse.dharabhavsar.scraggle;

import android.app.Fragment;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.neu.madcourse.dharabhavsar.main.R;

/**
 * Created by Dhara on 2/26/2016.
 */
public class ScraggleGameFragment2 extends Fragment {
    private int mSoundX, mSoundO, mSoundMiss, mSoundRewind;
    private SoundPool mSoundPool;
    private float mVolume = 1f;
    private Vibrator v;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retain this fragment across configuration changes.
        setRetainInstance(true);

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
}
