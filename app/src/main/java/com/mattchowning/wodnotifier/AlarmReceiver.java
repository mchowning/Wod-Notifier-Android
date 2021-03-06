package com.mattchowning.wodnotifier;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.mattchowning.wodnotifier.Update.UpdateService;

/*
 * AlarmReceiver
 * -----------------------------
 * Handles scheduling of future checks of the website for updates.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {

    private static final String TAG = AlarmReceiver.class.getName();

    // Empty constructor required to receive broadcasts.
    public AlarmReceiver() { super(); }

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent serviceIntent = new Intent(context, UpdateService.class);
        startWakefulService(context, serviceIntent); // Engages wakelock and starts service

        if (Intent.ACTION_MY_PACKAGE_REPLACED.equals(intent.getAction())) {
            Log.d(TAG, "Wod Notifier broadcast receiver notified that THIS package is/was replaced");
        } else if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(TAG, "Wod Notifier broadcast receiver notified that a boot was completed");
        } else { // TODO Specifically define to my calling package and create an error else clause
            Log.d(TAG, "Wod Notifier broadcast receiver alerted by something else--probably " +
                    "my UpdateScheduler");
        }
    }

    // Sets the receiver's enabled status in the AndroidManifest.  Note that a context has to be
    // passed to this method because it may be called before the onReceive method is called and
    // therefore the receiverContext may be null.
    public static void setReceiverEnabledStatus(Context context, boolean isEnabled) {
        ComponentName receiver = new ComponentName(context, AlarmReceiver.class);
        PackageManager pm = context.getPackageManager();
        int status = isEnabled
                ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        pm.setComponentEnabledSetting(receiver, status, PackageManager.DONT_KILL_APP);
    }
}
