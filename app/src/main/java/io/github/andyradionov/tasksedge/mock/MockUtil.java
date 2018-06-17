package io.github.andyradionov.tasksedge.mock;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.github.andyradionov.tasksedge.model.Task;
import io.github.andyradionov.tasksedge.model.TaskPriority;

/**
 * @author Andrey Radionov
 */

public class MockUtil {

    private static List<Task> mockTasks = new ArrayList<>();

    static {
        for (int i = 0; i < 5; i++) {
            Task task = new Task(i, "MOCK " + i, 0, new Date());
            mockTasks.add(task);
        }
    }

    private MockUtil() {
    }

    public static List<Task> getMockTasks() {
        return mockTasks;
    }
}
