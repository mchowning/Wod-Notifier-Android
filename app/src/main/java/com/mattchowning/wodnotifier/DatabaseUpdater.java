package com.mattchowning.wodnotifier;

import android.content.Context;

import com.mattchowning.wodnotifier.Database.MyContentProviderHelper;

import java.util.ArrayList;

public class DatabaseUpdater {

    private boolean updatedDatabase;
    private ArrayList<WodEntry> newWodEntries = new ArrayList<WodEntry>();
    private WodDownloader wodDownloader;
    private MyContentProviderHelper database;

    public DatabaseUpdater(WodDownloader wodDownloader, MyContentProviderHelper database ) {
        this.wodDownloader = wodDownloader;
        this.database = database;
    }

    public void update() {
        ArrayList<WodEntry> downloadedEntries = wodDownloader.getDownloadedWods();
        for (WodEntry entry : downloadedEntries) {
            if (!database.contains(entry)) {
                database.insert(entry);
                newWodEntries.add(entry);
                updatedDatabase = true;
            }
        }
    }

    public ArrayList<WodEntry> getNewWodEntries() {
        return newWodEntries;
    }
    public boolean databaseWasUpdated() {
        return updatedDatabase;
    }

}
