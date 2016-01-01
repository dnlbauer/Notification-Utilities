package net.headlezz.notificationlogger.createnotification;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Helper class to create the date to schedule a notification
 */
public class NotificationScheduleHelper {

    private Context mContext;
    private DispatchableNotification mNotification;
    NotificationScheduleManagerCallback mCallback;

    interface NotificationScheduleManagerCallback {
        void onNotificationScheduleCreated(Date date, DispatchableNotification dn);
    }

    public NotificationScheduleHelper(Context context, DispatchableNotification dn, NotificationScheduleManagerCallback cb) {
        mContext = context;
        mNotification = dn;
        mCallback = cb;
    }

    public void shedule() {
        openSheduleDialogs();
    }

    private void openSheduleDialogs() {
        NotificationSheduleCreationListener mListener = new NotificationSheduleCreationListener();
        Calendar cal = new GregorianCalendar();
        DatePickerDialog dialog = new DatePickerDialog(mContext, mListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    public void onTimeSet(int year, int monthOfYear, int dayOfMonth, int hour, int minute) {
        mCallback.onNotificationScheduleCreated(new Date(year-1900, monthOfYear, dayOfMonth, hour, minute), mNotification);
    }


    class NotificationSheduleCreationListener implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

        private int year;
        private int monthOfYear;
        private int dayOfMonth;

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            this.year = year;
            this.monthOfYear = monthOfYear;
            this.dayOfMonth = dayOfMonth;
            Calendar cal = new GregorianCalendar();
            new TimePickerDialog(mContext, this, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)+1, true).show();
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            NotificationScheduleHelper.this.onTimeSet(year, monthOfYear, dayOfMonth, hourOfDay, minute);
        }
    }

}
