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
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.mattchowning.wodnotifier.Views.MainActivity;

import java.util.ArrayList;

public class SendNotificationReceiver extends BroadcastReceiver {

    public static boolean notificationSound = false;
    public static boolean notificationVibrate = false;

    @Override
    public void onReceive(Context context, Intent intent) {

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
                            .setLargeIcon((((BitmapDrawable)context.getResources().
                                    getDrawable(R.drawable.ic_launcher)).getBitmap()))
                            .setSmallIcon(R.drawable.notification_icon_1)
                            .setContentTitle(title)
                            .setTicker(title)
                            .setContentText(text)
                            .setLights(Color.WHITE, 500, 2000)
                            .setAutoCancel(true);

            // Set notification to have sound if specified by preferences.
            if (notificationSound) {

                /* Cannot assign this using the onSharedPreferencesChanged method like I do with the
                other variables because for some reason changes to the default ringtone do not
                cause that method to fire.  Apparently this is an android bug:
                stackoverflow.com/questions/6725105/ringtonepreference-not-firing-onsharedpreferencechanged
                */
                SharedPreferences sPrefs =
                        PreferenceManager.getDefaultSharedPreferences(context);
                String soundChangePrefKey =
                        context.getResources().getString(R.string.pref_notification_sound_selection);
                String strSelectedNotificationSound =
                        sPrefs.getString(soundChangePrefKey, null);
                Uri selectedNotificationSound;
                if (strSelectedNotificationSound == null) {
                    selectedNotificationSound =
                            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                } else {
                    selectedNotificationSound = Uri.parse(strSelectedNotificationSound);
                }
                mBuilder.setSound(selectedNotificationSound);
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
            onClickIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pIntent = PendingIntent.getActivity(context, 0, onClickIntent, 0);
            mBuilder.setContentIntent(pIntent);
            NotificationManager mNotificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(0, mBuilder.build());
        }
    }
}
