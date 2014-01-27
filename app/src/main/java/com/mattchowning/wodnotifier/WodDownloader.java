package com.mattchowning.wodnotifier;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/*
 * WodDownloader
 * -------------
 * Uses an AsyncTask to download the RSS feed and get it parsed into an arrayList of WodEntry
 * objects.
 */
public class WodDownloader extends AsyncTask<String, Void, ArrayList<WodEntry>> {

    private static final String TAG = WodList.class.getName();
    private static final String URL =
            "http://www.crossfitreviver.com/index.php?format=feed&type=rss";

    private Context context;
    private XmlChecker xmlChecker;

    public WodDownloader(Context context, XmlChecker xmlChecker) {
        this.context = context;
        this.xmlChecker = xmlChecker;
        execute(URL);
    }

    @Override
    protected ArrayList<WodEntry> doInBackground(String... urls) {
        ArrayList<WodEntry> entries = new ArrayList<WodEntry>();
        try {
            entries = loadXmlFromNetwork(urls[0]);
        } catch (IOException e) {                                                                   // TODO Make sure the app sets up the alarms even when there isn't an internet connection.
            Log.w(TAG, "IOException downloading rss feed");
            xmlChecker.entriesReceived(null, false);                                                // FIXME These calls just go back to the WodList the first time (or anytime the WodList is the caller).
        } catch (XmlPullParserException e) {
            Log.w(TAG, "Xml parsing exception downloading rss feed");
            xmlChecker.entriesReceived(null, false);
        }
        return entries;
    }

    @Override
    protected void onPostExecute(ArrayList<WodEntry> results) {
        if (!results.isEmpty()) {
            boolean wereResultsUpdated = checkIfUpdated(results);
            xmlChecker.entriesReceived(results, wereResultsUpdated);
        }
    }

    /* Uploads XML from source url, parses the title, link, and originalHtmlDescription, and puts it
    into a WodEntry */
    private ArrayList<WodEntry> loadXmlFromNetwork(String urlString)
            throws XmlPullParserException, IOException {
        InputStream stream = null;
        ArrayList<WodEntry> entries = null;
        try {
            XmlParser xmlParser = new XmlParser();
            stream = downloadUrl(urlString);
            entries = xmlParser.parse(stream);
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

    private boolean checkIfUpdated(ArrayList<WodEntry> results) {
        String justDownloadedEntry = results.get(0).title;
        SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean wereResultsUpdated;
        String lastDownloadPrefKey =
                context.getResources().getString(R.string.pref_last_downloaded_wod);

        if (sPrefs.contains(lastDownloadPrefKey)) {
            String lastDownloadedEntry = sPrefs.getString(lastDownloadPrefKey, null);
            if (lastDownloadedEntry.equals(justDownloadedEntry)) {
                Log.d(TAG, "Downloaded entries same as previous.");
                wereResultsUpdated = false;
//                wereResultsUpdated = true;                                                        // TESTING (alarm)
            } else {
                Log.d(TAG, "Downloaded updated entries.");
                updateSharedPreferences(sPrefs, lastDownloadPrefKey, justDownloadedEntry);
                wereResultsUpdated = true;
            }
        } else {
            Log.d(TAG, "First time downloading entries.");
            updateSharedPreferences(sPrefs, lastDownloadPrefKey, justDownloadedEntry);
            wereResultsUpdated = false;

            // Start first alarm
            new AlarmManagerBroadcastReceiver(context);
        }
        return wereResultsUpdated;
    }

    private void updateSharedPreferences(SharedPreferences sPrefs, String lastDownloadPrefKey,
                                         String justDownloadedEntry)
    {
        SharedPreferences.Editor editor = sPrefs.edit();
        editor.putString(lastDownloadPrefKey, justDownloadedEntry);
        editor.apply();
    }
}