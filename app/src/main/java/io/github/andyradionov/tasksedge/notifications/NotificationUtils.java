package io.github.andyradionov.tasksedge.notifications;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import java.util.Date;

import io.github.andyradionov.tasksedge.R;
import io.github.andyradionov.tasksedge.model.Task;
import io.github.andyradionov.tasksedge.ui.MainActivity;


public class NotificationUtils {

    private static final int TASKS_PENDING_INTENT_ID = 8476;
    private static final String TASKS_NOTIFICATION_CHANNEL_ID = "tasks_notification_channel";
    private static final String TASKS_EDGE_NOTIFICATION_GROUP = "TasksEdge Notifications";

    public static synchronized void scheduleNotification(Context context, Task task) {
        if (task.getDueDate().compareTo(new Date()) <= 0) return;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;
        PendingIntent pendingIntent = createBroadcastIntent(context, task);

        alarmManager.set(AlarmManager.RTC_WAKEUP, task.getDueDate().getTime(),
                pendingIntent);
    }


    public static synchronized void cancelNotification(Context context, Task task) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        PendingIntent pendingIntent = createBroadcastIntent(context, task);
        alarmManager.cancel(pendingIntent);
    }

    public static synchronized void updateNotification(Context context, Task task) {
        cancelNotification(context, task);
        scheduleNotification(context, task);
    }

    public static synchronized void scheduleAllNotifications(Context context) {
        Intent noticeIntent = new Intent(NotificationIntentService.ACTION_SCHEDULE_ALL);
        context.startService(noticeIntent);
    }

    public static synchronized void cancelAllNotifications(Context context) {
        Intent noticeIntent = new Intent(NotificationIntentService.ACTION_CANCEL_ALL);
        context.startService(noticeIntent);
    }

    public static synchronized void setNotificationsEnabled(Context context, boolean isEnabled) {
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        int state = isEnabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;

        pm.setComponentEnabledSetting(receiver,
                state,
                PackageManager.DONT_KILL_APP);
    }

    private static synchronized Notification createNotification(Context context, String text) {

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
                .setSmallIcon(R.drawable.ic_done_white)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getString(R.string.notice_title))
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

    private static synchronized PendingIntent createContentIntent(Context context) {

        Intent startActivityIntent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(
                context,
                TASKS_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static synchronized PendingIntent createBroadcastIntent(Context context, Task task) {
        int id = task.getId();
        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.EXTRA_NOTIFICATION_ID, id);
        notificationIntent.putExtra(NotificationPublisher.EXTRA_NOTIFICATION,
                createNotification(context, task.getText()));
        return PendingIntent.getBroadcast(context, id,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static synchronized Bitmap largeIcon(Context context) {
        Resources resources = context.getResources();
        return BitmapFactory.decodeResource(resources, R.drawable.ic_done_white);
    }
}
