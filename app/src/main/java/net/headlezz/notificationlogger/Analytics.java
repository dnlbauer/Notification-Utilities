package net.headlezz.notificationlogger;

import android.support.v4.app.Fragment;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class Analytics {

    public static final String ACTION_APP_INFO_OPENED = "App Info opened";
    public static final String ACTION_NOTIFICATION_DETAILS_SHOWN = "Notification details shown";
    public static final String ACTION_CUSTOM_NOTIFICATION_DISPATCHED = "Notification dispatched";
    public static final String ACTION_CUSTOM_NOTIFICATION_SCHEDULED= "Notification scheduled";
    public static final String ACTION_ABOUT= "About opened";
    public static final String ACTION_DATABASE_CLEARED= "database cleared";
    public static final String ACTION_EXPORTED = "Notifications exported";

    private static Tracker getTracker() {
        return Application.singleton.getDefaultTracker();
    }

    public static void trackFragment(Fragment frag) {
        Tracker tracker = getTracker();
        tracker.setScreenName(frag.getClass().getSimpleName());
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public static void trackEvent(String action) {
        Tracker tracker = getTracker();
        tracker.send(new HitBuilders.EventBuilder()
        .setCategory("Action")
        .setAction(action)
        .build());
    }

}
