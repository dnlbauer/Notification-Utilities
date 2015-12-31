package net.headlezz.notificationlogger;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

public class PreferenceFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener {

    private PreferenceCallbacks mCallbacks;

    final String PREF_ABOUT = "pref_about";



    interface PreferenceCallbacks {
        void showAboutScreen();
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
        findPreference(PREF_ABOUT).setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if(preference.getKey().equals(PREF_ABOUT)) {
            mCallbacks.showAboutScreen();
            return true;
        }
        return false;
    }

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
