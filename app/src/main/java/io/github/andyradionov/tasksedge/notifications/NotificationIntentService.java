package io.github.andyradionov.tasksedge.notifications;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;

import io.github.andyradionov.tasksedge.R;
import io.github.andyradionov.tasksedge.database.RepositoryCallbacks;
import io.github.andyradionov.tasksedge.database.Repository;
import io.github.andyradionov.tasksedge.model.Task;

/**
 * @author Andrey Radionov
 */

public class NotificationIntentService extends IntentService implements RepositoryCallbacks {
    public static final String ACTION_SCHEDULE_ALL = "schedule_all";
    public static final String ACTION_CANCEL_ALL = "cancel_all";
    private Repository mRepository;
    private String mAction;

    public NotificationIntentService() {
        super(NotificationIntentService.class.getSimpleName());
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null || intent.getAction() == null) return;

        mAction = intent.getAction();
        if (ACTION_SCHEDULE_ALL.equals(mAction) || ACTION_CANCEL_ALL.equals(mAction)) {
            executeTask();
        }
    }

    public void executeTask() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) return;

        mRepository = Repository.getInstance();
        mRepository.attachDatabaseListener(getString(R.string.order_key), this);
    }

    @Override
    public void onTaskAdded(Task task) {
        if (mAction.equals(ACTION_SCHEDULE_ALL)) {
            NotificationUtils.scheduleNotification(NotificationIntentService.this, task);
        } else if (mAction.equals(ACTION_CANCEL_ALL)) {
            NotificationUtils.cancelNotification(NotificationIntentService.this, task);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRepository.detachDatabaseReadListener();
    }

    @Override
    public void onTaskUpdated(Task task) {}

    @Override
    public void onTaskRemoved(Task task) {}
}
