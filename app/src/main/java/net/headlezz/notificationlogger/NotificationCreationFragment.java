package net.headlezz.notificationlogger;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NotificationCreationFragment extends Fragment implements View.OnClickListener {

    // TODO intent
    // TODO check id size

    @Bind(R.id.create_etId)
    EditText etId;

    @Bind(R.id.create_etTitle)
    EditText etTitle;

    @Bind(R.id.create_etMessage)
    EditText etMessage;

    @Bind(R.id.create_cbAutoCancel)
    CheckBox cbAutoCancel;

    @Bind(R.id.create_cbBlink)
    CheckBox cbBlink;

    @Bind(R.id.create_cbSound)
    CheckBox cbSound;

    @Bind(R.id.create_cbVibrate)
    CheckBox cbVibrate;

    @Bind(R.id.create_spCategory)
    Spinner spCategory;

    @Bind(R.id.create_spIntent)
    Spinner spIntent;

    @Bind(R.id.create_spIcon)
    Spinner spIcon;

    @Bind(R.id.create_btDispatch)
    Button btDispatch;

    @Bind(R.id.create_btSchedule)
    Button btSchedule;

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

    final String[] categories = new String[] {
            NotificationCompat.CATEGORY_ALARM,
            NotificationCompat.CATEGORY_CALL,
            NotificationCompat.CATEGORY_EMAIL,
            NotificationCompat.CATEGORY_ERROR,
            NotificationCompat.CATEGORY_EVENT,
            NotificationCompat.CATEGORY_MESSAGE,
            NotificationCompat.CATEGORY_PROGRESS,
            NotificationCompat.CATEGORY_PROMO,
            NotificationCompat.CATEGORY_RECOMMENDATION,
            NotificationCompat.CATEGORY_SERVICE,
            NotificationCompat.CATEGORY_SOCIAL,
            NotificationCompat.CATEGORY_STATUS,
            NotificationCompat.CATEGORY_SYSTEM,
            NotificationCompat.CATEGORY_TRANSPORT
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notification_creation_frag, container, false);
        ButterKnife.bind(this, view);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(getContext(), R.array.create_categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(categoryAdapter);

        SpinnerIconAdapter iconAdapter = new SpinnerIconAdapter(getContext(), getResources().getStringArray(R.array.create_icons), mNotificationIcons);
        iconAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spIcon.setAdapter(iconAdapter);

        btDispatch.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {
        // TODO check input
        DispatchableNotification dn = new DispatchableNotification.Builder(getContext())
                .setTitle(etTitle.getText().toString())
                .setMessage(etMessage.getText().toString())
                .setIcon(Integer.valueOf(etId.getText().toString()))
                .setIcon(mNotificationIcons[spIcon.getSelectedItemPosition()])
                .setSound(cbSound.isChecked())
                .setVibrate(cbVibrate.isChecked())
                .setBlink(cbBlink.isChecked())
                .setAutoCancel(cbAutoCancel.isChecked())
                .setCategory(categories[spCategory.getSelectedItemPosition()])
                .build();

        if(v.getId() == R.id.create_btDispatch)
            dn.dispatch();
        else
            Toast.makeText(getContext(), "Not implemented", Toast.LENGTH_LONG)
    }
}
