package io.github.andyradionov.egdetasks;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.github.andyradionov.egdetasks.model.Task;

public class AddTaskActivity extends AppCompatActivity {

    private EditText mTextView;
    private EditText mPriorityView;
    private EditText mDateView;
    private EditText mTimeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarTitle = toolbar.findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText("Add Task");
        toolbar.setNavigationIcon(R.drawable.ic_close_black);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        initViews();

        final Calendar calendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd.MM.yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ROOT);

                mDateView.setText(sdf.format(calendar.getTime()));
            }

        };

        mDateView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddTaskActivity.this, date, calendar
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
                mTimePicker = new TimePickerDialog(AddTaskActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        mTimeView.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });
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
                    Task task = createTask();
                    Intent taskResult = new Intent();
                    taskResult.putExtra(EditTaskActivity.TASK_EXTRA, task);
                    setResult(RESULT_OK, taskResult);
                    finish();
                    return true;
                } else {
                    return false;
                }
        }
        return super.onOptionsItemSelected(item);
    }

//    private void updateLabel() {
//        String myFormat = "MM/dd/yy"; //In which you need put here
//        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//
//        edittext.setText(sdf.format(myCalendar.getTime()));
//    }

    private void initViews() {
        mTextView = findViewById(R.id.et_task_text);
        mPriorityView = findViewById(R.id.et_task_priority);
        mDateView = findViewById(R.id.et_date);
        mTimeView = findViewById(R.id.et_time);
    }
    private boolean checkInput() {
        return true;
    }

    private Task createTask() {
        String text = mTextView.getText().toString().trim();
        int priority = Integer.parseInt(mPriorityView.getText().toString());
        String dateText = mDateView.getText().toString().trim();
        String timeText = mTimeView.getText().toString().trim();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyyHH:mm");
        Date date = new Date();
        try {
            date = sdf.parse(dateText + timeText);
        } catch (ParseException e) {
            return null;
        }

        return new Task(0, text, date, priority);
    }
}
