package io.github.andyradionov.tasksedge.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.github.andyradionov.tasksedge.R;
import io.github.andyradionov.tasksedge.data.database.Task;

/**
 * @author Andrey Radionov
 */

public class PreferenceUtils {
    private static final String TAG = PreferenceUtils.class.getSimpleName();

    private PreferenceUtils() {
    }

    public static synchronized void saveQuote(Context context, String quote) {
        Log.d(TAG, "saveQuote: " + quote);
        String quoteKey = context.getString(R.string.pref_quote_key);
        String quoteDateKey = context.getString(R.string.pref_quote_date_key);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(quoteKey, quote);
        editor.putLong(quoteDateKey, new Date().getTime());
        editor.apply();
    }

    public static synchronized String getQuote(Context context) {
        Log.d(TAG, "getQuote");
        String quoteKey = context.getString(R.string.pref_quote_key);
        String quoteDefault = context.getString(R.string.pref_quote_default);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(quoteKey, quoteDefault);
    }


    public static synchronized boolean isNotificationsEnabled(Context context) {
        Log.d(TAG, "isNotificationsEnabled");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String enableNoticesKey = context.getString(R.string.pref_enable_notices_key);
        boolean noticesEnabledDefault = context.getResources()
                .getBoolean(R.bool.pref_enable_notices_default);
        return sharedPreferences.getBoolean(enableNoticesKey, noticesEnabledDefault);
    }

    public static synchronized void saveWidgetTasks(Context context, List<Task> tasks) {
        Log.d(TAG, "saveWidgetTasks");
        String widgetTasksKey = context.getString(R.string.pref_widget_tasks_key);

        StringBuilder sb = new StringBuilder();
        int maxSize = context.getResources().getInteger(R.integer.widget_list_size);
        int limit = tasks.size() > maxSize ? tasks.size() - maxSize : 0;
        for (int i = tasks.size() - 1; i >= limit; i--) {
            sb.append(tasks.get(i).getText()).append("\n");
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(widgetTasksKey, sb.toString().trim());
        editor.apply();
    }

    public static synchronized List<String> getWidgetTasks(Context context) {
        Log.d(TAG, "getWidgetTasks");
        String widgetTasksKey = context.getString(R.string.pref_widget_tasks_key);
        String widgetTasksDefault = context.getString(R.string.pref_widget_tasks_default);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String tasksString = preferences.getString(widgetTasksKey, widgetTasksDefault);
        if (tasksString.equals(widgetTasksDefault)) {
            return Collections.emptyList();
        }
        return Arrays.asList(tasksString.split("\n"));
    }
}
