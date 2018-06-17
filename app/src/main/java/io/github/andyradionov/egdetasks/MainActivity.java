package io.github.andyradionov.egdetasks;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import io.github.andyradionov.egdetasks.mock.MockUtil;
import io.github.andyradionov.egdetasks.model.Task;

public class MainActivity extends AppCompatActivity implements TasksAdapter.OnTaskCheckBoxClickListener, TasksAdapter.OnTaskCardClickListener {

    private static final int ADD_TASK_REQUEST_CODE = 100;
    private static final int EDIT_TASK_REQUEST_CODE = 101;

    private RecyclerView mTasksRecycler;
    private TasksAdapter mTasksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarTitle = toolbar.findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText("EdgeTasks");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addNewTask = new Intent(MainActivity.this, TaskActivity.class);
                startActivityForResult(addNewTask, ADD_TASK_REQUEST_CODE);
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
        Intent editTaskIntent = new Intent(this, TaskActivity.class);
        editTaskIntent.putExtra(TaskActivity.TASK_EXTRA, task);
        startActivityForResult(editTaskIntent, EDIT_TASK_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_TASK_REQUEST_CODE && resultCode == RESULT_OK) {
            Task task = data.getParcelableExtra(TaskActivity.TASK_EXTRA);
            MockUtil.getMockTasks().remove(task.getId());
            MockUtil.getMockTasks().add(task.getId(), task);
            mTasksAdapter.notifyDataSetChanged();
        } else if (requestCode == ADD_TASK_REQUEST_CODE && resultCode == RESULT_OK) {
            Task task = data.getParcelableExtra(TaskActivity.TASK_EXTRA);
            task.setId(MockUtil.getMockTasks().size());
            MockUtil.getMockTasks().add(task);
            mTasksAdapter.notifyDataSetChanged();
        }
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
