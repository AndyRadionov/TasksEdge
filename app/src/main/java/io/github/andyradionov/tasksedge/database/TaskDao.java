package io.github.andyradionov.tasksedge.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * @author Andrey Radionov
 */

@Dao
public interface TaskDao {

    @Query("SELECT * FROM task ORDER BY due_date DESC, priority DESC")
    LiveData<List<Task>> getAllTasks();

    @Query("SELECT * FROM task WHERE is_done = 0 ORDER BY due_date DESC, priority DESC")
    LiveData<List<Task>> getAllNotDoneTasks();

    @Insert
    long insertTask(Task task);

    @Query("DELETE FROM task WHERE id = :id")
    void deleteTaskById(int id);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(Task task);

    @Query("SELECT * FROM task WHERE id = :id")
    LiveData<Task> loadTaskById(int id);
}
