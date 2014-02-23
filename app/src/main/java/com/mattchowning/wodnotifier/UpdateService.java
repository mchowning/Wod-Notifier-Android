package com.mattchowning.wodnotifier;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.mattchowning.wodnotifier.Database.MyContentProvider;
import com.mattchowning.wodnotifier.Database.MyContentProviderHelper;
import com.mattchowning.wodnotifier.Database.MySQLiteHelper;
import com.mattchowning.wodnotifier.Views.WodList;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class UpdateService extends IntentService {

    private static final String TAG = WodList.class.getName();
    private static final String URL =
            "http://www.crossfitreviver.com/index.php?format=feed&type=rss";
    public static final String NEW_ENTRIES =
            "com.mattchowning.wodnotifier.entries";
    private static boolean firstDownload = true;

    public UpdateService() {
        super("UpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        WodDownloader wodDownloader = new WodDownloader(URL);
        MyContentProviderHelper database = new MyContentProviderHelper(this);
        DatabaseUpdater databaseUpdater = new DatabaseUpdater(wodDownloader, database);
        databaseUpdater.update();
        boolean updatedDatabaseWithNewEntries = databaseUpdater.databaseWasUpdated();
        scheduleNextUpdateCheck(intent, updatedDatabaseWithNewEntries);
        if (updatedDatabaseWithNewEntries && !firstDownload) {
            ArrayList<WodEntry> newEntries = databaseUpdater.getNewWodEntries();
            broadcastUpdate(newEntries);
            firstDownload = false;
        }
    }

    private void broadcastUpdate(ArrayList<WodEntry> newEntries) {
        Intent outgoingIntent = new Intent();
        outgoingIntent.putParcelableArrayListExtra(NEW_ENTRIES, newEntries);
        outgoingIntent.setAction("com.mattchowning.wodnotifier.UPDATE_COMPLETED");
        sendBroadcast(outgoingIntent);
    }

    private void scheduleNextUpdateCheck(Intent intent, boolean hasDownloadedNewEntries) {
        UpdateScheduler.setAlarms(this, hasDownloadedNewEntries, firstDownload);
        AlarmReceiver.completeWakefulIntent(intent); // Releases any wakelock held by AlarmReceiver
    }
}
