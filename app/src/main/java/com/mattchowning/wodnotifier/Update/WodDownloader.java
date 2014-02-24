package com.mattchowning.wodnotifier.Update;

import com.mattchowning.wodnotifier.WodEntry;
import com.mattchowning.wodnotifier.XmlParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class WodDownloader {

    public ArrayList<WodEntry> downloadedWods(String urlString) {
        InputStream stream = null;
        ArrayList<WodEntry> downloadedEntries = null;
        try {
            stream = downloadFromUrl(urlString);
            downloadedEntries = XmlParser.parse(stream);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } finally {
            closeStream(stream);
        }
        return downloadedEntries;
    }

    private void closeStream(InputStream stream){
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Given a string representation of a URL, sets up a connection and gets an input stream.
    private InputStream downloadFromUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setDoInput(true);
        conn.connect();
        return conn.getInputStream();
    }
}