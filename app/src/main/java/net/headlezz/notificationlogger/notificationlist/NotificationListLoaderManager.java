package net.headlezz.notificationlogger.notificationlist;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;

import net.headlezz.notificationlogger.logger.Logged_notificationTable;

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

    void loadNotificationsFiltered(@Nullable String titleFilter, @Nullable String messageFilter, @Nullable String appNameFilter, @Nullable String packageFilter) {
        Bundle bundle = new Bundle();

        if(titleFilter != null && !titleFilter.isEmpty())
            bundle.putString(Logged_notificationTable.FIELD_TITLE, titleFilter);

        if(messageFilter != null && !messageFilter .isEmpty())
            bundle.putString(Logged_notificationTable.FIELD_MESSAGE, messageFilter);


        if(appNameFilter != null && !appNameFilter.isEmpty())
            bundle.putString(Logged_notificationTable.FIELD_APP_NAME, appNameFilter);


        if(packageFilter != null && !packageFilter.isEmpty())
            bundle.putString(Logged_notificationTable.FIELD_PACKAGE_NAME, packageFilter);

        mLoaderManager.restartLoader(LOADER_ID, bundle, mLoaderCallbacks);
    }

}
