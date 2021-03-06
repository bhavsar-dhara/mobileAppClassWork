package edu.neu.madcourse.dharabhavsar.utils.gcmcomm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.neu.madcourse.dharabhavsar.model.communication.UserData;
import edu.neu.madcourse.dharabhavsar.ui.main.R;
import edu.neu.madcourse.dharabhavsar.utils.Constants;
import edu.neu.madcourse.dharabhavsar.utils.firebaseconn.RemoteClient;

public class CommunicationMain extends Activity implements OnClickListener {

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    public static final String PROPERTY_ALERT_TEXT = "alertText";
    public static final String PROPERTY_TITLE_TEXT = "titleText";
    public static final String PROPERTY_CONTENT_TEXT = "contentText";
    public static final String PROPERTY_NTYPE = "nType";

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    static final String TAG = "GCM Sample Demo";
    TextView mDisplay;
    EditText mMessage;
    GoogleCloudMessaging gcm;
    Context context;
    String regid;
    UserData mUserData;

    private Context appContext = this;
    private RemoteClient mRemoteClient;
    private static HashMap<String, UserData> fireBaseAllUserList = new HashMap<String, UserData>();
    private Handler mHandler = new Handler();
    private SharedPreferences prefs;
    private String userKey;
    private SharedPreferences prefsRemote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.communication_main);
        mDisplay = (TextView) findViewById(R.id.communication_display);
        mMessage = (EditText) findViewById(R.id.communication_edit_message);
        gcm = GoogleCloudMessaging.getInstance(this);
        context = getApplicationContext();

        prefsRemote = appContext.getSharedPreferences(RemoteClient.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    @SuppressLint("NewApi")
    public String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.d(TAG, "Registration not found.");
            return "";
        }
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
                Integer.MIN_VALUE);
        Log.d(TAG, String.valueOf(registeredVersion));
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.d(TAG, "App version changed.");
            return "";
        }
        Log.e(TAG, "RegistrationID found : " + registrationId);
        return registrationId;
    }

    private SharedPreferences getGCMPreferences(Context context) {
        return context.getSharedPreferences(CommunicationMain.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private static void setRegisterValues() {
        CommunicationConstants.alertText = CommunicationConstants.regAlertText;
        CommunicationConstants.titleText = "Register";
        CommunicationConstants.contentText = "Registering Successful!";
    }

    private static void setUnregisterValues() {
        CommunicationConstants.alertText = CommunicationConstants.unregAlertText;
        CommunicationConstants.titleText = "Unregister";
        CommunicationConstants.contentText = "Unregistering Successful!";
    }

    private static void setSendMessageValues(String msg) {
        CommunicationConstants.alertText = CommunicationConstants.msgAlertText;
        CommunicationConstants.titleText = "Sending Message";
        CommunicationConstants.contentText = msg;
    }

    private static void setCombatGameRequestValues(String msg) {
        CommunicationConstants.alertText = CommunicationConstants.combatAlertText;
        CommunicationConstants.titleText = "New Combat Game Request";
        CommunicationConstants.contentText = "default msg - " + msg;
    }

    private static void setCombineGameRequestValues(String msg) {
        CommunicationConstants.alertText = CommunicationConstants.combatAlertText;
        CommunicationConstants.titleText = "New Combine Game Request";
        CommunicationConstants.contentText = "default msg - " + msg;
    }


	private void registerInBackgroundTest() {
        new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
                    setRegisterValues();
					regid = gcm.register(CommunicationConstants.GCM_SENDER_ID);
                    // implementation to store and keep track of registered devices here
                    msg = "Device registered, registration ID=" + regid;
					sendRegistrationIdToBackend();
					storeRegistrationId(context, regid);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				mDisplay.append(msg + "\n");
			}
		}.execute(null, null, null);
	}

    private void sendRegistrationIdToBackend() {
        // Your implementation here.
    }

    private void registerInBackground(final Context context1, final String userName) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context1);
                    }
                    setRegisterValues();
                    regid = gcm.register(CommunicationConstants.GCM_SENDER_ID);
                    Log.e(TAG, "User GCM Id is : " + regid);
                    storeRegistrationId(context1, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                mRemoteClient = new RemoteClient(context1);
                Log.e(TAG, "User GCM Id is : " + regid);
                mUserData = new UserData(regid, userName, 0, 0, "", false, "", false, 0, 0, "", "");
                mRemoteClient.saveUserData(mUserData);
            }
        }.execute(null, null, null);
    }

    public void sendRegistrationIdToBackend(Context context, String userName) {
        registerInBackground(context, userName);
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.d(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.d(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onClick(final View view) {
        if (view == findViewById(R.id.communication_send)) {
            String message = ((EditText) findViewById(R.id.communication_edit_message))
                    .getText().toString();
            if (message != "") {
                sendMessage(message);
            } else {
                Toast.makeText(context, "Sending Context Empty!",
                        Toast.LENGTH_LONG).show();
            }
        } else if (view == findViewById(R.id.communication_clear)) {
            mMessage.setText("");
        } else if (view == findViewById(R.id.communication_unregistor_button)) {
            unregister();
        } else if (view == findViewById(R.id.communication_registor_button)) {
            if (checkPlayServices()) {
                regid = getRegistrationId(context);
                if (TextUtils.isEmpty(regid)) {
					registerInBackgroundTest();
                }
            }
        } else if (view == findViewById(R.id.communication_send_to)) {
            mRemoteClient = new RemoteClient(appContext);
            prefs = appContext.getSharedPreferences(RemoteClient.class.getSimpleName(), Context.MODE_PRIVATE);
            userKey = prefs.getString(Constants.USER_UNIQUE_KEY, "");
            fireBaseAllUserList = mRemoteClient.fetchAllUsers(Constants.USER_DATA, userKey);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (checkPlayServices()) {
                        String[] keys = new String[fireBaseAllUserList.size()];
                        UserData[] values = new UserData[fireBaseAllUserList.size()];
                        String[] userNameList = new String[fireBaseAllUserList.size()];
                        final String[] userRegIdList = new String[fireBaseAllUserList.size()];
                        int index = 0;
                        for (Map.Entry<String, UserData> mapEntry : fireBaseAllUserList.entrySet()) {
//                            keys[index] = mapEntry.getKey();
//                            values[index] = mapEntry.getValue();
                            userNameList[index] = mapEntry.getValue().getUserName();
                            userRegIdList[index] = mapEntry.getValue().getUserId();
                            index++;
                        }
                        /*Intent intent = new Intent(context, ShowUserListDialogActivity.class);
                        context.startActivity(intent);*/
                        final String[] regId = new String[1];
                        LayoutInflater inflater = LayoutInflater.from(appContext);
                        final View yourCustomView = inflater.inflate(R.layout.select_dialog_user_list, null);
                        AlertDialog dialog = new AlertDialog.Builder(appContext)
                                .setTitle(R.string.test_gcm_title)
                                .setView(yourCustomView)
                                .setItems(userNameList, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // The 'which' argument contains the index position
                                        // of the selected item
                                        // TODO
                                        Log.e(TAG, "userRegId, index = " + which);
                                        regId[0] = userRegIdList[which];
                                        Log.e(TAG, "userRegId = " + regId[0].toString());
                                        String message = ((EditText) findViewById(R.id.communication_edit_message))
                                                .getText().toString();
                                        if (message != "") {
                                            sendMessage(message, regId[0].toString());
                                        } else {
                                            Toast.makeText(context, "Sending Context Empty!",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                })
                                /*.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        regId = etName.getText().toString();
                                    }
                                })*/
                                .setNegativeButton("Cancel", null).create();
                        dialog.show();
                    }
                }
            }, 5000);
        }
    }

    protected void unregister() {
        Log.d(CommunicationConstants.TAG, "UNREGISTER USERID: " + regid);
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    msg = "Sent unregistration";
                    setUnregisterValues();
                    gcm.unregister();
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                removeRegistrationId(getApplicationContext());
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                ((TextView) findViewById(R.id.communication_display))
                        .setText(regid);
            }
        }.execute();
    }

    private void removeRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.d(CommunicationConstants.TAG, "Removing regId on app version "
                + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(PROPERTY_REG_ID);
        editor.commit();
        regid = null;
    }

    @SuppressLint("NewApi")
    public void sendMessage(final String message) {
        if (regid == null || regid.equals("")) {
            Toast.makeText(this, "You must register first", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (message.isEmpty()) {
            Toast.makeText(this, "Empty Message", Toast.LENGTH_LONG).show();
            return;
        }

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                List<String> regIds = new ArrayList<String>();
                String reg_device = regid;
                int nIcon = R.drawable.ic_launcher;
                int nType = CommunicationConstants.SIMPLE_NOTIFICATION;
                Map<String, String> msgParams;
                msgParams = new HashMap<String, String>();
                msgParams.put("data.alertText", "Notification");
                msgParams.put("data.titleText", "Notification Title");
                msgParams.put("data.contentText", message);
                msgParams.put("data.nIcon", String.valueOf(nIcon));
                msgParams.put("data.nType", String.valueOf(nType));
                setSendMessageValues(message);
                GcmNotification gcmNotification = new GcmNotification();
                regIds.clear();
                regIds.add(reg_device);
                gcmNotification.sendNotification(msgParams, regIds,
                        edu.neu.madcourse.dharabhavsar.utils.gcmcomm.CommunicationMain.this);
                msg = "sending information...";
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }.execute(null, null, null);
    }

    @SuppressLint("NewApi")
    private void sendMessage(final String message, final String regId) {
        if (regId == null || regId.equals("")) {
            Toast.makeText(this, "You must register first", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (message.isEmpty()) {
            Toast.makeText(this, "Empty Message", Toast.LENGTH_LONG).show();
            return;
        }

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                List<String> regIds = new ArrayList<String>();
                String reg_device = regId;
                int nIcon = R.drawable.ic_launcher;
                int nType = CommunicationConstants.SIMPLE_NOTIFICATION;
                Map<String, String> msgParams;
                msgParams = new HashMap<String, String>();
                msgParams.put("data.alertText", "Notification");
                msgParams.put("data.titleText", "Notification Title");
                msgParams.put("data.contentText", message);
                msgParams.put("data.nIcon", String.valueOf(nIcon));
                msgParams.put("data.nType", String.valueOf(nType));
                setSendMessageValues(message);
                GcmNotification gcmNotification = new GcmNotification();
                regIds.clear();
                regIds.add(reg_device);
                gcmNotification.sendNotification(msgParams, regIds,
                        edu.neu.madcourse.dharabhavsar.utils.gcmcomm.CommunicationMain.this);
                msg = "sending information...";
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }.execute(null, null, null);
    }

    @SuppressLint("NewApi")
    public void sendCombatGameRequest(final String message, final String regid) {
        if (regid == null || regid.equals("")) {
			/*Toast.makeText(this, "You must register first", Toast.LENGTH_LONG)
					.show();*/
            Log.e(TAG, "sendCombatGameRequest : regid is null");
            return;
        }
        if (message.isEmpty()) {
			/*Toast.makeText(this, "Empty Message", Toast.LENGTH_LONG).show();*/
            Log.e(TAG, "sendCombatGameRequest : message is null");
            return;
        }
        Log.e(TAG, "sendCombatGameRequest : regid : " + regid);
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                List<String> regIds = new ArrayList<String>();
                String reg_device = regid;
                int nIcon = R.drawable.ic_launcher;
                int nType = CommunicationConstants.SIMPLE_NOTIFICATION;
                Map<String, String> msgParams;
                msgParams = new HashMap<String, String>();
                msgParams.put("data.alertText", CommunicationConstants.combatAlertText);
                msgParams.put("data.titleText", "New Combat Game Request");
                msgParams.put("data.contentText", message);
                msgParams.put("data.nIcon", String.valueOf(nIcon));
                msgParams.put("data.nType", String.valueOf(nType));
                msgParams.put("data.gameKey", Constants.GAME_KEY);
                setCombatGameRequestValues(message);
                GcmNotification gcmNotification = new GcmNotification();
                regIds.clear();
                regIds.add(reg_device);
                gcmNotification.sendNotification(msgParams, regIds,
                        edu.neu.madcourse.dharabhavsar.utils.gcmcomm.CommunicationMain.this);
                msg = "sending information...";
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
//				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "sendCombatGameRequest : in onPostExecute");
            }
        }.execute(null, null, null);
    }

    @SuppressLint("NewApi")
    public void sendCombineGameRequest(final String message, final String regid) {
        if (regid == null || regid.equals("")) {
			/*Toast.makeText(this, "You must register first", Toast.LENGTH_LONG)
					.show();*/
            Log.e(TAG, "sendCombineGameRequest : regid is null");
            return;
        }
        if (message.isEmpty()) {
			/*Toast.makeText(this, "Empty Message", Toast.LENGTH_LONG).show();*/
            Log.e(TAG, "sendCombineGameRequest : message is null");
            return;
        }
        Log.e(TAG, "sendCombineGameRequest : regid : " + regid);
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                List<String> regIds = new ArrayList<String>();
                String reg_device = regid;
                int nIcon = R.drawable.ic_launcher;
                int nType = CommunicationConstants.SIMPLE_NOTIFICATION;
                Map<String, String> msgParams;
                msgParams = new HashMap<String, String>();
                msgParams.put("data.alertText", CommunicationConstants.combineAlertText);
                msgParams.put("data.titleText", "New Combine Game Request");
                msgParams.put("data.contentText", message);
                msgParams.put("data.nIcon", String.valueOf(nIcon));
                msgParams.put("data.nType", String.valueOf(nType));
                msgParams.put("data.gameKey", Constants.COMBINE_GAME_KEY);
                setCombineGameRequestValues(message);
                GcmNotification gcmNotification = new GcmNotification();
                regIds.clear();
                regIds.add(reg_device);
                gcmNotification.sendNotification(msgParams, regIds,
                        edu.neu.madcourse.dharabhavsar.utils.gcmcomm.CommunicationMain.this);
                msg = "sending information...";
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
//				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "sendCombineGameRequest : in onPostExecute");
            }
        }.execute(null, null, null);
    }

    @SuppressLint("NewApi")
    public void sendCombatGameOver(final String message, final String regid) {
        if (regid == null || regid.equals("")) {
			/*Toast.makeText(this, "You must register first", Toast.LENGTH_LONG)
					.show();*/
            Log.e(TAG, "sendCombatGameOver : regid is null");
            return;
        }
        if (message.isEmpty()) {
			/*Toast.makeText(this, "Empty Message", Toast.LENGTH_LONG).show();*/
            Log.e(TAG, "sendCombatGameOver : message is null");
            return;
        }
        Log.e(TAG, "sendCombatGameOver : regid : " + regid);
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                List<String> regIds = new ArrayList<String>();
                String reg_device = regid;
                int nIcon = R.drawable.ic_launcher;
                int nType = CommunicationConstants.SIMPLE_NOTIFICATION;
                Map<String, String> msgParams;
                msgParams = new HashMap<String, String>();
                msgParams.put("data.alertText", CommunicationConstants.combatAlertText);
                msgParams.put("data.titleText", "Previous Combat Game Over");
                msgParams.put("data.contentText", message);
                msgParams.put("data.nIcon", String.valueOf(nIcon));
                msgParams.put("data.nType", String.valueOf(nType));
                msgParams.put("data.gameKey", "--");
                setSendMessageValues(message);
                GcmNotification gcmNotification = new GcmNotification();
                regIds.clear();
                regIds.add(reg_device);
                gcmNotification.sendNotification(msgParams, regIds,
                        edu.neu.madcourse.dharabhavsar.utils.gcmcomm.CommunicationMain.this);
                msg = "sending information...";
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
//				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "sendCombatGameOver : in onPostExecute");
            }
        }.execute(null, null, null);
    }

    @SuppressLint("NewApi")
    public void sendCombineGameOver(final String message, final String regid) {
        if (regid == null || regid.equals("")) {
			/*Toast.makeText(this, "You must register first", Toast.LENGTH_LONG)
					.show();*/
            Log.e(TAG, "sendCombineGameOver : regid is null");
            return;
        }
        if (message.isEmpty()) {
			/*Toast.makeText(this, "Empty Message", Toast.LENGTH_LONG).show();*/
            Log.e(TAG, "sendCombineGameOver : message is null");
            return;
        }
        Log.e(TAG, "sendCombineGameOver : regid : " + regid);
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                List<String> regIds = new ArrayList<String>();
                String reg_device = regid;
                int nIcon = R.drawable.ic_launcher;
                int nType = CommunicationConstants.SIMPLE_NOTIFICATION;
                Map<String, String> msgParams;
                msgParams = new HashMap<String, String>();
                msgParams.put("data.alertText", CommunicationConstants.combineAlertText);
                msgParams.put("data.titleText", "PRevious Combine Game Over");
                msgParams.put("data.contentText", message);
                msgParams.put("data.nIcon", String.valueOf(nIcon));
                msgParams.put("data.nType", String.valueOf(nType));
                msgParams.put("data.gameKey", "--");
                setSendMessageValues(message);
                GcmNotification gcmNotification = new GcmNotification();
                regIds.clear();
                regIds.add(reg_device);
                gcmNotification.sendNotification(msgParams, regIds,
                        edu.neu.madcourse.dharabhavsar.utils.gcmcomm.CommunicationMain.this);
                msg = "sending information...";
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
//				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "sendCombineGameOver : in onPostExecute");
            }
        }.execute(null, null, null);
    }
}
