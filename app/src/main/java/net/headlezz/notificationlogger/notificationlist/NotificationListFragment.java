package net.headlezz.notificationlogger.notificationlist;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import net.headlezz.notificationlogger.NotificationInfoDialog;
import net.headlezz.notificationlogger.R;
import net.headlezz.notificationlogger.logger.LoggedNotification;
import net.headlezz.notificationlogger.logger.Logged_notificationTable;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

public class NotificationListFragment extends Fragment implements NotificationListLoaderCallbacks.NotificationListView, NotificationListAdapter.NotificationClickListener, NotificationListFilterDialog.NotificationListFilterDialogCallbacks {

    @Bind(R.id.nList_emptyView)
    View mEmptyView;

    @Bind(R.id.nList_notificationList)
    RecyclerView mNotificationList;

    private NotificationListAdapter mAdapter;
    private boolean isFiltered = false;
    private NotificationListLoaderManager mLoaderManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        mLoaderManager = new NotificationListLoaderManager(
                getContext(),
                getLoaderManager(),
                this);
    }

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
        mLoaderManager.loadNotificationsUnfiltered();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.notification_list_frag, menu);
        if (isFiltered)
            menu.findItem(R.id.menu_filter).setVisible(false);
        else
            menu.findItem(R.id.menu_clear_filter).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                new NotificationListFilterDialog(getContext(), this).show();
                return true;
            case R.id.menu_clear_filter:
                mLoaderManager.loadNotificationsUnfiltered();
                setIsFiltered(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setIsFiltered(boolean filtered) {
        isFiltered = filtered;
        if (getActivity() != null)
            getActivity().supportInvalidateOptionsMenu();
    }

    @Override
    public void onChangeCursor(Cursor cursor) {
        mAdapter.changeCursor(cursor);
        if (mAdapter.getItemCount() == 0) {
            mNotificationList.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mNotificationList.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNotificationClick(long id) {
        String qry = Logged_notificationTable.FIELD__ID + " = ? ";
        String[] args = {String.valueOf(id)};
        Cursor cursor = getContext().getContentResolver().query(Logged_notificationTable.CONTENT_URI, null, qry, args, null);
        LoggedNotification notification = Logged_notificationTable.getRow(cursor, true);
        Timber.e(notification.title);

        new NotificationInfoDialog(getContext(), notification).show();
    }

    @Override
    public void onFilterNotificationList(String title, String message, String appName, String packageName) {
        Timber.d("Selected filter: " + title + "/" + message + "/" + appName + "/" + packageName);
        setIsFiltered(true);
        mLoaderManager.loadNotificationsFiltered(title, message, appName, packageName);
    }
}
