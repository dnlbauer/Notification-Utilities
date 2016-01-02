package net.headlezz.notificationlogger.logger;

import ckm.simple.sql_provider.UpgradeScript;
import ckm.simple.sql_provider.annotation.ProviderConfig;
import ckm.simple.sql_provider.annotation.SimpleSQLConfig;

@SimpleSQLConfig(
        name="NotificationProvider",
        authority = "net.headlezz.notificationlogger.logger.authority",
        database = "notifications.db",
        version = 1
)
public class NotificationProviderConfig implements ProviderConfig {
    @Override
    public UpgradeScript[] getUpdateScripts() {
        return new UpgradeScript[0];
    }
}
