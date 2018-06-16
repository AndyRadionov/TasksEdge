package io.github.andyradionov.egdetasks.mock;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.github.andyradionov.egdetasks.model.Task;
import io.github.andyradionov.egdetasks.model.TaskPriority;

/**
 * @author Andrey Radionov
 */

public class MockUtil {

    private static List<Task> mockTasks = new ArrayList<>();

    static {
        for (int i = 0; i < 5; i++) {
            Task task = new Task(i, "MOCK " + i, new Date(), 0);
            mockTasks.add(task);
        }
    }

    private MockUtil() {
    }

    public static List<Task> getMockTasks() {
        return mockTasks;
    }
}
