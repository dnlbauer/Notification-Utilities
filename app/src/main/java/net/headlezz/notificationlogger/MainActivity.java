package net.headlezz.notificationlogger;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import net.headlezz.notificationlogger.createnotification.NotificationCreationFragment;
import net.headlezz.notificationlogger.logger.LoggerUtils;
import net.headlezz.notificationlogger.notificationlist.NotificationListFragment;
import net.headlezz.notificationlogger.preferences.PreferenceActivity;
import net.headlezz.notificationlogger.presenter.LoggerWarningPresenter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, LoggerWarningPresenter.LoggerWarningView {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.main_menu_fab_add_notification)
    FloatingActionButton mFAB;

    @Bind(R.id.adView)
    AdView mAdView;

    SharedPreferences mSharedPrefs;

    LoggerWarningPresenter mLoggerWarningPresenter;
    Snackbar mLoggerWarningView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mLoggerWarningPresenter = new LoggerWarningPresenter(this);

        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNotificationCreationScreen();
            }
        });

        if (getSupportFragmentManager().findFragmentById(R.id.main_menu_fragment_container) == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_menu_fragment_container, new NotificationListFragment())
                    .commit();
        }
        setupAdView();
    }

    private void setupAdView() {
        AdRequest req = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(req);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!LoggerUtils.isLoggerRunning())
            LoggerUtils.buildNotificationAccessDialog(this).show();
        if(getSupportFragmentManager().getBackStackEntryCount() > 0)
            mFAB.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSharedPrefs.registerOnSharedPreferenceChangeListener(this);
        mLoggerWarningPresenter.setCurrentLoggerPreference(mSharedPrefs.getBoolean("pref_logging_enabled", true));
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_settings) {
            Intent intent = new Intent(this, PreferenceActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("ConstantConditions")
    void openNotificationCreationScreen() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_menu_fragment_container, new NotificationCreationFragment())
                .addToBackStack(NotificationCreationFragment.class.getSimpleName())
                .commit();
        mFAB.setVisibility(View.GONE);
        getSupportActionBar().setTitle(getString(R.string.title_create_notification));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onBackPressed() {
        // make fab visible and change title if we come back to first fragment
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            mFAB.setVisibility(View.VISIBLE);
            getSupportActionBar().setTitle(getString(R.string.app_name));
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        super.onBackPressed();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("pref_logging_enabled"))
            mLoggerWarningPresenter.setCurrentLoggerPreference(sharedPreferences.getBoolean("pref_logging_enabled", true));
    }

    @Override
    public void showLoggerWarning() {
        if (mLoggerWarningView == null) {
            mLoggerWarningView = Snackbar.make(findViewById(R.id.viewholder), R.string.main_snack_logging_disabled_warning, Snackbar.LENGTH_INDEFINITE);
            mLoggerWarningView.setAction(R.string.main_snack_logging_disabled_warning_action, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSharedPrefs.edit().putBoolean("pref_logging_enabled", true).apply();
                }
            });
            // fix for FAB not moving down after dismiss
            mLoggerWarningView.getView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    mFAB.setTranslationY(0);
                }
            });
        }
        mLoggerWarningView.show();
    }

    @Override
    public void hideLoggerWarning() {
        if (mLoggerWarningView != null && mLoggerWarningView.isShownOrQueued())
            mLoggerWarningView.dismiss();
    }
}
