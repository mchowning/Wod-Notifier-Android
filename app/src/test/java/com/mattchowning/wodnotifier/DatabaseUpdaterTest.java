package com.mattchowning.wodnotifier;

import com.mattchowning.wodnotifier.Database.MyContentProviderHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class DatabaseUpdaterTest {

    private ArrayList<WodEntry> preexistingDatabaseEntries;
    private ArrayList<WodEntry> newEntries;
    private MyContentProviderHelper databaseMock;
    private ArrayList<WodEntry> databaseUnderlyingData;
    private WodDownloader wodDownloaderMock;


    @Before
    public void setUp() {
        initializeMyContentProviderHelper();
        initializeWodDownloaderMock();
    }

    private void initializeWodDownloaderMock() {
        wodDownloaderMock = Mockito.mock(WodDownloader.class);
        when(wodDownloaderMock.getDownloadedWods()).thenReturn(newEntries);
    }

    private void initializeMyContentProviderHelper() {
        setupContentProviderHelperMock();
        createOriginalEntries();
        insertOriginalEntriesIntoMyContentProviderHelperMock();
        createNewEntries();
    }

    private void setupContentProviderHelperMock() {
        databaseMock = Mockito.mock(MyContentProviderHelper.class);
        databaseUnderlyingData = new ArrayList<WodEntry>();
        when(databaseMock.insert((WodEntry)anyObject()))
                .thenAnswer(new Answer() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        WodEntry entry = getWodEntryArg(invocation);
                        databaseUnderlyingData.add(entry);
                        return null;
                    }
                });
        when(databaseMock.contains((WodEntry)anyObject()))
                .thenAnswer(new Answer() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        WodEntry entry = getWodEntryArg(invocation);
                        return databaseUnderlyingData.contains(entry);
                    }
                });
    }

    private WodEntry getWodEntryArg(InvocationOnMock invocation) {
        Object[] args = invocation.getArguments();
        return (WodEntry) args[0];
    }

    private void createOriginalEntries() {
        preexistingDatabaseEntries = new ArrayList<WodEntry>();
        for (int i = 0; i < 3; i++) {
            String num = Integer.toString(i+1);
            String title = "title" + num;
            String link = "link" + num;
            String description = "description" + num;
            WodEntry entry = new WodEntry(title, link, description);
            preexistingDatabaseEntries.add(entry);
        }
    }

    private void insertOriginalEntriesIntoMyContentProviderHelperMock() {
        for (WodEntry entry : preexistingDatabaseEntries) {
            // If I use databaseMock.insert, then it is difficult
            // to verify the number of times the tested code calls the insert method.
            databaseUnderlyingData.add(entry);
        }
    }

    private void createNewEntries() {
        newEntries = new ArrayList<WodEntry>();
        for (int i = 0; i < 2; i++) {
            String num = Integer.toString(i+1);
            String title = "New Entry title" + num;
            String link = "New Entry link" + num;
            String description = "New Entry description" + num;
            WodEntry entry = new WodEntry(title, link, description);
            newEntries.add(entry);
        }
    }

    @Test
    public void testDatabaseNotUpdatedBeforeUpdateMethodCalled() {
        DatabaseUpdater databaseUpdater = new DatabaseUpdater(wodDownloaderMock,
                databaseMock);
        assertFalse(databaseUpdater.databaseWasUpdated());
    }

    @Test
    public void testDatabaseUpdatedOnUpdateMethodCall() {
        DatabaseUpdater databaseUpdater = new DatabaseUpdater(wodDownloaderMock,
                databaseMock);
        databaseUpdater.update();
        assertTrue(databaseUpdater.databaseWasUpdated());
    }

    @Test
    public void testGetNewWodEntriesAfterUpdate() {
        DatabaseUpdater databaseUpdater = new DatabaseUpdater(wodDownloaderMock,
                databaseMock);
        databaseUpdater.update();
        ArrayList<WodEntry> newEntriesFromDatabase = databaseUpdater.getNewWodEntries();
        for (WodEntry entry : newEntries) {
            assertTrue(newEntriesFromDatabase.contains(entry));
        }
    }

    @Test
    public void testThatMethodCallToInsertEntriesIntoDatabaseIsMade() {
        DatabaseUpdater databaseUpdater = new DatabaseUpdater(wodDownloaderMock,
                databaseMock);
        databaseUpdater.update();
        for (WodEntry entry : newEntries) {
            verify(databaseMock).insert(entry);
        }
    }

    @Test
    public void testNoDuplicateEntriesInserted() {
        when(wodDownloaderMock.getDownloadedWods()).thenReturn(preexistingDatabaseEntries);
        DatabaseUpdater databaseUpdater = new DatabaseUpdater(wodDownloaderMock,
                databaseMock);
        databaseUpdater.update();
        assertFalse(databaseUpdater.databaseWasUpdated());
    }
}
