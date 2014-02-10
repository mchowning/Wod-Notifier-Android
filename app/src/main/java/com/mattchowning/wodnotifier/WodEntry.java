package com.mattchowning.wodnotifier;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/*
 * WodEntry
 * --------
 * Simple class for holding data about the WOD for a particular day.
 */
public class WodEntry implements Parcelable {
    public long id;
    public String title;                    // XML title field from XML, which is the Wod's date at
                                            // CFR in the form of [MM]/[DD]/[YY]
    public String link;                     // XML link field to CFR posting of Wod
    public String originalHtmlDescription;  // Original XML description field of the Wod in html
    public Date date;

    private static final String TAG = WodEntry.class.getName();

    public WodEntry(long id, String title, String link, String originalHtmlDescription) {
        this.id = id;
        this.title = title;
        this.link = link;
        this.date = getDate(title);
        this.originalHtmlDescription = originalHtmlDescription;
    }

    public WodEntry(String title, String link, String originalHtmlDescription) {
        this(0, title, link, originalHtmlDescription);
    }

    public WodEntry(Parcel parcel) {
        id = parcel.readLong();
        title = parcel.readString();
        link = parcel.readString();
        originalHtmlDescription = parcel.readString();
        date = getDate(title);
    }

    // Parses the title into a date, but only if it is of the format MM/DD/YY, MM-DD-YY, or MM\DD\YY
    private Date getDate(String text) {
        Date result = null;
        String dateFormatRegExp = "\\d\\d[-\\/\\\\]\\d\\d[-\\/\\\\]\\d\\d";
        if (text != null && text.matches(dateFormatRegExp)) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
                result = sdf.parse(text);
            } catch (ParseException ex) {
                Log.w(TAG, "SimpleDateFormat could not parse the Wod's date");
            }
        } else {
            Log.w(TAG, "SimpleDateFormat could not parse the Wod's date");
        }
        return result;
    }

    /* This method sets the description by taking the html description, converting it to plain text,
    then removing everything from "*Post results to comments on" on if
    that substring is present.  Last, the method then removes any new line characters at the end
    of the string. */
    public String getPlainTextDescription() {
        String result = null;
        if (originalHtmlDescription != null) {
            result = Html.fromHtml(originalHtmlDescription).toString();
        }
        return result;

        // If I want to break out the Wod portion
//        String breakingPoint = "*Post results to comments";
//        int indexOfBreakingPoint = (plainTextDescription.contains(breakingPoint)) ?
//                plainTextDescription.indexOf(breakingPoint) : plainTextDescription.length();
//        while (true) {
//            char lastChar = plainTextDescription.charAt(indexOfBreakingPoint - 1);
//            if (lastChar == '\n') {
//                indexOfBreakingPoint--;
//            } else {
//                break;
//            }
//        }
//        plainTextDescription = plainTextDescription.substring(0, indexOfBreakingPoint);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(title);
        parcel.writeString(link);
        parcel.writeString(originalHtmlDescription);
    }

    public static final Parcelable.Creator<WodEntry> CREATOR = new Creator<WodEntry>() {

        @Override
        public WodEntry createFromParcel(Parcel parcel) {
            return new WodEntry(parcel);
        }

        @Override
        public WodEntry[] newArray(int i) {
            return new WodEntry[i];
        }
    };

}
