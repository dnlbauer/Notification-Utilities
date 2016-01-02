package net.headlezz.notificationlogger.presenter;

public class LoggerWarningPresenter {

    public interface LoggerWarningView {
        void showLoggerWarning();
        void hideLoggerWarning();
    }

    LoggerWarningView mView;

    public LoggerWarningPresenter(LoggerWarningView lwv) {
        mView = lwv;
    }

    public void setCurrentLoggerPreference(boolean loggingEnabled) {
        if(loggingEnabled)
            mView.hideLoggerWarning();
        else
            mView.showLoggerWarning();
    }

}
