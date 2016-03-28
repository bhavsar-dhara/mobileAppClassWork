package edu.neu.madcourse.dharabhavsar.utils.gcmcomm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import edu.neu.madcourse.dharabhavsar.ui.main.R;

public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    static final String TAG = "GCM_Communication";

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG, "in onHandleIntent function");
        String alertText = CommunicationConstants.alertText;
        String titleText = CommunicationConstants.titleText;
        String contentText = CommunicationConstants.contentText;

        Bundle extras = intent.getExtras();
        Log.e(String.valueOf(extras.size()), extras.toString());
        if (!extras.isEmpty()) {
            alertText = extras.getString("alertText");
            titleText = extras.getString("titleText");
            contentText = extras.getString("contentText");
            sendNotification(alertText, titleText, contentText);
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    public void sendNotification(String alertText, String titleText,
                                 String contentText) {
        Log.e(TAG, "in sending notification from intent");
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent;
        notificationIntent = new Intent(this,
                edu.neu.madcourse.dharabhavsar.utils.gcmcomm.CommunicationMain.class);
        /*notificationIntent = new Intent(this,
                edu.neu.madcourse.dharabhavsar.ui.communication.ScraggleGameActivity2.class);*/
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.putExtra("show_response", "show_response");
        PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(
                        this, CommunicationMain.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        /*PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(
                        this, ScraggleGameActivity2.class),
                PendingIntent.FLAG_UPDATE_CURRENT);*/

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(titleText)
                .setStyle(
                        new NotificationCompat.BigTextStyle()
                                .bigText(contentText))
                .setContentText(contentText).setTicker(alertText)
                .setAutoCancel(true);
        mBuilder.setContentIntent(intent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

}