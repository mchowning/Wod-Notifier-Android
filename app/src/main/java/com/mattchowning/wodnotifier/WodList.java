package com.mattchowning.wodnotifier;

import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/*
 * WodList
 * -------
 * Fragment containing a list of WodEntries Fragment containing a list of WodEntries that it fills
 * using an AsyncTask to make a network call.
 */

public class WodList extends ListFragment {
    public static final String WIFI = "Wi-Fi";
    public static final String ANY = "Any";
    private static final String URL =
            "http://www.crossfitreviver.com/index.php?format=feed&type=rss";

    private static boolean isWifiConnected = false;     // Is the device connected to wifi?
    private static boolean isMobileConnected = false;   // Is the device connected to cell service?
    public static boolean displayShouldRefresh = true;  // Does the display need to refresh?
    public static String sPref = null;                  // User's preferences on above variables
    private WodEntryAdapter adapter;

    private static final String TAG = WodList.class.getName();

    // FIXME Doesn't check to make sure there is an internet connection
    // Uses AsyncTask to download the specified xml feed
    public WodList() {
//        if ((sPref.equals(ANY)) && (isWifiConnected || isMobileConnected)) {
            new DownloadXmlTask().execute(URL);
//        } else if (sPref.equals(WIFI) && isWifiConnected) {
//            new DownloadXmlTask().execute(URL);
//        } else {
//
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        adapter = new WodEntryAdapter(getActivity(), android.R.layout.simple_spinner_item);
        setListAdapter(adapter);
        return inflater.inflate(R.layout.fragment_wod_list, container, false);
    }

    private class DownloadXmlTask extends AsyncTask<String, Void, ArrayList<WodEntry>> {

        @Override
        protected ArrayList<WodEntry> doInBackground(String... urls) {
            ArrayList<WodEntry> arrayList = new ArrayList<WodEntry>();
            try {
                arrayList = loadXmlFromNetwork(urls[0]);
            } catch (IOException e) {
                Log.w(TAG, "IOException downloading rss feed");
            } catch (XmlPullParserException e) {
                Log.w(TAG, "Xml parsing exception downloading rss feed");
            }
            return arrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<WodEntry> results) {
            adapter.addAll(results);

        }

        /* Uploads XML from source url, parses the title, link, and htmlDescription, and puts it
        into a WodEntry */
        private ArrayList<WodEntry> loadXmlFromNetwork(String urlString)
                throws XmlPullParserException, IOException {
            InputStream stream = null;
            XmlParser xmlParser = new XmlParser();
            ArrayList<WodEntry> entries = null;
            try {
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
//            conn.setRequestMethod("GET"); // This seems unnecessary because it is the default,
                                            // but it was in the Android Developers sample.
            conn.setDoInput(true);
            conn.connect();
            return conn.getInputStream();
        }
    }
}
