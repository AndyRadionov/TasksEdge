package io.github.andyradionov.tasksedge.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import io.github.andyradionov.tasksedge.R;
import io.github.andyradionov.tasksedge.utils.PreferenceUtils;

/**
 * @author Andrey Radionov
 */

public class WidgetListService extends RemoteViewsService {
    private static final String TAG = WidgetListService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext());
    }


    private static class ListRemoteViewsFactory implements RemoteViewsFactory {
        private static final String TAG = ListRemoteViewsFactory.class.getSimpleName();

        private Context mContext;
        private List<String> mTasksText;

        private ListRemoteViewsFactory(Context applicationContext) {
            mContext = applicationContext;
            mTasksText = new ArrayList<>();
        }

        @Override
        public void onCreate() {
        }

        @Override
        public void onDataSetChanged() {
            Log.d(TAG, "onDataSetChanged");
            mTasksText = PreferenceUtils.getWidgetTasks(mContext);
        }

        @Override
        public void onDestroy() {
        }

        @Override
        public int getCount() {
            Log.d(TAG, "getCount");
            if (mTasksText == null) return 0;
            if (mTasksText.size() > 4) return 4;
            return mTasksText.size();
        }

        /**
         * This method acts like the onBindViewHolder method in an Adapter
         *
         * @param position The current position of the item in the GridView to be displayed
         * @return The RemoteViews object to display for the provided postion
         */
        @Override
        public RemoteViews getViewAt(int position) {
            Log.d(TAG, "getViewAt: " + position + "List Size: " + mTasksText.size());
            if (mTasksText.size() == 0 || position >= mTasksText.size()) return null;

            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.item_task_widget);
            views.setTextViewText(R.id.tv_widget_task_text, mTasksText.get(position));
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
    }
}
