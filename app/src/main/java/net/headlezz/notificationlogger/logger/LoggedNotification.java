package net.headlezz.notificationlogger.logger;

import java.util.Date;

import ckm.simple.sql_provider.annotation.SimpleSQLColumn;
import ckm.simple.sql_provider.annotation.SimpleSQLTable;

@SimpleSQLTable(table="logged_notification", provider="NotificationProvider")
public class LoggedNotification {

    @SimpleSQLColumn("_id")
    public long id;

    @SimpleSQLColumn("title")
    public String title;

    @SimpleSQLColumn("message")
    public String message;

    @SimpleSQLColumn("date")
    public Date date;

    @SimpleSQLColumn("app_name")
    public String appName;

    @SimpleSQLColumn("package_name")
    public String packageName;

    @SimpleSQLColumn("notification_id")
    public int notificationId;

    @SimpleSQLColumn("user_id")
    public int userId;

    @SimpleSQLColumn("small_icon_id")
    public int smallIconId;

}
