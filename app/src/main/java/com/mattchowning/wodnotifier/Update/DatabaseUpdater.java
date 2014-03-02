package com.mattchowning.wodnotifier.Update;

import com.mattchowning.wodnotifier.Database.MyContentProviderHelper;
import com.mattchowning.wodnotifier.WodEntry;

import java.util.ArrayList;

public class DatabaseUpdater {

    private final MyContentProviderHelper database;
    private boolean updatedDatabase;
    private ArrayList<WodEntry> newWodEntries = new ArrayList<WodEntry>();

    public DatabaseUpdater(MyContentProviderHelper database) {
        this.database = database;
        updatedDatabase = false;
    }

    public void insertIntoDatabaseIfMissing(ArrayList<WodEntry> downloadedEntries) {
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