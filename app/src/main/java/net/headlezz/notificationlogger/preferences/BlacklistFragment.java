package net.headlezz.notificationlogger.preferences;

import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.headlezz.notificationlogger.Analytics;
import net.headlezz.notificationlogger.DatabaseUtils;
import net.headlezz.notificationlogger.PackageUtils;
import net.headlezz.notificationlogger.R;
import net.headlezz.notificationlogger.logger.BlacklistItem;
import net.headlezz.notificationlogger.logger.BlacklistTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

public class BlacklistFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener, BlacklistListAdapter.BlacklistCallbacks {

    final int LOADER_ID = 1752;

    @Bind(R.id.blacklistList_emptyView)
    View mEmptyView;

    @Bind(R.id.blacklistList_list)
    RecyclerView mBlacklistList;

    @Bind(R.id.blacklistList_fab)
    FloatingActionButton mFab;

    BlacklistListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.blacklist_frag, container, false);
        ButterKnife.bind(this, view);
        mBlacklistList.setLayoutManager(new LinearLayoutManager(getContext()));
        mFab.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new BlacklistListAdapter(getContext(), null, this);
        mBlacklistList.setAdapter(mAdapter);
        getLoaderManager().initLoader(LOADER_ID, Bundle.EMPTY, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Analytics.trackFragment(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Timber.d("blacklistloader created");
        return new CursorLoader(getContext(),
                BlacklistTable.CONTENT_URI,
                null,
                null,
                null,
                BlacklistTable.FIELD_PACKAGE_NAME + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Timber.d("blacklistloader finished: " + data.getCount() + " items.");
        mAdapter.changeCursor(data);
        checkEmpty();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Timber.d("blacklistloader reset");
        mAdapter.changeCursor(null);
        checkEmpty();
    }

    private void checkEmpty() {
        if(mAdapter.getItemCount() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
            mBlacklistList.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            mBlacklistList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        loadBlacklistPickerDialog();
    }

    private void loadBlacklistPickerDialog() {
        // disable fab until the list is loaded
        mFab.setEnabled(false);
        new AsyncTask<Void, Void, List<String>>() {

            @Override
            protected List<String> doInBackground(Void... params) {
                PackageManager pm = getContext().getPackageManager();
                List<ApplicationInfo> installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
                List<String> appPackages = new ArrayList<>();
                for(ApplicationInfo appInfo : installedApps) {
                    appPackages.add(appInfo.packageName);
                }
                Collections.sort(appPackages, new Comparator<String>() {
                    @Override
                    public int compare(String lhs, String rhs) {
                        String lhsName, rhsName;
                        try {
                            lhsName = PackageUtils.getAppName(getContext(), lhs);
                            rhsName = PackageUtils.getAppName(getContext(), rhs);
                        } catch (PackageManager.NameNotFoundException e) {
                            return 0;
                        }
                        return lhsName.compareTo(rhsName);
                    }
                });
                return appPackages;
            }

            @Override
            protected void onPostExecute(final List<String> packageNames) {
                mFab.setEnabled(true);
                new AlertDialog.Builder(getContext())
                        .setTitle(getContext().getString(R.string.blacklist_add_dialog_title))
                        .setAdapter(new AppsAdapter(packageNames), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String packageName = packageNames.get(which);
                                BlacklistItem item = new BlacklistItem();
                                item.packageName = packageName;
                                if(!DatabaseUtils.isPackageBlacklisted(getContext(), packageName)) {
                                    DatabaseUtils.insertBlacklistItem(getContext(), item);
                                }
                            }
                        })
                        .create().show();
            }
        }.execute();
    }

    @Override
    public void onBlacklistItemLongClicked(final BlacklistItem item) {
        String appName;
        try {
            appName = PackageUtils.getAppName(getContext(), item.packageName);
        } catch (PackageManager.NameNotFoundException e) {
            appName = item.packageName;
        }
        new AlertDialog.Builder(getContext())
                .setTitle(getContext().getString(R.string.blacklist_dialog_remove_title))
                .setMessage(getContext().getString(R.string.blacklist_fragment_remove_message, appName))
                .setPositiveButton(getContext().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseUtils.deleteBlacklistItem(getContext(), item.packageName);
                    }
                })
                .setNegativeButton(getContext().getString(R.string.cancel), null)
                .create().show();

    }
}
