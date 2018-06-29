package io.github.andyradionov.tasksedge.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;
import io.github.andyradionov.tasksedge.R;
import io.github.andyradionov.tasksedge.database.FirebaseRepository;
import io.github.andyradionov.tasksedge.database.RepositoryCallbacks;
import io.github.andyradionov.tasksedge.database.Task;
import io.github.andyradionov.tasksedge.network.QuoteFetcherUtils;
import io.github.andyradionov.tasksedge.notifications.NotificationManager;
import io.github.andyradionov.tasksedge.utils.AnalyticsUtils;
import io.github.andyradionov.tasksedge.utils.PreferenceUtils;

import static android.support.v7.widget.helper.ItemTouchHelper.LEFT;
import static android.support.v7.widget.helper.ItemTouchHelper.RIGHT;
import static android.support.v7.widget.helper.ItemTouchHelper.SimpleCallback;

public class MainActivity extends BaseActivity implements
        TasksAdapter.OnTaskCardClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener,
        RepositoryCallbacks {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 324;

    @BindView(R.id.rv_tasks_container) RecyclerView mTasksRecycler;
    private TasksAdapter mTasksAdapter;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseRepository mRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);

        AnalyticsUtils.logAppOpenEvent(this);
        mFirebaseAuth = FirebaseAuth.getInstance();
        QuoteFetcherUtils.scheduleUpdate(this);

        setUpToolbar(getString(R.string.app_name));
        setUpRecycler();
        setUpAuthListener();

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        detachDatabaseListener();
        mTasksAdapter.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Log.d(TAG, "onOptionsItemSelected: settings");
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivityAnimate(startSettingsActivity,
                    R.anim.slide_in_right, R.anim.slide_out_right);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
       if (key.equals(getString(R.string.pref_enable_notices_key))) {
           Log.d(TAG, "onSharedPreferenceChanged: notifications");
           boolean notificationsEnabled = sharedPreferences.getBoolean(key,
                    getResources().getBoolean(R.bool.pref_enable_notices_default));
           NotificationManager.setNotificationsEnabled(this, notificationsEnabled);
           if (notificationsEnabled) {
                NotificationManager.scheduleAllNotifications(this);
            } else {
                NotificationManager.cancelAllNotifications(this);
            }
       }
    }

    @Override
    public void onCheckClick(@NonNull Task task) {
        Log.d(TAG, "onCheckClick: " + task);
        mRepository.removeValue(task.getKey());
    }

    @Override
    public void onCardClick(@NonNull Task task) {
        Log.d(TAG, "onCardClick: " + task);
        Intent editTaskIntent = new Intent(this, TaskActivity.class);
        editTaskIntent.putExtra(TaskActivity.TASK_EXTRA, task);
        startActivityAnimate(editTaskIntent, R.anim.slide_in_up, R.anim.slide_out_up);
    }

    @Override
    public void onTaskAdded(Task task) {
        Log.d(TAG, "onTaskAdded: " + task);
        mTasksAdapter.addTask(task);
        if (PreferenceUtils.isNotificationsEnabled(this)) {
            NotificationManager.scheduleNotification(MainActivity.this, task);
        }
    }

    @Override
    public void onTaskUpdated(Task task) {
        Log.d(TAG, "onTaskUpdated: " + task);
        mTasksAdapter.sort();
        if (PreferenceUtils.isNotificationsEnabled(this)) {
            NotificationManager.updateNotification(MainActivity.this, task);
        }
    }

    @Override
    public void onTaskRemoved(Task task) {
        Log.d(TAG, "onTaskRemoved: " + task);
        mTasksAdapter.removeTask(task);
        if (PreferenceUtils.isNotificationsEnabled(this)) {
            NotificationManager.cancelNotification(MainActivity.this, task);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "onActivityResult: Canceled Sign In");
                finish();
            }
        }
    }

    private void startActivityAnimate(Intent intent, int enterAnim, int exitAnim) {
        Log.d(TAG, "startActivityAnimate");
        startActivity(intent);
        overridePendingTransition(enterAnim, exitAnim);
    }

    @OnClick(R.id.fab_add_task)
    public void addTask() {
        Intent addNewTask = new Intent(MainActivity.this, TaskActivity.class);
        startActivityAnimate(addNewTask, R.anim.slide_in_up, R.anim.slide_out_up);
    }

    private void setUpRecycler() {
        Log.d(TAG, "setUpRecycler");

        mTasksAdapter = new TasksAdapter(this);
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
                Log.d(TAG, "onSwiped: " + key);
                mRepository.removeValue(key);
            }
        });
        touchHelper.attachToRecyclerView(mTasksRecycler);
    }

    private void setUpAuthListener() {
        Log.d(TAG, "setUpAuthListener");
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
        Log.d(TAG, "onSignedInInitialize");
        mRepository = FirebaseRepository.getInstance();
        mRepository.attachDatabaseListener(getString(R.string.order_key), this);
    }

    private void onSignedOutCleanup() {
        Log.d(TAG, "onSignedOutCleanup");
        mTasksAdapter.clear();
        detachDatabaseListener();
    }

    private void detachDatabaseListener() {
        Log.d(TAG, "detachDatabaseListener");
        if (mRepository != null) {
            mRepository.detachDatabaseReadListener();
        }
    }
}
