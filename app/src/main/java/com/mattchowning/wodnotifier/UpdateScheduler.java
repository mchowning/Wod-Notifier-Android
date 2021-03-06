package com.mattchowning.wodnotifier;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class UpdateScheduler {

    private static final String TAG = UpdateScheduler.class.getName();
    private static final int DAILY_ALARM_REQUEST_CODE = 1;
    private static final int INTERVAL_ALARM_REQUEST_CODE = 2;
    private final Context context;

    public UpdateScheduler(Context context) {
        this.context = context;
    }

    public void setAlarms(boolean databaseUpdated, boolean firstDownload) {
        AlarmReceiver.setReceiverEnabledStatus(context, true);
        if (databaseUpdated && !firstDownload) {
            cancelAlarm(context, INTERVAL_ALARM_REQUEST_CODE);
        } else {
            setIntervalAlarm(context);                                                              // FIXME The interval alarm is immediately restarted if the user checks the app before the daily alarm fires.
        }
        setDailyAlarm(context);
    }

    // Cancels any alarm that has a matching pending intent.  Since all the alarms are set with
    // the same pending intent, this effectively cancels any set alarm.  This method also
    // disables this BroadcastReceiver.
    public void cancelAllAlarms(Context context) {
        Log.d(TAG, "Cancelling all alarms and disabling the receiver");
        cancelAlarm(context, DAILY_ALARM_REQUEST_CODE);
        cancelAlarm(context, INTERVAL_ALARM_REQUEST_CODE);
        AlarmReceiver.setReceiverEnabledStatus(context, false);
    }

    private void setIntervalAlarm(Context context) {
        Calendar cal = Calendar.getInstance();
        long alarmInterval = getAlarmInterval(cal.get(Calendar.HOUR_OF_DAY));
        long currentTime = cal.getTimeInMillis();
        long startTime = currentTime + alarmInterval;

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pIntent =
                PendingIntent.getBroadcast(context, INTERVAL_ALARM_REQUEST_CODE, intent, 0);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, startTime, alarmInterval, pIntent);
    }

    private long getAlarmInterval(int currentHour) {
        long alarmInterval;
        switch (currentHour) {
            case 17:    // 5:00 p.m.
            case 18:    // 6:00 p.m.
                alarmInterval = AlarmManager.INTERVAL_HALF_HOUR;
                Log.d(TAG, "Setting inexact alarm interval of 30 minutes");
                break;
            case 19:    // 7:00 p.m.
            case 20:    // 8:00 p.m.
            case 21:    // 9:00 p.m.
                alarmInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
                Log.d(TAG, "Setting inexact alarm interval of 15 minutes");
                break;
            case 22:    // 10:00 p.m.
            case 23:    // 11:00 p.m.
                alarmInterval = AlarmManager.INTERVAL_HALF_HOUR;
                Log.d(TAG, "Setting inexact alarm interval of 30 minutes");
                break;
            default:
                alarmInterval = AlarmManager.INTERVAL_HOUR;
                Log.d(TAG, "Setting inexact alarm interval of 1 hour");
                break;
        }
        return alarmInterval;
    }

    private void setDailyAlarm(Context context) {
        Calendar cal = Calendar.getInstance();
        int currentHour = cal.get(Calendar.HOUR_OF_DAY);
        int currentMinute = cal.get(Calendar.MINUTE);
        int alarmHour = 17;  // 5:00 p.m.
        int alarmMinute = 0;

        // If alarm time has already passed today, don't start alarm until next day.  Otherwise
        // the alarm will fire immediately.
        boolean isCurrentlyAfterAlarmTime =  currentHour >= alarmHour &&
                                    currentMinute >= alarmMinute;
        if(isCurrentlyAfterAlarmTime) cal.add(Calendar.DATE, 1);

        cal.set(Calendar.HOUR_OF_DAY, alarmHour);
        cal.set(Calendar.MINUTE, alarmMinute);
        long startTime = cal.getTimeInMillis();

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, DAILY_ALARM_REQUEST_CODE, intent,0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, startTime, AlarmManager.INTERVAL_DAY, pIntent);

        Log.d(TAG, "Setting daily alarm of " + alarmHour + " hour and " + alarmMinute + " minute");
    }

    private void cancelAlarm(Context context, int alarmRequestCode) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmRequestCode, intent, 0);
        am.cancel(pendingIntent);
    }
}
