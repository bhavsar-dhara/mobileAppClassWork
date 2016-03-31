package edu.neu.madcourse.dharabhavsar.ui.communication2player;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

import edu.neu.madcourse.dharabhavsar.ui.main.R;

/**
 * Created by Dhara on 3/30/2016.
 */
public class MySwipingEvent extends GridLayout {

//    private int mTouchSlop;
    private GridLayout touchview;
    private static int defaultStates[];
    private Button mLastButton;
    private final static int[] STATE_PRESSED = {
            android.R.attr.state_pressed,
            android.R.attr.state_focused
                    | android.R.attr.state_enabled};

    public MySwipingEvent(Context context) {
        super(context);
        touchview = (GridLayout) findViewById(R.id.wgboard_small);
    }

    public MySwipingEvent(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        /*ViewConfiguration vc = ViewConfiguration.get(touchview.getContext());
        mTouchSlop = vc.getScaledTouchSlop();*/

        int x = (int) ev.getX();
        int y = (int) ev.getY();

        Log.e("Touch testing", "X = " + x);
        Log.e("Touch testing", "Y = " + y);

        Log.e("Touch testing", "getChildCount = " + touchview.getChildCount());
        for (int i = 0; i < touchview.getChildCount(); i++) {
            View current = touchview.getChildAt(i);
            Log.e("Touch testing", "inside for");
            if (current instanceof Button) {
                Button b = (Button) current;
                Log.e("Touch testing", "inside if");
                if (!isPointWithin(x, y, b.getLeft(), b.getRight(), b.getTop(),
                        b.getBottom())) {
                    b.getBackground().setState(defaultStates);
                    Log.e("Touch testing", "inside if if");
                }
                if (isPointWithin(x, y, b.getLeft(), b.getRight(), b.getTop(),
                        b.getBottom())) {
                    Log.e("Touch testing", "inside fif if");
                    b.getBackground().setState(STATE_PRESSED);
                    if (b != mLastButton) {
                        Log.e("Touch testing", "inside if if if");
                        mLastButton = b;
                        Log.e("Touch testing", "inside if if if" + b.getText());
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return true;
    }

    static boolean isPointWithin(int x, int y, int x1, int x2, int y1, int y2) {
        return (x <= x2 && x >= x1 && y <= y2 && y >= y1);
    }
}
