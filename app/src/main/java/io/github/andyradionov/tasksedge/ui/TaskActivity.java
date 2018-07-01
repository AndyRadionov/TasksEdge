package io.github.andyradionov.tasksedge.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import io.github.andyradionov.tasksedge.R;
import io.github.andyradionov.tasksedge.database.FirebaseRepository;
import io.github.andyradionov.tasksedge.database.Task;
import io.github.andyradionov.tasksedge.utils.AnalyticsUtils;
import io.github.andyradionov.tasksedge.utils.DateUtils;
import io.github.andyradionov.tasksedge.utils.PreferenceUtils;

/**
 * @author Andrey Radionov
 */

public class TaskActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String TASK_EXTRA = "task_extra";

    @BindView(R.id.et_input_text) EditText mTextInput;
    @BindView(R.id.et_date) EditText mDateView;
    @BindView(R.id.et_time) EditText mTimeView;
    @BindView(R.id.tv_quote) TextView mQuoteView;

    private Task mTask;
    private FirebaseRepository mRepository;
    private boolean isNewTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_task);

        mRepository = FirebaseRepository.getInstance();

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
        if (isNewTask) {
            Log.d(TAG, "onOptionsItemSelected: Add New Task");
            mRepository.addValue(mTask);
            AnalyticsUtils.logNewTaskLengthEvent(this, mTask.getText().length());
        } else {
            Log.d(TAG, "onOptionsItemSelected: Edit Task");
            mRepository.updateValue(mTask);
        }
        finish();
    }

    private void initViews() {
        Log.d(TAG, "initViews");

        mTextInput.setText(mTask.getText());
        mDateView.setText(DateUtils.formatDate(mTask.getDueDate()));
        mTimeView.setText(DateUtils.formatTime(mTask.getDueDate()));

        mTextInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    handleDoneAction();
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void setQuote() {
        Log.d(TAG, "setQuote");
        String quote = PreferenceUtils.getQuote(this);
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
        String text = mTextInput.getText().toString().replaceAll("\\n+", " ").trim();
        String dateText = mDateView.getText().toString().trim();
        String timeText = mTimeView.getText().toString().trim();

        mTask.setText(text);

        Date date = DateUtils.parseDateTime(dateText + ", " + timeText);
        if (date != null) {
            mTask.setDueDate(date);
        }
    }
}
