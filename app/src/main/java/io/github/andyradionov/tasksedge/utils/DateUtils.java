package io.github.andyradionov.tasksedge.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Andrey Radionov
 */

public class DateUtils {
    private static final String TAG = DateUtils.class.getSimpleName();

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy", Locale.ROOT);
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.ROOT);
    private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd.MM.yyyyHH:mm",
            Locale.ROOT);

    private DateUtils() {
    }

    public static synchronized String formatDateTime(Date dateTime) {
        Log.d(TAG, "formatDateTime: " + dateTime);
        return DATE_TIME_FORMAT.format(dateTime);
    }

    public static synchronized Date parseDateTime(String dateTime) {
        Log.d(TAG, "parseDateTime: " + dateTime);
        try {
            return DATE_TIME_FORMAT.parse(dateTime);
        } catch (ParseException e) {
            Log.d(TAG, "parseDateTime: " + dateTime);
            return null;
        }
    }

    public static synchronized String formatDate(Date dateTime) {
        Log.d(TAG, "formatDate: " + dateTime);
        return DATE_FORMAT.format(dateTime);
    }

    public static synchronized String formatTime(Date dateTime) {
        Log.d(TAG, "formatTime: " + dateTime);
        return TIME_FORMAT.format(dateTime);
    }
}
