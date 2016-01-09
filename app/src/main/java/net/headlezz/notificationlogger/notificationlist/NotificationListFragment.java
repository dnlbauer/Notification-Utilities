package net.headlezz.notificationlogger.notificationlist;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import net.headlezz.notificationlogger.Analytics;
import net.headlezz.notificationlogger.DatabaseUtils;
import net.headlezz.notificationlogger.NotificationInfoDialog;
import net.headlezz.notificationlogger.R;
import net.headlezz.notificationlogger.logger.BlacklistItem;
import net.headlezz.notificationlogger.logger.LoggedNotification;

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
    public void onResume() {
        super.onResume();
        Analytics.trackFragment(this);
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
        showNotificationInfo(id);
    }

    @Override
    public void onFilterNotificationList(String title, String message, String appName, String packageName) {
        Timber.d("Selected filter: " + title + "/" + message + "/" + appName + "/" + packageName);
        setIsFiltered(true);
        mLoaderManager.loadNotificationsFiltered(title, message, appName, packageName);
    }

    private void showNotificationInfo(long id) {
        LoggedNotification notification = DatabaseUtils.getNotificationById(getContext(), id);
        new NotificationInfoDialog(getContext(), notification).show();
    }

    @Override
    public void onNotificationLongClick(final long id) {
        String[] options = getResources().getStringArray(R.array.notification_list_options);
        new AlertDialog.Builder(getContext())
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                showNotificationInfo(id);
                                break;
                            case 1:
                                DatabaseUtils.deleteNotification(getContext(), id);
                                break;
                            case 2:
                                showPreselectedFilter(id);
                                break;
                            case 3:
                                blacklistNotificationPackage(id);
                                break;
                            case 4:
                                shareMessage(id);
                                break;
                        }
                    }
                })
                .create().show();
    }

    private void shareMessage(long id) {
        LoggedNotification notification = DatabaseUtils.getNotificationById(getContext(), id);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, notification.message);
        shareIntent.putExtra(Intent.EXTRA_TITLE, notification.title);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, notification.title);
        startActivity(Intent.createChooser(shareIntent, getContext().getString(R.string.share_title)));
    }

    private void showPreselectedFilter(long id) {
        LoggedNotification notification = DatabaseUtils.getNotificationById(getContext(), id);
        new NotificationListFilterDialog(
                getContext(),
                this,
                notification.title,
                notification.message,
                notification.appName,
                notification.packageName)
                .show();
    }

    private void blacklistNotificationPackage(long id) {
        final LoggedNotification ln = DatabaseUtils.getNotificationById(getContext(), id);
        if (!DatabaseUtils.isPackageBlacklisted(getContext(), ln.packageName)) {
            BlacklistItem item = new BlacklistItem();
            item.packageName = ln.packageName;
            DatabaseUtils.insertBlacklistItem(getContext(), item);
        }
        if (getActivity() != null) {
            Snackbar snackbar = Snackbar.make(
                    getActivity().findViewById(android.R.id.content),
                    String.format(getContext().getString(R.string.notification_list_frag_blacklist_snack), ln.packageName),
                    Snackbar.LENGTH_LONG
            );
            snackbar.setAction(R.string.notification_list_frag_blacklist_snack_action, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseUtils.deleteBlacklistItem(getContext(), ln.packageName);
                }
            });
            snackbar.show();
        }

    }

}
