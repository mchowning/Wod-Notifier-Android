package com.mattchowning.wodnotifier;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.mattchowning.wodnotifier.Database.MyContentProvider;
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

/**
 * Created by Matt on 1/29/14.
 */
public class UpdateService extends IntentService {

    private static final String TAG = WodList.class.getName();
    private static final String URL =
            "http://www.crossfitreviver.com/index.php?format=feed&type=rss";
    public static final String NEW_ENTRIES =
            "com.mattchowning.wodnotifier.entries";
    public static boolean firstDownload = true;

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

        boolean databaseUpdated = false;
        ArrayList<WodEntry> newEntries = new ArrayList<WodEntry>();

        for (WodEntry entry : entries) {
            if (!wodInDatabase(entry)) {
                insertWodIntoDatabase(entry);
                databaseUpdated = true;
            } else {
                Log.d(null, "Wod already in database, so not added");
            }
        }

        if (databaseUpdated) {
            Intent outgoingIntent = new Intent();
            outgoingIntent.putParcelableArrayListExtra(NEW_ENTRIES, newEntries);
            outgoingIntent.setAction("com.mattchowning.wodnotifier.UPDATE_COMPLETED");
            sendBroadcast(outgoingIntent);
        }

        UpdateScheduler.setAlarms(this, databaseUpdated, firstDownload);
        AlarmReceiver.completeWakefulIntent(intent); // Releases any wakelock held by AlarmReceiver
        firstDownload = false;
    }

    private void insertWodIntoDatabase(WodEntry entry) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_TITLE, entry.title);
        values.put(MySQLiteHelper.COLUMN_LINK, entry.link);
        values.put(MySQLiteHelper.COLUMN_DESCRIPTION, entry.originalHtmlDescription);
        String sqlDate = getSqlDate(entry.date);
        values.put(MySQLiteHelper.COLUMN_DATE, sqlDate);
        getContentResolver().insert(MyContentProvider.WOD_URI, values);
    }

    private boolean wodInDatabase(WodEntry entry) {
        String entrySqlDate = getSqlDate(entry.date);
        String[] projection = {MySQLiteHelper.COLUMN_DATE};
        Cursor cursor = getContentResolver().query(MyContentProvider.WOD_URI,
                projection,
                MySQLiteHelper.COLUMN_DATE + "=" + entrySqlDate,
                null, null);
        return (cursor.getCount() > 0);
    }

    /* Returns a date object as a String in the format stored in the sql database */
    private String getSqlDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(MySQLiteHelper.DATE_FORMAT, Locale.US);
        return sdf.format(date);
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
