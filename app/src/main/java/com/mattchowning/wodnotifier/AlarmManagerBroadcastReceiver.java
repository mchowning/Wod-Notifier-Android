package com.mattchowning.wodnotifier;

import android.app.AlarmManager;
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
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

/*
 * AlarmManagerBroadcastReceiver
 * -----------------------------
 * Handles scheduling of future checks of the website for updates.
 */
public class AlarmManagerBroadcastReceiver extends BroadcastReceiver implements XmlChecker {

    private static final int ONE_SECOND = 1000;
    private static final int ONE_MINUTE = 60 * ONE_SECOND;
    private static final int ONE_HOUR = 60 * ONE_MINUTE;
    private static final String TAG = AlarmManagerBroadcastReceiver.class.getName();

    private Context receiverContext;

    private static boolean notificationSound = false;
    private static boolean notificationVibrate = false;

    // Empty constructor required to receive broadcasts.
    public AlarmManagerBroadcastReceiver() { super(); }

    public AlarmManagerBroadcastReceiver(Context context) {
        receiverContext = context;
        setReceiverEnabledStatus(receiverContext, true);

        // Use setCustomRepeatingAlarm as first alarm because this ensures that the user doesn't
        // miss an update no matter when they start the app for the first time.  For example, if
        // they started it on Day 1 after the setNextDayAlarm time, but before the WOD had
        // been updated that day, then if the setNextDayAlarm method had been called, the
        // user would not get notified when a new wod was posted on Day 1 (because the
        // alarm would be set for Day 2).  The negative effect of this approach is that it
        // causes more alarms to be triggered, especially where the Wod for the next day has
        // already been downloaded.
        setCustomRepeatingAlarm();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        receiverContext = context;
        Log.d(TAG, "AlarmManagerBroadcastReceiver's onReceive() method was called and a " +
                "WodDownloader is being started");
        new WodDownloader(context, this);
    }

    // Callback from WodDownloader
    @Override
    public void entriesReceived(ArrayList<WodEntry> entries, boolean wereResultsUpdated) {

        if (wereResultsUpdated) {

            Log.d(TAG, "Alarm notified of new results.");

            // TODO Implement once notification preference can disable notifications while still
            // allowing the WOD to be downloaded in order to update saved WODs.
//            SharedPreferences sPrefs =
//                    PreferenceManager.getDefaultSharedPreferences(receiverContext);
//            String n = receiverContext.getResources().getString(R.string.pref_notification);
//            boolean notificationPref = sPrefs.getBoolean(n, true);
//            if (notificationPref) {
            String newWodDescription = entries.get(0).plainTextDescription;
            String notificationTitle = receiverContext.getString(R.string.notification_title);
            sendNotification(notificationTitle, newWodDescription);
//            }

            setNextDayAlarm();
//            setCustomRepeatingAlarm();                                                            // TESTING (alarm) Make sure and undo comment on setNextDayAlarm call on line above this one

        } else {
            Log.d(TAG, "Alarm notified of no new results.");
            setCustomRepeatingAlarm();
        }
    }

    private void sendNotification(String title, String text) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(receiverContext)
                        .setSmallIcon(R.drawable.ic_launcher)                                       // TODO Fix icon to be consistent with Android guidelines
                        .setContentTitle(title)
                        .setTicker(title)
                        .setContentText(text)
                        .setLights(Color.WHITE, 500, 2000)
                        .setAutoCancel(true);

        SharedPreferences sPrefs =
                PreferenceManager.getDefaultSharedPreferences(receiverContext);

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

        Intent intent = new Intent(receiverContext, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(receiverContext, 0, intent, 0);
        mBuilder.setContentIntent(pIntent);
        NotificationManager mNotificationManager = (NotificationManager)
                receiverContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }

