package io.github.andyradionov.tasksedge.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import java.util.Date;

import io.github.andyradionov.tasksedge.R;

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
}
