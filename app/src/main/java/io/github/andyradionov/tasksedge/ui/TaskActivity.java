package io.github.andyradionov.tasksedge.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
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

import io.github.andyradionov.tasksedge.R;
import io.github.andyradionov.tasksedge.database.FirebaseRepository;
import io.github.andyradionov.tasksedge.model.Task;
import io.github.andyradionov.tasksedge.notifications.NotificationUtils;
import io.github.andyradionov.tasksedge.utils.AnalyticsUtils;
import io.github.andyradionov.tasksedge.utils.DateUtils;

/**
 * @author Andrey Radionov
 */

public class TaskActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String TASK_EXTRA = "task_extra";

    private FirebaseRepository mRepository;

    private EditText mTextInput;
    private EditText mDateView;
    private EditText mTimeView;
    private TextView mQuoteView;
    private Task mTask;
    private boolean isNewTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_task);

        mRepository = FirebaseRepository.getInstance();

        Intent intent = getIntent();
        if (intent.hasExtra(TASK_EXTRA)) {
            setUpToolbar(getString(R.string.edit_task_title));
            mTask = intent.getParcelableExtra(TASK_EXTRA);
        } else {
            isNewTask = true;
            setUpToolbar(getString(R.string.add_task_title));
            mTask = new Task();
        }

        initViews();
        setUpDateTimePickers();
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
            parseTaskInput();
            if (isNewTask) {
                Log.d(TAG, "onOptionsItemSelected: Add New Task");
                mRepository.addValue(mTask);
                AnalyticsUtils.logNewTaskLengthEvent(this, mTask.getText().length());
            } else {
                Log.d(TAG, "onOptionsItemSelected: Edit Task");
                mRepository.updateValue(mTask);
                NotificationUtils.cancelNotification(this, mTask);
            }
            NotificationUtils.scheduleNotification(this, mTask);
            finish();
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

    private void setUpToolbar(String title) {
        Log.d(TAG, "setUpToolbar");
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarTitle = toolbar.findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(title);
        toolbar.setNavigationIcon(R.drawable.ic_close_white);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    private void initViews() {
        Log.d(TAG, "initViews");
        mTextInput = findViewById(R.id.et_input_text);
        mDateView = findViewById(R.id.et_date);
        mTimeView = findViewById(R.id.et_time);
        mQuoteView = findViewById(R.id.tv_quote);

        mTextInput.setText(mTask.getText());
        mDateView.setText(DateUtils.formatDate(mTask.getDueDate()));
        mTimeView.setText(DateUtils.formatTime(mTask.getDueDate()));
    }

    private void setQuote() {
        Log.d(TAG, "setQuote");
        String quoteKey = getString(R.string.pref_quote_key);
        String quoteDefault = getString(R.string.pref_quote_default);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String quote = prefs.getString(quoteKey, quoteDefault);
        mQuoteView.setText(quote);
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
                mDateView.setText(DateUtils.formatDate(calendar.getTime()));
            }

        };

        mDateView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(TaskActivity.this, date, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        mTimeView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker = new TimePickerDialog(TaskActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour,
                                          int selectedMinute) {
                        mTimeView.setText(String.format(Locale.ROOT, getString(R.string.time_format),
                                selectedHour, selectedMinute));
                    }
                }, hour, minute, true);

                mTimePicker.setTitle(getString(R.string.time_picker_title));
                mTimePicker.show();

            }
        });
    }

    private boolean checkInput() {
        Log.d(TAG, "checkInput");
        String text = mTextInput.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            mTextInput.setError(getString(R.string.input_error));
            return false;
        }
        return true;
    }

    private void parseTaskInput() {
        Log.d(TAG, "parseTaskInput");
        String text = mTextInput.getText().toString().trim();
        String dateText = mDateView.getText().toString().trim();
        String timeText = mTimeView.getText().toString().trim();

        mTask.setText(text);

        Date date = DateUtils.parseDateTime(dateText + ", " + timeText);
        if (date != null) {
            mTask.setDueDate(date);
        }
    }
}
