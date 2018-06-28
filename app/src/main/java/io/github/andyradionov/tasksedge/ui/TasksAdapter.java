package io.github.andyradionov.tasksedge.ui;

import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import io.github.andyradionov.tasksedge.R;
import io.github.andyradionov.tasksedge.model.Task;

/**
 * @author Andrey Radionov
 */

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskViewHolder> {
    private static final DateFormat DATE_FORMAT =
            new SimpleDateFormat("dd:MM:yyyy, HH:mm", Locale.ROOT);

    interface OnTaskCheckBoxClickListener {
        void onCheckClick(@NonNull Task task);
    }
    interface OnTaskCardClickListener {
        void onCardClick(@NonNull Task task);
    }

    private List<Task> mTasks;

    private OnTaskCheckBoxClickListener mCheckBoxClickListener;
    private OnTaskCardClickListener mCardClickListener;
    private Comparator<Task> mComparator = intComparator();

    public TasksAdapter(OnTaskCheckBoxClickListener checkBoxClickListener,
                        OnTaskCardClickListener cardClickListener) {
        mTasks = new ArrayList<>();
        mCheckBoxClickListener = checkBoxClickListener;
        mCardClickListener = cardClickListener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final CardView taskCard = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);

        return new TaskViewHolder(taskCard);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = mTasks.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return mTasks != null ? mTasks.size() : 0;
    }

    public void clear() {
        mTasks.clear();
        notifyDataSetChanged();
    }

    public void add(Task task) {
        mTasks.add(0, task);
        sort();
        notifyDataSetChanged();
    }

    public void remove(Task task) {
        mTasks.remove(task);
        notifyDataSetChanged();
    }

    public void sort() {
        Collections.sort(mTasks, mComparator);
        notifyDataSetChanged();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CheckBox isTaskDoneView;
        private TextView taskTextView;
        private TextView taskDateView;

        private TaskViewHolder(View itemView) {
            super(itemView);
            taskTextView = itemView.findViewById(R.id.tv_task_text);
            taskDateView = itemView.findViewById(R.id.tv_task_date);
            isTaskDoneView = itemView.findViewById(R.id.cb_is_done);
            isTaskDoneView.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        private void bind(Task task) {
            itemView.setTag(task.getKey());
            taskTextView.setText(task.getText());
            taskDateView.setText(DATE_FORMAT.format(task.getDueDate()));
            setCardChecked(false);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Task task = mTasks.get(position);

            switch (v.getId()) {
                case R.id.cb_is_done:
                    mCheckBoxClickListener.onCheckClick(task);
                    setCardChecked(true);
                    break;
                case R.id.cv_task_card:
                    mCardClickListener.onCardClick(task);
                    break;
            }
        }

        private void setCardChecked(boolean isChecked) {

            int flag = isChecked ? taskTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
                    : taskTextView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG;

            isTaskDoneView.setChecked(isChecked);
            taskTextView.setPaintFlags(flag);
            taskDateView.setPaintFlags(flag);
        }
    }

    private Comparator<Task> intComparator() {
        return new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                return o2.getDueDate().compareTo(o1.getDueDate());
            }
        };
    }
}
