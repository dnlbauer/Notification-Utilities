package net.headlezz.notificationlogger.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import net.headlezz.notificationlogger.R;

public class ListWidgetProvider extends AppWidgetProvider {

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

        rvs.setRemoteAdapter(R.id.list_widget_listView, serviceIntent);
        rvs.setEmptyView(R.id.list_widget_listView, R.id.list_widget_empty_view);



        return rvs;
    }
}
