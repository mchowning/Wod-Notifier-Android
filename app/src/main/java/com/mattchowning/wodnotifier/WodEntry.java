package com.mattchowning.wodnotifier;

import android.text.Html;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * WodEntry
 * --------
 * Simple class for holding data about the WOD for a particular day.
 */
public class WodEntry {
    public String title;            // Title field from XML, which is the Wod's date at this gym in
                                    // the form of ##/##/##
    public String link;             // Link to CFR posting of Wod
    public String htmlDescription;  // Description of the Wod with original html formatting
    public String description;      // Description of the Wod in plain text
    public String date;             // Date of the Wod with the form Month Day, Year

    private static final String TAG = WodEntry.class.getName();

    public WodEntry(String title, String link, String htmlDescription) {
        this.title = title;
        setDate(title);
        this.link = link;
        this.htmlDescription = htmlDescription;
        setDescription();
    }

    /* This method parses the date from the Wod title, converts it into a Date object, and then
    forms a human readable string for storage in the date instance variable. */
    private void setDate(String dateString) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
            Date dateObject = sdf.parse(dateString);
            sdf = new SimpleDateFormat("MMMM d, yyyy" );
            date = sdf.format(dateObject.getTime());
            date += "\n";
            sdf.applyPattern("EEEE");
            date += sdf.format(dateObject);
        } catch (ParseException ex) {
            Log.e(TAG, "SimpleDateFormat could not parse the Wod's date");
        }

        // Alternative regex parsing of date
        // Regular Expression to test whether the dateString is in expected format of ##/##/##.
        // The dividers can be either /, \, or -.
//        String dateFormatRegExp = "\\d\\d[-\\/\\\\]\\d\\d[-\\/\\\\]\\d\\d";
//        if (dateString.matches(dateFormatRegExp)) {
//            int month = Integer.parseInt(dateString.substring(0, 2));
//            int day = Integer.parseInt(dateString.substring(3, 5));
//            int year = 2000 + Integer.parseInt(dateString.substring(6));
//            date = new GregorianCalendar(year, month, day);
//        }
    }

    /* This method sets the description by taking the html description, converting it to plain text,
    then removing everything from "*Post results to comments on" on if
    that substring is present.  Last, the method then removes any new line characters at the end
    of the string. */
    private void setDescription() {
        if (htmlDescription != null) {
            String plaintTextDescription = Html.fromHtml(htmlDescription).toString();
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
            description = plaintTextDescription.substring(0, indexOfBreakingPoint);
        }
    }
}
