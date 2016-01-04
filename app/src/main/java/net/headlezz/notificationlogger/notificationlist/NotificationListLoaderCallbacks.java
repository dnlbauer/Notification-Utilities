package net.headlezz.notificationlogger.notificationlist;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import net.headlezz.notificationlogger.logger.Logged_notificationTable;

import timber.log.Timber;

public class NotificationListLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

    public interface NotificationListView {
        void onChangeCursor(Cursor cursor);
    }

    private Context mContext;
    private NotificationListView mView;

    public NotificationListLoaderCallbacks(Context context, NotificationListView view) {
        mContext = context;
        mView = view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Timber.d("Loader created.");
        return new CursorLoader(
                mContext,
                Logged_notificationTable.CONTENT_URI,
                null,
                null,
                null,
                Logged_notificationTable.FIELD_DATE + " DESC"
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Timber.d("Loader finished. " + data.getCount() + " items.");
        mView.onChangeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Timber.d("Loader reset.");
        mView.onChangeCursor(null);
    }

}
