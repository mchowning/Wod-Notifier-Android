package com.mattchowning.wodnotifier.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This class is responsible for creating the Database
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    // Databases can have different tables
    public static final String TABLE_WOD_ENTRIES = "wodentries";

    // Tables can hold various values, which are listed below
    // Looks like you create a column for each data field you want to keep track of.
    // I think this first one serves as an identifier for the row -- ?kind of like the row
    // number in excel?
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_DESCRIPTION = "description";

    private static final String DATABASE_NAME = "woddata.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "CREATE TABLE " +
            TABLE_WOD_ENTRIES + "(" + COLUMN_ID + " INTEGER PRIMARY KEY, " +
            COLUMN_TITLE + " TEXT, " + COLUMN_LINK + " TEXT, " + COLUMN_DESCRIPTION + ");";

    public MySQLiteHelper(Context context) { //, String name, SQLiteDatabase.CursorFactory factory, int version) {
//        super(context, name, factory, version);
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to " + newVersion +
                        ", which will destroy all old data");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_WOD_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}