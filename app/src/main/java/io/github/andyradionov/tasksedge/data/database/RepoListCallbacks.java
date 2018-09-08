package io.github.andyradionov.tasksedge.data.database;

import java.util.List;

/**
 * @author Andrey Radionov
 */

public interface RepoListCallbacks {

    void onListFetched(List<Task> tasks);
}
