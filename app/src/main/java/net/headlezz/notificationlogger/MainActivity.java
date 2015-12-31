package net.headlezz.notificationlogger;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.main_menu_fab_add_notification)
    FloatingActionButton mFAB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNotificationCreationScreen();
            }
        });

        if(getSupportFragmentManager().findFragmentById(R.id.main_menu_fragment_container) == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_menu_fragment_container, new NotificationListFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_settings) {
            Intent intent = new Intent(this, PreferenceActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("ConstantConditions")
    void openNotificationCreationScreen() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_menu_fragment_container, new NotificationCreationFragment())
                .addToBackStack(NotificationCreationFragment.class.getSimpleName())
                .commit();
        mFAB.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        // make fab visible if we come back to first fragment
        if(getSupportFragmentManager().getBackStackEntryCount() == 1)
            mFAB.setVisibility(View.VISIBLE);
        super.onBackPressed();
    }
}
