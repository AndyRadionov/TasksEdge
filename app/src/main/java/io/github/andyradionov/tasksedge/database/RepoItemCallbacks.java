package io.github.andyradionov.tasksedge.database;

/**
 * @author Andrey Radionov
 */

public interface RepoItemCallbacks {

    void onTaskAdded(Task task);

    void onTaskUpdated(Task task);

    void onTaskRemoved(Task task);
}
