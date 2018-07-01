package io.github.andyradionov.tasksedge.database;

import java.util.List;

/**
 * @author Andrey Radionov
 */

public interface RepoListCallbacks {

    void onListFetched(List<Task> tasks);
}
