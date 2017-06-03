package kr.kro.awesometic.plankhelper.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import kr.kro.awesometic.plankhelper.R;
import kr.kro.awesometic.plankhelper.data.source.PlankLogsRepository;
import kr.kro.awesometic.plankhelper.data.source.local.PlankLogsLocalDataSource;
import kr.kro.awesometic.plankhelper.util.ActivityUtils;

/**
 * Created by Awesometic on 2017-04-19.
 */

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_act);

        // toolbar 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // fragment 설정
        SettingsFragment settingsFragment = (SettingsFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (settingsFragment == null) {
            settingsFragment = SettingsFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), settingsFragment, R.id.contentFrame);
        }

        // presenter 설정
        new SettingsPresenter(
                PlankLogsRepository.getInstance(
                        PlankLogsLocalDataSource.getInstance(getApplicationContext())),
                settingsFragment);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
