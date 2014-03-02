package com.mattchowning.wodnotifier.Update;

import android.app.IntentService;
import android.content.Intent;

import com.mattchowning.wodnotifier.AlarmReceiver;
import com.mattchowning.wodnotifier.UpdateScheduler;

public class UpdateService extends IntentService {

    public UpdateService() {
        super("UpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        UpdateFactory factory = new UpdateFactory(this);
        Updater updater = factory.getUpdater();
        updater.update();
        releaseWakelockIfPresent(intent);
    }

    private void releaseWakelockIfPresent(Intent intent) {
        AlarmReceiver.completeWakefulIntent(intent);
    }
}
