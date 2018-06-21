package io.github.andyradionov.tasksedge.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

import io.github.andyradionov.tasksedge.database.AppDatabase;
import io.github.andyradionov.tasksedge.database.Task;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<Task>> mTasks;

    public MainViewModel(Application application) {
        super(application);
    }

    public void setShowDone(boolean showDone) {
        AppDatabase db = AppDatabase.getInstance(this.getApplication().getApplicationContext());
        if (showDone) {
            mTasks = db.taskDao().getAllTasks();
        } else {
            mTasks = db.taskDao().getAllNotDoneTasks();
        }
    }

    public LiveData<List<Task>> getTasks() {
        return mTasks;
    }
}
