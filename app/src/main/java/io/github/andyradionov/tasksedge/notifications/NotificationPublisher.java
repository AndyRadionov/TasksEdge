package io.github.andyradionov.tasksedge.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author Andrey Radionov
 */

public class NotificationPublisher extends BroadcastReceiver {
    private static final String TAG = NotificationPublisher.class.getSimpleName();
    public static String EXTRA_NOTIFICATION_ID = "notification_id";
    public static String EXTRA_NOTIFICATION = "notification";

    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) return;

        Notification notification = intent.getParcelableExtra(EXTRA_NOTIFICATION);
        int id = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0);
        notificationManager.notify(id, notification);
    }
}
