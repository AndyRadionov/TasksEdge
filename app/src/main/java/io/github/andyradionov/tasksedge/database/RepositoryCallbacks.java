package io.github.andyradionov.tasksedge.database;

import io.github.andyradionov.tasksedge.model.Task;

/**
 * @author Andrey Radionov
 */

public interface RepositoryCallbacks {

    void onTaskAdded(Task task);

    void onTaskUpdated(Task task);

    void onTaskRemoved(Task task);
}
