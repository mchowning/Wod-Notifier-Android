package com.mattchowning.wodnotifier;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

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
        String soundPrefKey = getResources().getString(R.string.pref_notification_sound);
        String vibratePrefKey = getResources().getString(R.string.pref_notification_vibrate);
                                                                                                    // TODO Change to switch statement with API19 and Java 1.7
        if (key.equals(updateInBackgroundKey)) {
            boolean updatePref = sPrefs.getBoolean(updateInBackgroundKey, true);
            if (updatePref) {
                new AlarmManagerBroadcastReceiver(this);
            } else {
                AlarmManagerBroadcastReceiver.cancelAlarm(this);
            }

        } else if (key.equals(soundPrefKey)) {
            boolean soundPref = sPrefs.getBoolean(soundPrefKey, false);
            AlarmManagerBroadcastReceiver.setNotificationSound(soundPref);

        } else if (key.equals(vibratePrefKey)) {
            boolean vibratePref = sPrefs.getBoolean(vibratePrefKey, false);
            AlarmManagerBroadcastReceiver.setNotificationVibrate(vibratePref);
        }
    }
}
