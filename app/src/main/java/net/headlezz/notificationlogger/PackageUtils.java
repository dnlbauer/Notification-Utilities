package net.headlezz.notificationlogger;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import timber.log.Timber;

public class PackageUtils {

    public static String getAppName(Context context, String packageName) throws PackageManager.NameNotFoundException {
        PackageManager pm = context.getPackageManager();
        ApplicationInfo appInfo = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
        return (String) pm.getApplicationLabel(appInfo);
    }

    /**
     *
     * @param context
     * @param packageName
     * @return Drawable or Null
     */
    public static Drawable getApplicationLauncherIcon(Context context, String packageName) {
        try {
            return context.getPackageManager().getApplicationIcon(packageName);
        } catch(PackageManager.NameNotFoundException e) {
            Timber.w("package not found: " + packageName);
            return null;
        }
    }

    /**
     *
     * @param context
     * @param packageName
     * @param iconId
     * @return Drawable or Null
     */
    public static Drawable getApplicationDrawable(Context context, String packageName, int iconId) {
        try {
            Resources res = context.getPackageManager().getResourcesForApplication(packageName);
            return res.getDrawable(iconId);
        } catch (PackageManager.NameNotFoundException e) {
            Timber.w("package not found: " + packageName);
            return null;
        }

    }


}
