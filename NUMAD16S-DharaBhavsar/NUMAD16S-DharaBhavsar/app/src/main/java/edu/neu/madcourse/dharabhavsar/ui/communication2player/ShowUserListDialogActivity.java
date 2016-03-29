package edu.neu.madcourse.dharabhavsar.ui.communication2player;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import edu.neu.madcourse.dharabhavsar.model.communication.UserData;
import edu.neu.madcourse.dharabhavsar.ui.main.R;
import edu.neu.madcourse.dharabhavsar.utils.Constants;
import edu.neu.madcourse.dharabhavsar.utils.firebaseconn.RemoteClient;
import edu.neu.madcourse.dharabhavsar.utils.gcmcomm.CommunicationMain;

/**
 * Created by Dhara on 3/27/2016.
 */
public class ShowUserListDialogActivity extends Activity {

    private static final String LOG_TAG = "UserListDialogActivity";
    private Context appContext = this;
    private RemoteClient mRemoteClient;
    private static HashMap<String, UserData> fireBaseAllUserList = new HashMap<String, UserData>();
    private Handler mHandler = new Handler();
    private SharedPreferences prefs;
    private String userKey;
    private CommunicationMain mCommMain = new CommunicationMain();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(LOG_TAG, "showing dialog!");
        mRemoteClient = new RemoteClient(appContext);
        prefs = appContext.getSharedPreferences(RemoteClient.class.getSimpleName(), Context.MODE_PRIVATE);
        userKey = prefs.getString(Constants.USER_UNIQUE_KEY, "");

        fireBaseAllUserList = mRemoteClient.fetchAllUsers(Constants.USER_DATA, userKey);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String[] keys = new String[fireBaseAllUserList.size()];
                UserData[] values = new UserData[fireBaseAllUserList.size()];
                String[] userNameList = new String[fireBaseAllUserList.size()];
                int index = 0;
                for (Map.Entry<String, UserData> mapEntry : fireBaseAllUserList.entrySet()) {
                    keys[index] = mapEntry.getKey();
                    values[index] = mapEntry.getValue();
                    userNameList[index] = mapEntry.getValue().getUserName();
                    index++;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(ShowUserListDialogActivity.this,
                        AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                // Set the dialog title
                builder.setTitle(R.string.select_team_mate)
                            .setItems(userNameList, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // The 'which' argument contains the index position
                                    // of the selected item
                                    // TODO
                                }
                            });
                builder.create().show();
            }
        }, 5000);
        /*Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.select_dialog_user_list);
        dialog.setTitle("Please select a team mate to play with...");
        dialog.setCancelable(true);
//        dialog.setCanceledOnTouchOutside(true);
        TextView text = (TextView) dialog.findViewById(R.id.text1);
        text.setText("Message");

        dialog.show();
        //
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            public void onCancel(DialogInterface arg0) {
                finish();
            }

        });*/
    }
}
