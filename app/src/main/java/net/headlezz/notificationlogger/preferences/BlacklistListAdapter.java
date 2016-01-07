package net.headlezz.notificationlogger.preferences;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.headlezz.notificationlogger.PackageUtils;
import net.headlezz.notificationlogger.logger.BlacklistItem;
import net.headlezz.notificationlogger.logger.BlacklistTable;
import net.headlezz.notificationlogger.notificationlist.CursorRecyclerViewAdapter;

public class BlacklistListAdapter extends CursorRecyclerViewAdapter implements View.OnLongClickListener {

    interface BlacklistCallbacks {
        void onBlacklistItemLongClicked(BlacklistItem item);
    }

    class BlacklistViewHolder extends RecyclerView.ViewHolder {

        TextView mPackageName;
        TextView mAppName;

        public BlacklistViewHolder(View itemView) {
            super(itemView);
            mAppName = (TextView) itemView.findViewById(android.R.id.text1);
            mPackageName = (TextView) itemView.findViewById(android.R.id.text2);
        }

        public void setBlacklistItem(BlacklistItem item) {
            mPackageName.setText(item.packageName);
            String appName;
            try {
                appName = PackageUtils.getAppName(
                        itemView.getContext(),
                        item.packageName
                );
            } catch (PackageManager.NameNotFoundException e) {
                appName = "";
            }
            mAppName.setText(appName);
            itemView.setTag(item);
        }
    }

    BlacklistCallbacks mCallbacks;

    public BlacklistListAdapter(Context context, Cursor cursor, BlacklistCallbacks cb) {
        super(context, cursor);
        mCallbacks = cb;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        BlacklistItem item = BlacklistTable.getRow(cursor, false);
        ((BlacklistViewHolder) viewHolder).setBlacklistItem(item);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        view.setOnLongClickListener(this);
        return new BlacklistViewHolder(view);
    }

    @Override
    public boolean onLongClick(View v) {
        BlacklistItem item = (BlacklistItem) v.getTag();
        mCallbacks.onBlacklistItemLongClicked(item);
        return true;
    }
}
