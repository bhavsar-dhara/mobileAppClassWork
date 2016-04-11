package edu.neu.madcourse.dharabhavsar.ui.scraggle;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import edu.neu.madcourse.dharabhavsar.R;

public class ScraggleTile {

    private ScraggleGameFragment mGame;
    private View mView;
    private ScraggleTile mSubTiles[];
    private Boolean isSelected; // for both the phases
    private Boolean isBlank; // for phase 2
    private String innerText;

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

    public void animation1(View tile) {
        Animation animation1 = AnimationUtils.loadAnimation(mGame.getActivity(), R.anim.gametimer);
        tile.startAnimation(animation1);
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
