package io.github.andyradionov.tasksedge.app;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * @author Andrey Radionov
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
