package com.mattchowning.wodnotifier;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.mattchowning.wodnotifier.Views.MainActivity;

import java.util.ArrayList;

public class SendNotificationReceiver extends BroadcastReceiver {

    private static boolean notificationSound = false;
    private static boolean notificationVibrate = false;

    @Override
    public void onReceive(Context context, Intent intent) {                                         // FIXME When clicking a notification, I then have to back out twice to exit -- fix the back stack!

        boolean receivedUpdate = intent.getBooleanExtra(UpdateService.WERE_ENTRIES_UPDATED, false);

        if (receivedUpdate) {

            ArrayList<WodEntry> entries = intent.getParcelableArrayListExtra(UpdateService.ENTRIES);

            // This should be unnecessary
            if (entries.isEmpty()) return;

            WodEntry newEntry = entries.get(0);
            String title = context.getResources().getString(R.string.notification_title);
            String text = newEntry.title + "\n" + newEntry.plainTextDescription;

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_launcher)                                       // TODO Fix icon to be consistent with Android guidelines
                            .setContentTitle(title)
                            .setTicker(title)
                            .setContentText(text)
                            .setLights(Color.WHITE, 500, 2000)
                            .setAutoCancel(true);

            SharedPreferences sPrefs =
                    PreferenceManager.getDefaultSharedPreferences(context);

            // Set notification to have sound if specified by preferences.
            if (notificationSound) {
                Uri nSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                mBuilder.setSound(nSound);
            }

            // Set notification to vibrate if specified by preferences.
            if (notificationVibrate) {
                mBuilder.setVibrate(new long[]{500, 500, 500, 500});
            }

            // Enable expanded notifications for use in Android 4.1+
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.bigText(text);
            mBuilder.setStyle(bigTextStyle);

            Intent onClickIntent = new Intent(context, MainActivity.class);
            PendingIntent pIntent = PendingIntent.getActivity(context, 0, onClickIntent, 0);
            mBuilder.setContentIntent(pIntent);
            NotificationManager mNotificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(0, mBuilder.build());
        }
    }

    public static void setNotificationSound(boolean soundPref) {
        notificationSound = soundPref;
    }

    public static void setNotificationVibrate(boolean vibratePref) {
        notificationVibrate = vibratePref;
    }

    // Sets the receiver's enabled status in the AndroidManifest.  Note that a context has to be
    // passed to this method because it may be called before the onReceive method is called and
    // therefore the context may be null.
    private static void setReceiverEnabledStatus(Context context, boolean isEnabled) {
        ComponentName receiver = new ComponentName(context, AlarmReceiver.class);
        PackageManager pm = context.getPackageManager();
        int status = (isEnabled) ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        pm.setComponentEnabledSetting(receiver, status, PackageManager.DONT_KILL_APP);
    }
}
