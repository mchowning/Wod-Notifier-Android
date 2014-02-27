package com.mattchowning.wodnotifier.Update;

import android.content.Context;

import com.mattchowning.wodnotifier.Database.MyContentProviderHelper;
import com.mattchowning.wodnotifier.UpdateScheduler;

public class UpdateFactory {

    private final Context context;

    public UpdateFactory(Context context) {
        this.context = context;
    }

    private MyContentProviderHelper getMyContentProviderHelper() {
        return new MyContentProviderHelper(context);
    }

    public WodDownloader getWodDownloader() {
        return new WodDownloader();
    }

    public DatabaseUpdater getDatabaseUpdater() {
        MyContentProviderHelper mcph = new MyContentProviderHelper(context);
        return new DatabaseUpdater(mcph);
    }

    public UpdateScheduler getUpdateScheduler() {
        return new UpdateScheduler();
    }
}
