package io.github.andyradionov.tasksedge.utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import java.util.List;

import io.github.andyradionov.tasksedge.NotificationPublisher;
import io.github.andyradionov.tasksedge.R;
import io.github.andyradionov.tasksedge.database.Task;
import io.github.andyradionov.tasksedge.ui.MainActivity;


public class NotificationUtils {

    private static final int TASKS_PENDING_INTENT_ID = 8476;
    private static final String TASKS_NOTIFICATION_CHANNEL_ID = "tasks_notification_channel";
    private static final String TASKS_EDGE_NOTIFICATION_GROUP = "TasksEdge Notifications";

    public static void scheduleNotification(Context context, Task task) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        PendingIntent pendingIntent = createBroadcastIntent(context, task);

        alarmManager.set(AlarmManager.RTC_WAKEUP, task.getDueDate().getTime(),
                pendingIntent);
    }

    public static void scheduleAllNotifications(Context context, List<Task> tasks) {

        for (Task task : tasks) {
            scheduleNotification(context, task);
        }
    }

    public static void cancelNotification(Context context, Task task) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        PendingIntent pendingIntent = createBroadcastIntent(context, task);
        alarmManager.cancel(pendingIntent);
    }

    public static void cancelAllNotifications(Context context, List<Task> tasks) {

        for (Task task : tasks) {
            cancelNotification(context, task);
        }
    }

    private static Notification createNotification(Context context, String text) {

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) return null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    TASKS_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat
                .Builder(context, TASKS_NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_done_black)
                .setLargeIcon(largeIcon(context))
                .setContentTitle("You have task!")
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setGroup(TASKS_EDGE_NOTIFICATION_GROUP)
                .setContentIntent(createContentIntent(context))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        return notificationBuilder.build();
    }

    private static PendingIntent createContentIntent(Context context) {

        Intent startActivityIntent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(
                context,
                TASKS_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static PendingIntent createBroadcastIntent(Context context, Task task) {
        int id = (int) task.getId();
        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.EXTRA_NOTIFICATION_ID, id);
        notificationIntent.putExtra(NotificationPublisher.EXTRA_NOTIFICATION,
                createNotification(context, task.getText()));
        return PendingIntent.getBroadcast(context, id,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Bitmap largeIcon(Context context) {
        Resources resources = context.getResources();
        return BitmapFactory.decodeResource(resources, R.drawable.ic_done_black);
    }
}
