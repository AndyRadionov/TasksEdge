package io.github.andyradionov.egdetasks;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.github.andyradionov.egdetasks.model.Task;

/**
 * @author Andrey Radionov
 */

public class TaskActivity extends AppCompatActivity {
    public static final String TASK_EXTRA = "task_extra";

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy", Locale.ROOT);
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.ROOT);
    private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd.MM.yyyyHH:mm",
            Locale.ROOT);


    private EditText mTextView;
    private EditText mPriorityView;
    private EditText mDateView;
    private EditText mTimeView;
    private Task mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        Intent intent = getIntent();
        if (intent.hasExtra(TASK_EXTRA)) {
            setUpToolbar("Edit Task");
            mTask = intent.getParcelableExtra(TASK_EXTRA);
        } else {
            setUpToolbar("Add Task");
            mTask = new Task();
        }

        initViews();
        setUpDateTimePickers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.action_done:
                if (checkInput()) {
                    parseTaskInput();
                    Intent taskResult = new Intent();
                    taskResult.putExtra(TaskActivity.TASK_EXTRA, mTask);
                    setResult(RESULT_OK, taskResult);
                    finish();
                    return true;
                } else {
                    return false;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpToolbar(String title) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarTitle = toolbar.findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(title);
        toolbar.setNavigationIcon(R.drawable.ic_close_black);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    private void initViews() {
        mTextView = findViewById(R.id.et_task_text);
        mPriorityView = findViewById(R.id.et_task_priority);
        mDateView = findViewById(R.id.et_date);
        mTimeView = findViewById(R.id.et_time);

        mTextView.setText(mTask.getText());
        mPriorityView.setText(String.valueOf(mTask.getPriority()));
        mDateView.setText(DATE_FORMAT.format(mTask.getDueDate()));
        mTimeView.setText(TIME_FORMAT.format(mTask.getDueDate()));
    }

    private void setUpDateTimePickers() {
        final Calendar calendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mDateView.setText(DATE_FORMAT.format(calendar.getTime()));
            }

        };

        mDateView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(TaskActivity.this, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        mTimeView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(TaskActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        mTimeView.setText(String.format(Locale.ROOT, "%d:%d",
                                selectedHour, selectedMinute));
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });
    }

    private boolean checkInput() {
        String text = mTextView.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            mTextView.setBackgroundColor(Color.parseColor("#FF0000"));
            return false;
        }
        return true;
    }

    private void parseTaskInput() {
        String text = mTextView.getText().toString().trim();
        int priority = Integer.parseInt(mPriorityView.getText().toString());
        String dateText = mDateView.getText().toString().trim();
        String timeText = mTimeView.getText().toString().trim();

        mTask.setText(text);
        mTask.setPriority(priority);

        try {
            Date date = DATE_TIME_FORMAT.parse(dateText + timeText);
            mTask.setDueDate(date);
        } catch (ParseException e) {
            Log.d(TAG, "DateTime parse Error");
        }

    }
}