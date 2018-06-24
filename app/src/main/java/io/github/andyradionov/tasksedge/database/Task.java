package io.github.andyradionov.tasksedge.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * @author Andrey Radionov
 */
@Entity(tableName = "task")
public class Task implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String text;
    private int priority;
    @ColumnInfo(name = "due_date")
    private Date dueDate;
    @ColumnInfo(name = "is_done")
    private boolean isDone;

    @Ignore
    public Task() {
        this(0, "", 0, new Date(), false);
    }

    public Task(int id, String text, int priority, Date dueDate, boolean isDone) {
        this.id = id;
        this.text = text;
        this.priority = priority;
        this.dueDate = dueDate;
        this.isDone = isDone;
    }

    protected Task(Parcel in) {
        id = in.readInt();
        text = in.readString();
        priority = in.readInt();
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
        dest.writeInt(priority);
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
