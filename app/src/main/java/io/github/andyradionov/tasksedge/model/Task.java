package io.github.andyradionov.tasksedge.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * @author Andrey Radionov
 */

public class Task implements Parcelable {
    private int id;
    private String text;
    private int priority;
    private Date dueDate;
    private boolean isDone;

    public Task() {
        this(0, "", 0, new Date());
    }

    public Task(int id, String text, int priority, Date dueDate) {
        this.id = id;
        this.text = text;
        this.priority = priority;
        this.dueDate = dueDate;
    }

    protected Task(Parcel in) {
        id = in.readInt();
        text = in.readString();
        isDone = in.readByte() != 0;
        dueDate = new Date(in.readLong());
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(text);
        dest.writeByte((byte) (isDone ? 1 : 0));
        dest.writeLong(dueDate.getTime());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
