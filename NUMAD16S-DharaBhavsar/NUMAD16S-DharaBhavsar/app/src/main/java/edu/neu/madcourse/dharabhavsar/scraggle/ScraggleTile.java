package edu.neu.madcourse.dharabhavsar.scraggle;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.view.View;

import edu.neu.madcourse.dharabhavsar.main.R;

public class ScraggleTile {

    private final ScraggleGameFragment mGame;
    private View mView;
    private ScraggleTile mSubTiles[];
    private Boolean isSelected; // for both the phases
    private Boolean isBlank; // for phase 2

    public ScraggleTile(ScraggleGameFragment game) {
        this.mGame = game;
        this.isSelected = false; // none of the tiles are selected initially
        this.isBlank = false; // none of the tiles are blank initially - required to drop
                              // unselected letters for phase 2
    }

    public View getView() {
        return mView;
    }

    public void setView(View view) {
        this.mView = view;
    }

    public ScraggleTile[] getSubTiles() {
        return mSubTiles;
    }

    public void setSubTiles(ScraggleTile[] subTiles) {
        this.mSubTiles = subTiles;
    }

    public void animate() {
        Animator anim = AnimatorInflater.loadAnimator(mGame.getActivity(),
                R.animator.tictactoe);
        if (getView() != null) {
            anim.setTarget(getView());
            anim.start();
        }
    }

    public Boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }

    public Boolean getIsBlank() {
        return isBlank;
    }

    public void setIsBlank(Boolean isBlank) {
        this.isBlank = isBlank;
    }
}
