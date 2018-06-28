package io.github.andyradionov.tasksedge.utils;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Date;

/**
 * @author Andrey Radionov
 */

public class AnalyticsUtils {
    private static final String ANALYTIC_PARAM_TEXT_LENGTH = "text_length";
    private static final String ANALYTIC_EVENT_NEW_TASK_LENGTH = "add_new_task";

    private AnalyticsUtils() {
    }

    public static synchronized void logAppOpenEvent(Context context) {
        Bundle openDate = new Bundle();
        openDate.putString(FirebaseAnalytics.Param.START_DATE, DateUtils.formatDateTime(new Date()));
        FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Event.APP_OPEN, openDate);
    }

    public static synchronized void logNewTaskLengthEvent(Context context, int length) {
        Bundle textLength = new Bundle();
        textLength.putInt(ANALYTIC_PARAM_TEXT_LENGTH, length);
        FirebaseAnalytics.getInstance(context).logEvent(ANALYTIC_EVENT_NEW_TASK_LENGTH, textLength);
    }
}
