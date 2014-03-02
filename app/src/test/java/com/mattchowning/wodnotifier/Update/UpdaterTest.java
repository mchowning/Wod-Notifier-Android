package com.mattchowning.wodnotifier.Update;

import android.content.Context;
import android.content.Intent;

import com.mattchowning.wodnotifier.UpdateScheduler;
import com.mattchowning.wodnotifier.WodEntry;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class UpdaterTest {

//    private UpdateScheduler updateSchedulerMock;

    @Test
    public void testBroadcastUpdateWithNewEntries() {
        Context realContext = Robolectric.getShadowApplication().getApplicationContext();
        Context contextSpy = spy(realContext);
        DatabaseUpdater databaseUpdaterMock = Mockito.mock(DatabaseUpdater.class);
        when(databaseUpdaterMock.databaseWasUpdated()).thenReturn(true);
        UpdateScheduler updateSchedulerMock = Mockito.mock(UpdateScheduler.class);
        WodDownloader wodDownloaderMock = Mockito.mock(WodDownloader.class);
        when(wodDownloaderMock.downloadedWods((String)anyObject()))
                .thenReturn(new ArrayList<WodEntry>());
        Updater updater = new Updater(contextSpy, databaseUpdaterMock, updateSchedulerMock,
                wodDownloaderMock);

        // Verify no insertIntoDatabaseIfMissing broadcast on first download
//        updater.update();
//        verify(contextSpy, never()).sendBroadcast((Intent) anyObject());
        // FIXME This verify test fails sometimes, but not always.

        // Verify insertIntoDatabaseIfMissing broadcast on second download
        updater.update();
        verify(contextSpy).sendBroadcast((Intent) anyObject());
    }

    @Test
    public void testNoBroadcastUpdateWithNoNewEntries() {
        Context realContext = Robolectric.getShadowApplication().getApplicationContext();
        Context contextSpy = spy(realContext);
        DatabaseUpdater databaseUpdaterMock = Mockito.mock(DatabaseUpdater.class);
        when(databaseUpdaterMock.databaseWasUpdated()).thenReturn(false);
        UpdateScheduler updateSchedulerMock = Mockito.mock(UpdateScheduler.class);
        WodDownloader wodDownloaderMock = Mockito.mock(WodDownloader.class);
        when(wodDownloaderMock.downloadedWods((String)anyObject()))
                .thenReturn(new ArrayList<WodEntry>());
        Updater updater = new Updater(contextSpy, databaseUpdaterMock, updateSchedulerMock,
                wodDownloaderMock);

        updater.update();
        updater.update();
        updater.update();
        // Verify no insertIntoDatabaseIfMissing broadcast despite multiple insertIntoDatabaseIfMissing checks which return no new entries.
        verify(contextSpy, never()).sendBroadcast((Intent) anyObject());
    }

    @Test
    public void testCallToUpdateScheduler() {
        Context realContext = Robolectric.getShadowApplication().getApplicationContext();
        Context contextSpy = spy(realContext);
        DatabaseUpdater databaseUpdaterMock = Mockito.mock(DatabaseUpdater.class);
        when(databaseUpdaterMock.databaseWasUpdated()).thenReturn(true);
        UpdateScheduler updateSchedulerMock = Mockito.mock(UpdateScheduler.class);
        WodDownloader wodDownloaderMock = Mockito.mock(WodDownloader.class);
        when(wodDownloaderMock.downloadedWods((String)anyObject()))
                .thenReturn(new ArrayList<WodEntry>());
        Updater updater = new Updater(contextSpy, databaseUpdaterMock, updateSchedulerMock,
                wodDownloaderMock);
        updater.update();
//        Context realContext = Robolectric.getShadowApplication().getApplicationContext();
//        Context contextSpy = spy(realContext);
//        UpdateScheduler updateSchedulerMock = new UpdateScheduler(contextSpy);
//
//        Updater updater = new Updater(null, null, updateSchedulerMock);
//        updater.update();
//
        verify(updateSchedulerMock).setAlarms(anyBoolean(), anyBoolean());
    }
}

