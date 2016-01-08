package net.headlezz.notificationlogger;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import net.headlezz.notificationlogger.logger.BlacklistItem;
import net.headlezz.notificationlogger.logger.BlacklistTable;
import net.headlezz.notificationlogger.logger.LoggedNotification;
import net.headlezz.notificationlogger.logger.Logged_notificationTable;

public class DatabaseUtils {

    public static void insertNotification(Context context, LoggedNotification ln) {
        context.getContentResolver().insert(
                Logged_notificationTable.CONTENT_URI,
                Logged_notificationTable.getContentValues(ln, false)
        );
    }

    public static LoggedNotification getNotificationById(Context context, long id) {
        String qry = Logged_notificationTable.FIELD__ID + " = ? ";
        String[] args = {String.valueOf(id)};
        Cursor cursor = context.getContentResolver().query(Logged_notificationTable.CONTENT_URI, null, qry, args, null);
        return Logged_notificationTable.getRow(cursor, true);
    }


    public static void removeAllNotifications(Context context) {
        ContentResolver resolver = context.getContentResolver();
        // there are no notifications with negative ids, so we use this to select all rows
        String whereClause = Logged_notificationTable.FIELD_NOTIFICATION_ID + " != -1";
        resolver.delete(Logged_notificationTable.CONTENT_URI, whereClause, new String[0]);
    }

    public static boolean isPackageBlacklisted(Context context, String packageName) {
        Cursor cursor = context.getContentResolver().query(
                BlacklistTable.CONTENT_URI,
                null,
                BlacklistTable.FIELD_PACKAGE_NAME + " = ?",
                new String[]{String.valueOf(packageName)},
                null,
                null
        );
        boolean isBlacklisted = cursor != null && cursor.getCount() > 0;
        if (cursor != null)
            cursor.close();
        return isBlacklisted;
    }

    public static int getBlacklistItemCount(Context context) {
        Cursor cursor = context.getContentResolver().query(
                BlacklistTable.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        cursor.moveToFirst();
        return cursor.getCount();
    }

    public static void insertBlacklistItem(Context context, BlacklistItem item) {
        context.getContentResolver().insert(
                BlacklistTable.CONTENT_URI,
                BlacklistTable.getContentValues(item, false)
        );
    }

    public static void deleteBlacklistItem(Context context, String packageName) {
        context.getContentResolver().delete(
                BlacklistTable.CONTENT_URI,
                BlacklistTable.FIELD_PACKAGE_NAME + " = ?",
                new String[] {packageName}
        );
    }

}
