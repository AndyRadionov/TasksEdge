package io.github.andyradionov.tasksedge.app;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

import io.github.andyradionov.tasksedge.di.AppComponent;
import io.github.andyradionov.tasksedge.di.DaggerAppComponent;

/**
 * @author Andrey Radionov
 */

public class App extends Application {
    private static AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mAppComponent = DaggerAppComponent.create();
    }

    public static AppComponent getAppComponent() {
        return mAppComponent;
    }
}
