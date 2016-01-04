package net.headlezz.notificationlogger.notificationlist;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

public class NotificationListLoaderManager {

    final int LOADER_ID = 124;

    LoaderManager mLoaderManager;
    NotificationListLoaderCallbacks mLoaderCallbacks;

    public NotificationListLoaderManager(Context context, LoaderManager loaderManager, NotificationListLoaderCallbacks.NotificationListView notificationListView) {
        mLoaderManager = loaderManager;
        mLoaderCallbacks = new NotificationListLoaderCallbacks(context, notificationListView);
    }

    void loadNotificationsUnfiltered() {
        mLoaderManager.restartLoader(LOADER_ID, Bundle.EMPTY, mLoaderCallbacks);
    }

}
