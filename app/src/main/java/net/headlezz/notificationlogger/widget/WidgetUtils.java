package net.headlezz.notificationlogger.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;

import net.headlezz.notificationlogger.R;

public class WidgetUtils {

    public static void notifyWidgetsUpdate(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = manager.getAppWidgetIds(new ComponentName(context, ListWidgetProvider.class));
        manager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list_widget_listView);
    }

}
