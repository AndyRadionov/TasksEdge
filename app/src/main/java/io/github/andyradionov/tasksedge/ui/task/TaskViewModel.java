package io.github.andyradionov.tasksedge.ui.task;

import android.arch.lifecycle.ViewModel;

import io.github.andyradionov.tasksedge.data.database.FirebaseRepository;
import io.github.andyradionov.tasksedge.data.database.Task;

/**
 * @author Andrey Radionov
 */
public class TaskViewModel extends ViewModel {

    private FirebaseRepository mRepository;

    public TaskViewModel(FirebaseRepository repository) {
        mRepository = repository;
    }

    public void addTask(Task task) {
        mRepository.addValue(task);
    }

    public void updateTask(Task task) {
        mRepository.updateValue(task);
    }
}
