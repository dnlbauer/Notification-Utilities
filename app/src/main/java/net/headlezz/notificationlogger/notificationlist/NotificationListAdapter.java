package net.headlezz.notificationlogger.notificationlist;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.headlezz.notificationlogger.PackageUtils;
import net.headlezz.notificationlogger.R;
import net.headlezz.notificationlogger.logger.LoggedNotification;
import net.headlezz.notificationlogger.logger.Logged_notificationTable;

import butterknife.Bind;
import butterknife.ButterKnife;


public class NotificationListAdapter extends CursorRecyclerViewAdapter<NotificationListAdapter.NotificationViewHolder> implements View.OnClickListener, View.OnLongClickListener {

    class NotificationViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.notification_item_message)
        TextView tvMessage;

        @Bind(R.id.notification_item_title)
        TextView tvTitle;

        @Bind(R.id.notification_item_appName)
        TextView tvAppName;

        @Bind(R.id.notification_item_date)
        TextView tvDate;

        @Bind(R.id.notification_item_appIcon)
        ImageView ivAppIcon;

        @Bind(R.id.notification_item_smallIcon)
        ImageView ivSmallIcon;

        public NotificationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setNotification(LoggedNotification n) {
            Context context = itemView.getContext();

            tvMessage.setText(n.message);
            tvTitle.setText(n.title);
            tvAppName.setText(n.appName);
            CharSequence formattedDate = DateUtils.getRelativeTimeSpanString(n.date, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL);
            tvDate.setText(formattedDate);

            Drawable appIcon = PackageUtils.getApplicationLauncherIcon(context, n.packageName);
            ivAppIcon.setImageDrawable(appIcon);
            ivAppIcon.setContentDescription(String.format(context.getString(R.string.app_icon_cd), n.appName));

            Drawable smallIcon = DrawableCompat.wrap(PackageUtils.getApplicationDrawable(context, n.packageName, n.smallIconId));
            DrawableCompat.setTint(smallIcon, Color.BLACK);


            ivSmallIcon.setImageDrawable(smallIcon);

            itemView.setTag(n.id);
        }
    }

    interface NotificationClickListener {
        void onNotificationClick(long id);
        void onNotificationLongClick(long id);
    }

    private NotificationClickListener mNotificationClickListener;

    public NotificationListAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    public void setNotificationClickListener(NotificationClickListener listener) {
        mNotificationClickListener = listener;
    }

    @Override
    public void onBindViewHolder(NotificationViewHolder viewHolder, Cursor cursor) {
        LoggedNotification n = Logged_notificationTable.getRow(cursor, false);
        viewHolder.setNotification(n);
    }

    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_list_item, parent, false);
        v.setOnClickListener(this);
        v.setOnLongClickListener(this);
        return new NotificationViewHolder(v);
    }


    @Override
    public void onClick(View v) {
        if(mNotificationClickListener != null) {
            long id = (long) v.getTag();
            mNotificationClickListener.onNotificationClick(id);
        }
    }


    @Override
    public boolean onLongClick(View v) {
        if(mNotificationClickListener != null) {
            long id = (long) v.getTag();
            mNotificationClickListener.onNotificationLongClick(id);
        }
        return true;
    }

}
