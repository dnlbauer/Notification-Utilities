package net.headlezz.notificationlogger.createnotification;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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

import net.headlezz.notificationlogger.Analytics;
import net.headlezz.notificationlogger.R;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NotificationCreationFragment extends Fragment implements View.OnClickListener, NotificationScheduleHelper.NotificationScheduleManagerCallback {

    // TODO intent

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
        btSchedule.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Analytics.trackFragment(this);
    }


    @Override
    public void onClick(View v) {
        boolean validationError = false;
        String message = etMessage.getText().toString();
        if(message.isEmpty()) {
            etMessage.setError("Message must not be empty.");
            validationError = true;
        }
        String title = etTitle.getText().toString();
        if(title.isEmpty()) {
            etTitle.setError("Title must not be empty.");
            validationError = true;
        }
        String idString = etId.getText().toString();
        int id = 1;
        try {
            id = Integer.parseInt(idString);
        } catch(NumberFormatException e) {
            etId.setError("Please enter a valid id");
            validationError = true;
        }

        if(validationError)
            return;

        DispatchableNotification dn = new DispatchableNotification.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setId(id)
                .setIcon(mNotificationIcons[spIcon.getSelectedItemPosition()])
                .setSound(cbSound.isChecked())
                .setVibrate(cbVibrate.isChecked())
                .setBlink(cbBlink.isChecked())
                .setAutoCancel(cbAutoCancel.isChecked())
                .setCategory(categories[spCategory.getSelectedItemPosition()])
                .build();

        if(v.getId() == R.id.create_btDispatch) {
            dn.dispatch();
            Analytics.trackEvent(Analytics.ACTION_CUSTOM_NOTIFICATION_DISPATCHED);
        } else
            sheduleNotification(dn);
    }

    private void sheduleNotification(DispatchableNotification dn) {
        new NotificationScheduleHelper(getContext(), dn, this).shedule();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onNotificationScheduleCreated(Date date, DispatchableNotification dn) {
        // delayn the shedule to see if user presses the undo button
        Snackbar snack = Snackbar.make(getView(), getString(R.string.create_shedule_snack, date.toString()), Snackbar.LENGTH_LONG);
        ScheduleSnackbarListener listener = new ScheduleSnackbarListener(dn, date);
        snack.setCallback(listener);
        snack.setAction(getString(R.string.create_shedule_snack_undo), listener);
        snack.show();
    }

    /**
     * Snackbar listener to track if the user undoes the schedule operation
     */
    class ScheduleSnackbarListener extends Snackbar.Callback implements View.OnClickListener {

        private DispatchableNotification mNotification;
        private Date mDispatchDate;
        private boolean dispatch = true;

        public ScheduleSnackbarListener(DispatchableNotification notification, Date dispatchDate) {
            mDispatchDate = dispatchDate;
            mNotification = notification;
        }

        @Override
        public void onClick(View v) {
            dispatch = false;
        }

        @Override
        public void onDismissed(Snackbar snackbar, int event) {
            super.onDismissed(snackbar, event);
            if(dispatch) {
                mNotification.shedule(mDispatchDate);
                Analytics.trackEvent(Analytics.ACTION_CUSTOM_NOTIFICATION_SCHEDULED);
            }
        }
    }
}
