package edu.neu.madcourse.dharabhavsar.ui.communication;

//import android.app.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map;
import java.util.TreeMap;

import edu.neu.madcourse.dharabhavsar.ui.main.R;
import edu.neu.madcourse.dharabhavsar.utils.Constants;
import edu.neu.madcourse.dharabhavsar.utils.firebaseconn.RemoteClient;

/**
 * Created by Dhara on 3/17/2016.
 */
public class Tab1Fragment extends Fragment {

    private static final String TAG = "Tab1Fragment";
    final Handler handler = new Handler();
    private RemoteClient remoteClient;

    private Timer timer;
    private TimerTask timerTask;

    private HashMap<Integer, String> combatScoreList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        remoteClient = new RemoteClient(getActivity());

        TextView tv = new TextView(getActivity());
        tv.setTextSize(25);
        tv.setGravity(Gravity.CENTER_VERTICAL);
        tv.setText("This Is Tab1 Activity");
//        setContentView(tv);

        remoteClient.fetchCombatScoreBoardData(Constants.USER_DATA, Constants.USER_KEY);

        // any polling mechanism can be used
        startTimer(Constants.USER_KEY);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            // We have different layouts, and in one of them this
            // fragment's containing frame doesn't exist.  The fragment
            // may still be created from its saved state, but there is
            // no reason to try to create its view hierarchy because it
            // won't be displayed.  Note this is not needed -- we could
            // just run the code below, where we would create and return
            // the view hierarchy; it would just never be used.
            return null;
        }

        View rootView = inflater.inflate(R.layout.tab1_layout, container, false);

        // Get ListView object from xml
        final ListView listView = (ListView) rootView.findViewById(R.id.listView);

        // Create a new Adapter
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1);

        // Assign adapter to ListView
        listView.setAdapter(adapter);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Map<Integer, String> treeMap = new TreeMap<Integer, String>(
                        new Comparator<Integer>() {

                            @Override
                            public int compare(Integer o1, Integer o2) {
                                return o2.compareTo(o1);
                            }

                        });

                treeMap.putAll(combatScoreList);
                int i = 0;

                for (Map.Entry<Integer, String> entry : treeMap.entrySet()) {
                    adapter.add(String.valueOf(++i) + " " + entry.getValue() + " - " + entry.getKey());
                    if (i > 4) {
                        break;
                    }
                }

            }
        }, 5000);

        return rootView;
    }

    public void startTimer(String key) {
        //set a new Timer
        timer = new Timer();
        //initialize the TimerTask's job
        initializeTimerTask(key);
        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        // The values can be adjusted depending on the performance
        timer.schedule(timerTask, 5000, 1000);
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask(final String key) {
        timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {

                    public void run() {
                        combatScoreList = remoteClient.getCombatScore(key);
                        Log.d(TAG, "Value >>>> " + combatScoreList.size());
                    }
                });
                stoptimertask();
            }
        };
    }
}
