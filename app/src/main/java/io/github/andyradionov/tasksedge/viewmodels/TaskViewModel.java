package io.github.andyradionov.tasksedge.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import io.github.andyradionov.tasksedge.database.AppDatabase;
import io.github.andyradionov.tasksedge.database.Task;

public class TaskViewModel extends ViewModel {

    private LiveData<Task> mTask;

    public TaskViewModel(AppDatabase database, int taskId) {
        mTask = database.taskDao().loadTaskById(taskId);
    }

    public LiveData<Task> getTask() {
        return mTask;
    }
}
