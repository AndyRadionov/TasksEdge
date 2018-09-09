package io.github.andyradionov.tasksedge.ui.common;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import io.github.andyradionov.tasksedge.data.database.FirebaseRepository;
import io.github.andyradionov.tasksedge.ui.main.MainViewModel;
import io.github.andyradionov.tasksedge.ui.task.TaskViewModel;

/**
 * @author Andrey Radionov
 */

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final FirebaseRepository mRepository;

    @Inject
    public ViewModelFactory(FirebaseRepository repository) {
        mRepository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TaskViewModel.class)) {
            return (T) new TaskViewModel(mRepository);
        }
        else if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(mRepository);
        }
        else {
            throw new IllegalArgumentException("ViewModel type was not found");
        }
    }
}
