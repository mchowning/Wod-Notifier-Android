package com.mattchowning.wodnotifier.Update;

import android.content.Context;

import com.mattchowning.wodnotifier.Database.MyContentProviderHelper;

/**
 * Created by Matt on 2/23/14.
 */
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
}
