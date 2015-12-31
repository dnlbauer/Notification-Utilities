package net.headlezz.notificationlogger;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NotificationCreationFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TextView textView = new TextView(getContext());
        textView.setGravity(Gravity.CENTER);
        textView.setText(getClass().getSimpleName());
        return textView;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((AppCompatActivity) activity).getSupportActionBar().setTitle(getString(R.string.title_create_notification));
    }
}
