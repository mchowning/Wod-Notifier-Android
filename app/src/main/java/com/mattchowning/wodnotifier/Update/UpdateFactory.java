package com.mattchowning.wodnotifier.Update;

import android.content.Context;

import com.mattchowning.wodnotifier.Database.MyContentProviderHelper;
import com.mattchowning.wodnotifier.UpdateScheduler;

public class UpdateFactory {

    public MyContentProviderHelper getMyContentProviderHelper(Context context) {
        return new MyContentProviderHelper(context);
    }

    public WodDownloader getWodDownloader() {
        return new WodDownloader();
    }

    public DatabaseUpdater getDatabaseUpdater() {
        return new DatabaseUpdater();
    }

    public UpdateScheduler getUpdateScheduler() {
        return new UpdateScheduler();
    }
}
