package io.github.andyradionov.tasksedge.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;

import io.github.andyradionov.tasksedge.R;
import io.github.andyradionov.tasksedge.database.RepositoryCallbacks;
import io.github.andyradionov.tasksedge.database.Repository;
import io.github.andyradionov.tasksedge.model.Task;
import io.github.andyradionov.tasksedge.network.QuoteFetcherUtils;
import io.github.andyradionov.tasksedge.notifications.NotificationUtils;
import io.github.andyradionov.tasksedge.utils.AnalyticsUtils;

import static android.support.v7.widget.helper.ItemTouchHelper.LEFT;
import static android.support.v7.widget.helper.ItemTouchHelper.RIGHT;
import static android.support.v7.widget.helper.ItemTouchHelper.SimpleCallback;

public class MainActivity extends AppCompatActivity implements
        TasksAdapter.OnTaskCheckBoxClickListener,
        TasksAdapter.OnTaskCardClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener,
        RepositoryCallbacks {

    private static final DateFormat DATE_FORMAT =
            new SimpleDateFormat("dd:MM:yyyy, HH:mm", Locale.ROOT);
    private static final int RC_SIGN_IN = 324;

    private RecyclerView mTasksRecycler;
    private TasksAdapter mTasksAdapter;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private Repository mRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AnalyticsUtils.logAppOpenEvent(this);
        mFirebaseAuth = FirebaseAuth.getInstance();
        QuoteFetcherUtils.scheduleUpdate(this);

        setUpToolbar();
        setUpFab();
        setUpRecycler();
        setupAuthListener();

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        detachDatabaseListener();
        mTasksAdapter.clear();
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
       if (key.equals(getString(R.string.pref_enable_notices_key))) {
           boolean notificationsEnabled = sharedPreferences.getBoolean(key,
                    getResources().getBoolean(R.bool.pref_enable_notices_default));
           NotificationUtils.setNotificationsEnabled(this, notificationsEnabled);
           if (notificationsEnabled) {
                NotificationUtils.scheduleAllNotifications(this);
            } else {
                NotificationUtils.cancelAllNotifications(this);
            }
       }
    }

    @Override
    public void onCheckClick(@NonNull Task task) {
        mRepository.removeValue(task.getKey());
    }

    @Override
    public void onCardClick(@NonNull Task task) {
        Intent editTaskIntent = new Intent(this, TaskActivity.class);
        editTaskIntent.putExtra(TaskActivity.TASK_EXTRA, task);
        startActivity(editTaskIntent);
    }

    @Override
    public void onTaskAdded(Task task) {
        mTasksAdapter.add(task);
        if (isNotificationsEnabled()) {
            NotificationUtils.scheduleNotification(MainActivity.this, task);
        }
    }

    @Override
    public void onTaskUpdated(Task task) {
        mTasksAdapter.sort();
        if (isNotificationsEnabled()) {
            NotificationUtils.updateNotification(MainActivity.this, task);
        }
    }

    @Override
    public void onTaskRemoved(Task task) {
        mTasksAdapter.remove(task);
        if (isNotificationsEnabled()) {
            NotificationUtils.cancelNotification(MainActivity.this, task);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }

    private void startTaskActivityAnimate(Intent intent) {

    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarTitle = toolbar.findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(getString(R.string.app_name));
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
                startActivity(addNewTask);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
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
                String key = (String) viewHolder.itemView.getTag();
                mRepository.removeValue(key);
            }
        });
        touchHelper.attachToRecyclerView(mTasksRecycler);
    }

    private void setupAuthListener() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    onSignedInInitialize();
                } else {
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);

                }
            }
        };
    }

    private void onSignedInInitialize() {
        mRepository = Repository.getInstance();
        mRepository.attachDatabaseListener(getString(R.string.order_key), this);
    }

    private void onSignedOutCleanup() {
        mTasksAdapter.clear();
        detachDatabaseListener();
    }

    private void detachDatabaseListener() {
        if (mRepository != null) {
            mRepository.detachDatabaseReadListener();
        }
    }

    private boolean isNotificationsEnabled() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String enableNoticesKey = getString(R.string.pref_enable_notices_key);
        boolean noticesEnabledDefault = getResources()
                .getBoolean(R.bool.pref_enable_notices_default);
        return sharedPreferences.getBoolean(enableNoticesKey, noticesEnabledDefault);
    }
}