    // Set alarm to start the "next" day at 5:00 p.m. Presumably, the wod for "this" day has
    // already been downloaded.  Note that the "next" day could be today if the
    // WOD for today wasn't posted yesterday (i.e., it wasn't posted the day before like normal,
    // but was instead posted, say, the following morning).
    private void setNextDayAlarm() {

        setReceiverEnabledStatus(receiverContext, true);

        Calendar cal = Calendar.getInstance();
        int alarmHour = 17;  // 5:00 p.m.
        int alarmMinute = 0;
        int currentHour = cal.get(Calendar.HOUR_OF_DAY);
        int currentMinute = cal.get(Calendar.MINUTE);

        // If calling this function after the next day alarm start time (5:00 p.m.) make sure
        // alarm is set for tomorrow at 5:00 p.m.  Otherwise, the alarm is overdue and
        // immediately fires.
        if(currentHour >= alarmHour && currentMinute >= alarmMinute) cal.add(Calendar.DATE, 1);

        cal.set(Calendar.HOUR_OF_DAY, alarmHour);
        cal.set(Calendar.MINUTE, alarmMinute);
        startAlarm(cal.getTimeInMillis());
        Log.d(TAG, "Enabling the receiver and setting the next day alarm");
    }

    private void setCustomRepeatingAlarm() {

        setReceiverEnabledStatus(receiverContext, true);

        Calendar cal = Calendar.getInstance();
//        int timeToNextAlarm = 10 * ONE_SECOND;                                                    // TESTING (alarm)
        int timeToNextAlarm;
        int currentHour = cal.get(Calendar.HOUR_OF_DAY);
        switch (currentHour) {
            case 17:    // 5:00 p.m.
            case 18:    // 6:00 p.m.
                timeToNextAlarm = 30 * ONE_MINUTE;
                break;
            case 19:    // 7:00 p.m.
                timeToNextAlarm = 20 * ONE_MINUTE;
                break;
            case 20:    // 8:00 p.m.
                timeToNextAlarm = 15 * ONE_MINUTE;
                break;
            case 21:    // 9:00 p.m.
                timeToNextAlarm = 20 * ONE_MINUTE;
                break;
            case 22:    // 10:00 p.m.
            case 23:    // 11:00 p.m.
                timeToNextAlarm = 30 * ONE_MINUTE;
                break;
            default:
                timeToNextAlarm = ONE_HOUR;
                break;
        }

        startAlarm(cal.getTimeInMillis() + timeToNextAlarm);
        Log.d(TAG, "Enabling receiver and setting custom repeating alarm in " +
                Integer.toString(timeToNextAlarm / ONE_MINUTE) + " minutes.");
    }

    // startTime argument is derived from a Calendar object's getTimeInMillis() method (plus
    // any additional milliseconds that need to be added).
    private void startAlarm(long startTime) {
        AlarmManager am = (AlarmManager) receiverContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(receiverContext, AlarmManagerBroadcastReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(receiverContext, 0, intent, 0);
        am.set(AlarmManager.RTC_WAKEUP, startTime, pIntent);
    }

    // Cancels any alarm that has a matching pending intent.  Since all the alarms are set with
    // the same pending intent, this effectively cancels any set alarm.  This method also
    // disables the receiver.
    public static void cancelAlarm(Context context) {
        Log.d(TAG, "Cancelling all alarms and disabling the receiver");
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.cancel(pIntent);
        setReceiverEnabledStatus(context, false);
    }

    public static void setNotificationSound(boolean soundPref) {
        notificationSound = soundPref;
    }

    public static void setNotificationVibrate(boolean vibratePref) {
        notificationVibrate = vibratePref;
    }

    // Sets the receiver's enabled status in the AndroidManifest.  Note that a context has to be
    // passed to this method because it may be called before the onReceive method is called and
    // therefore the receiverContext may be null.
    private static void setReceiverEnabledStatus(Context context, boolean isEnabled) {
        ComponentName receiver = new ComponentName(context, AlarmManagerBroadcastReceiver.class);
        PackageManager pm = context.getPackageManager();
        int status = (isEnabled) ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
                                   PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        pm.setComponentEnabledSetting(receiver, status, PackageManager.DONT_KILL_APP);
    }
}
