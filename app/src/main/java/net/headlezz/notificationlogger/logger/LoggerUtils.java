package net.headlezz.notificationlogger.logger;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import net.headlezz.notificationlogger.R;

public class LoggerUtils {

    public static void openNotificationSystemSettings(Context context) {
        Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        context.startActivity(intent);
    }

    public static boolean isLoggerRunning() {
        return NotificationLoggerService.isRunning;
    }

    public static AlertDialog buildNotificationAccessDialog(final Context context) {
        return new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.main_dialog_enable_notification_access_title))
                .setMessage(context.getString(R.string.main_dialog_enable_notification_access_message))
                .setPositiveButton(R.string.main_dialog_enable_notification_access_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openNotificationSystemSettings(context);
                    }
                })
                .setNegativeButton(R.string.main_dialog_enable_notification_access_negative, null)
                .create();
    }

}
