package com.mattchowning.wodnotifier;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.mattchowning.wodnotifier.Database.WodEntryDataSource;
import com.mattchowning.wodnotifier.Views.WodList;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Matt on 1/29/14.
 */
public class UpdateService extends IntentService {

    private static final String TAG = WodList.class.getName();
    private static final String URL =
            "http://www.crossfitreviver.com/index.php?format=feed&type=rss";
    public static final String NEW_ENTRIES =
            "com.mattchowning.wodnotifier.entries";

    public UpdateService() {
        super("UpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ArrayList<WodEntry> entries = new ArrayList<WodEntry>();
        try {
            entries = loadXmlFromNetwork(URL);
        } catch (IOException e) {
            Log.w(TAG, "IOException downloading rss feed");
        } catch (XmlPullParserException e) {
            Log.w(TAG, "Xml parsing exception downloading rss feed");
        }

        WodEntryDataSource datasource = new WodEntryDataSource(this);
        boolean firstDownload = false;
        boolean databaseUpdated = false;
        ArrayList<WodEntry> newEntries = new ArrayList<WodEntry>();
        try {
            datasource.open();
            firstDownload = datasource.isEmpty();
            for (WodEntry entry : entries) {
                if (!datasource.containsWod(entry)) {
                    datasource.insertWodIntoDatabase(entry);
                    newEntries.add(entry);
                    databaseUpdated = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (databaseUpdated) {
            Intent outgoingIntent = new Intent();
            outgoingIntent.putParcelableArrayListExtra(NEW_ENTRIES, newEntries);
            outgoingIntent.setAction("com.mattchowning.wodnotifier.UPDATE_COMPLETED");
            sendOrderedBroadcast(outgoingIntent, null);
        }

        UpdateScheduler.setAlarms(this, databaseUpdated, firstDownload);
        AlarmReceiver.completeWakefulIntent(intent); // Releases any wakelock held by AlarmReceiver
    }

    /* Uploads XML from source url, parses the title, link, and originalHtmlDescription, and puts it
    into a WodEntry */
    private ArrayList<WodEntry> loadXmlFromNetwork(String urlString)
            throws XmlPullParserException, IOException {
        InputStream stream = null;
        ArrayList<WodEntry> entries = null;
        try {
            stream = downloadUrl(urlString);
            entries = XmlParser.parse(stream);
        } finally {
            if (stream != null) stream.close();
        }
        return entries;
    }

    // Given a string representation of a URL, sets up a connection and gets an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setDoInput(true);
        conn.connect();
        return conn.getInputStream();
    }
}
