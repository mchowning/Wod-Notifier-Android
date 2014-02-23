package com.mattchowning.wodnotifier.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.mattchowning.wodnotifier.WodEntry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyContentProviderHelper {

    public static final String DATE_FORMAT = "yyyy-mm-dd";    // Standard SQL Date format
    private Context context;

    public MyContentProviderHelper(Context context) {
        this.context = context;
    }
    public boolean contains(WodEntry entry) {
        String entrySqlDate = getSqlDate(entry.date);
        String[] projection = {MySQLiteHelper.COLUMN_DATE};
        String selection = MySQLiteHelper.COLUMN_TITLE + "=? AND " +
                MySQLiteHelper.COLUMN_LINK + "=? AND " +
                MySQLiteHelper.COLUMN_DESCRIPTION + "=? AND " +
                MySQLiteHelper.COLUMN_DATE + "=?";
        String[] selectionArgs = { entry.title, entry.link, entry.originalHtmlDescription,
                entrySqlDate };
        Cursor cursor = context.getContentResolver().query(MyContentProvider.WOD_URI,
                projection,
                selection,
                selectionArgs,
                null);
        return (cursor.getCount() > 0);
    }

    public boolean insert(WodEntry entry) {
        if (contains(entry)) return false;
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_TITLE, entry.title);
        values.put(MySQLiteHelper.COLUMN_LINK, entry.link);
        values.put(MySQLiteHelper.COLUMN_DESCRIPTION, entry.originalHtmlDescription);
        String sqlDate = getSqlDate(entry.date);
        values.put(MySQLiteHelper.COLUMN_DATE, sqlDate);
        context.getContentResolver().insert(MyContentProvider.WOD_URI, values);
        return true;
    }

    private String getSqlDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        return sdf.format(date);
    }
}
