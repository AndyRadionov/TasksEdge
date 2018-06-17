package io.github.andyradionov.tasksedge;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.andyradionov.tasksedge.mock.MockUtil;
import io.github.andyradionov.tasksedge.model.Task;

public class MainActivity extends AppCompatActivity implements
        TasksAdapter.OnTaskCheckBoxClickListener,
        TasksAdapter.OnTaskCardClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int ADD_TASK_REQUEST_CODE = 100;
    private static final int EDIT_TASK_REQUEST_CODE = 101;

    private RecyclerView mTasksRecycler;
    private TasksAdapter mTasksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpToolbar();
        setUpFab();
        setUpRecycler();
        setupSharedPreferences();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckClick(@NonNull Task task) {
        task.setDone(!task.isDone());
    }

    @Override
    public void onCardClick(@NonNull Task task) {
        Intent editTaskIntent = new Intent(this, TaskActivity.class);
        editTaskIntent.putExtra(TaskActivity.TASK_EXTRA, task);
        startActivityForResult(editTaskIntent, EDIT_TASK_REQUEST_CODE);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_show_done_key))) {
            boolean showDone = sharedPreferences.getBoolean(key,
                    getResources().getBoolean(R.bool.pref_show_done_default));
            if (showDone) {
                mTasksAdapter.updateData(MockUtil.getMockTasks());
            } else {
                List<Task> tasks = new ArrayList<>();
                for (Task task : MockUtil.getMockTasks()) {
                    if (!task.isDone()) tasks.add(task);
                }
                mTasksAdapter.updateData(tasks);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Task task = data.getParcelableExtra(TaskActivity.TASK_EXTRA);
            if (requestCode == EDIT_TASK_REQUEST_CODE) {
                MockUtil.getMockTasks().remove(task.getId());
                MockUtil.getMockTasks().add(task.getId(), task);
            } else if (requestCode == ADD_TASK_REQUEST_CODE) {
                task.setId(MockUtil.getMockTasks().size());
                MockUtil.getMockTasks().add(task);
            }
            mTasksAdapter.notifyDataSetChanged();
        }
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarTitle = toolbar.findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText("TasksEdge");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    private void setUpFab() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addNewTask = new Intent(MainActivity.this, TaskActivity.class);
                startActivityForResult(addNewTask, ADD_TASK_REQUEST_CODE);
            }
        });
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

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String showDoneKey = getString(R.string.pref_show_done_key);
        boolean showDoneDefault = getResources().getBoolean(R.bool.pref_show_done_default);
        boolean showDone = sharedPreferences.getBoolean(showDoneKey, showDoneDefault);

        String enableNoticesKey = getString(R.string.pref_enable_notices_key);
        boolean noticesEnabledDefault = getResources().getBoolean(R.bool.pref_enable_notices_default);
        boolean noticesEnabled = sharedPreferences.getBoolean(enableNoticesKey, noticesEnabledDefault);

        String enableSyncKey = getString(R.string.pref_enable_synchronization_key);
        boolean syncEnabledDefault = getResources().getBoolean(R.bool.pref_enable_sync_default);
        boolean syncEnabled = sharedPreferences.getBoolean(enableSyncKey, syncEnabledDefault);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }
}
