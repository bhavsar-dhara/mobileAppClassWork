package edu.neu.madcourse.dharabhavsar.ui.communication2player;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.view.View;
import android.widget.Button;

import edu.neu.madcourse.dharabhavsar.R;

public class ScraggleTile2 {

    private ScraggleGameFragment2 mGame;
    private ScraggleGameFragment2Combine mGameCombine;
    private View mView;
    private ScraggleTile2 mSubTiles[];
    private Boolean isSelected; // for both the phases
    private Boolean isBlank; // for phase 2
    private String innerText;

    public ScraggleTile2(ScraggleGameFragment2 game) {
        this.mGame = game;
        this.isSelected = false; // none of the tiles are selected initially
        this.isBlank = false; // none of the tiles are blank initially - required to drop
                              // unselected letters for phase 2
    }

    public ScraggleTile2(ScraggleGameFragment2Combine game) {
        this.mGameCombine = game;
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

    public ScraggleTile2[] getSubTiles() {
        return mSubTiles;
    }

    public void setSubTiles(ScraggleTile2[] subTiles) {
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

    public void animateCombine() {
        Animator anim = AnimatorInflater.loadAnimator(mGameCombine.getActivity(),
                R.animator.tictactoe);
        if (getView() != null) {
            anim.setTarget(getView());
            anim.start();
        }
    }

    public void updateDrawableState() {
        if (mView == null) return;
        boolean isSelFlag = getIsSelected();
        if (mView.getBackground() != null) {
            if(isSelFlag)
                mView.getBackground().setLevel(R.drawable.tile_selected_scraggle);
            else
                mView.getBackground().setLevel(R.drawable.tile_not_selected_scraggle);
        }
        if (mView instanceof Button) {
            mView.getBackground().setLevel(R.drawable.tile_deselected_scraggle);
            /*if (isSelFlag)
                mView.setBackgroundDrawable(getResources().getDrawable(R.drawable.tile_selected_scraggle));
            else
                mView.setBackgroundDrawable(getResources().getDrawable(R.drawable.tile_not_selected_scraggle));*/
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

    public String getInnerText() {
        return innerText;
    }

    public void setInnerText(String innerText) {
        this.innerText = innerText;
    }
}
