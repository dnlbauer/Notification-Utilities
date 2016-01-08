package net.headlezz.notificationlogger;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.widget.Toast;

import net.headlezz.notificationlogger.logger.LoggedNotification;

public class NotificationInfoDialog extends AlertDialog implements DialogInterface.OnClickListener {

    LoggedNotification mNotification;
    
    public NotificationInfoDialog(Context context, LoggedNotification notification) {
        super(context);
        mNotification = notification;
        
        setTitle(notification.appName);
        setIcon(PackageUtils.getApplicationLauncherIcon(context, notification.packageName));
        setMessage(getDialogContent());
        setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.main_notification_details_button_ok), this);
        setButton(AlertDialog.BUTTON_NEUTRAL, context.getString(R.string.main_notification_details_button_app_info), this);
    }
    
    private Spanned getDialogContent() {
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append(getContext().getString(R.string.main_notification_details_package, mNotification.packageName));
        contentBuilder.append("<br />");
        contentBuilder.append(getContext().getString(R.string.main_notification_details_date,
                DateUtils.formatDateTime(getContext(), mNotification.date, DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE)));
        contentBuilder.append("<br />");
        contentBuilder.append(getContext().getString(R.string.main_notification_details_userid, mNotification.userId));
        contentBuilder.append("<br />");
        contentBuilder.append("<br />");
        contentBuilder.append(getContext().getString(R.string.main_notification_details_title, mNotification.title));
        contentBuilder.append("<br />");
        contentBuilder.append(getContext().getString(R.string.main_notification_details_message, mNotification.message));
        return Html.fromHtml(contentBuilder.toString());
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(which == AlertDialog.BUTTON_NEUTRAL) {
            if(!PackageUtils.showAppInfoActivity(getContext(), mNotification.packageName))
                Toast.makeText(getContext(), R.string.notification_info_dialog_app_info_error, Toast.LENGTH_LONG).show();
        }
    }
}
