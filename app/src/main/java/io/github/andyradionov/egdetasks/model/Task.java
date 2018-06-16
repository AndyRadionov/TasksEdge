package io.github.andyradionov.egdetasks.model;

import java.util.Date;

/**
 * @author Andrey Radionov
 */

public class Task {
    private int id;
    private String text;
    private Date dueDate;
    private TaskPriority priority;
    private boolean isDone;

    public Task(int id, String text, Date dueDate, TaskPriority priority) {
        this.id = id;
        this.text = text;
        this.dueDate = dueDate;
        this.priority = priority;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
