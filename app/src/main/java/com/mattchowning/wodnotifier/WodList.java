package com.mattchowning.wodnotifier;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/*
 * WodList
 * -------
 * Fragment containing a list of WodEntries Fragment containing a list of WodEntries that it fills
 * using an AsyncTask to make a network call.
 */

public class WodList extends ListFragment implements XmlChecker{
    public static final String WIFI = "Wi-Fi";
    public static final String ANY = "Any";
    private static final String URL =
            "http://www.crossfitreviver.com/index.php?format=feed&type=rss";

    private static boolean isWifiConnected = false;     // Is the device connected to wifi?
    private static boolean isMobileConnected = false;   // Is the device connected to cell service?
    public static boolean displayShouldRefresh = true;  // Does the display need to refresh?
    public static String sPref = null;                  // User's preferences on above variables
    private WodEntryAdapter adapter;

    private static final String LAST_DOWNLOAD = "Title of most recent entry downloaded previously";
    private static final String TAG = WodList.class.getName();

    private Activity mActivity;

    @Override
    public void onResume() {
        super.onResume();
        new WodDownloader(mActivity, this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        if ((sPref.equals(ANY)) && (isWifiConnected || isMobileConnected)) {
//        new WodDownloader(activity, this);                                                          // TODO Make it show a title screen while the WOD is being downloaded?
//        } else if (sPref.equals(WIFI) && isWifiConnected) {                                       // Seems to be more of an issue on my phone.
//            new WodDownloader().execute(URL);
//        } else {
//
//        }
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        adapter = new WodEntryAdapter(getActivity(), android.R.layout.simple_spinner_item);
        setListAdapter(adapter);
        return inflater.inflate(R.layout.fragment_wod_list, container, false);
    }

    @Override
    public void entriesReceived(ArrayList<WodEntry> entries, boolean wereResultsUpdated) {
        adapter.clear();
        if (entries == null) {
            WodEntry entry = new WodEntry("Entries unavailable", null, null);                       // TODO Handle lack of internect connection in a better way
            adapter.add(entry);
        } else {
            adapter.addAll(entries);
        }
    }
}
