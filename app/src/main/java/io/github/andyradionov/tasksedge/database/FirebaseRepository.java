package io.github.andyradionov.tasksedge.database;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrey Radionov
 */

public class FirebaseRepository {
    private static final String TAG = FirebaseRepository.class.getSimpleName();
    private static final FirebaseRepository INSTANCE = new FirebaseRepository();

    private final DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    private FirebaseRepository() {
        Log.d(TAG, "FirebaseRepository constructor call");
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = firebaseDatabase.getReference().child(firebaseAuth
                .getCurrentUser().getUid());
    }

    public static FirebaseRepository getInstance() {
        return INSTANCE;
    }

    public void attachChildListener(String sortOrder, final RepoItemCallbacks dbCallbacks) {
        if (mChildEventListener == null) {
            Log.d(TAG, "attachChildListener");
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "onChildAdded");
                    Task task = dataSnapshot.getValue(Task.class);
                    dbCallbacks.onTaskAdded(task);
                }

                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "onChildChanged");
                    Task task = dataSnapshot.getValue(Task.class);
                    dbCallbacks.onTaskUpdated(task);
                }

                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onChildRemoved");
                    Task task = dataSnapshot.getValue(Task.class);
                    dbCallbacks.onTaskRemoved(task);
                }

                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
                }

                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };
            mDatabaseReference.orderByChild(sortOrder).addChildEventListener(mChildEventListener);
        }
    }

    public void attachValueListener(String sortOrder, final RepoListCallbacks dbCallbacks) {
        final List<Task> tasks = new ArrayList<>();
        mDatabaseReference.orderByChild(sortOrder)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    //Getting the data from snapshot
                    Task task = postSnapshot.getValue(Task.class);
                    tasks.add(task);
                }
                mDatabaseReference.removeEventListener(this);
                dbCallbacks.onListFetched(tasks);
                Log.d(TAG, "onDataChange: " + tasks.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            Log.d(TAG, "detachDatabaseReadListener");
            mDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    public void addValue(Task task) {
        Log.d(TAG, "addValue: " + task);
        String key = mDatabaseReference.push().getKey();
        task.setKey(key);
        mDatabaseReference.child(key).setValue(task);
    }

    public void updateValue(Task task) {
        Log.d(TAG, "updateValue: " + task);
        mDatabaseReference.child(task.getKey()).setValue(task);
    }

    public void removeValue(String key) {
        Log.d(TAG, "removeValue for key: " + key);
        mDatabaseReference.child(key).removeValue();
    }
}