package io.github.andyradionov.tasksedge.notifications;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.github.andyradionov.tasksedge.model.Task;

/**
 * @author Andrey Radionov
 */

public class NotificationIntentService extends IntentService {
    public static final String ACTION_SCHEDULE_ALL = "schedule_all";
    public static final String ACTION_CANCEL_ALL = "cancel_all";

    public NotificationIntentService() {
        super(NotificationIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null || intent.getAction() == null) return;

        String action = intent.getAction();
        executeTask(action, intent);
    }

    public void executeTask(String action, Intent intent) {
        if (ACTION_SCHEDULE_ALL.equals(action)) {
            executeActionOnTasks(ACTION_SCHEDULE_ALL);
        } else if (ACTION_CANCEL_ALL.equals(action)) {
            executeActionOnTasks(ACTION_CANCEL_ALL);
        }
    }

    private void executeActionOnTasks(final String action) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) return;
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference =
                firebaseDatabase.getReference().child(firebaseAuth
                        .getCurrentUser().getUid());

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                Task task = dataSnapshot.getValue(Task.class);
                if (action.equals(ACTION_SCHEDULE_ALL)) {
                    NotificationUtils.scheduleNotification(NotificationIntentService.this, task);
                } else if (action.equals(ACTION_CANCEL_ALL)) {
                    NotificationUtils.cancelNotification(NotificationIntentService.this, task);
                }
            }

            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {}
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        databaseReference.addChildEventListener(childEventListener);
    }
}
