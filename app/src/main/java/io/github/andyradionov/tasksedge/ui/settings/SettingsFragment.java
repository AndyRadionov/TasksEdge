package io.github.andyradionov.tasksedge.ui.settings;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;

import io.github.andyradionov.tasksedge.R;

/**
 * @author Andrey Radionov
 */

public class SettingsFragment extends PreferenceFragmentCompat {
    private static final String TAG = SettingsFragment.class.getSimpleName();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Log.d(TAG, "onCreatePreferences");
        addPreferencesFromResource(R.xml.preferences);
    }
}
