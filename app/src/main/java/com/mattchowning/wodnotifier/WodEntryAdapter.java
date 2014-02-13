package com.mattchowning.wodnotifier;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.Html;
import android.text.Layout;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.mattchowning.wodnotifier.Database.MySQLiteHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Matt on 2/9/14.
 */

public class WodEntryAdapter extends CursorAdapter {

    public WodEntryAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item_wod, viewGroup, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        WodEntry entry = getWodEntry(cursor);
        fillHeadingView(view, context, entry);
        fillDescriptionView(view, entry);
    }

    private WodEntry getWodEntry(Cursor cursor) {
        String title = cursor.getString(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_TITLE));
        String link = cursor.getString(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_LINK));
        String description =
                cursor.getString(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_DESCRIPTION));
        return new WodEntry(title, link, description);
    }

    private void fillHeadingView(View view, final Context context, WodEntry entry) {                // TODO Making this context final so I can use it in my onClickListener does not feel right
        TextView headingView = (TextView) view.findViewById(R.id.list_item_wod_date);
        Date wodDate = entry.date;
        if (wodDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
            String monthDayYear = sdf.format(wodDate);
            sdf.applyLocalizedPattern("EEEE");
            String dayOfWeek = sdf.format(wodDate);
            String text = monthDayYear + "\n" + dayOfWeek;
            headingView.setText(text);
        } else {
            headingView.setText(entry.title);
        }

        final Uri url = Uri.parse(entry.link);
        headingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, url);
                context.startActivity(browserIntent);
            }
        });
    }

    private void fillDescriptionView(View view, WodEntry entry) {
        TextView descriptionView =
                (TextView) view.findViewById(R.id.list_item_wod_description);
        descriptionView.setMovementMethod(LinkMovementMethod.getInstance());
        descriptionView.setText(Html.fromHtml(entry.originalHtmlDescription));
    }
}
