package io.github.andyradionov.tasksedge.ui;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import io.github.andyradionov.tasksedge.R;

/**
 * @author Andrey Radionov
 */

public abstract class BaseActivity extends AppCompatActivity {


    protected void setUpToolbar(String title) {
        setUpToolbar(title, R.drawable.ic_back_white, false);
    }

    protected void setUpToolbar(String title, int navigationIcon) {
        setUpToolbar(title, navigationIcon, true);
    }

    private void setUpToolbar(String title, int navigationIcon, boolean showHomeAsUp) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarTitle = toolbar.findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(title);
        toolbar.setNavigationIcon(navigationIcon);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(showHomeAsUp);
        }
    }
}
