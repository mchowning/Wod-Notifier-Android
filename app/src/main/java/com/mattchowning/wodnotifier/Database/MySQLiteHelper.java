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
    public static final String COLUMN_DATE = "date";
    public static final String[] ALL_COLUMNS = {COLUMN_ID, COLUMN_TITLE, COLUMN_LINK,
            COLUMN_DESCRIPTION, COLUMN_DATE};
    public static final String DATE_FORMAT = "yyyy-mm-dd";    // Standard SQL Date format

    private static final String DATABASE_NAME = "woddata.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_WOD_ENTRIES + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY, " +
            COLUMN_TITLE + " TEXT, " +
            COLUMN_LINK + " TEXT, " +
            COLUMN_DESCRIPTION + " TEXT, " +
            COLUMN_DATE + " TEXT);";

    private static MySQLiteHelper instance;

    // Apparently this has to be a singleton to avoid leaking SQLite Connections
    // http://www.androiddesignpatterns.com/2012/05/correctly-managing-your-sqlite-database.html
    public static MySQLiteHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MySQLiteHelper(context);
        }
        return instance;
    }

    private MySQLiteHelper(Context context) {
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