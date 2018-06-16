package io.github.andyradionov.egdetasks;

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
import java.util.List;
import java.util.Locale;

import io.github.andyradionov.egdetasks.model.Task;

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

    public TasksAdapter(List<Task> tasks, OnTaskCheckBoxClickListener checkBoxClickListener,
                        OnTaskCardClickListener cardClickListener) {
        mTasks = tasks;
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
            taskTextView.setText(task.getText());
            taskDateView.setText(DATE_FORMAT.format(task.getDueDate()));

            if (task.isDone()) {
                setCardChecked(true);
            }
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Task task = mTasks.get(position);

            switch (v.getId()) {
                case R.id.cb_is_done:
                    task.setDone(!task.isDone());
                    setCardChecked(task.isDone());
                    mCheckBoxClickListener.onCheckClick(task);
                    break;
                case R.id.cv_task_card:
                    mCardClickListener.onCardClick(task);
                    break;
            }
        }

        private void setCardChecked(boolean isChecked) {
            int flag = isChecked ? taskTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
                    : taskTextView.getPaintFlags() ^ Paint.STRIKE_THRU_TEXT_FLAG;
            isTaskDoneView.setChecked(isChecked);
            taskTextView.setPaintFlags(flag);
            taskDateView.setPaintFlags(flag);
        }
    }
}
