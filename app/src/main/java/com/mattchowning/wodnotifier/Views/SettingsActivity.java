package com.mattchowning.wodnotifier.Views;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.mattchowning.wodnotifier.R;
import com.mattchowning.wodnotifier.UpdateScheduler;
import com.mattchowning.wodnotifier.UpdateService;

public class SettingsActivity extends Activity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences sPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sPrefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sPrefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    // Cancels or sets alarms (as appropriate) if the user changes the "check in background"
    // preference.
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String updateInBackgroundKey = getResources().getString(R.string.pref_background_updates);
        if (key.equals(updateInBackgroundKey)) {
            boolean updatePref = sPrefs.getBoolean(updateInBackgroundKey, true);
            if (updatePref) {
                Intent intent = new Intent(this, UpdateService.class);
                startService(intent);
            } else {
                UpdateScheduler.cancelAllAlarms(this);
            }
        }
    }
}
