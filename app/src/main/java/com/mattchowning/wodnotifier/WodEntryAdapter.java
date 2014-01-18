package com.mattchowning.wodnotifier;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/*
 * WodEntryAdapter
 * ---------------
 * Adapter for putting individual WodEntries into a list entry in the WodList.
 */

public class WodEntryAdapter extends ArrayAdapter<WodEntry> {

    public WodEntryAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)
                    getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_wod, null);
        }

        WodEntry thisEntry = getItem(position);
        // Find better way to do error/null checking??
        if (thisEntry != null) {
            if (thisEntry.date != null) {
                TextView dateView = (TextView) convertView.findViewById(R.id.list_item_wod_date);
                dateView.setText(thisEntry.date);
            }
            if (thisEntry.description != null) {
                TextView descriptionView =
                        (TextView) convertView.findViewById(R.id.list_item_wod_description);
                descriptionView.setText(thisEntry.description);
            }
        }

        return convertView;
    }
}
