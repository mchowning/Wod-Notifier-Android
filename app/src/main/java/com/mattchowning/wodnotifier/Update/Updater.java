package com.mattchowning.wodnotifier.Update;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mattchowning.wodnotifier.UpdateScheduler;
import com.mattchowning.wodnotifier.WodEntry;

import java.util.ArrayList;

/**
 * Created by Matt on 2/23/14.
 */
public class Updater {

    public static final String NEW_ENTRIES =
            "com.mattchowning.wodnotifier.entries";
    private static final String URL_STRING =
            "http://www.crossfitreviver.com/index.php?format=feed&type=rss";
    private final DatabaseUpdater databaseUpdater;
    private final WodDownloader wodDownloader;
    private final UpdateScheduler updateScheduler;

    private Context context;
    private static boolean firstDownload = true;
            // TODO Make this check the database (or just let there be an initial notification)

    public Updater(Context context, DatabaseUpdater databaseUpdater, UpdateScheduler updateScheduler,
                   WodDownloader wodDownloader) {
        this.context = context;
        this.databaseUpdater = databaseUpdater;
        this.updateScheduler = updateScheduler;
        this.wodDownloader = wodDownloader;
    }

    public void update() {
        ArrayList<WodEntry> downloadedWods = wodDownloader.downloadedWods(URL_STRING);
        databaseUpdater.insertIntoDatabaseIfMissing(downloadedWods);
        boolean updatedDatabaseWithNewEntries = databaseUpdater.databaseWasUpdated();
        scheduleNextCheckForUpdate(updatedDatabaseWithNewEntries);
        if (updatedDatabaseWithNewEntries && !firstDownload) {
            Log.d(null, "Broadcast indicating updated database with new entries.");
            ArrayList<WodEntry> newEntries = databaseUpdater.getNewWodEntries();
            broadcastUpdate(newEntries);
        }
        firstDownload = false;
    }

    private void broadcastUpdate(ArrayList<WodEntry> newEntries) {
        Intent outgoingIntent = new Intent();
        outgoingIntent.putParcelableArrayListExtra(NEW_ENTRIES, newEntries);
        outgoingIntent.setAction("com.mattchowning.wodnotifier.UPDATE_COMPLETED");
        context.sendBroadcast(outgoingIntent);
    }

    private void scheduleNextCheckForUpdate(boolean hasDownloadedNewEntries) {
        updateScheduler.setAlarms(hasDownloadedNewEntries, firstDownload);
    }
}
