package io.github.andyradionov.tasksedge.ui.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;

import butterknife.OnClick;
import io.github.andyradionov.tasksedge.R;
import io.github.andyradionov.tasksedge.ui.common.BaseActivity;

public class SettingsActivity extends BaseActivity {
    private static final String TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_settings);
        setUpToolbar(getString(R.string.settings_title), R.drawable.ic_back_white);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    @OnClick(R.id.btn_sign_out)
    public void signOut() {
        Log.d(TAG, "signOut");
        AuthUI.getInstance().signOut(this);
        finish();
    }
}
