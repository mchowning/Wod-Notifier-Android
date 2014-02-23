package com.mattchowning.wodnotifier;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class WodDownloader {

    private ArrayList<WodEntry> downloadedEntries;  // TODO Could be null???

    public WodDownloader(String urlString) {
        InputStream stream = null;
        try {
            stream = downloadUrl(urlString);
            downloadedEntries = XmlParser.parse(stream);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } finally {
            closeStream(stream);
        }
    }

    private void closeStream(InputStream stream){
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public ArrayList<WodEntry> getDownloadedWods() {
        return downloadedEntries;
    }
}