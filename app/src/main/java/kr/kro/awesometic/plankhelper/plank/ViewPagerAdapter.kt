package kr.kro.awesometic.plankhelper.plank

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

import kr.kro.awesometic.plankhelper.R
import kr.kro.awesometic.plankhelper.data.source.PlankLogsRepository
import kr.kro.awesometic.plankhelper.data.source.local.PlankLogsLocalDataSource
import kr.kro.awesometic.plankhelper.plank.stopwatch.StopwatchFragment
import kr.kro.awesometic.plankhelper.plank.stopwatch.StopwatchPresenter
import kr.kro.awesometic.plankhelper.plank.timer.TimerFragment
import kr.kro.awesometic.plankhelper.plank.timer.TimerPresenter

/**
 * Created by Awesometic on 2017-04-19.
 */

class ViewPagerAdapter(fragmentManager: FragmentManager, private val mContext: Context) : FragmentPagerAdapter(fragmentManager) {

    private val mPageCount: Int
    private val mTabTitles: Array<String>

    private var mStopwatchFragment: StopwatchFragment? = null
    private var mTimerFragment: TimerFragment? = null

    val plankServiceManager: PlankServiceManager

    init {

        mPageCount = 2
        mTabTitles = arrayOf(mContext.resources.getString(R.string.plank_stopwatch_title), mContext.resources.getString(R.string.plank_timer_title))

        if (mStopwatchFragment == null) {
            mStopwatchFragment = StopwatchFragment.newInstance()
        }

        if (mTimerFragment == null) {
            mTimerFragment = TimerFragment.newInstance()
        }

        plankServiceManager = PlankServiceManager(
                StopwatchPresenter(
                        PlankLogsRepository.getInstance(
                                PlankLogsLocalDataSource.getInstance(mContext)),
                        mStopwatchFragment!!
                ),
                TimerPresenter(
                        PlankLogsRepository.getInstance(
                                PlankLogsLocalDataSource.getInstance(mContext)),
                        mTimerFragment!!
                ),
                mContext
        )
    }

    override fun getPageTitle(position: Int): CharSequence {
        return mTabTitles[position]
    }

    override fun getItem(position: Int): Fragment? {

        when (position) {
            0 -> return mStopwatchFragment
            1 -> return mTimerFragment

            else -> return null
        }
    }

    override fun getCount(): Int {
        return mPageCount
    }
}
