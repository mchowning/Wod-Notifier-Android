package com.mattchowning.wodnotifier.Views;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mattchowning.wodnotifier.Database.MyContentProvider;
import com.mattchowning.wodnotifier.Database.MySQLiteHelper;
import com.mattchowning.wodnotifier.Database.WodEntryDataSource;
import com.mattchowning.wodnotifier.R;
import com.mattchowning.wodnotifier.UpdateService;
import com.mattchowning.wodnotifier.WodEntryAdapter;

import java.sql.SQLException;

/*
 * WodList
 * -------
 * Fragment containing a list of WodEntries Fragment containing a list of WodEntries that it fills
 * using an AsyncTask to make a network call.
 */

public class WodList extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private WodEntryAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new WodEntryAdapter(getActivity(), null, 0);
        setListAdapter(adapter);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onResume() {
        // TODO Do I want to check for updates every time this activity resumes?
        Intent updateServiceIntent = new Intent(getActivity(), UpdateService.class);
        getActivity().startService(updateServiceIntent);
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_wod_list, container, false);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {MySQLiteHelper.COLUMN_ID,
                MySQLiteHelper.COLUMN_TITLE,
                MySQLiteHelper.COLUMN_LINK,
                MySQLiteHelper.COLUMN_DESCRIPTION };
        String sortOrder = MySQLiteHelper.COLUMN_DATE + " DESC";
        CursorLoader cursorLoader = new CursorLoader(getActivity(), MyContentProvider.WOD_URI, projection,
                null, null, sortOrder);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        adapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        adapter.changeCursor(null);
    }
}
