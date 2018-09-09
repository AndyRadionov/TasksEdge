package io.github.andyradionov.tasksedge.ui.common;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import io.github.andyradionov.tasksedge.R;

/**
 * @author Andrey Radionov
 */

public abstract class BaseActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mToolbarTitle;

    protected void bindToolbar(Toolbar toolbar, TextView toolbarTitle) {
        mToolbar = toolbar;
        mToolbarTitle = toolbarTitle;
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
