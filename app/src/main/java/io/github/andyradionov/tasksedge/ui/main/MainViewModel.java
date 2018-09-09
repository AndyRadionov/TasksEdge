package io.github.andyradionov.tasksedge.ui.main;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.github.andyradionov.tasksedge.data.database.FirebaseRepository;
import io.github.andyradionov.tasksedge.data.database.RepoItemCallbacks;
import io.github.andyradionov.tasksedge.data.database.Task;
import io.github.andyradionov.tasksedge.notifications.NotificationManager;
import io.github.andyradionov.tasksedge.utils.PreferenceUtils;
import io.github.andyradionov.tasksedge.widget.WidgetUpdateService;

/**
 * @author Andrey Radionov
 */
public class MainViewModel extends ViewModel implements RepoItemCallbacks {

    private FirebaseRepository mRepository;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private MutableLiveData<Task> mAddTaskLiveData;
    private MutableLiveData<Task> mRemoveTaskLiveData;
    private MutableLiveData<Task> mUpdateTaskLiveData;
    private MutableLiveData<Boolean> mAuthLiveData;

    public MainViewModel(FirebaseRepository repository) {

        mFirebaseAuth = FirebaseAuth.getInstance();
        mRepository = repository;
        mAddTaskLiveData = new MutableLiveData<>();
        mRemoveTaskLiveData = new MutableLiveData<>();
        mUpdateTaskLiveData = new MutableLiveData<>();
        mAuthLiveData = new MutableLiveData<>();
        initAuthListener();
    }

    public void addAuthListener() {
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    public void removeAuthListener() {
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    public MutableLiveData<Task> getAddTaskLiveData() {
        return mAddTaskLiveData;
    }

    public MutableLiveData<Task> getDeleteTaskLiveData() {
        return mRemoveTaskLiveData;
    }

    public MutableLiveData<Task> getUpdateTaskLiveData() {
        return mUpdateTaskLiveData;
    }

    public MutableLiveData<Boolean> getAuthLiveData() {
        return mAuthLiveData;
    }

    public void removeTask(String key) {
        mRepository.removeValue(key);
    }

    public void attachDbListener(String order) {
        mRepository.attachDbListener(order, this);
    }

    public void detachDbListener() {
        if (mRepository != null) {
            mRepository.detachDbListener();
        }
    }

    @Override
    public void onTaskAdded(Task task) {
        mAddTaskLiveData.setValue(task);
    }

    @Override
    public void onTaskUpdated(Task task) {
        mUpdateTaskLiveData.setValue(task);
    }

    @Override
    public void onTaskRemoved(Task task) {
        mRemoveTaskLiveData.setValue(task);
    }

    private void initAuthListener() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    mAuthLiveData.setValue(true);
                } else {
                    mAuthLiveData.setValue(false);
                }
            }
        };
    }
}
