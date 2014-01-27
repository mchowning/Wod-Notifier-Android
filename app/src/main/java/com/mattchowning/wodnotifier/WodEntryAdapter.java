package com.mattchowning.wodnotifier;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.net.Uri.parse;

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
            TextView dateView = (TextView) convertView.findViewById(R.id.list_item_wod_date);
            Date wodDate = thisEntry.date;
            if (wodDate != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
                String monthDayYear = sdf.format(wodDate.getTime());
                sdf.applyLocalizedPattern("EEEE");
                String dayOfWeek = sdf.format(wodDate.getTime());
                String text = monthDayYear + "\n" + dayOfWeek;
                dateView.setText(text);
            } else {
                dateView.setText(thisEntry.title);
            }

            TextView descriptionView =
                    (TextView) convertView.findViewById(R.id.list_item_wod_description);

            if (thisEntry.originalHtmlDescription != null) {
                descriptionView.setText(Html.fromHtml(thisEntry.originalHtmlDescription));

                // Makes html "links" clickable
                descriptionView.setMovementMethod(LinkMovementMethod.getInstance());
            }

            // Make clicking the text fields take you to the CFR webpage
            if (thisEntry.link != null) {
                final Uri url = Uri.parse(thisEntry.link);
                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, url);
                        getContext().startActivity(browserIntent);
                    }
                };
                dateView.setOnClickListener(listener);
            }
        }

        return convertView;
    }
}
