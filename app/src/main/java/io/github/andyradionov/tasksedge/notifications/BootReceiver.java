package io.github.andyradionov.tasksedge.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author Andrey Radionov
 */

public class BootReceiver extends BroadcastReceiver {
    private static final String BOOT_COMPLETED_ACTION = "android.intent.action.BOOT_COMPLETED";

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(BOOT_COMPLETED_ACTION)) {
            Intent noticeIntent = new Intent(NotificationIntentService.ACTION_SCHEDULE_ALL);
            context.startService(noticeIntent);
        }
    }
}
