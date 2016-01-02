package net.headlezz.notificationlogger.logger;

import android.app.Notification;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import net.headlezz.notificationlogger.PackageUtils;

import java.util.Date;

public class NotificationLoggerService extends NotificationListenerService {

    public String TAG = NotificationLoggerService.class.getSimpleName();

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        Log.d(TAG, "Notificaton logged (id: " + sbn.getId() + ")");

        LoggedNotification ln = buildLoggedNotification(sbn);
        getApplicationContext().getContentResolver().insert(
                Logged_notificationTable.CONTENT_URI,
                Logged_notificationTable.getContentValues(ln, false)
        );
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
            Log.w(TAG, "App name not found for " + packageName);
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

}
