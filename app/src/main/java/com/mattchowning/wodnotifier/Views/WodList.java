package com.mattchowning.wodnotifier.Views;

import android.app.Activity;
import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mattchowning.wodnotifier.R;
import com.mattchowning.wodnotifier.UpdateService;
import com.mattchowning.wodnotifier.WodEntry;
import com.mattchowning.wodnotifier.WodEntryAdapter;

import java.util.ArrayList;

/*
 * WodList
 * -------
 * Fragment containing a list of WodEntries Fragment containing a list of WodEntries that it fills
 * using an AsyncTask to make a network call.
 */

public class WodList extends ListFragment {
//    public static final String WIFI = "Wi-Fi";
//    public static final String ANY = "Any";
//
//    private static boolean isWifiConnected = false;     // Is the device connected to wifi?
//    private static boolean isMobileConnected = false;   // Is the device connected to cell service?
//    public static boolean displayShouldRefresh = true;  // Does the display need to refresh?
//    public static String sPref = null;                  // User's preferences on above variables

    private static final String URL =
            "http://www.crossfitreviver.com/index.php?format=feed&type=rss";
    private WodEntryAdapter adapter;
    private BroadcastReceiver receiver;

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        if ((sPref.equals(ANY)) && (isWifiConnected || isMobileConnected)) {
//        new WodDownloader(activity, this);                                                        // TODO Make it show a title screen while the WOD is being downloaded?
//        } else if (sPref.equals(WIFI) && isWifiConnected) {                                       // Seems to be more of an issue on my phone.
//            new WodDownloader().execute(URL);
//        } else {
//
//        }
//    }

    @Override
    public void onResume() {
        super.onResume();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ArrayList<WodEntry> entries = intent.getParcelableArrayListExtra(UpdateService.ENTRIES);
                adapter.clear();
                if (entries == null || entries.isEmpty()) {
                    WodEntry emptyEntry = new WodEntry("Entries unavailable", null, null);                       // TODO Handle lack of internet connection in a better way
                    adapter.add(emptyEntry);
                } else {
                    adapter.addAll(entries);
                }

                // Make sure broadcast is not received by SendNotificationReceiver
                abortBroadcast();
            }
        };
        IntentFilter filter = new IntentFilter("com.mattchowning.wodnotifier.UPDATE_COMPLETED");
        filter.setPriority(1);
        getActivity().registerReceiver(receiver, filter);

        Intent updateServiceIntent = new Intent(getActivity(), UpdateService.class);
        getActivity().startService(updateServiceIntent);
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
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }

}
