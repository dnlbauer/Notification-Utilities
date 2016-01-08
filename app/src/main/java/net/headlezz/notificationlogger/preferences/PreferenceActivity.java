package net.headlezz.notificationlogger.preferences;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.ui.LibsSupportFragment;

import net.headlezz.notificationlogger.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PreferenceActivity extends AppCompatActivity implements PreferenceFragment.PreferenceCallbacks {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preference_activity);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getSupportFragmentManager().findFragmentById(R.id.fragment_holder) == null) {
            showSettingsScreen();
        }
    }

    @Override
    public void showAboutScreen() {
        LibsSupportFragment aboutFrag = new LibsBuilder()
                .withAboutAppName(getString(R.string.app_name))
                .withAboutIconShown(true)
                .withAboutVersionShown(true)
                .withAboutVersionShownCode(true)
                .supportFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_holder, aboutFrag)
                .addToBackStack("about")
                .commit();
    }

    @Override
    public void showBlacklistScreen() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_holder, new BlacklistFragment())
                .addToBackStack("blacklist")
                .commit();
        getSupportActionBar().setTitle(getString(R.string.title_blacklist));
    }

    public void showSettingsScreen() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_holder, new PreferenceFragment())
                .commit();
        getSupportActionBar().setTitle(getString(R.string.title_settings));
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportActionBar().setTitle(getString(R.string.title_settings));
        super.onBackPressed();
    }

    // fix for callback on fragment not called
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.fragment_holder);
        if(frag != null)
            frag.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
