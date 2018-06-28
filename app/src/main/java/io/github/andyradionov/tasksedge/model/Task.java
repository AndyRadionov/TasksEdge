package io.github.andyradionov.tasksedge.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * @author Andrey Radionov
 */
public class Task implements Parcelable {

    private String key;
    private int id;
    private String text;
    private Date dueDate;

    public Task() {
        this(null, "", new Date());
    }

    public Task(String text) {
        this(null, text, new Date());
    }

    public Task(String key, String text, Date dueDate) {
        this.key = key;
        this.text = text;
        this.dueDate = dueDate;
        this.id = (int) new Date().getTime();
    }

    protected Task(Parcel in) {
        key = in.readString();
        id = in.readInt();
        text = in.readString();
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
        dest.writeString(key);
        dest.writeInt(id);
        dest.writeString(text);
        dest.writeLong(dueDate.getTime());
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        return key.equals(task.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return "Task{" +
                "key='" + key + '\'' +
                ", id=" + id +
                ", text='" + text + '\'' +
                ", dueDate=" + dueDate +
                '}';
    }
}
