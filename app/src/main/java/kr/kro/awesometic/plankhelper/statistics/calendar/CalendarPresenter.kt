package kr.kro.awesometic.plankhelper.statistics.calendar

import android.content.Context

import kr.kro.awesometic.plankhelper.data.source.PlankLogsRepository

import com.google.common.base.Preconditions.checkNotNull

/**
 * Created by Awesometic on 2017-05-17.
 */

class CalendarPresenter(plankLogsRepository: PlankLogsRepository,
                        stopwatchView: CalendarContract.View) : CalendarContract.Presenter {

    private val mPlankLogsRepository: PlankLogsRepository
    private val mCalendarView: CalendarContract.View

    private var mRecyclerViewAdapter: RecyclerViewAdapter? = null
    private var mApplicationContext: Context? = null

    init {
        mPlankLogsRepository = checkNotNull(plankLogsRepository, "plankLogsRepository cannot be null")
        mCalendarView = checkNotNull(stopwatchView, "calendarView cannot be null")

        mCalendarView.setPresenter(this)
    }

    override fun start() {
        initPresenter()
        initView()
    }

    private fun initPresenter() {
        mApplicationContext = mCalendarView.applicationContext as Context
    }

    private fun initView() {
        mCalendarView.showLoading()

        mRecyclerViewAdapter = RecyclerViewAdapter()
        mCalendarView.setRecyclerViewAdapter(mRecyclerViewAdapter)

        mRecyclerViewAdapter!!.setPlankLogs(null)

        mCalendarView.showCalendar()
    }

    override fun bindViewsFromViewHolderToFrag() {
        mCalendarView.bindViewsFromViewHolder()
    }
}
