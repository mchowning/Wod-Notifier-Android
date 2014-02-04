package com.mattchowning.wodnotifier;

import android.text.Html;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/*
 * XmlParser
 * ---------
 * Parses down through the Crossfit Reviver XML feed with the form of
 *  rss
 *      channel
 *          . . .
 *          item
 *              title
 *              link
 *              . . .
 *              originalHtmlDescription
 *              . . .
 *
 * and get the title, link, and originalHtmlDescription.
 */

public class XmlParser {

    private static final String ns = null;  // TODO Not sure what the point of this is...

    public static ArrayList<WodEntry> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            ArrayList<WodEntry> entries = readRss(parser);
            return entries;
        } finally {
            in.close();
        }
    }

    private static ArrayList<WodEntry> readRss(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<WodEntry> entries = new ArrayList<WodEntry>();
        parser.require(XmlPullParser.START_TAG, ns, "rss"); // Tests to make sure at proper ("rss") position
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) continue;
            String name = parser.getName();
            if (name.equals("channel")) {
                return readChannel(parser, entries);
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    private static ArrayList<WodEntry> readChannel(XmlPullParser parser, ArrayList<WodEntry> entries)
            throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "channel"); // Test to make sure at proper ("channel") position
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) continue;
            String name = parser.getName();
            if (name.equals("item")) {
                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    private static WodEntry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "item"); // Test to make sure at proper ("item") position
        String title = null;
        String link = null;
        String htmlDescription = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) continue;
            String name = parser.getName();
            if (name.equals("title")) {
                title = readTitle(parser);
            } else if (name.equals("link")) {
                link = readLink(parser);
            } else if (name.equals("description")) {
                htmlDescription = readDescription(parser);
            } else {
                skip(parser);
            }
        }
        return new WodEntry(title, link, htmlDescription);
    }

    private static String readTitle(XmlPullParser parser) throws XmlPullParserException, IOException {
       parser.require(XmlPullParser.START_TAG, ns, "title");
       String title = readText(parser); // FIXME Change variable name
       parser.require(XmlPullParser.END_TAG, ns, "title");
       return title;
   }

    private static String readLink(XmlPullParser parser) throws XmlPullParserException, IOException {
        String link = "";
        parser.require(XmlPullParser.START_TAG, ns, "link");
        String tag = parser.getName();  // Does this line just duplicate the previous line?
        if (tag.equals("link")) {
            link = readText(parser);
        }
        parser.require(XmlPullParser.END_TAG, ns, "link");
        return link;
    }

    private static String readDescription(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "description");
        String description = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "description");
        return description;
    }

    private static String readText(XmlPullParser parser) throws XmlPullParserException, IOException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    /* Takes a parser at a starting tag and skips that entire tag, including any tags it contains. */
    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) throw new IllegalStateException();
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
