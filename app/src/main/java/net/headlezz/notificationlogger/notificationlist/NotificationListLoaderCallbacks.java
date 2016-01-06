package net.headlezz.notificationlogger.notificationlist;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import net.headlezz.notificationlogger.logger.Logged_notificationTable;

import java.util.ArrayList;
import java.util.List;

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
        // TODO apply filters
        Timber.d("Loader created.");
        String titleFilter = args.containsKey(Logged_notificationTable.FIELD_TITLE) ? args.getString(Logged_notificationTable.FIELD_TITLE) : "";
        String messageFilter = args.containsKey(Logged_notificationTable.FIELD_MESSAGE) ? args.getString(Logged_notificationTable.FIELD_MESSAGE) : "";
        String appNameFilter = args.containsKey(Logged_notificationTable.FIELD_APP_NAME) ? args.getString(Logged_notificationTable.FIELD_APP_NAME) : "";
        String packageFilter = args.containsKey(Logged_notificationTable.FIELD_PACKAGE_NAME) ? args.getString(Logged_notificationTable.FIELD_PACKAGE_NAME) : "";

        StringBuilder selectionClauseBuilder = new StringBuilder();
        List<String> selectionParams = new ArrayList<>();
        if(!titleFilter.isEmpty()) {
            selectionClauseBuilder.append(Logged_notificationTable.FIELD_TITLE + " like ?");
            selectionParams.add("%" + titleFilter + "%");
        }
        if(!messageFilter.isEmpty()) {
            if(selectionClauseBuilder.length() > 0)
                selectionClauseBuilder.append(" AND ");
            selectionClauseBuilder.append(Logged_notificationTable.FIELD_MESSAGE + " like ?");
            selectionParams.add("%" + messageFilter + "%");
        }
        if(!appNameFilter.isEmpty()) {
            if(selectionClauseBuilder.length() > 0)
                selectionClauseBuilder.append(" AND ");
            selectionClauseBuilder.append(Logged_notificationTable.FIELD_APP_NAME + " like ?");
            selectionParams.add("%" + appNameFilter + "%");
        }
        if(!packageFilter.isEmpty()) {
            if(selectionClauseBuilder.length() > 0)
                selectionClauseBuilder.append(" AND ");
            selectionClauseBuilder.append(Logged_notificationTable.FIELD_PACKAGE_NAME + " like ?");
            selectionParams.add("%" + packageFilter + "%");
        }

        return new CursorLoader(
                mContext,
                Logged_notificationTable.CONTENT_URI,
                null,
                selectionClauseBuilder.toString(),
                selectionParams.toArray(new String[selectionParams.size()]),
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
