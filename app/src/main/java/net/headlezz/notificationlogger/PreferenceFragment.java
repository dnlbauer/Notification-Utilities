package net.headlezz.notificationlogger;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

public class PreferenceFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener {

    private PreferenceCallbacks mCallbacks;

    final String PREF_ABOUT = "pref_about";
    final String PREF_BLACKLIST = "pref_blacklist";


    interface PreferenceCallbacks {
        void showAboutScreen();
        void showBlacklistScreen();
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
        findPreference(PREF_ABOUT).setOnPreferenceClickListener(this);
        findPreference(PREF_BLACKLIST).setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if(key.equals(PREF_ABOUT)) {
            mCallbacks.showAboutScreen();
            return true;
        } else if(key.equals(PREF_BLACKLIST)) {
            mCallbacks.showBlacklistScreen();
            return true;
        }
        return false;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (PreferenceCallbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }
}
