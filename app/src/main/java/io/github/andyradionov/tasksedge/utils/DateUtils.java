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
    private static final DateFormat DATE_FORMAT =
            new SimpleDateFormat("dd:MM:yyyy, HH:mm", Locale.ROOT);

    private DateUtils() {
    }

    public static synchronized String formatDateTime(Date dateTime) {
        return DATE_FORMAT.format(dateTime);
    }

    public static synchronized Date parseDateTime(String dateTime) {
        try {
            return DATE_FORMAT.parse(dateTime);
        } catch (ParseException e) {
            Log.d(TAG, "parseDateTime: " + dateTime);
            return null;
        }

    }
}
