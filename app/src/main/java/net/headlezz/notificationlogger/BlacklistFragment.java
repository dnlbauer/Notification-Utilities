package net.headlezz.notificationlogger;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

public class BlacklistFragment extends Fragment {

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((AppCompatActivity) activity).getSupportActionBar().setTitle("Blacklist");
    }
}
