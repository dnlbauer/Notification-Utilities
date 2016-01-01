package net.headlezz.notificationlogger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationAlarmReceiver extends BroadcastReceiver {

    public static String BROADCAST_INTENT_ACTION = "net.headlezz.notificationbroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        DispatchableNotification dn = DispatchableNotification.fromBundle(context, intent.getExtras());
        dn.dispatch();
    }

}
