package net.headlezz.notificationlogger;


import android.app.Notification;
import android.content.Context;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;

import java.util.Date;

public class DispatchableNotification {

    private Context mContext;
    private int mId;
    private Notification mNotification;


    private DispatchableNotification(Context mContext, String mMessage, String mTitle, int mId, boolean mSound, boolean mVibrate, boolean mBlink, boolean mAutoCancel, int mIcon, String mCategory) {
        this.mContext = mContext;
        this.mId = mId;

        int defaults;
        if(mSound && mVibrate && mBlink)
            defaults = Notification.DEFAULT_ALL;
        else {
            if(mSound) {
                if(mVibrate)
                    defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
                else if(mBlink)
                    defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS;
                else
                    defaults = Notification.DEFAULT_SOUND;
            } else if(mVibrate) {
                if(mBlink)
                    defaults = Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS;
                else
                    defaults = Notification.DEFAULT_VIBRATE;
            } else
                defaults = Notification.DEFAULT_LIGHTS;
        }

        mNotification = new NotificationCompat.Builder(mContext)
                .setContentText(mMessage)
                .setContentTitle(mTitle)
                .setSmallIcon(mIcon)
                .setCategory(mCategory)
                .setAutoCancel(mAutoCancel)
                .setDefaults(defaults)
                .build();
    }

    public void dispatch() {
        NotificationManagerCompat.from(mContext).notify(mId, mNotification);
    }

    public void shedule(Date date) {
        // TODO
    }

    public static class Builder {
        private Context mContext;

        private String mMessage;
        private String mTitle;
        private int mId;

        private boolean mSound;
        private boolean mVibrate;
        private boolean mBlink;
        private boolean mAutoCancel;

        private int mIcon;
        private String mCategory;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setMessage(String message) {
            this.mMessage = message;
            return this;
        }

        public Builder setTitle(String title) {
            this.mTitle = title;
            return this;
        }

        public Builder setId(int id) {
            this.mId = id;
            return this;
        }

        public Builder setSound(boolean sound) {
            this.mSound = sound;
            return this;
        }

        public Builder setVibrate(boolean vibrate) {
            this.mVibrate = vibrate;
            return this;
        }

        public Builder setBlink(boolean blink) {
            this.mBlink = blink;
            return this;
        }

        public Builder setAutoCancel(boolean autoCancel) {
            this.mAutoCancel = autoCancel;
            return this;
        }

        public Builder setIcon(int icon) {
            this.mIcon = icon;
            return this;
        }

        public Builder setCategory(String category) {
            this.mCategory = category;
            return this;
        }

        public DispatchableNotification build() {
            return new DispatchableNotification(
                    mContext,
                    mMessage,
                    mTitle,
                    mId,
                    mSound,
                    mVibrate,
                    mBlink,
                    mAutoCancel,
                    mIcon,
                    mCategory
            );
        }
    }

}
