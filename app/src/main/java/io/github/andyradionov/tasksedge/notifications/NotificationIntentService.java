package io.github.andyradionov.tasksedge.notifications;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import io.github.andyradionov.tasksedge.R;
import io.github.andyradionov.tasksedge.database.RepositoryCallbacks;
import io.github.andyradionov.tasksedge.database.FirebaseRepository;
import io.github.andyradionov.tasksedge.model.Task;

/**
 * @author Andrey Radionov
 */

public class NotificationIntentService extends IntentService implements RepositoryCallbacks {
    private static final String TAG = NotificationIntentService.class.getSimpleName();
    public static final String ACTION_SCHEDULE_ALL = "schedule_all";
    public static final String ACTION_CANCEL_ALL = "cancel_all";
    private FirebaseRepository mRepository;
    private String mAction;

    public NotificationIntentService() {
        super(NotificationIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent");
        if (intent == null || intent.getAction() == null) return;

        mAction = intent.getAction();
        if (ACTION_SCHEDULE_ALL.equals(mAction) || ACTION_CANCEL_ALL.equals(mAction)) {
            executeTask();
        }
    }

    public void executeTask() {
        Log.d(TAG, "executeTask " + mAction);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) return;

        mRepository = FirebaseRepository.getInstance();
        mRepository.attachDatabaseListener(getString(R.string.order_key), this);
    }

    @Override
    public void onTaskAdded(Task task) {
        Log.d(TAG, "onTaskAdded: " + task);
        if (mAction.equals(ACTION_SCHEDULE_ALL)) {
            NotificationUtils.scheduleNotification(NotificationIntentService.this, task);
        } else if (mAction.equals(ACTION_CANCEL_ALL)) {
            NotificationUtils.cancelNotification(NotificationIntentService.this, task);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        mRepository.detachDatabaseReadListener();
    }

    @Override
    public void onTaskUpdated(Task task) {}

    @Override
    public void onTaskRemoved(Task task) {}
}
