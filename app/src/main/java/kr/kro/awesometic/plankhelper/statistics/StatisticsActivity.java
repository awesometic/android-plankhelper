package kr.kro.awesometic.plankhelper.statistics;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import kr.kro.awesometic.plankhelper.R;
import kr.kro.awesometic.plankhelper.data.source.PlankLogsRepository;
import kr.kro.awesometic.plankhelper.data.source.local.PlankLogsLocalDataSource;
import kr.kro.awesometic.plankhelper.util.ActivityUtils;

/**
 * Created by Awesometic on 2017-04-17.
 */

public class StatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics_act);

        // toolbar 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.statistics_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // view pager 설정
        ViewPager viewPager = (ViewPager) findViewById(R.id.statistics_view_pager);
        PagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), StatisticsActivity.this);
        viewPager.setAdapter(pagerAdapter);

        // tab layout 설정
        TabLayout tabLayout = (TabLayout) findViewById(R.id.statistics_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(StatisticsActivity.this);
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
