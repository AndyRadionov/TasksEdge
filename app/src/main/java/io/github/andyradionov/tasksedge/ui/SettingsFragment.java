package io.github.andyradionov.tasksedge.ui;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import io.github.andyradionov.tasksedge.R;

/**
 * @author Andrey Radionov
 */

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
    }
}
