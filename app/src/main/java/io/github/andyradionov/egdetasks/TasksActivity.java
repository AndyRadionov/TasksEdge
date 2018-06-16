package io.github.andyradionov.egdetasks;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import io.github.andyradionov.egdetasks.mock.MockUtil;
import io.github.andyradionov.egdetasks.model.Task;

public class TasksActivity extends AppCompatActivity implements TasksAdapter.OnTaskCheckBoxClickListener, TasksAdapter.OnTaskCardClickListener {

    private RecyclerView mTasksRecycler;
    private TasksAdapter mTasksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addNewTask = new Intent(TasksActivity.this, AddTaskActivity.class);
                startActivity(addNewTask);
            }
        });

        setUpRecycler();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckClick(@NonNull Task task) {

    }

    @Override
    public void onCardClick(@NonNull Task task) {

    }

    private void setUpRecycler() {
        mTasksRecycler = findViewById(R.id.rv_tasks_container);

        List<Task> tasks = MockUtil.getMockTasks();
        mTasksAdapter = new TasksAdapter(tasks, this, this);
        mTasksRecycler.setAdapter(mTasksAdapter);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mTasksRecycler.setLayoutManager(layoutManager);
    }
}
