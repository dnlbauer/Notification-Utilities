package net.headlezz.notificationlogger;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import timber.log.Timber;

public class Application extends android.app.Application {

    public static Application singleton;

    private Tracker mTracker;

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        Timber.plant(new Timber.DebugTree());
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            if(BuildConfig.DEBUG)
                analytics.setDryRun(true);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }
}
