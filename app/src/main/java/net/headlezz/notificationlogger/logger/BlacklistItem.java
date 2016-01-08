package net.headlezz.notificationlogger.logger;


import ckm.simple.sql_provider.annotation.SimpleSQLColumn;
import ckm.simple.sql_provider.annotation.SimpleSQLTable;

@SimpleSQLTable(table="blacklist", provider="NotificationProvider")
public class BlacklistItem {

    @SimpleSQLColumn(value = "_id", primary = true)
    public long id;

    @SimpleSQLColumn("package_name")
    public String packageName;
}
