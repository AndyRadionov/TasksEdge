package io.github.andyradionov.tasksedge.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import io.github.andyradionov.tasksedge.R;
import io.github.andyradionov.tasksedge.database.AppDatabase;
import io.github.andyradionov.tasksedge.database.Task;
import io.github.andyradionov.tasksedge.network.QuoteFetcherUtils;
import io.github.andyradionov.tasksedge.utils.NotificationUtils;
import io.github.andyradionov.tasksedge.viewmodels.MainViewModel;

import static android.support.v7.widget.helper.ItemTouchHelper.*;

public class MainActivity extends AppCompatActivity implements
        TasksAdapter.OnTaskCheckBoxClickListener,
        TasksAdapter.OnTaskCardClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int ADD_TASK_REQUEST_CODE = 100;
    private static final int EDIT_TASK_REQUEST_CODE = 101;

    private RecyclerView mTasksRecycler;
    private TasksAdapter mTasksAdapter;
    private AppDatabase mDb;
    private boolean mShowDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        QuoteFetcherUtils.scheduleUpdate(this);

        mDb = AppDatabase.getInstance(this);
        setUpToolbar();
        setUpFab();
        setupSharedPreferences();
        setUpRecycler();
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
        AppDatabase.getInstance(MainActivity.this)
                .taskDao().updateTask(task);
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
            mShowDone = sharedPreferences.getBoolean(key,
                    getResources().getBoolean(R.bool.pref_show_done_default));

            setupViewModel();
        } else if (key.equals(getString(R.string.pref_enable_notices_key))) {
            boolean notificationsEnabled = sharedPreferences.getBoolean(key,
                    getResources().getBoolean(R.bool.pref_enable_notices_default));
            NotificationUtils.setNotificationsEnabled(this, notificationsEnabled);
            setupNotifications(notificationsEnabled);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Task task = data.getParcelableExtra(TaskActivity.TASK_EXTRA);
            if (requestCode == EDIT_TASK_REQUEST_CODE) {
                AppDatabase.getInstance(MainActivity.this)
                        .taskDao().updateTask(task);
                NotificationUtils.cancelNotification(this, task);
            } else if (requestCode == ADD_TASK_REQUEST_CODE) {
                long id = mDb.taskDao().insertTask(task);
                task.setId((int) id);
            }
            NotificationUtils.scheduleNotification(this, task);
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

        mTasksAdapter = new TasksAdapter(this, this);
        mTasksRecycler.setAdapter(mTasksAdapter);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mTasksRecycler.setLayoutManager(layoutManager);

        ItemTouchHelper touchHelper = new ItemTouchHelper(new SimpleCallback(0, LEFT | RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int id = (int) viewHolder.itemView.getTag();
                AppDatabase.getInstance(MainActivity.this)
                        .taskDao().deleteTaskById(id);
            }
        });
        touchHelper.attachToRecyclerView(mTasksRecycler);
        setupViewModel();
    }

    private void setupViewModel() {
        //MainViewModelFactory factory = new MainViewModelFactory(mDb, mShowDone);
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.setShowDone(mShowDone);


        viewModel.getTasks().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(@Nullable List<Task> tasks) {
                mTasksAdapter.updateData(tasks);
            }
        });
    }

    private void setupNotifications(final boolean isEnabled) {
//        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
//        viewModel.getTasks().observe(this, new Observer<List<Task>>() {
//            @Override
//            public void onChanged(@Nullable List<Task> tasks) {
//                if (isEnabled) {
//                    NotificationUtils.scheduleAllNotifications(MainActivity.this, tasks);
//                } else {
//                    NotificationUtils.cancelAllNotifications(MainActivity.this, tasks);
//                }
//            }
//        });
    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String showDoneKey = getString(R.string.pref_show_done_key);
        boolean showDoneDefault = getResources().getBoolean(R.bool.pref_show_done_default);
        mShowDone = sharedPreferences.getBoolean(showDoneKey, showDoneDefault);

        String enableNoticesKey = getString(R.string.pref_enable_notices_key);
        boolean noticesEnabledDefault = getResources().getBoolean(R.bool.pref_enable_notices_default);
        boolean noticesEnabled = sharedPreferences.getBoolean(enableNoticesKey, noticesEnabledDefault);
        NotificationUtils.setNotificationsEnabled(this, noticesEnabled);
        setupNotifications(noticesEnabled);

        //todo
        String enableSyncKey = getString(R.string.pref_enable_synchronization_key);
        boolean syncEnabledDefault = getResources().getBoolean(R.bool.pref_enable_sync_default);
        boolean syncEnabled = sharedPreferences.getBoolean(enableSyncKey, syncEnabledDefault);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }
}
