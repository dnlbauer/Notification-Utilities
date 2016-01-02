package net.headlezz.notificationlogger.logger;

import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import net.headlezz.notificationlogger.PackageUtils;

import java.util.Date;

import timber.log.Timber;

public class NotificationLoggerService extends NotificationListenerService implements SharedPreferences.OnSharedPreferenceChangeListener {

    /**
     * true if logger is enabled in system settings
     */
    public static boolean isRunning = false;

    /**
     * true if logger is enabled in app settings
     */
    public static boolean isEnabled = true;

    SharedPreferences mSharedPrefs;

    @Override
    public IBinder onBind(Intent intent) {
        isRunning = true;
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        isEnabled = mSharedPrefs.getBoolean("pref_logging_enabled", true);
        mSharedPrefs.registerOnSharedPreferenceChangeListener(this);
        Timber.d(String.format("Logger started (running: %s, enabled: %s)", isRunning, isEnabled));
        return super.onBind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mSharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
        Timber.d("Logger stopped");
        return super.onUnbind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);

        if(!isEnabled) {
            Timber.d("Notification posted but logger is disabled");
            return;
        }


        LoggedNotification ln = buildLoggedNotification(sbn);
        getApplicationContext().getContentResolver().insert(
                Logged_notificationTable.CONTENT_URI,
                Logged_notificationTable.getContentValues(ln, false)
        );
        Timber.d(String.format("Notificaton logged (id: %d)", ln.notificationId));
    }

    private LoggedNotification buildLoggedNotification(StatusBarNotification sbn) {
        int notificationId = sbn.getId();

        int userId = sbn.getUserId();
        Date date = new Date(sbn.getPostTime());
        String packageName = sbn.getPackageName();
        int iconId = sbn.getNotification().icon;

        String appName;
        try {
            appName = PackageUtils.getAppName(getApplicationContext(), packageName);
        } catch (PackageManager.NameNotFoundException e) {
            Timber.w("App name not found for " + packageName);
            appName = "";
        }

        Notification notification = sbn.getNotification();
        Bundle extras = notification.extras;

        String title = extras.getString("android.title", "");
        String message = extras.getCharSequence("android.text", "").toString();

        if(message.isEmpty() && notification.tickerText != null)
            message = notification.tickerText.toString();
        if(message.isEmpty() && extras.containsKey("android.infoText"))
            message = extras.get("android.infoText").toString();

        LoggedNotification ln = new LoggedNotification();
        ln.title = title;
        ln.message = message;
        ln.date = date;
        ln.appName = appName;
        ln.packageName = packageName;
        ln.notificationId = notificationId;
        ln.userId = userId;
        ln.smallIconId = iconId;
        return ln;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("pref_logging_enabled")) {
            isEnabled = sharedPreferences.getBoolean("pref_logging_enabled", true);
            Timber.d("Logging is now " + isEnabled);
        }
    }
}
