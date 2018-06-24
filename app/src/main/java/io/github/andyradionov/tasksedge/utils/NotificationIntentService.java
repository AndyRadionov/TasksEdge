package io.github.andyradionov.tasksedge.utils;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * @author Andrey Radionov
 */

public class NotificationIntentService extends IntentService {
    public static final String ACTION_TASK_DONE = "task_done";
    public static final String ACTION_DISMISS_NOTIFICATION = "dismiss_notification";
    public static final String EXTRA_TASK_ID = "task_id";

    public NotificationIntentService() {
        super(NotificationIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        int taskId = intent.getIntExtra(EXTRA_TASK_ID, 0);
        executeTask(this, action, taskId);
    }



    public static void executeTask(Context context, String action, int taskId) {
        if (ACTION_TASK_DONE.equals(action)) {
            markTaskDone(context, taskId);
        } else if (ACTION_DISMISS_NOTIFICATION.equals(action)) {
            //todo
            //NotificationUtils.clearNotification(context, taskId);
        }
    }

    private static void markTaskDone(Context context, int taskId) {
        //todo update database
    }
}
