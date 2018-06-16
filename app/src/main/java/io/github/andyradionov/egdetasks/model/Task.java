package io.github.andyradionov.egdetasks.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * @author Andrey Radionov
 */

public class Task implements Parcelable {
    private int id;
    private String text;
    private Date dueDate;
    private int priority;
    private boolean isDone;

    public Task(int id, String text, Date dueDate, int priority) {
        this.id = id;
        this.text = text;
        this.dueDate = dueDate;
        this.priority = priority;
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

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
