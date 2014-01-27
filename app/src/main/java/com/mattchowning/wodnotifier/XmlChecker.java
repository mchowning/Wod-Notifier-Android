package com.mattchowning.wodnotifier;

import java.util.ArrayList;

/*
 * XmlChecker
 * ----------
 * This interface is so that an object implementing the interface can receive a callback when it
 * has the app check the rss feed and new entries are found.
 */
public interface XmlChecker {

    // Called by the class that checks the rss feed when it receives entries. Returns the entries
    // received as well as a boolean indicating whether the entries received are different from
    // the last entries received.
    public void entriesReceived(ArrayList<WodEntry> entries, boolean wereResultsUpdated);
}
