package io.github.andyradionov.tasksedge.ui.common;

import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import javax.inject.Inject;

import io.github.andyradionov.tasksedge.R;
import io.github.andyradionov.tasksedge.app.App;

/**
 * @author Andrey Radionov
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Inject
    protected ViewModelProvider.Factory mViewModelFactory;
    private Toolbar mToolbar;
    private TextView mToolbarTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
    }

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
