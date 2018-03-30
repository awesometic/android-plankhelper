package kr.kro.awesometic.plankhelper.plank.stopwatch

import android.app.Activity
import android.content.Context
import android.widget.Toast

import java.util.Locale

import kr.kro.awesometic.plankhelper.R
import kr.kro.awesometic.plankhelper.data.LapTime
import kr.kro.awesometic.plankhelper.data.PlankLog
import kr.kro.awesometic.plankhelper.data.source.PlankLogsDataSource
import kr.kro.awesometic.plankhelper.data.source.PlankLogsRepository
import kr.kro.awesometic.plankhelper.plank.LapTimeListViewAdapter
import kr.kro.awesometic.plankhelper.util.Constants
import kr.kro.awesometic.plankhelper.util.TimeUtils

import com.google.common.base.Preconditions.checkNotNull

/**
 * Created by Awesometic on 2017-04-19.
 */

class StopwatchPresenter(plankLogsRepository: PlankLogsRepository,
                         stopwatchView: StopwatchContract.View) : StopwatchContract.Presenter {

    private val mPlankLogsRepository: PlankLogsRepository
    private val mStopwatchView: StopwatchContract.View
    private var mStopwatchPresenterCallback: IStopwatchPresenterCallback? = null
    private val mMethod = Constants.WORK_METHOD.STOPWATCH

    private var mLapTimeListViewAdapter: LapTimeListViewAdapter? = null
    private var mRecyclerViewAdapter: RecyclerViewAdapter? = null
    private var mActivityContext: Context? = null

    override var stopwatchStart: Boolean = false
        private set

    val lapCount: Int
        get() = mLapTimeListViewAdapter!!.count

    val lastLapMSec: Long
        get() = if (mLapTimeListViewAdapter!!.count > 0)
            TimeUtils.timeFormatToMSec(
                    mLapTimeListViewAdapter!!.getItem(mLapTimeListViewAdapter!!.count - 1).passedTime)
        else
            0

    interface IStopwatchPresenterCallback {
        fun stopwatchCommandToService(method: Int, what: Int)
        fun updateWidgetsByCurrentState(method: Int)
    }

    init {
        mPlankLogsRepository = checkNotNull(plankLogsRepository, "plankLogsRepository cannot be null")
        mStopwatchView = checkNotNull(stopwatchView, "stopwatchView cannot be null")

        mStopwatchView.setPresenter(this)
    }

    private fun initStopwatchPresenter() {
        mActivityContext = mStopwatchView.activityContext as Context
        stopwatchStart = false
    }

    private fun initStopwatchView() {
        mStopwatchView.showLoading()

        if (mLapTimeListViewAdapter == null && mRecyclerViewAdapter == null) {
            mLapTimeListViewAdapter = LapTimeListViewAdapter()
            mRecyclerViewAdapter = RecyclerViewAdapter()
        }

        mStopwatchView.setLapTimeAdapter(mLapTimeListViewAdapter)
        mStopwatchView.setRecyclerViewAdapter(mRecyclerViewAdapter)

        mRecyclerViewAdapter!!.setPlankLogs(null)

        mStopwatchView.showStopwatch()
    }

    override fun start() {
        initStopwatchPresenter()
        initStopwatchView()
    }

    override fun bindViewsFromViewHolderToFrag() {
        mStopwatchView.bindViewsFromViewHolder { mStopwatchPresenterCallback!!.updateWidgetsByCurrentState(mMethod) }
    }

    override fun controlFromFrag(what: Int) {
        mStopwatchPresenterCallback!!.stopwatchCommandToService(mMethod, what)
    }

    fun controlFromService(what: Int) {
        (mActivityContext as Activity).runOnUiThread {
            when (what) {
                Constants.SERVICE_WHAT.STOPWATCH_START -> {
                    mStopwatchView.onOffButtonValue = mActivityContext!!.getString(R.string.plank_stopwatch_pause)
                    mStopwatchView.resetLapButtonValue = mActivityContext!!.getString(R.string.plank_stopwatch_lap)

                    stopwatchStart = true
                }

                Constants.SERVICE_WHAT.STOPWATCH_PAUSE -> {
                    mStopwatchView.onOffButtonValue = mActivityContext!!.getString(R.string.plank_stopwatch_resume)
                    mStopwatchView.resetLapButtonValue = mActivityContext!!.getString(R.string.plank_stopwatch_reset)
                }

                Constants.SERVICE_WHAT.STOPWATCH_RESUME -> {
                    mStopwatchView.onOffButtonValue = mActivityContext!!.getString(R.string.plank_stopwatch_pause)
                    mStopwatchView.resetLapButtonValue = mActivityContext!!.getString(R.string.plank_stopwatch_lap)
                }

                Constants.SERVICE_WHAT.STOPWATCH_RESET -> {
                    updateWidgetsOnFragment(0)
                    mStopwatchView.onOffButtonValue = mActivityContext!!.getString(R.string.plank_stopwatch_on)
                    mStopwatchView.resetLapButtonValue = mActivityContext!!.getString(R.string.plank_stopwatch_reset)

                    stopwatchStart = false
                }

                else -> {
                }
            }
        }
    }

    fun registerCallback(callback: IStopwatchPresenterCallback) {
        mStopwatchPresenterCallback = callback
    }

    fun updateWidgetsOnFragment(mSec: Long) {
        val timeFormat = TimeUtils.mSecToTimeFormat(mSec)
        val timeFormatSplit = timeFormat.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        val resultHour = Integer.valueOf(timeFormatSplit[0])
        val resultMin = Integer.valueOf(timeFormatSplit[1])
        val resultSec = Integer.valueOf(timeFormatSplit[2].split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])
        val resultMSec = Integer.valueOf(timeFormatSplit[2].split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1])

        (mActivityContext as Activity).runOnUiThread {
            mStopwatchView.setHour(String.format(Locale.getDefault(), "%02d", resultHour))
            mStopwatchView.setMin(String.format(Locale.getDefault(), "%02d", resultMin))
            mStopwatchView.setSec(String.format(Locale.getDefault(), "%02d", resultSec))
            mStopwatchView.setMSec(String.format(Locale.getDefault(), "%03d", resultMSec))
        }
    }

    fun addLapTimeItem(passedMSec: Long, intervalMSec: Long) {
        val order = mLapTimeListViewAdapter!!.count + 1
        val passedTime = TimeUtils.mSecToTimeFormat(passedMSec)
        val interval = TimeUtils.mSecToTimeFormat(intervalMSec)

        val lapTime = LapTime(
                Constants.DATABASE.EMPTY_PARENT_ID,
                order,
                passedTime,
                interval
        )

        (mActivityContext as Activity).runOnUiThread {
            mLapTimeListViewAdapter!!.addItem(lapTime)
            mLapTimeListViewAdapter!!.notifyDataSetChanged()
        }
    }

    fun clearLapTimeItem() {
        (mActivityContext as Activity).runOnUiThread {
            mLapTimeListViewAdapter!!.clear()
            mLapTimeListViewAdapter!!.notifyDataSetChanged()
        }
    }

    fun savePlankLogData() {
        val lapCount = mLapTimeListViewAdapter!!.count

        val plankLog = PlankLog(
                TimeUtils.currentDatetimeFormatted,
                TimeUtils.timeFormatToMSec(mStopwatchView.timeString),
                Constants.DATABASE.METHOD_STOPWATCH,
                lapCount,
                mLapTimeListViewAdapter!!.allItems
        )

        mPlankLogsRepository.savePlankLog(plankLog) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(
                        mActivityContext,
                        mActivityContext!!.getString(R.string.plank_toast_save_planklog_success),
                        Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                        mActivityContext,
                        mActivityContext!!.getString(R.string.plank_toast_save_planklog_fail),
                        Toast.LENGTH_SHORT
                ).show()
            }
        }

        if (lapCount > 0) {
            mPlankLogsRepository.saveLapTimes(plankLog.id, mLapTimeListViewAdapter!!.allItems)
        }
    }

    fun setWidgetsEnabled(isEnabled: Boolean) {
        (mActivityContext as Activity).runOnUiThread {
            mStopwatchView.setOnOffButtonEnabled(isEnabled)
            mStopwatchView.setResetLapButtonEnabled(isEnabled)
        }
    }
}
