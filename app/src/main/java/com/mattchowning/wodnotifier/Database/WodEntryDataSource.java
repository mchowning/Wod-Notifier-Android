package com.mattchowning.wodnotifier.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mattchowning.wodnotifier.WodEntry;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is the "data access object", which is responsible for handling the database
 * connection and for accessing and modifying the data.  It will also convert the database objects
 * into real Java objects, so that our user interface does not have to deal with the persistence
 * layer.  Using a DAO is not always the right approach.  A DAO creates Java model objects; using a
 * database directly or via a ContentProvider is typically more resource efficient as you can avoid
 * the creation of model objects.
 *
 * This class maintains our database connection and supports adding new WodEntrys and fetching all
 * WodEntrys.
 */
public class WodEntryDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper helper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
                                    MySQLiteHelper.COLUMN_TITLE,
                                    MySQLiteHelper.COLUMN_LINK,
                                    MySQLiteHelper.COLUMN_DESCRIPTION};

    public WodEntryDataSource(Context context) {
        helper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = helper.getWritableDatabase();
    }

    public void close() throws SQLException {
        helper.close();
    }

    public WodEntry createWodEntry(String title, String link, String description) {
        // Insert wod data into database
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_TITLE, title);
        values.put(MySQLiteHelper.COLUMN_LINK, link);
        values.put(MySQLiteHelper.COLUMN_DESCRIPTION, description);
        long insertId = database.insert(MySQLiteHelper.TABLE_WOD_ENTRIES, null, values);

        // Extract WodEntry from database
        Cursor cursor = database.query(MySQLiteHelper.TABLE_WOD_ENTRIES, allColumns,
                MySQLiteHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        WodEntry entry = cursorToWod(cursor);
        cursor.close();
        return entry;
    }

    public void addWodEntry(WodEntry entry) {
        createWodEntry(entry.title, entry.link, entry.originalHtmlDescription);
    }

    public void deleteWodEntry(WodEntry entry) {
        long id = entry.id;
        System.out.println("WodEntry deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_WOD_ENTRIES, MySQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    public List<WodEntry> getAllWods() {
        List<WodEntry> entries = new ArrayList<WodEntry>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_WOD_ENTRIES, allColumns, null, null, null,
                null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            WodEntry entry = cursorToWod(cursor);
            entries.add(entry);
            cursor.moveToNext();
        }
        cursor.close();
        return entries;
    }

    public Cursor getCursor() {
       return database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_WOD_ENTRIES, null);
    }

    private WodEntry cursorToWod(Cursor cursor) {
        long id = cursor.getLong(0);
        String title = cursor.getString(1);
        String link = cursor.getString(2);
        String description = cursor.getString(3);
        return new WodEntry(id, title, link, description);
    }
}

