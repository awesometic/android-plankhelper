package kr.kro.awesometic.plankhelper.statistics

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

import kr.kro.awesometic.plankhelper.R
import kr.kro.awesometic.plankhelper.data.source.PlankLogsRepository
import kr.kro.awesometic.plankhelper.data.source.local.PlankLogsLocalDataSource
import kr.kro.awesometic.plankhelper.statistics.calendar.CalendarFragment
import kr.kro.awesometic.plankhelper.statistics.calendar.CalendarPresenter
import kr.kro.awesometic.plankhelper.statistics.chart.ChartFragment
import kr.kro.awesometic.plankhelper.statistics.chart.ChartPresenter
import kr.kro.awesometic.plankhelper.statistics.log.LogFragment
import kr.kro.awesometic.plankhelper.statistics.log.LogPresenter

/**
 * Created by Awesometic on 2017-05-17.
 */

// TODO add filter to select date range to toolbar when users are going to chart fragment

class ViewPagerAdapter(fragmentManager: FragmentManager, private val mContext: Context) : FragmentPagerAdapter(fragmentManager) {

    private val mPageCount: Int
    private val mTabTitles: Array<String>

    private var mCalendarFragment: CalendarFragment? = null
    private var mChartFragment: ChartFragment? = null
    private var mLogFragment: LogFragment? = null

    private val mCalendarPresenter: CalendarPresenter
    private val mChartPresenter: ChartPresenter
    private val mLogPresenter: LogPresenter

    init {

        mPageCount = 3
        mTabTitles = arrayOf(mContext.resources.getString(R.string.statistics_calendar_title), mContext.resources.getString(R.string.statistics_chart_title), mContext.resources.getString(R.string.statistics_log_title))

        if (mCalendarFragment == null) {
            mCalendarFragment = CalendarFragment.newInstance()
        }

        if (mChartFragment == null) {
            mChartFragment = ChartFragment.newInstance()
        }

        if (mLogFragment == null) {
            mLogFragment = LogFragment.newInstance()
        }

        mCalendarPresenter = CalendarPresenter(
                PlankLogsRepository.getInstance(
                        PlankLogsLocalDataSource.getInstance(mContext)),
                mCalendarFragment!!)
        mChartPresenter = ChartPresenter(
                PlankLogsRepository.getInstance(
                        PlankLogsLocalDataSource.getInstance(mContext)),
                mChartFragment!!)
        mLogPresenter = LogPresenter(
                PlankLogsRepository.getInstance(
                        PlankLogsLocalDataSource.getInstance(mContext)),
                mLogFragment!!)
    }

    override fun getPageTitle(position: Int): CharSequence {
        return mTabTitles[position]
    }

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> return mCalendarFragment
            1 -> return mChartFragment
            2 -> return mLogFragment

            else -> return null
        }
    }

    override fun getCount(): Int {
        return mPageCount
    }
}
