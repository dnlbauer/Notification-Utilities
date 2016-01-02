package net.headlezz.notificationlogger.notificationlist;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.headlezz.notificationlogger.R;
import net.headlezz.notificationlogger.logger.LoggedNotification;
import net.headlezz.notificationlogger.logger.Logged_notificationTable;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

public class NotificationListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, NotificationListAdapter.NotificationClickListener {

    final int LOADER_ID = 124;

    @Bind(R.id.nList_emptyView)
    View mEmptyView;

    @Bind(R.id.nList_notificationList)
    RecyclerView mNotificationList;

    private NotificationListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notification_list_fragment, container, false);
        ButterKnife.bind(this, view);
        mNotificationList.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new NotificationListAdapter(getContext(), null);
        mAdapter.setNotificationClickListener(this);
        mNotificationList.setAdapter(mAdapter);
        getLoaderManager().initLoader(LOADER_ID, Bundle.EMPTY, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Timber.d("Loader created.");
        return new CursorLoader(
                getContext(),
                Logged_notificationTable.CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Timber.d("Loader finished. " + data.getCount() + " items.");
        mAdapter.changeCursor(data);
        checkIfEmpty();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Timber.d("Loader reset.");
        mAdapter.changeCursor(null);
        checkIfEmpty();
    }

    private void checkIfEmpty() {
        if(mAdapter.getItemCount() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
            mNotificationList.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            mNotificationList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNotificationClick(long id) {
        String qry = Logged_notificationTable.FIELD__ID + " = ? ";
        String[] args = { String.valueOf(id) };
        Cursor cursor = getContext().getContentResolver().query(Logged_notificationTable.CONTENT_URI, null, qry, args, null);
        LoggedNotification notification = Logged_notificationTable.getRow(cursor, true);
        Timber.e(notification.title);
    }
}
