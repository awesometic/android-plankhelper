package kr.kro.awesometic.plankhelper.plank;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import kr.kro.awesometic.plankhelper.R;
import kr.kro.awesometic.plankhelper.settings.SettingsActivity;
import kr.kro.awesometic.plankhelper.statistics.StatisticsActivity;

public class PlankActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plank_act);

        // toolbar 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.plank_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // view pager 설정
        ViewPager viewPager = (ViewPager) findViewById(R.id.plank_view_pager);
        PagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), PlankActivity.this);
        viewPager.setAdapter(pagerAdapter);

        // tab layout 설정
        TabLayout tabLayout = (TabLayout) findViewById(R.id.plank_tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        // navigation drawer 설정
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Toast.makeText(this, getString(R.string.back_button_notice), Toast.LENGTH_SHORT).show();
            moveTaskToBack(true);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        Intent intent;

                        switch (menuItem.getItemId()) {
                            case R.id.plank_navigation_menu_item:
                                break;
                            case R.id.statistics_navigation_menu_item:
                                intent = new Intent(PlankActivity.this, StatisticsActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.settings_navigation_menu_item:
                                intent = new Intent(PlankActivity.this, SettingsActivity.class);
                                startActivity(intent);
                                break;

                            default:
                                break;
                        }

                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                }
        );
    }
}
