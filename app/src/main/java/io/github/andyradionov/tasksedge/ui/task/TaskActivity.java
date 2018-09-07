package io.github.andyradionov.tasksedge.ui.task;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.github.andyradionov.tasksedge.R;
import io.github.andyradionov.tasksedge.data.database.Task;
import io.github.andyradionov.tasksedge.databinding.ActivityTaskBinding;
import io.github.andyradionov.tasksedge.ui.common.BaseActivity;
import io.github.andyradionov.tasksedge.ui.main.MainActivity;
import io.github.andyradionov.tasksedge.utils.AnalyticsUtils;
import io.github.andyradionov.tasksedge.utils.DateUtils;
import io.github.andyradionov.tasksedge.utils.PreferenceUtils;

/**
 * @author Andrey Radionov
 */

public class TaskActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String TASK_EXTRA = "task_extra";

    private Task mTask;
    private boolean isNewTask;
    private ActivityTaskBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_task);
        bindToolbar(mBinding.toolbar, mBinding.tvToolbarTitle);

        Intent intent = getIntent();
        if (intent.hasExtra(TASK_EXTRA)) {
            setUpToolbar(getString(R.string.edit_task_title), R.drawable.ic_close_white);
            mTask = intent.getParcelableExtra(TASK_EXTRA);
        } else {
            isNewTask = true;
            setUpToolbar(getString(R.string.add_task_title), R.drawable.ic_close_white);
            mTask = new Task();
        }
        initViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_done && checkInput()) {
            Log.d(TAG, "onOptionsItemSelected: Action Done");
            handleDoneAction();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            Log.d(TAG, "onOptionsItemSelected: Home");
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        setQuote();
    }

    private void handleDoneAction() {
        parseTaskInput();
        TaskViewModel viewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        if (isNewTask) {
            Log.d(TAG, "onOptionsItemSelected: Add New Task");
            viewModel.addTask(mTask);
            AnalyticsUtils.logNewTaskLengthEvent(this, mTask.getText().length());
        } else {
            Log.d(TAG, "onOptionsItemSelected: Edit Task");
            viewModel.updateTask(mTask);
        }
        finish();
    }

    private void initViews() {
        Log.d(TAG, "initViews");
        mBinding.etInputText.setText(mTask.getText());
        mBinding.etDate.setText(DateUtils.formatDate(mTask.getDueDate()));
        mBinding.etTime.setText(DateUtils.formatTime(mTask.getDueDate()));

        mBinding.etInputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    handleDoneAction();
                    return true;
                } else {
                    return false;
                }
            }
        });
        setUpDateTimePickers();
    }

    private void setQuote() {
        Log.d(TAG, "setQuote");
        String quote = PreferenceUtils.getQuote(this);
        mBinding.tvQuote.setText(quote);
    }

    private void setUpDateTimePickers() {
        Log.d(TAG, "setUpDateTimePickers");
        final Calendar calendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mBinding.etDate.setText(DateUtils.formatDate(calendar.getTime()));
            }

        };
        mBinding.etDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DatePickerDialog dialog =
                        new DatePickerDialog(TaskActivity.this, date, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMinDate(new Date().getTime());
                dialog.show();
            }
        });

        mBinding.etTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePicker = new TimePickerDialog(TaskActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour,
                                          int selectedMinute) {
                        if (!checkTimeInput(selectedHour, selectedMinute)) {
                            showDateError();
                            return;
                        }
                        mBinding.etTime.setText(String.format(Locale.ROOT, getString(R.string.time_format),
                                selectedHour, selectedMinute));
                    }
                }, hour, minute, true);
                timePicker.setTitle(getString(R.string.time_picker_title));
                timePicker.show();

            }
        });
    }

    private void showDateError() {
        Toast.makeText(TaskActivity.this, R.string.error_date_input, Toast.LENGTH_SHORT).show();
    }

    private boolean checkInput() {
        Log.d(TAG, "checkInput");
        String text = mBinding.etTime.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            mBinding.etInputText.setError(getString(R.string.input_error));
            return false;
        } else if (parseDateInput() == null) {
            showDateError();
            return false;
        }
        return true;
    }

    private void parseTaskInput() {
        Log.d(TAG, "parseTaskInput");
        String text = mBinding.etInputText.getText().toString().replaceAll("\\n+", " ").trim();

        mTask.setText(text);

        Date date = parseDateInput();
        if (date != null) {
            mTask.setDueDate(date);
        }
    }

    private boolean checkTimeInput(int hour, int minute) {
        Log.d(TAG, "checkTimeInput");
        String dateText = mBinding.etDate.getText().toString().trim();
        Date date = DateUtils.parseDateTime(dateText + ", " + hour + ":" + minute);
        return date != null && date.after(new Date());
    }

    private Date parseDateInput() {
        Log.d(TAG, "parseDateInput");
        String dateText = mBinding.etDate.getText().toString().trim();
        String timeText = mBinding.etTime.getText().toString().trim();
        Date date = DateUtils.parseDateTime(dateText + ", " + timeText);
        if (date != null && date.after(new Date())) {
            return date;
        }
        return null;
    }
}
