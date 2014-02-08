package com.mattchowning.wodnotifier;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.mattchowning.wodnotifier.Views.MainActivity;

import java.util.ArrayList;

public class SendNotificationReceiver extends BroadcastReceiver {

    private Context mContext;
    private static final int VIBRATION_LENGTH = 500;
    private static final int LIGHT_ON = 500;
    private static final int LIGHT_OFF = 2000;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        boolean receivedUpdate = intent.getBooleanExtra(UpdateService.WERE_ENTRIES_UPDATED, false);

        if (receivedUpdate) {
            ArrayList<WodEntry> entries = intent.getParcelableArrayListExtra(UpdateService.ENTRIES);
            if (entries.isEmpty()) return; // ArrayList should never be empty, so this should never happen
            WodEntry firstEntry = entries.get(0);
            String notificationText = firstEntry.title + "\n" + firstEntry.plainTextDescription;
            String notificationTitle = context.getResources().getString(R.string.notification_title);
            buildNotification(notificationTitle, notificationText);
        }
    }

    private void buildNotification(String title, String text) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setLargeIcon((((BitmapDrawable) mContext.getResources().
                                getDrawable(R.drawable.ic_launcher)).getBitmap()))
                        .setSmallIcon(R.drawable.notification_icon_1)
                        .setContentTitle(title)
                        .setTicker(title)
                        .setContentText(text)
                        .setLights(Color.WHITE, LIGHT_ON, LIGHT_OFF)
                        .setAutoCancel(true);
        SharedPreferences sPrefs =
                PreferenceManager.getDefaultSharedPreferences(mContext);
        setNotificationSound(mBuilder, sPrefs);
        setNotificationVibrate(mBuilder, sPrefs);
        setExpandedNotification(text, mBuilder);
        setNotificationPendingIntent(mBuilder);
        sendNotification(mBuilder);
    }

    private void setNotificationSound(NotificationCompat.Builder mBuilder, SharedPreferences sPrefs) {
        String soundPrefKey =
                mContext.getResources().getString(R.string.pref_notification_sound);
        boolean notificationSound = sPrefs.getBoolean(soundPrefKey, false);
        if (notificationSound) {
            Uri selectedNotificationSound = getNotificationSound(mContext, sPrefs);
            mBuilder.setSound(selectedNotificationSound);
        }
    }

    /* Returns either the notification sound selected by the user in the settings or, if nothing
    has been selected, the default notification sound. */
    private Uri getNotificationSound(Context context, SharedPreferences sPrefs) {
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
        return selectedNotificationSound;
    }

    private void setNotificationVibrate(NotificationCompat.Builder mBuilder, SharedPreferences sPrefs) {
        String vibratePrefKey =
                mContext.getResources().getString(R.string.pref_notification_vibrate);
        boolean notificationVibrate = sPrefs.getBoolean(vibratePrefKey, false);
        if (notificationVibrate) {
            mBuilder.setVibrate(new long[]{ VIBRATION_LENGTH,
                                            VIBRATION_LENGTH,
                                            VIBRATION_LENGTH,
                                            VIBRATION_LENGTH});
        }
    }

    /* Enable expanded notifications for use in Android 4.1+ */
    private void setExpandedNotification(String text, NotificationCompat.Builder mBuilder) {
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.bigText(text);
        mBuilder.setStyle(bigTextStyle);
    }

    private void setNotificationPendingIntent(NotificationCompat.Builder mBuilder) {
        Intent onClickIntent = new Intent(mContext, MainActivity.class);
        onClickIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, onClickIntent, 0);
        mBuilder.setContentIntent(pIntent);
    }

    private void sendNotification(NotificationCompat.Builder mBuilder) {
        NotificationManager mNotificationManager = (NotificationManager)
                mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }
}
