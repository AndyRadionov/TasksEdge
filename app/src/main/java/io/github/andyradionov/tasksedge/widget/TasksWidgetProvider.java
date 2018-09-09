package io.github.andyradionov.tasksedge.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import io.github.andyradionov.tasksedge.R;
import io.github.andyradionov.tasksedge.ui.main.MainActivity;
import io.github.andyradionov.tasksedge.ui.task.TaskActivity;

/**
 * Implementation of App Widget functionality.
 */
public class TasksWidgetProvider extends AppWidgetProvider {
    private static final String TAG = TasksWidgetProvider.class.getSimpleName();

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                        int appWidgetId) {
        Log.d(TAG, "updateAppWidget");
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.tasks_widget);

        views.setOnClickPendingIntent(R.id.ibtn_add_task,
                getTaskPendingIntent(context, TaskActivity.class));
        views.setPendingIntentTemplate(R.id.lv_widget_tasks_container,
                getTaskPendingIntent(context, MainActivity.class));

        views.setPendingIntentTemplate(R.id.lv_widget_tasks_container,
                getTaskPendingIntent(context, MainActivity.class));

        views.setEmptyView(R.id.lv_widget_tasks_container, R.id.empty_view);

        Intent intent = new Intent(context, WidgetListService.class);
        views.setRemoteAdapter(R.id.lv_widget_tasks_container, intent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate");
        WidgetUpdateService.startActionUpdatePlantWidgets(context);
    }

    public static void updateTasksWidgets(Context context, AppWidgetManager appWidgetManager,
                                          int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }

    private static PendingIntent getTaskPendingIntent(Context context, Class activity) {
        Log.d(TAG, "getTaskPendingIntent");
        Intent intent = new Intent(context, activity);
        return PendingIntent.getActivity(context, 0, intent, 0);
    }
}

