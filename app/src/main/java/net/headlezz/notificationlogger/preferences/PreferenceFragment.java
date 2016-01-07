package net.headlezz.notificationlogger.preferences;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.text.Html;

import net.headlezz.notificationlogger.R;
import net.headlezz.notificationlogger.logger.BlacklistTable;
import net.headlezz.notificationlogger.logger.Logged_notificationTable;

public class PreferenceFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener {

    private PreferenceCallbacks mCallbacks;

    final String PREF_ABOUT = "pref_about";
    final String PREF_BLACKLIST = "pref_blacklist";
    final String PREF_CLEAR_DATABASE = "pref_clear_database";


    interface PreferenceCallbacks {
        void showAboutScreen();

        void showBlacklistScreen();
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
        findPreference(PREF_ABOUT).setOnPreferenceClickListener(this);
        findPreference(PREF_BLACKLIST).setOnPreferenceClickListener(this);
        findPreference(PREF_CLEAR_DATABASE).setOnPreferenceClickListener(this);
        setBlacklistItemCount();
    }

    private void setBlacklistItemCount() {
        new AsyncTask<Void, Void, Integer>() {


            @Override
            protected Integer doInBackground(Void... params) {
                Cursor cursor = getContext().getContentResolver().query(
                        BlacklistTable.CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                );
                cursor.moveToFirst();
                return cursor.getCount();
            }

            @Override
            protected void onPostExecute(Integer blacklistItemCount) {
                Preference blacklistPref = findPreference(PREF_BLACKLIST);
                if (blacklistItemCount > 0)
                    blacklistPref.setSummary(Html.fromHtml(getString(R.string.pref_blacklist_summary_filled, blacklistItemCount)));
                else
                    blacklistPref.setSummary(getString(R.string.pref_blacklist_summary_empty));
            }
        }.execute();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (key.equals(PREF_ABOUT)) {
            mCallbacks.showAboutScreen();
            return true;
        } else if (key.equals(PREF_BLACKLIST)) {
            mCallbacks.showBlacklistScreen();
            return true;
        } else if (key.equals(PREF_CLEAR_DATABASE))
            askClearDatabase();
        return false;
    }

    private void askClearDatabase() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.pref_clear_database_dialog_title)
                .setMessage(R.string.pref_clear_database_dialog_message)
                .setNegativeButton(R.string.pref_clear_database_dialog_negative, null)
                .setPositiveButton(R.string.pref_clear_database_dialog_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearDatabase();
                    }
                })
                .create().show();
    }

    private void clearDatabase() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                ContentResolver resolver = getContext().getContentResolver();
                // there are no notifications with negative ids, so we use this to select all rows
                String whereClause = Logged_notificationTable.FIELD_NOTIFICATION_ID + " != -1";
                resolver.delete(Logged_notificationTable.CONTENT_URI, whereClause, new String[0]);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (getActivity() != null)
                    Snackbar.make(getActivity().findViewById(android.R.id.content), R.string.pref_clear_database_snackbar_cleared, Snackbar.LENGTH_SHORT).show();
            }
        }.execute();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (PreferenceCallbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }
}
