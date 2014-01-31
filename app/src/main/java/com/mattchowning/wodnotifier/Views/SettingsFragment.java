package com.mattchowning.wodnotifier.Views;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.mattchowning.wodnotifier.R;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
