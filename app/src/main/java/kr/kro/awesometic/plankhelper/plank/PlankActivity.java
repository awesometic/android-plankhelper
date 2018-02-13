package kr.kro.awesometic.plankhelper.plank;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.kro.awesometic.plankhelper.R;
import kr.kro.awesometic.plankhelper.settings.SettingsActivity;
import kr.kro.awesometic.plankhelper.statistics.StatisticsActivity;
import kr.kro.awesometic.plankhelper.util.LogManager;
import kr.kro.awesometic.plankhelper.util.SharedPreferenceManager;
import kr.kro.awesometic.plankhelper.util.Singleton;

public class PlankActivity extends AppCompatActivity {

    private Singleton mSingleton = Singleton.getInstance();

    @BindView(R.id.plank_materialViewPager)
    MaterialViewPager mMaterialViewPager;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.nav_view)
    NavigationView mNavigationView;

    private ActionBarDrawerToggle mDrawerToggle;

    private ViewPagerAdapter mViewPagerAdapter;
    private PlankServiceManager mPlankServiceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plank_act);
        ButterKnife.bind(this);

        SharedPreferenceManager.init(getApplicationContext());

        // 싱글톤 설정
        // 임시로 일요일(Calendar.SUNDAY)
        mSingleton.setStartOfTheWeek(1);
        // 임시로 10초
        mSingleton.setLineChartUnitOfAxisY(10000);

        Toolbar toolbar = mMaterialViewPager.getToolbar();
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), PlankActivity.this);
        PagerAdapter pagerAdapter = mViewPagerAdapter;
        ViewPager viewPager = mMaterialViewPager.getViewPager();
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(pagerAdapter.getCount());

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        mMaterialViewPager.getPagerTitleStrip().setViewPager(viewPager);
        mMaterialViewPager.setMaterialViewPagerListener(new MaterialViewPager.Listener() {
            @Override
            public HeaderDesign getHeaderDesign(int page) {
                switch (page) {
                    case 0:
                        return HeaderDesign.fromColorResAndDrawable(
                                R.color.plankHeaderDefault,
                                getDrawable(R.drawable.plank_stopwatch_header_image));
                    case 1:
                        return HeaderDesign.fromColorResAndDrawable(
                                R.color.plankHeaderDefault,
                                getDrawable(R.drawable.plank_timer_header_image));
                }

                //execute others actions if needed (ex : modify your header logo)

                return null;
            }
        });

        mPlankServiceManager = mViewPagerAdapter.getPlankServiceManager();

        if (mNavigationView != null) {
            setupDrawerContent(mNavigationView);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mPlankServiceManager.bindService(this);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            openOverlaySettings();
        }
    }

    @Override
    protected void onDestroy() {
        mPlankServiceManager.plankActivityDestroyed();

        super.onDestroy();
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
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
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
                                menuItem.setChecked(true);
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

                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                }
        );
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void openOverlaySettings() {
        final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        try {
            startActivityForResult(intent, 1207);
        } catch (ActivityNotFoundException e) {
            LogManager.e(e.getMessage());
        }
    }
}
