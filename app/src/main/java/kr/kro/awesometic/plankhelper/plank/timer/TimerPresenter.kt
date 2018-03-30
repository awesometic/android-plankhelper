package kr.kro.awesometic.plankhelper.plank.timer

import android.app.Activity
import android.content.Context
import android.widget.Toast

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

class TimerPresenter(plankLogsRepository: PlankLogsRepository,
                     TimerView: TimerContract.View) : TimerContract.Presenter {

    private val mPlankLogsRepository: PlankLogsRepository
    private val mTimerView: TimerContract.View
    private var mTimerPresenterCallback: TimerPresenter.ITimerPresenterCallback? = null
    private val mMethod = Constants.WORK_METHOD.TIMER

    private var mLapTimeListViewAdapter: LapTimeListViewAdapter? = null
    private var mRecyclerViewAdapter: RecyclerViewAdapter? = null
    private var mActivityContext: Context? = null

    override var timerStart: Boolean = false
        private set

    val lapCount: Int
        get() = mLapTimeListViewAdapter!!.count

    val lastLapMSec: Long
        get() = if (mLapTimeListViewAdapter!!.count > 0)
            TimeUtils.timeFormatToMSec(
                    mLapTimeListViewAdapter!!.getItem(mLapTimeListViewAdapter!!.count - 1).passedTime)
        else
            0

    val startTimeMSec: Long
        get() = TimeUtils.timeFormatToMSec(mTimerView.timeString)

    interface ITimerPresenterCallback {
        val timerStartMSec: Long
        fun timerCommandToService(method: Int, what: Int)
        fun updateWidgetsByCurrentState(method: Int)
    }

    init {
        mPlankLogsRepository = checkNotNull(plankLogsRepository, "plankLogsRepository cannot be null")
        mTimerView = checkNotNull(TimerView, "TimerView cannot be null")

        mTimerView.setPresenter(this)
    }

    private fun initTimerPresenter() {
        mActivityContext = mTimerView.activityContext as Context
        timerStart = false
    }

    private fun initTimerView() {
        mTimerView.showLoading()

        if (mLapTimeListViewAdapter == null && mRecyclerViewAdapter == null) {
            mLapTimeListViewAdapter = LapTimeListViewAdapter()
            mRecyclerViewAdapter = RecyclerViewAdapter()
        }

        mTimerView.setLapTimeAdapter(mLapTimeListViewAdapter)
        mTimerView.setRecyclerViewAdapter(mRecyclerViewAdapter)

        mRecyclerViewAdapter!!.setPlankLogs(null)

        mTimerView.showTimer()
    }

    override fun start() {
        initTimerPresenter()
        initTimerView()
    }

    override fun bindViewsFromViewHolderToFrag() {
        mTimerView.bindViewsFromViewHolder { mTimerPresenterCallback!!.updateWidgetsByCurrentState(mMethod) }
    }

    override fun controlFromFrag(what: Int) {
        mTimerPresenterCallback!!.timerCommandToService(mMethod, what)
    }

    fun controlFromService(what: Int) {
        (mActivityContext as Activity).runOnUiThread {
            when (what) {
                Constants.SERVICE_WHAT.TIMER_START -> {
                    mTimerView.setAllNumberPickersEnabled(false)
                    mTimerView.onOffButtonValue = mActivityContext!!.getString(R.string.plank_timer_pause)
                    mTimerView.resetLapButtonValue = mActivityContext!!.getString(R.string.plank_timer_lap)

                    timerStart = true
                }

                Constants.SERVICE_WHAT.TIMER_PAUSE -> {
                    mTimerView.onOffButtonValue = mActivityContext!!.getString(R.string.plank_timer_resume)
                    mTimerView.resetLapButtonValue = mActivityContext!!.getString(R.string.plank_timer_reset)
                }

                Constants.SERVICE_WHAT.TIMER_RESUME -> {
                    mTimerView.onOffButtonValue = mActivityContext!!.getString(R.string.plank_timer_pause)
                    mTimerView.resetLapButtonValue = mActivityContext!!.getString(R.string.plank_timer_lap)
                }

                Constants.SERVICE_WHAT.TIMER_RESET -> {
                    updateWidgetsOnFragment(0)
                    mTimerView.setAllNumberPickersEnabled(true)
                    mTimerView.onOffButtonValue = mActivityContext!!.getString(R.string.plank_timer_on)
                    mTimerView.resetLapButtonValue = mActivityContext!!.getString(R.string.plank_timer_reset)

                    timerStart = false
                }

                else -> {
                }
            }
        }
    }

    fun registerCallback(callback: ITimerPresenterCallback) {
        mTimerPresenterCallback = callback
    }

    fun updateWidgetsOnFragment(mSec: Long) {
        val resultTimeFormat = TimeUtils.mSecToTimeFormat(mSec)
        val resultTimeFormatSplit = resultTimeFormat.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        val resultHour = Integer.valueOf(resultTimeFormatSplit[0])
        val resultMin = Integer.valueOf(resultTimeFormatSplit[1])
        val resultSec = Integer.valueOf(resultTimeFormatSplit[2].split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])

        val currentTimeFormat = TimeUtils.mSecToTimeFormat(mSec + 1000)

        (mActivityContext as Activity).runOnUiThread {
            if (mTimerView.timeString == currentTimeFormat) {
                if (mTimerView.hour > resultHour) {
                    mTimerView.numberPickerChangeValueByOne(Constants.NUMBERPICKER_TYPE.HOUR, false)
                    mTimerView.numberPickerChangeValueByOne(Constants.NUMBERPICKER_TYPE.MIN, false)
                    mTimerView.numberPickerChangeValueByOne(Constants.NUMBERPICKER_TYPE.SEC, false)
                } else if (mTimerView.min > resultMin) {
                    mTimerView.numberPickerChangeValueByOne(Constants.NUMBERPICKER_TYPE.MIN, false)
                    mTimerView.numberPickerChangeValueByOne(Constants.NUMBERPICKER_TYPE.SEC, false)
                } else if (mTimerView.sec > resultSec && resultSec >= 0) {
                    mTimerView.numberPickerChangeValueByOne(Constants.NUMBERPICKER_TYPE.SEC, false)
                }
            } else {
                mTimerView.hour = resultHour
                mTimerView.min = resultMin
                mTimerView.sec = resultSec
            }
        }
    }

    fun addLapTimeItem(passedMSec: Long, intervalMSec: Long) {
        val order = mLapTimeListViewAdapter!!.count + 1
        val passedTime = TimeUtils.mSecToTimeFormat(passedMSec)
        val leftTime = TimeUtils.mSecToTimeFormat(mTimerPresenterCallback!!.timerStartMSec - passedMSec)
        val interval = TimeUtils.mSecToTimeFormat(intervalMSec)

        val lapTime = LapTime(
                Constants.DATABASE.EMPTY_PARENT_ID,
                order,
                passedTime,
                leftTime,
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
                mTimerPresenterCallback!!.timerStartMSec,
                Constants.DATABASE.METHOD_TIMER,
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
            mTimerView.setAllNumberPickersEnabled(isEnabled)
            mTimerView.setOnOffButtonEnabled(isEnabled)
            mTimerView.setResetLapButtonEnabled(isEnabled)
        }
    }
}
