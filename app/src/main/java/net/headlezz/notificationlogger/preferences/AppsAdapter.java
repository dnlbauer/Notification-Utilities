package net.headlezz.notificationlogger.preferences;

import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.headlezz.notificationlogger.PackageUtils;
import net.headlezz.notificationlogger.R;

import java.util.List;

import timber.log.Timber;

public class AppsAdapter extends BaseAdapter {

    private List<String> mPackageNames;

    class ViewHolder {
        TextView tvName;
        TextView tvPackageName;
        ImageView ivIcon;
    }

    public AppsAdapter(List<String> packageNames) {
        mPackageNames = packageNames;
    }

    @Override
    public int getCount() {
        return mPackageNames.size();
    }

    @Override
    public Object getItem(int position) {
        return mPackageNames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.apps_adapter_view, parent, false);
            viewHolder = new ViewHolder();
            convertView.setTag(viewHolder);
            viewHolder.ivIcon = (ImageView) convertView.findViewById(android.R.id.icon1);
            viewHolder.tvName = (TextView) convertView.findViewById(android.R.id.text1);
            viewHolder.tvPackageName = (TextView) convertView.findViewById(android.R.id.text2);
        } else
            viewHolder = (ViewHolder) convertView.getTag();

        String packageName = (String) getItem(position);
        viewHolder.tvPackageName.setText(packageName);
        try {
            viewHolder.tvName.setText(PackageUtils.getAppName(convertView.getContext(), packageName));
            viewHolder.ivIcon.setImageDrawable(PackageUtils.getApplicationLauncherIcon(convertView.getContext(), packageName));
        } catch (PackageManager.NameNotFoundException e) {
            Timber.d("app name not found", e);
            viewHolder.tvName.setText("");
            viewHolder.ivIcon.setImageDrawable(null);
        }

        return convertView;
    }
}
