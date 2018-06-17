package io.github.andyradionov.tasksedge;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

/**
 * @author Andrey Radionov
 */

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
    }
}
