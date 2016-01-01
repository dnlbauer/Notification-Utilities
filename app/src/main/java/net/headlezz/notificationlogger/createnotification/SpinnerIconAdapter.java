package net.headlezz.notificationlogger.createnotification;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.headlezz.notificationlogger.R;

public class SpinnerIconAdapter extends ArrayAdapter<CharSequence> {

    private static final int DROPDOWN_RESOURCE = R.layout.simple_spinner_dropdown_item_with_icon;
    private static final int ITEM_RESOURCE = R.layout.simple_spinner_item_with_icon;

    private int[] itemIcons;
    private LayoutInflater mInflater;

    public SpinnerIconAdapter(Context context, CharSequence[] items, int[] itemIcons) {
        super(context, ITEM_RESOURCE, items);
        if(itemIcons.length != getCount())
            throw new IllegalArgumentException("Icon resource size doesnt match item count");
        this.itemIcons = itemIcons;
        mInflater = LayoutInflater.from(getContext());

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = mInflater.inflate(ITEM_RESOURCE, parent, false);
        }
        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
        textView.setText(getItem(position));
        ImageView iconView = (ImageView) convertView.findViewById(R.id.spinner_icon);
        Drawable icon = getContext().getResources().getDrawable(itemIcons[position]);
        iconView.setImageDrawable(icon);
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = mInflater.inflate(DROPDOWN_RESOURCE, parent, false);
        }
        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
        textView.setText(getItem(position));
        ImageView iconView = (ImageView) convertView.findViewById(R.id.spinner_icon);
        Drawable icon = getContext().getResources().getDrawable(itemIcons[position]);
        iconView.setImageDrawable(icon);
        return convertView;
    }

}
