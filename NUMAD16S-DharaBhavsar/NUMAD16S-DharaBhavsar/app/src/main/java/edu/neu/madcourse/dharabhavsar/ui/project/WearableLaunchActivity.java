package edu.neu.madcourse.dharabhavsar.ui.project;

import android.content.IntentSender;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Collection;
import java.util.HashSet;

import edu.neu.madcourse.dharabhavsar.ui.main.R;

public class WearableLaunchActivity extends AppCompatActivity implements
        MessageApi.MessageListener, NodeApi.NodeListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_RESOLVE_ERROR = 1000;

    private static final String START_TRICKIEST_PART = "/start-activity-trickiest-part";
    private static final String START_FINAL_PROJECT = "/start-activity-final-project";

    private static final String TAG = "WearableLaunchActivity";
    private boolean finalProject = false;
    private TextView appDescription;
    private Button launchButton;
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wearable_launch);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        appDescription = (TextView) findViewById(R.id.project_description);
        launchButton = (Button) findViewById(R.id.launch_on_android_wear);

        finalProject = getIntent().getBooleanExtra("finalProject", false);
        if(finalProject) {
            myToolbar.setTitle(R.string.final_project_label);
            appDescription.setText(R.string.final_project_description);
        }
        else {
            myToolbar.setTitle(R.string.trickiest_part_label);
            appDescription.setText(R.string.trickiest_part_description);
        }

        myToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(myToolbar);

        snackbar = Snackbar.make(findViewById(R.id.project_root),
                R.string.wearable_not_connected, Snackbar.LENGTH_INDEFINITE);
        //snackbar.show();
        //launchButton.setVisibility(View.GONE);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (!mResolvingError) {
            Wearable.MessageApi.removeListener(mGoogleApiClient, this);
            Wearable.NodeApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "onConnected");
        mResolvingError = false;
        launchButton.setEnabled(true);
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
        Wearable.NodeApi.addListener(mGoogleApiClient, this);
    }

    @Override //ConnectionCallbacks
    public void onConnectionSuspended(int cause) {
        Log.e(TAG, "Connection to Google API client was suspended");
        launchButton.setEnabled(false);
    }

    @Override //OnConnectionFailedListener
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.i(TAG, "onConnectionFailed");
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            Log.e(TAG, "Connection to Google API client has failed");
            mResolvingError = false;
            launchButton.setEnabled(false);
            Wearable.MessageApi.removeListener(mGoogleApiClient, this);
            Wearable.NodeApi.removeListener(mGoogleApiClient, this);
        }
    }


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

    }

    @Override
    public void onPeerConnected(Node node) {
        Log.i(TAG, "Peer Connected");
        snackbar.dismiss();
        launchButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPeerDisconnected(Node node) {
        Log.i(TAG, "Peer Disconnected");
        snackbar.show();
        launchButton.setVisibility(View.GONE);
    }

    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<>();
        NodeApi.GetConnectedNodesResult nodes =
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();

        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }

        return results;
    }

    private void sendStartActivityMessage(String node) {
        String path = "";
        if(finalProject)
            path = START_FINAL_PROJECT;
        else
            path = START_TRICKIEST_PART;
        Wearable.MessageApi.sendMessage(
                mGoogleApiClient, node, path, "Start".getBytes()).setResultCallback(
                new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                        if (!sendMessageResult.getStatus().isSuccess()) {
                            Log.e(TAG, "Failed to send message with status code: "
                                    + sendMessageResult.getStatus().getStatusCode());
                        }
                    }
                }
        );
    }

    private class StartWearableActivityTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... args) {
            Collection<String> nodes = getNodes();
            for (String node : nodes) {
                sendStartActivityMessage(node);
            }
            Log.i(TAG, "Nodes are -"+nodes.isEmpty() + nodes);
            return null;
        }
    }

    /**
     * Sends an RPC to start a fullscreen Activity on the wearable.
     */
    public void onStartWearableActivityClick(View view) {
        Log.i(TAG, "Generating RPC");

        // Trigger an AsyncTask that will query for a list of connected nodes and send a
        // "start-activity" message to each connected node.
        new StartWearableActivityTask().execute();
    }



}
