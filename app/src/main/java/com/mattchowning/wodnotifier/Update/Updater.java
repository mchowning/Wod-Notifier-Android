package com.mattchowning.wodnotifier.Update;

import android.content.Context;
import android.content.Intent;

import com.mattchowning.wodnotifier.Database.MyContentProviderHelper;
import com.mattchowning.wodnotifier.UpdateScheduler;
import com.mattchowning.wodnotifier.WodEntry;

import java.util.ArrayList;

/**
 * Created by Matt on 2/23/14.
 */
public class Updater {

    public static final String NEW_ENTRIES =
            "com.mattchowning.wodnotifier.entries";
    private static final String URL =
            "http://www.crossfitreviver.com/index.php?format=feed&type=rss";

    private UpdateFactory factory;
    private Context context;
    private static boolean firstDownload = true;
            // TODO Make this check the database (or just let there be an initial notification)

    public Updater(Context context, UpdateFactory factory) {
        this.factory = factory;
        this.context = context;
    }

    public void update() {
        DatabaseUpdater databaseUpdater = updateDatabase();
        boolean updatedDatabaseWithNewEntries = databaseUpdater.databaseWasUpdated();
        scheduleNextUpdateCheck(updatedDatabaseWithNewEntries);
        if (updatedDatabaseWithNewEntries && !firstDownload) {
            ArrayList<WodEntry> newEntries = databaseUpdater.getNewWodEntries();
            broadcastUpdate(newEntries);
        }
        firstDownload = false;
    }

    private DatabaseUpdater updateDatabase() {
        WodDownloader wodDownloader = factory.getWodDownloader();
        ArrayList<WodEntry> downloadedWodEntries = wodDownloader.downloadedWods(URL);

        MyContentProviderHelper database = factory.getMyContentProviderHelper(context);
        DatabaseUpdater databaseUpdater = factory.getDatabaseUpdater();
        databaseUpdater.update(downloadedWodEntries, database);
        return databaseUpdater;
    }

    private void broadcastUpdate(ArrayList<WodEntry> newEntries) {
        Intent outgoingIntent = new Intent();
        outgoingIntent.putParcelableArrayListExtra(NEW_ENTRIES, newEntries);
        outgoingIntent.setAction("com.mattchowning.wodnotifier.UPDATE_COMPLETED");
        context.sendBroadcast(outgoingIntent);
    }

    private void scheduleNextUpdateCheck(boolean hasDownloadedNewEntries) {
        UpdateScheduler updateScheduler = factory.getUpdateScheduler();
        updateScheduler.setAlarms(context, hasDownloadedNewEntries, firstDownload);
    }
}
