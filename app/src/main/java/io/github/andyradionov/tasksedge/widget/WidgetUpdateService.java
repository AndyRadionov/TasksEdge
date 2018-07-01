package io.github.andyradionov.tasksedge.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import io.github.andyradionov.tasksedge.R;
import io.github.andyradionov.tasksedge.database.FirebaseRepository;
import io.github.andyradionov.tasksedge.database.RepoListCallbacks;
import io.github.andyradionov.tasksedge.database.Task;
import io.github.andyradionov.tasksedge.utils.PreferenceUtils;

/**
 * @author Andrey Radionov
 */

public class WidgetUpdateService extends IntentService implements RepoListCallbacks {
    public static final String ACTION_UPDATE_WIDGET = "action_update";

    private FirebaseRepository mRepository;

    public WidgetUpdateService() {
        super(WidgetUpdateService.class.getSimpleName());
    }

    public static void startActionUpdatePlantWidgets(Context context) {
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

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) return;

        mRepository = FirebaseRepository.getInstance();
        mRepository.attachValueListener(getString(R.string.order_key), this);
    }

    @Override
    public void onListFetched(List<Task> tasks) {

        PreferenceUtils.saveWidgetTasks(this, tasks);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                TasksWidgetProvider.class));

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.lv_widget_tasks_container);
        TasksWidgetProvider.updateTasksWidgets(this, appWidgetManager, appWidgetIds);
    }
}
