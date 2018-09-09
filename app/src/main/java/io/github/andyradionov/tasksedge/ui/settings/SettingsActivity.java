package io.github.andyradionov.tasksedge.ui.settings;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.auth.AuthUI;

import io.github.andyradionov.tasksedge.R;
import io.github.andyradionov.tasksedge.databinding.ActivitySettingsBinding;
import io.github.andyradionov.tasksedge.ui.common.BaseActivity;

public class SettingsActivity extends BaseActivity {
    private static final String TAG = SettingsActivity.class.getSimpleName();

    private ActivitySettingsBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        initViews();
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

    private void initViews() {
        bindToolbar(mBinding.toolbar, mBinding.tvToolbarTitle);
        setUpToolbar(getString(R.string.settings_title), R.drawable.ic_back_white);

        mBinding.btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "signOut");
                AuthUI.getInstance().signOut(SettingsActivity.this);
                finish();
            }
        });
    }
}
