package net.headlezz.notificationlogger.preferences;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.text.Html;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import net.headlezz.notificationlogger.Analytics;
import net.headlezz.notificationlogger.DatabaseUtils;
import net.headlezz.notificationlogger.R;
import net.headlezz.notificationlogger.logger.LoggedNotification;
import net.headlezz.notificationlogger.logger.Logged_notificationTable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import timber.log.Timber;

public class PreferenceFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener {

    private PreferenceCallbacks mCallbacks;

    public static final int PERMISSION_REQUEST_CODE = 182;

    final String PREF_ABOUT = "pref_about";
    final String PREF_BLACKLIST = "pref_blacklist";
    final String PREF_CLEAR_DATABASE = "pref_clear_database";
    final String PREF_EXPORT = "pref_export";

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
        findPreference(PREF_EXPORT).setOnPreferenceClickListener(this);
        setBlacklistItemCount();
    }

    @Override
    public void onResume() {
        super.onResume();
        Analytics.trackFragment(this);
    }

    private void setBlacklistItemCount() {
        new AsyncTask<Void, Void, Integer>() {


            @Override
            protected Integer doInBackground(Void... params) {
                return DatabaseUtils.getBlacklistItemCount(getContext());
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
        if (key.equals(PREF_ABOUT))
            mCallbacks.showAboutScreen();
        else if (key.equals(PREF_BLACKLIST))
            mCallbacks.showBlacklistScreen();
        else if (key.equals(PREF_CLEAR_DATABASE))
            askClearDatabase();
        else if (key.equals(PREF_EXPORT))
            tryExportDatabase();
        return true;
    }

    private void tryExportDatabase() {
        int requestPermission = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(requestPermission == PackageManager.PERMISSION_GRANTED)
            exportDatabase();
        else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PreferenceFragment.PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            exportDatabase();
        } else
            Toast.makeText(getContext(), getString(R.string.pref_error_no_export_permission), Toast.LENGTH_SHORT).show();

    }

    public void exportDatabase() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getContext().getString(R.string.pref_export_progress_message));
        progressDialog.show();

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                Cursor cursor = getContext().getContentResolver().query(
                        Logged_notificationTable.CONTENT_URI,
                        null, null, null, Logged_notificationTable.FIELD_DATE + " ASC"
                );

                JsonArray json = new JsonArray();
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        LoggedNotification notification = Logged_notificationTable.getRow(cursor, false);
                        json.add(gson.toJson(notification, LoggedNotification.class));
                    } while (cursor.moveToNext());
                }
                if (cursor != null)
                    cursor.close();

                File sdCard = Environment.getExternalStorageDirectory();
                File file = new File(sdCard, getContext().getString(R.string.notification_export_filename));
                FileOutputStream fos = null;
                try {
                    if (!file.exists())
                        file.createNewFile();
                    fos = new FileOutputStream(file, false);
                    fos.write(gson.toJson(json).getBytes());
                } catch (IOException e) {
                    Timber.e(e, "Failed to export data");
                    return "";
                } finally {
                    if (fos != null)
                        try {
                            fos.close();
                        } catch (IOException ignored) {
                        }
                }
                return file.getAbsolutePath();
            }

            @Override
            protected void onPostExecute(String path) {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                if (getActivity() != null) {

                    if (path.isEmpty()) {
                        String message = getContext().getString(R.string.pref_export_snack_failed);
                        Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT);
                        snack.show();
                    } else {
                        String message = String.format(getContext().getString(R.string.pref_export_snack_success), path);
                        Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT);
                        snack.setAction(R.string.pref_export_snack_action, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showFolder();
                            }
                        });
                        snack.show();
                    }
                }
                Analytics.trackEvent(Analytics.ACTION_EXPORTED);
            }
        }.execute();
    }

    private void showFolder() {
        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath());
            intent.setDataAndType(uri, "file/*");
            startActivity(Intent.createChooser(intent, getContext().getString(R.string.pref_export_snack_show_chooser_title)));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), R.string.pref_export_snack_show_activity_not_found, Toast.LENGTH_SHORT).show();
        }
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
                        Analytics.trackEvent(Analytics.ACTION_DATABASE_CLEARED);
                    }
                })
                .create().show();
    }

    private void clearDatabase() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                DatabaseUtils.removeAllNotifications(getContext());
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
