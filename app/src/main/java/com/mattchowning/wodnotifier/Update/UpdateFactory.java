package com.mattchowning.wodnotifier.Update;

import android.content.Context;

import com.mattchowning.wodnotifier.Database.MyContentProviderHelper;
import com.mattchowning.wodnotifier.UpdateScheduler;

public class UpdateFactory {

    private final Context context;

    public UpdateFactory(Context context) {
        this.context = context;
    }

    public Updater getUpdater() {
        DatabaseUpdater databaseUpdater = getDatabaseUpdater();
        UpdateScheduler updateScheduler = getUpdateScheduler();
        WodDownloader wodDownloader = new WodDownloader();
        return new Updater(context, databaseUpdater, updateScheduler, wodDownloader);
    }

    private DatabaseUpdater getDatabaseUpdater() {
        MyContentProviderHelper mcph = new MyContentProviderHelper(context);
        return new DatabaseUpdater(mcph);
    }

    private MyContentProviderHelper getMyContentProviderHelper() {
        return new MyContentProviderHelper(context);
    }

    private UpdateScheduler getUpdateScheduler() {
        return new UpdateScheduler(context);
    }
}
