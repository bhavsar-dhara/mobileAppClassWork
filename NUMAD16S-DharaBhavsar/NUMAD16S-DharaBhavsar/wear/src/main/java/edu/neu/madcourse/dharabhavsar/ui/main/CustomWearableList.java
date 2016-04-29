package edu.neu.madcourse.dharabhavsar.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.wearable.view.WearableListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Dhara on 4/28/2016.
 */
public class CustomWearableList extends Activity {

    private static final String TAG = "CustomWearableList";
    private static final int REQUEST_CODE = 1;
    private static ArrayList<Integer> mIcons;
    private TextView mHeader;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_manual_duration);

        sp = PreferenceManager.getDefaultSharedPreferences(this);

        // Sample icons for the list
        mIcons = new ArrayList<Integer>();
        for(int i = 0; i < 61; i++)
            mIcons.add(R.drawable.ic_timer_white_24dp);
        /*mIcons.add(R.drawable.ic_action_attach);
        mIcons.add(R.drawable.ic_action_call);
        mIcons.add(R.drawable.ic_action_locate);
        mIcons.add(R.drawable.ic_action_mail);
        mIcons.add(R.drawable.ic_action_microphone);
        mIcons.add(R.drawable.ic_action_photo);
        mIcons.add(R.drawable.ic_action_star);
        mIcons.add(R.drawable.ic_action_user);
        mIcons.add(R.drawable.ic_action_video);*/

        // This is our list header
        mHeader = (TextView) findViewById(R.id.header);

        String noOfSeconds = sp.getString(Constants.manualDurationSet, getString(R.string._30));
//        Log.i(TAG, "noOfSeconds = " + noOfSeconds);

        WearableListView wearableListView =
                (WearableListView) findViewById(R.id.wearable_List);
        wearableListView.setAdapter(new WearableAdapter(this, mIcons));
        wearableListView.setClickListener(mClickListener);
        wearableListView.addOnScrollListener(mOnScrollListener);
        wearableListView.scrollToPosition(Integer.parseInt(noOfSeconds)-1);
    }

    // Handle our Wearable List's click events
    private WearableListView.ClickListener mClickListener =
            new WearableListView.ClickListener() {
                @Override
                public void onClick(WearableListView.ViewHolder viewHolder) {
                    /*Toast.makeText(CustomWearableList.this,
                            String.format("You selected item #%s",
                                    viewHolder.getLayoutPosition()+1),
                            Toast.LENGTH_SHORT).show();*/
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", String.valueOf(viewHolder.getLayoutPosition()+1));
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }

                @Override
                public void onTopEmptyRegionClick() {
                    /*Toast.makeText(CustomWearableList.this,
                            "Top empty area tapped", Toast.LENGTH_SHORT).show();*/
                    //do nothing
                }
            };

    // The following code ensures that the title scrolls as the user scrolls up
    // or down the list
    private WearableListView.OnScrollListener mOnScrollListener =
            new WearableListView.OnScrollListener() {
                @Override
                public void onAbsoluteScrollChange(int i) {
                    // Only scroll the title up from its original base position
                    // and not down.
                    if (i > 0) {
                        mHeader.setY(-i);
                    }
                }

                @Override
                public void onScroll(int i) {
                    // Placeholder
                }

                @Override
                public void onScrollStateChanged(int i) {
                    // Placeholder
                }

                @Override
                public void onCentralPositionChanged(int i) {
                    // Placeholder
                }
            };
}