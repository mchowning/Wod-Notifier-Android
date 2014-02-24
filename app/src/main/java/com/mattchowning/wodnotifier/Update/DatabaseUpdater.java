package com.mattchowning.wodnotifier.Update;

import com.mattchowning.wodnotifier.Database.MyContentProviderHelper;
import com.mattchowning.wodnotifier.WodEntry;

import java.util.ArrayList;

public class DatabaseUpdater {

    private boolean updatedDatabase;
    private ArrayList<WodEntry> newWodEntries = new ArrayList<WodEntry>();

    public DatabaseUpdater() {
        updatedDatabase = false;
    }

    public void update(ArrayList<WodEntry> downloadedEntries, MyContentProviderHelper database ) {
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