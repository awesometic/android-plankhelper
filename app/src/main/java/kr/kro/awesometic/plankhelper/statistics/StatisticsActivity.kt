package kr.kro.awesometic.plankhelper.statistics

import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem

import com.github.florent37.materialviewpager.MaterialViewPager
import com.github.florent37.materialviewpager.header.HeaderDesign

import butterknife.BindView
import butterknife.ButterKnife
import kr.kro.awesometic.plankhelper.R

/**
 * Created by Awesometic on 2017-04-17.
 */

class StatisticsActivity : AppCompatActivity() {

    @BindView(R.id.statistics_materialViewPager)
    internal var mMaterialViewPager: MaterialViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.statistics_act)
        ButterKnife.bind(this)

        val toolbar = mMaterialViewPager!!.toolbar
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        actionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp)
        actionBar.setDisplayHomeAsUpEnabled(true)

        val pagerAdapter = ViewPagerAdapter(supportFragmentManager, this@StatisticsActivity)
        val viewPager = mMaterialViewPager!!.viewPager
        viewPager.adapter = pagerAdapter
        viewPager.offscreenPageLimit = pagerAdapter.count

        mMaterialViewPager!!.pagerTitleStrip.setViewPager(viewPager)
        mMaterialViewPager!!.setMaterialViewPagerListener(MaterialViewPager.Listener { page ->
            when (page) {
                0 -> return@Listener HeaderDesign.fromColorResAndDrawable(
                        R.color.plankHeaderDefault,
                        getDrawable(R.drawable.plank_stopwatch_header_image))
                1 -> return@Listener HeaderDesign.fromColorResAndDrawable(
                        R.color.plankHeaderDefault,
                        getDrawable(R.drawable.plank_timer_header_image))
                2 -> return@Listener HeaderDesign.fromColorResAndDrawable(
                        R.color.plankHeaderDefault,
                        getDrawable(R.drawable.plank_stopwatch_header_image))
            }

            //execute others actions if needed (ex : modify your header logo)

            null
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> {
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
