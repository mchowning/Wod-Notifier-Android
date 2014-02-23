package com.mattchowning.wodnotifier;

import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.Test;

import org.robolectric.RobolectricTestRunner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//import dalvik.annotation.TestTargetClass;

@RunWith(RobolectricTestRunner.class)
public class WodEntryTest {

    // Test 'title' assignment
    @Test
    public void testConstructorTitleDate() {
        String titleInput = "01/02/03";
        WodEntry entry = new WodEntry(titleInput, null, null);
        assertEquals("Title in WodEntry constructor is not properly assigned",
                titleInput, entry.title);
    }

    // Test setDescription() method with valid date
    @Test
    public void testConstructorValidDate() {
        String titleInput = "01/02/03";
        WodEntry entry = new WodEntry(titleInput, null, null);
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
        String entryMonthDayYear = sdf.format(entry.date);
        String expectedEntryMonthDayYear = "January 2, 2003";
        assertEquals("Processed date appropriate to valid WodEntry input",
                expectedEntryMonthDayYear, entryMonthDayYear);
    }

    // Test setDescription() method with invalid date
    @Test
    public void testConstructorInvalidDate() {
        String nonDateTitle = "Title";
        WodEntry entry = new WodEntry(nonDateTitle, null, null);
        assertNull("A non-date title should result in a null date value", entry.date);
    }

    @Test
    public void testConstructorLink() {
        String linkInput = "http://www.mattchowning.com";
        WodEntry entry = new WodEntry(null, linkInput, null);
        assertEquals("Link in WodEntry constructor is not properly assigned",
                linkInput, entry.link);
    }

    @Test
    public void testConstructorHtmlDescription() {
        String originalHtmlDescriptionInput =
                "<p><strong>1. :60 on / 2mins off x 4 @ 100%</strong></p>";
        WodEntry entry = new WodEntry(null, null, originalHtmlDescriptionInput);
        assertEquals("originalHtmlDescription is not properly assigned",
                originalHtmlDescriptionInput, entry.originalHtmlDescription);
    }

    @Test
    public void testConstructorPlainDescription() {
        String originalHtmlDescriptionInput =
                "<p><strong>1. :60 on / 2mins off x 4 @ 100%</strong></p>";
        WodEntry entry = new WodEntry(null, null, originalHtmlDescriptionInput);
        String expectedPlainTextDescription = "1. :60 on / 2mins off x 4 @ 100%";
        assertEquals("plainTextDescription not properly parsed",
                expectedPlainTextDescription, entry.getPlainTextDescription());
    }
}