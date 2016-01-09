package net.headlezz.notificationlogger.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import net.headlezz.notificationlogger.MainActivity;
import net.headlezz.notificationlogger.R;

public class ListWidgetProvider extends AppWidgetProvider {

    public static final String SHOW_NOTIFICATION_ACTION = "net.headlezz.notificationlogger.listwidget.SHOW_NOTIFICATION_ACTION";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for(int widgetId : appWidgetIds) {
            RemoteViews rvs = updateWidgetListView(context, widgetId);
            appWidgetManager.updateAppWidget(appWidgetIds, rvs);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private RemoteViews updateWidgetListView(Context context, int appWidgetId) {
        RemoteViews rvs = new RemoteViews(context.getPackageName(), R.layout.widget_list);

        Intent serviceIntent = new Intent(context, ListWidgetService.class);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

        Intent showNotificationIntent = new Intent(context, ListWidgetProvider.class);
        showNotificationIntent.setAction(ListWidgetProvider.SHOW_NOTIFICATION_ACTION);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, showNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        rvs.setPendingIntentTemplate(R.id.list_widget_listView, pi);

        rvs.setRemoteAdapter(R.id.list_widget_listView, serviceIntent);
        rvs.setEmptyView(R.id.list_widget_listView, R.id.list_widget_empty_view);

        return rvs;
    }

    // receives the intent broadcast from list items
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SHOW_NOTIFICATION_ACTION)) {
            Intent mainActivityIntent = new Intent(context, MainActivity.class);
            mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainActivityIntent);
        }
        super.onReceive(context, intent);
    }
}
