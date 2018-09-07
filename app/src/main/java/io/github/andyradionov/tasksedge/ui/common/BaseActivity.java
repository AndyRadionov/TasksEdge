package io.github.andyradionov.tasksedge.ui.common;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.andyradionov.tasksedge.R;

/**
 * @author Andrey Radionov
 */

public abstract class BaseActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.tv_toolbar_title) TextView mToolbarTitle;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    protected void setUpToolbar(String title) {
        setUpToolbar(title, R.drawable.ic_back_white, false);
    }

    protected void setUpToolbar(String title, int navigationIcon) {
        setUpToolbar(title, navigationIcon, true);
    }

    private void setUpToolbar(String title, int navigationIcon, boolean showHomeAsUp) {
        mToolbarTitle.setText(title);
        mToolbar.setNavigationIcon(navigationIcon);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(showHomeAsUp);
        }
    }
}
