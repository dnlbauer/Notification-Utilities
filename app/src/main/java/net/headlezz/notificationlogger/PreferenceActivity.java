package net.headlezz.notificationlogger;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.ui.LibsSupportFragment;

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
        getSupportActionBar().setTitle(R.string.title_settings);

        if(getSupportFragmentManager().findFragmentById(R.id.fragment_holder) == null) {
            PreferenceFragment pf = new PreferenceFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_holder, pf)
                    .commit();
        }
    }

    public void showAboutScreen() {
        // TODO add other libraries
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
}
