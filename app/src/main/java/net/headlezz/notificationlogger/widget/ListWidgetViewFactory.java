package net.headlezz.notificationlogger.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.format.DateUtils;
import android.text.style.StyleSpan;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import net.headlezz.notificationlogger.R;
import net.headlezz.notificationlogger.logger.LoggedNotification;
import net.headlezz.notificationlogger.logger.Logged_notificationTable;

import timber.log.Timber;

public class ListWidgetViewFactory implements RemoteViewsService.RemoteViewsFactory {

    int mAppWidgetId;
    Context mContext;

    Cursor mCursor;

    public ListWidgetViewFactory(Context context, int appWidgetId) {
        mContext = context;
        this.mAppWidgetId = appWidgetId;
    }


    @Override
    public void onCreate() { /** ignored **/ }

    @Override
    public void onDataSetChanged() {
        Timber.d("ON DATA SET CHANGED");
        if(mCursor != null) {
            mCursor.close();
        }
        mCursor = mContext.getContentResolver().query(Logged_notificationTable.CONTENT_URI, null, null, null, Logged_notificationTable.FIELD_DATE + " DESC");
    }

    @Override
    public void onDestroy() {
        if(mCursor != null)
            mCursor.close();
    }

    @Override
    public int getCount() {
        if(mCursor == null) {
            Timber.d("cursor is null");
            return 0;
        }
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);

        mCursor.moveToPosition(position);
        LoggedNotification notification = Logged_notificationTable.getRow(mCursor, false);

        rv.setTextViewText(R.id.widget_list_item_title, notification.appName);

        String title = notification.title + ":";
        SpannableString titleSpan = new SpannableString(title + " " + notification.message);
        titleSpan.setSpan(new StyleSpan(Typeface.BOLD), 0, title.length(), 0);

        rv.setTextViewText(R.id.widget_list_item_notification, titleSpan);

        CharSequence formattedDate;
        if(DateUtils.isToday(notification.date))
            formattedDate = DateUtils.formatDateTime(mContext, notification.date, DateUtils.FORMAT_SHOW_TIME);
        else
            formattedDate = DateUtils.formatDateTime(mContext, notification.date, DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE);
        rv.setTextViewText(R.id.widget_list_item_date, formattedDate);

        Intent intent = new Intent();
        intent.putExtra(Logged_notificationTable.FIELD__ID, notification.id);
        rv.setOnClickFillInIntent(R.id.widget_list_item, intent);
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
