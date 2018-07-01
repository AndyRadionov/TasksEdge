package io.github.andyradionov.tasksedge.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.andyradionov.tasksedge.R;
import io.github.andyradionov.tasksedge.database.FirebaseRepository;
import io.github.andyradionov.tasksedge.database.RepoItemCallbacks;
import io.github.andyradionov.tasksedge.database.RepoListCallbacks;
import io.github.andyradionov.tasksedge.database.Task;

/**
 * @author Andrey Radionov
 */

public class WidgetListService extends RemoteViewsService {
    private static final String TAG = WidgetListService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext());
    }

    private static synchronized void notifyDataChange(Context context) {
        Log.d(TAG, "notifyDataChange");
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, TasksWidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.lv_widget_tasks_container);

        TasksWidgetProvider.updateTasksPlantWidgets(context, appWidgetManager, appWidgetIds);
    }

    private static class ListRemoteViewsFactory implements RemoteViewsFactory, RepoListCallbacks {
        private static final String TAG = ListRemoteViewsFactory.class.getSimpleName();

        private Context mContext;
        private FirebaseRepository mRepository;
        private List<Task> mTasks;

        private ListRemoteViewsFactory(Context applicationContext) {
            mContext = applicationContext;
            mTasks = new ArrayList<>();
        }

        @Override
        public void onCreate() {
        }

        @Override
        public void onDataSetChanged() {
            Log.d(TAG, "onDataSetChanged");
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            if (firebaseAuth.getCurrentUser() == null) return;

            mRepository = FirebaseRepository.getInstance();
            mRepository.attachValueListener(mContext.getString(R.string.order_key), this);
        }

        @Override
        public void onDestroy() {
        }

        @Override
        public int getCount() {
            Log.d(TAG, "getCount");
            if (mTasks == null) return 0;
            if (mTasks.size() > 4) return 4;
            return mTasks.size();
        }

        /**
         * This method acts like the onBindViewHolder method in an Adapter
         *
         * @param position The current position of the item in the GridView to be displayed
         * @return The RemoteViews object to display for the provided postion
         */
        @Override
        public RemoteViews getViewAt(int position) {
            Log.d(TAG, "getViewAt: " + position + "List Size: " + mTasks.size());
            if (mTasks.size() == 0 || position >= mTasks.size()) return null;

            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.item_task_widget);
            views.setTextViewText(R.id.tv_widget_task_text, mTasks.get(position).getText());
            Intent fillInIntent = new Intent();
            fillInIntent.putExtra(AppWidgetManager.EXTRA_CUSTOM_INFO, position);
            views.setOnClickFillInIntent(R.id.item_widget, fillInIntent);
            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public void onListFetched(List<Task> tasks) {
            Log.d(TAG, "onListFetched: " + tasks);
            mTasks.clear();
            mTasks.addAll(tasks);
            Collections.reverse(mTasks);

            notifyDataChange(mContext);
        }
    }
}
