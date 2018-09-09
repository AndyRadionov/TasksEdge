package io.github.andyradionov.tasksedge.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author Andrey Radionov
 */

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = BootReceiver.class.getSimpleName();
    private static final String BOOT_COMPLETED_ACTION = "android.intent.action.BOOT_COMPLETED";

    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        if (intent.getAction() != null && intent.getAction().equals(BOOT_COMPLETED_ACTION)) {
            Intent noticeIntent = new Intent(NotificationIntentService.ACTION_SCHEDULE_ALL);
            context.startService(noticeIntent);
        }
    }
}
