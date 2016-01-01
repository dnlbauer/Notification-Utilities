package net.headlezz.notificationlogger;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NotificationCreationFragment extends Fragment {

    @Bind(R.id.create_etId)
    EditText mEtId;

    @Bind(R.id.create_etTitle)
    EditText mEtTitle;

    @Bind(R.id.create_etMessage)
    EditText mEtMessage;

    @Bind(R.id.create_cbAutoCancel)
    CheckBox mCbAutoCancel;

    @Bind(R.id.create_cbBlink)
    CheckBox mCbBlink;

    @Bind(R.id.create_cbSound)
    CheckBox mCbSound;

    @Bind(R.id.create_cbVibrate)
    CheckBox mCbVibrate;

    @Bind(R.id.create_spCategory)
    Spinner mSpCategory;

    @Bind(R.id.create_spIntent)
    Spinner mSpIntent;

    @Bind(R.id.create_spIcon)
    Spinner mSpIcon;

    final int[] mNotificationIcons = new int[]{
            R.drawable.ic_adb,
            R.drawable.ic_bluetooth_audio,
            R.drawable.ic_drive_eta,
            R.drawable.ic_event_note,
            R.drawable.ic_ondemand_video,
            R.drawable.ic_power,
            R.drawable.ic_sd_card,
            R.drawable.ic_sms
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notification_creation_frag, container, false);
        ButterKnife.bind(this, view);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(getContext(), R.array.create_categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpCategory.setAdapter(categoryAdapter);

        SpinnerIconAdapter iconAdapter = new SpinnerIconAdapter(getContext(), getResources().getStringArray(R.array.create_icons), mNotificationIcons);
        iconAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpIcon.setAdapter(iconAdapter);

        return view;
    }

}
