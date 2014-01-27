package com.mattchowning.wodnotifier;

import android.text.Html;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/*
 * WodEntry
 * --------
 * Simple class for holding data about the WOD for a particular day.
 */
public class WodEntry {
    public String title;                    // XML title field from XML, which is the Wod's date at
                                            // CFR in the form of [MM]/[DD]/[YY]
    public String link;                     // XML link field to CFR posting of Wod
    public String originalHtmlDescription;  // Original XML description field of the Wod in html
    public String plainTextDescription;     // Description of the Wod in plain text
    public Date date;

    private static final String TAG = WodEntry.class.getName();

    public WodEntry(String title, String link, String originalHtmlDescription) {
        this.title = title;
        this.link = link;
        this.originalHtmlDescription = originalHtmlDescription;
        setDescription();
        setDate();
    }

    // Parses the title into a date, but only if it is of the format MM/DD/YY, MM-DD-YY, or MM\DD\YY
    private void setDate() {
        if (title == null) return;
        String dateFormatRegExp = "\\d\\d[-\\/\\\\]\\d\\d[-\\/\\\\]\\d\\d";
        if (title.matches(dateFormatRegExp)) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
                date = sdf.parse(title);
            } catch (ParseException ex) {}
        }
    }

    /* This method sets the description by taking the html description, converting it to plain text,
    then removing everything from "*Post results to comments on" on if
    that substring is present.  Last, the method then removes any new line characters at the end
    of the string. */
    private void setDescription() {
        if (originalHtmlDescription == null) return;
        String plaintTextDescription = Html.fromHtml(originalHtmlDescription).toString();
        String breakingPoint = "*Post results to comments";
        int indexOfBreakingPoint = (plaintTextDescription.contains(breakingPoint)) ?
                plaintTextDescription.indexOf(breakingPoint) : plaintTextDescription.length();
        while (true) {
            char lastChar = plaintTextDescription.charAt(indexOfBreakingPoint - 1);
            if (lastChar == '\n') {
                indexOfBreakingPoint--;
            } else {
                break;
            }
        }
        plainTextDescription = plaintTextDescription.substring(0, indexOfBreakingPoint);
    }
}
