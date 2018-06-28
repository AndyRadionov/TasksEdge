package io.github.andyradionov.tasksedge.database;

import android.support.annotation.NonNull;

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

public class Repository {
    private static final Repository INSTANCE = new Repository();

    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    private Repository() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = firebaseDatabase.getReference().child(firebaseAuth
                .getCurrentUser().getUid());
    }

    public static Repository getInstance() {
        return INSTANCE;
    }

    public void attachDatabaseListener(String sortOrder, final RepositoryCallbacks dbCallbacks) {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                    Task task = dataSnapshot.getValue(Task.class);
                    dbCallbacks.onTaskAdded(task);
                }

                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                    Task task = dataSnapshot.getValue(Task.class);
                    dbCallbacks.onTaskUpdated(task);
                }
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    Task task = dataSnapshot.getValue(Task.class);
                    dbCallbacks.onTaskRemoved(task);
                }
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {}
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            };
            mDatabaseReference.orderByChild(sortOrder).addChildEventListener(mChildEventListener);
        }
    }

    public void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    public void addValue(Task task) {
        String key = mDatabaseReference.push().getKey();
        task.setKey(key);
        mDatabaseReference.child(key).setValue(task);
    }

    public void updateValue(Task task) {
        mDatabaseReference.child(task.getKey()).setValue(task);
    }

    public void removeValue(String key) {
        mDatabaseReference.child(key).removeValue();
    }
}
