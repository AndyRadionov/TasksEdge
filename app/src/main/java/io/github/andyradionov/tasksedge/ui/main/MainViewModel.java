package io.github.andyradionov.tasksedge.ui.main;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import io.github.andyradionov.tasksedge.data.database.FirebaseRepository;
import io.github.andyradionov.tasksedge.data.database.RepoItemCallbacks;
import io.github.andyradionov.tasksedge.data.database.Task;

/**
 * @author Andrey Radionov
 */
public class MainViewModel extends ViewModel implements RepoItemCallbacks {

    private FirebaseRepository mRepository;
    private MutableLiveData<Task> mAddTaskLiveData;
    private MutableLiveData<Task> mRemoveTaskLiveData;
    private MutableLiveData<Task> mUpdateTaskLiveData;

    public MainViewModel(FirebaseRepository repository) {
        mRepository = repository;
        mAddTaskLiveData = new MutableLiveData<>();
        mRemoveTaskLiveData = new MutableLiveData<>();
        mUpdateTaskLiveData = new MutableLiveData<>();
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
}
