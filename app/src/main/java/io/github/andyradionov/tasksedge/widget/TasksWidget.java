package io.github.andyradionov.tasksedge.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import io.github.andyradionov.tasksedge.R;
import io.github.andyradionov.tasksedge.ui.MainActivity;
import io.github.andyradionov.tasksedge.ui.TaskActivity;

/**
 * Implementation of App Widget functionality.
 */
public class TasksWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.tasks_widget);

        views.setOnClickPendingIntent(R.id.widget,
                getAddTaskPendingIntent(context, TaskActivity.class));
        views.setOnClickPendingIntent(R.id.tv_widget,
                getAddTaskPendingIntent(context, MainActivity.class));
        views.setOnClickPendingIntent(R.id.ibtn_add_task,
                getAddTaskPendingIntent(context, TaskActivity.class));

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {}

    @Override
    public void onDisabled(Context context) {}

    private static PendingIntent getAddTaskPendingIntent(Context context, Class activity) {
        Intent intent = new Intent(context, activity);
        return PendingIntent.getActivity(context, 0, intent, 0);
    }
}

