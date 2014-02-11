package com.mattchowning.wodnotifier.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.mattchowning.wodnotifier.WodEntry;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
                                    MySQLiteHelper.COLUMN_DESCRIPTION,
                                    MySQLiteHelper.COLUMN_DATE};

    public WodEntryDataSource(Context context) {
        helper = MySQLiteHelper.getInstance(context);
    }

    public void open() throws SQLException {
        database = helper.getWritableDatabase();
    }

    public void close() throws SQLException {
        helper.close();
    }

    // Returns the COLUMN_ID of the newly insert wod entry, or -1 if Wod was already present in
    // database.
    public long insertWodIntoDatabase(WodEntry entry) {
        long result = -1;

        boolean databaseAlreadyContainsWod = containsWod(entry);
        if (databaseAlreadyContainsWod) {

            // FIXME Need to check if there is already a wod with this date and handle that.
            // Best to err on the side of keeping all info, even if that means it is
            // possible to have duplicates?

        } else {
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.COLUMN_TITLE, entry.title);
            values.put(MySQLiteHelper.COLUMN_LINK, entry.link);
            values.put(MySQLiteHelper.COLUMN_DESCRIPTION, entry.originalHtmlDescription);
            String sqlDate = getSqlDate(entry.date);
            values.put(MySQLiteHelper.COLUMN_DATE, sqlDate);
            result = database.insert(MySQLiteHelper.TABLE_WOD_ENTRIES, null, values);
        }
        return result;
    }

    private String getSqlDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyymmdd", Locale.US);
        return sdf.format(date);
    }

    public WodEntry getWodEntry(long insertId) {
        Cursor cursor = database.query(MySQLiteHelper.TABLE_WOD_ENTRIES, allColumns,
                MySQLiteHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        WodEntry entry = convertToWod(cursor);
        cursor.close();
        return entry;
    }

    public WodEntry getWodEntry(Date date) {
        String sqlDate = getSqlDate(date);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_WOD_ENTRIES, allColumns,
                MySQLiteHelper.COLUMN_DATE + " = " + sqlDate, null, null, null, null);
        cursor.moveToFirst();
        WodEntry entry = convertToWod(cursor);
        cursor.close();
        return entry;
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
            WodEntry entry = convertToWod(cursor);
            entries.add(entry);
            cursor.moveToNext();
        }
        cursor.close();
        return entries;
    }

    public boolean containsWod(WodEntry entry) {
        String entryDate = getSqlDate(entry.date);
        Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_WOD_ENTRIES +
                " WHERE " + MySQLiteHelper.COLUMN_DATE + " = " + entryDate, null);
        boolean isWodInDatabase = cursor.getCount() > 0;
        cursor.close();
        return isWodInDatabase;
    }

    public boolean isEmpty() {
        Cursor cursor =
                database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_WOD_ENTRIES, null);
        boolean isEmpty = (cursor.getCount() == 0);
        cursor.close();
        return isEmpty;
    }

    public Cursor getCursor() {
       return database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_WOD_ENTRIES, null);
    }

    private WodEntry convertToWod(Cursor cursor) {
        long id = cursor.getLong(0);
        String title = cursor.getString(1);
        String link = cursor.getString(2);
        String description = cursor.getString(3);
        return new WodEntry(id, title, link, description);
    }
}

