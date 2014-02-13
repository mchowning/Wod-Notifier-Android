package com.mattchowning.wodnotifier.Database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;

public class MyContentProvider extends ContentProvider {

    // database
    private MySQLiteHelper helper;

    // used for UriMatcher
    private static final int WODS_URI_PATTERN = 1;
    private static final int WOD_ID_URI_PATTERN = 2;
    private static final String AUTHORITY =
            "com.mattchowning.wodnotifier.Database.MyContentProvider";
    private static final String WOD_PATH = "wod";
    public static final Uri WOD_URI = Uri.parse("content://" + AUTHORITY + "/" + WOD_PATH);

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(AUTHORITY, WOD_PATH, WODS_URI_PATTERN);
        sUriMatcher.addURI(AUTHORITY, WOD_PATH + "/#", WOD_ID_URI_PATTERN);
    }

    // TODO?? Make thread safe?

//    public MyContentProvider() {}

    @Override
    public boolean onCreate() {
        helper = MySQLiteHelper.getInstance(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        checkColumns(projection);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(MySQLiteHelper.TABLE_WOD_ENTRIES);
        int uriType = sUriMatcher.match(uri);
        switch (uriType) {
            case WODS_URI_PATTERN:
                break;
            case WOD_ID_URI_PATTERN:
                queryBuilder.appendWhere(MySQLiteHelper.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknwon URI " + uri);
        }

        SQLiteDatabase database = helper.getWritableDatabase();
        Cursor cursor = queryBuilder.query(database, projection, selection, selectionArgs, null,
                null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    private void checkColumns(String[] projection) {
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns =
                    new HashSet<String>(Arrays.asList(MySQLiteHelper.ALL_COLUMNS));
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase database = helper.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case WODS_URI_PATTERN:
                rowsDeleted =
                        database.delete(MySQLiteHelper.TABLE_WOD_ENTRIES,
                                selection,
                                selectionArgs);
                break;
            case WOD_ID_URI_PATTERN:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = database.delete(MySQLiteHelper.TABLE_WOD_ENTRIES,
                            MySQLiteHelper.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = database.delete(MySQLiteHelper.TABLE_WOD_ENTRIES,
                            MySQLiteHelper.COLUMN_ID + "=" + id + "and" + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase database = helper.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case WODS_URI_PATTERN:
                rowsUpdated = database.update(MySQLiteHelper.TABLE_WOD_ENTRIES,
                        values,
                        selection,
                        selectionArgs);
                break;
            case WOD_ID_URI_PATTERN:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = database.update(MySQLiteHelper.TABLE_WOD_ENTRIES,
                            values,
                            MySQLiteHelper.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = database.update(MySQLiteHelper.TABLE_WOD_ENTRIES,
                            values,
                            MySQLiteHelper.COLUMN_ID + "=" + id + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase database = helper.getWritableDatabase();
        long id = 0;
        switch (uriType) {
            case WODS_URI_PATTERN:
                id = database.insert(MySQLiteHelper.TABLE_WOD_ENTRIES, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return uri.parse(WOD_PATH + "/" + id);
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }
}