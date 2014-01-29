package com.mattchowning.wodnotifier;

import static org.junit.Assert.assertEquals;
import org.junit.runner.RunWith;
import org.junit.Test;

import org.robolectric.RobolectricTestRunner;

import dalvik.annotation.TestTargetClass;


@RunWith(RobolectricTestRunner.class)
public class Test_WodEntry {

    @Test
    public void testConstructor() {
        String titleInput = "01/02/03";
        String linkInput = "http://www.mattchowning.com";
        String originalHtmlDescriptionInput =
                "<p><strong>1. :60 on / 2mins off x 4 @ 100%</strong></p>";

        WodEntry entry = new WodEntry(titleInput, linkInput, originalHtmlDescriptionInput);
        assertEquals("Title in WodEntry constructor is not properly assigned",
                entry.title, titleInput);
        assertEquals("Link in WodEntry constructor is not properly assigned", entry.link, linkInput);
    }
}