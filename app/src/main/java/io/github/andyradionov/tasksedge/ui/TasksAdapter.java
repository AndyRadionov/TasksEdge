package io.github.andyradionov.tasksedge.ui;

import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.github.andyradionov.tasksedge.R;
import io.github.andyradionov.tasksedge.model.Task;
import io.github.andyradionov.tasksedge.utils.DateUtils;

/**
 * @author Andrey Radionov
 */

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskViewHolder> {

    private static final String TAG = TasksAdapter.class.getSimpleName();

    interface OnTaskCardClickListener {
        void onCardClick(@NonNull Task task);
        void onCheckClick(@NonNull Task task);
    }

    private List<Task> mTasks;
    private OnTaskCardClickListener mCardClickListener;
    private Comparator<Task> mComparator = initComparator();

    public TasksAdapter(OnTaskCardClickListener cardClickListener) {
        Log.d(TAG, "TasksAdapter: constructor call");
        mTasks = new ArrayList<>();
        mCardClickListener = cardClickListener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        final CardView taskCard = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);

        return new TaskViewHolder(taskCard);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder");
        Task task = mTasks.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount");
        return mTasks != null ? mTasks.size() : 0;
    }

    public void clear() {
        Log.d(TAG, "clear");
        mTasks.clear();
        notifyDataSetChanged();
    }

    public void addTask(Task task) {
        Log.d(TAG, "addTask: " + task);
        mTasks.add(0, task);
        sort();
        notifyDataSetChanged();
    }

    public void removeTask(Task task) {
        Log.d(TAG, "removeTask: " + task);
        mTasks.remove(task);
        notifyDataSetChanged();
    }

    public void sort() {
        Log.d(TAG, "sort");
        Collections.sort(mTasks, mComparator);
        notifyDataSetChanged();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CheckBox isTaskDoneView;
        private TextView taskTextView;
        private TextView taskDateView;

        private TaskViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "TaskViewHolder: constructor call");
            taskTextView = itemView.findViewById(R.id.tv_task_text);
            taskDateView = itemView.findViewById(R.id.tv_task_date);
            isTaskDoneView = itemView.findViewById(R.id.cb_is_done);
            isTaskDoneView.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        private void bind(Task task) {
            Log.d(TAG, "bind: " + task);
            itemView.setTag(task.getKey());
            taskTextView.setText(task.getText());
            taskDateView.setText(DateUtils.formatDateTime(task.getDueDate()));
            setCardChecked(false);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Task task = mTasks.get(position);

            switch (v.getId()) {
                case R.id.cb_is_done:
                    Log.d(TAG, "onClick: onCheckClick: " + task);
                    mCardClickListener.onCheckClick(task);
                    setCardChecked(true);
                    break;
                case R.id.cv_task_card:
                    Log.d(TAG, "onClick: onCardClick: " + task);
                    mCardClickListener.onCardClick(task);
            }
        }

        private void setCardChecked(boolean isChecked) {
            Log.d(TAG, "setCardChecked: " + isChecked);
            int flag = isChecked ? taskTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
                    : taskTextView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG;

            isTaskDoneView.setChecked(isChecked);
            taskTextView.setPaintFlags(flag);
            taskDateView.setPaintFlags(flag);
        }
    }

    private Comparator<Task> initComparator() {
        Log.d(TAG, "initComparator");
        return new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                return o2.getDueDate().compareTo(o1.getDueDate());
            }
        };
    }
}
