package edu.neu.madcourse.dharabhavsar.ui.main;

/**
 * Created by Dhara on 4/27/2016.
 */

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

public class NotificationBuilder {

    public static final int NOTIFICATION_ID = 1;

    private final Context mContext;

    public NotificationBuilder(Context context) {
        this.mContext = context;
    }

    public Notification buildNotification() {
        // Setup our notification contents and icon
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                .setContentTitle(mContext.getString(R.string.running))
                .setColor(Color.BLACK)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.mipmap.ic_launcher2);

        // Create an intent that will be launched inside of an activity view,
        // that is, WorkoutActivity.class to display the custom notification.
        Intent workoutIntent = new Intent(mContext, WorkoutViewActivity.class);

        workoutIntent.setAction("workoutIntent");
        // The intent needs to be packaged into a pending intent so that the
        // notification service can fire it on our behalf.
        PendingIntent workoutPendingIntent =
                PendingIntent.getActivity(mContext, 0, workoutIntent,
                        PendingIntent.FLAG_ONE_SHOT);

        Intent stopIntent = new Intent(mContext, StatsActivity.class);
        stopIntent.setAction("stopIntent");
        PendingIntent stopPendingIntent = PendingIntent.getActivity(mContext, 0, stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(
                R.drawable.ic_stop_white_36dp, mContext.getString(R.string.finish_meal),
                stopPendingIntent).build();

        // Setup background, custom card size and set an intent to launch
        // inside an activity view when displaying this notification
        NotificationCompat.WearableExtender wearableExtender =
                new NotificationCompat.WearableExtender()
                        .setBackground(BitmapFactory.decodeResource(
                                mContext.getResources(), R.drawable.image_meal1))
                        .setCustomSizePreset(Notification.WearableExtender.SIZE_DEFAULT)
                        .addAction(action)
                        //.addAction(action2)
                        .setDisplayIntent(workoutPendingIntent);

        // Add wearable specific features to our builder
        builder.extend(wearableExtender);

        return builder.build();
    }

    public static void update(Context context) {
        NotificationBuilder builder = new NotificationBuilder(context);
        Notification notification = builder.buildNotification();
        notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}