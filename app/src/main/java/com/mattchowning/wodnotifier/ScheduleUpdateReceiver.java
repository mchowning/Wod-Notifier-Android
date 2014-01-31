package com.mattchowning.wodnotifier;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class ScheduleUpdateReceiver extends BroadcastReceiver {

    private static final String TAG = ScheduleUpdateReceiver.class.getName();

    private static final int ONE_SECOND = 1000;
    private static final int ONE_MINUTE = 60 * ONE_SECOND;
    private static final int ONE_HOUR = 60 * ONE_MINUTE;

    @Override
    public void onReceive(Context context, Intent intent) {                                         // FIXME The next day alarm is cancelled ofr a repeating alarm if you open the app
        boolean resultsUpdated =
                intent.getBooleanExtra(UpdateService.WERE_ENTRIES_UPDATED, false);
        if (resultsUpdated) {
            setNextDayAlarm(context);
        } else {
            setCustomRepeatingAlarm(context);
        }
        AlarmReceiver.setReceiverEnabledStatus(context, true);
    }

    // Set alarm to start the "next" day at 5:00 p.m. Presumably, the wod for "this" day has
    // already been downloaded.  Note that the "next" day could be today if the
    // WOD for today wasn't posted yesterday (i.e., it wasn't posted the day before like normal,
    // but was instead posted, say, the following morning).
    private void setNextDayAlarm(Context context) {

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

        startAlarm(context, cal.getTimeInMillis());
        Log.d(TAG, "Setting the next day alarm");
    }

    private void setCustomRepeatingAlarm(Context context) {

        Calendar cal = Calendar.getInstance();
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
//        timeToNextAlarm = 20 * ONE_SECOND;                                                        // TESTING (alarm)

        startAlarm(context, cal.getTimeInMillis() + timeToNextAlarm);
        Log.d(TAG, "Setting custom repeating alarm in " +
                Integer.toString(timeToNextAlarm / ONE_MINUTE) + " minutes.");
    }

    // startTime argument is derived from a Calendar object's getTimeInMillis() method (plus
    // any additional milliseconds that need to be added).
    private void startAlarm(Context context, long startTime) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.set(AlarmManager.RTC_WAKEUP, startTime, pIntent);
    }

    // Cancels any alarm that has a matching pending intent.  Since all the alarms are set with
    // the same pending intent, this effectively cancels any set alarm.  This method also
    // disables the receiver.
    public static void cancelAlarm(Context context) {
        Log.d(TAG, "Cancelling all alarms and disabling the receiver");
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.cancel(pIntent);
        AlarmReceiver.setReceiverEnabledStatus(context, false);
    }
}
