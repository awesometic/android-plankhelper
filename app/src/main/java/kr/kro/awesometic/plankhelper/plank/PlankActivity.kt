package kr.kro.awesometic.plankhelper.plank

import android.annotation.TargetApi
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.Settings
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.Toast

import com.github.florent37.materialviewpager.MaterialViewPager
import com.github.florent37.materialviewpager.header.HeaderDesign

import butterknife.BindView
import butterknife.ButterKnife
import kr.kro.awesometic.plankhelper.R
import kr.kro.awesometic.plankhelper.settings.SettingsActivity
import kr.kro.awesometic.plankhelper.statistics.StatisticsActivity
import kr.kro.awesometic.plankhelper.util.LogManager
import kr.kro.awesometic.plankhelper.util.SharedPreferenceManager
import kr.kro.awesometic.plankhelper.util.Singleton

class PlankActivity : AppCompatActivity() {

    private val mSingleton = Singleton.instance

    @BindView(R.id.plank_materialViewPager)
    internal var mMaterialViewPager: MaterialViewPager? = null

    @BindView(R.id.drawer_layout)
    internal var mDrawerLayout: DrawerLayout? = null

    @BindView(R.id.nav_view)
    internal var mNavigationView: NavigationView? = null

    private var mDrawerToggle: ActionBarDrawerToggle? = null

    private var mViewPagerAdapter: ViewPagerAdapter? = null
    private var mPlankServiceManager: PlankServiceManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.plank_act)
        ButterKnife.bind(this)

        SharedPreferenceManager.init(applicationContext)

        // 싱글톤 설정
        // 임시로 일요일(Calendar.SUNDAY)
        mSingleton.startOfTheWeek = 1
        // 임시로 10초
        mSingleton.lineChartUnitOfAxisY = 10000

        val toolbar = mMaterialViewPager!!.toolbar
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        actionBar!!.setHomeAsUpIndicator(R.drawable.ic_menu_24dp)
        actionBar.setDisplayHomeAsUpEnabled(true)

        mViewPagerAdapter = ViewPagerAdapter(supportFragmentManager, this@PlankActivity)
        val pagerAdapter = mViewPagerAdapter
        val viewPager = mMaterialViewPager!!.viewPager
        viewPager.adapter = pagerAdapter
        viewPager.offscreenPageLimit = pagerAdapter!!.count

        mDrawerToggle = object : ActionBarDrawerToggle(
                this, /* host Activity */
                mDrawerLayout, /* DrawerLayout object */
                R.string.navigation_drawer_open, /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            override fun onDrawerClosed(view: View?) {
                super.onDrawerClosed(view)
                invalidateOptionsMenu() // creates call to onPrepareOptionsMenu()
            }

            override fun onDrawerOpened(drawerView: View?) {
                super.onDrawerOpened(drawerView)
                invalidateOptionsMenu() // creates call to onPrepareOptionsMenu()
            }
        }
        mDrawerLayout!!.addDrawerListener(mDrawerToggle!!)

        mMaterialViewPager!!.pagerTitleStrip.setViewPager(viewPager)
        mMaterialViewPager!!.setMaterialViewPagerListener(MaterialViewPager.Listener { page ->
            when (page) {
                0 -> return@Listener HeaderDesign.fromColorResAndDrawable(
                        R.color.plankHeaderDefault,
                        getDrawable(R.drawable.plank_stopwatch_header_image))
                1 -> return@Listener HeaderDesign.fromColorResAndDrawable(
                        R.color.plankHeaderDefault,
                        getDrawable(R.drawable.plank_timer_header_image))
            }

            //execute others actions if needed (ex : modify your header logo)

            null
        })

        mPlankServiceManager = mViewPagerAdapter!!.plankServiceManager

        if (mNavigationView != null) {
            setupDrawerContent(mNavigationView!!)
        }
    }

    override fun onStart() {
        super.onStart()

        mPlankServiceManager!!.bindService(this)
    }

    override fun onPostResume() {
        super.onPostResume()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            openOverlaySettings()
        }
    }

    override fun onDestroy() {
        mPlankServiceManager!!.plankActivityDestroyed()

        super.onDestroy()
    }

    override fun onBackPressed() {
        if (mDrawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout!!.closeDrawer(GravityCompat.START)
        } else {
            Toast.makeText(this, getString(R.string.back_button_notice), Toast.LENGTH_SHORT).show()
            moveTaskToBack(true)
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle!!.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mDrawerToggle!!.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                mDrawerLayout!!.openDrawer(GravityCompat.START)
                return true
            }
            else -> {
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            val intent: Intent

            when (menuItem.itemId) {
                R.id.plank_navigation_menu_item -> menuItem.isChecked = true
                R.id.statistics_navigation_menu_item -> {
                    intent = Intent(this@PlankActivity, StatisticsActivity::class.java)
                    startActivity(intent)
                }
                R.id.settings_navigation_menu_item -> {
                    intent = Intent(this@PlankActivity, SettingsActivity::class.java)
                    startActivity(intent)
                }

                else -> {
                }
            }

            mDrawerLayout!!.closeDrawers()
            true
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun openOverlaySettings() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName"))
        try {
            startActivityForResult(intent, 1207)
        } catch (e: ActivityNotFoundException) {
            LogManager.e(e.message)
        }

    }
}
