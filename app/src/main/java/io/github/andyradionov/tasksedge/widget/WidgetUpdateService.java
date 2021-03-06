package io.github.andyradionov.tasksedge.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import javax.inject.Inject;

import io.github.andyradionov.tasksedge.R;
import io.github.andyradionov.tasksedge.app.App;
import io.github.andyradionov.tasksedge.data.database.FirebaseRepository;
import io.github.andyradionov.tasksedge.data.database.RepoListCallbacks;
import io.github.andyradionov.tasksedge.data.database.Task;
import io.github.andyradionov.tasksedge.utils.PreferenceUtils;

/**
 * @author Andrey Radionov
 */

public class WidgetUpdateService extends IntentService implements RepoListCallbacks {
    private static final String TAG = WidgetUpdateService.class.getSimpleName();
    public static final String ACTION_UPDATE_WIDGET = "action_update";

    @Inject
    FirebaseRepository mFirebaseRepository;

    @Inject
    public WidgetUpdateService() {
        super(WidgetUpdateService.class.getSimpleName());
        App.getAppComponent().inject(this);
    }

    public static void startActionUpdatePlantWidgets(Context context) {
        Log.d(TAG, "startActionUpdatePlantWidgets: ");
        Intent intent = new Intent(context, WidgetUpdateService.class);
        intent.setAction(ACTION_UPDATE_WIDGET);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_WIDGET.equals(action)) {
                handleUpdateWidget();
            }
        }
    }

    private void handleUpdateWidget() {
        Log.d(TAG, "handleUpdateWidget");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) return;
        mFirebaseRepository.performListFetch(getString(R.string.order_key), this);
    }

    @Override
    public void onListFetched(List<Task> tasks) {
        Log.d(TAG, "onListFetched" + tasks.size());
        PreferenceUtils.saveWidgetTasks(this, tasks);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                TasksWidgetProvider.class));

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.lv_widget_tasks_container);
        TasksWidgetProvider.updateTasksWidgets(this, appWidgetManager, appWidgetIds);
    }
}
