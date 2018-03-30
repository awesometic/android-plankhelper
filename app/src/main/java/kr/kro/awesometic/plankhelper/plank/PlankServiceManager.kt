package kr.kro.awesometic.plankhelper.plank

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.support.v4.app.ActivityCompat

import kr.kro.awesometic.plankhelper.plank.stopwatch.StopwatchPresenter
import kr.kro.awesometic.plankhelper.plank.timer.TimerPresenter
import kr.kro.awesometic.plankhelper.plankservice.PlankService
import kr.kro.awesometic.plankhelper.util.Constants

/**
 * Created by Awesometic on 2017-06-07.
 */

class PlankServiceManager(private val mStopwatchPresenter: StopwatchPresenter, private val mTimerPresenter: TimerPresenter, private val mActivityContext: Context) {

    private val mStopwatchCallback = object : StopwatchPresenter.IStopwatchPresenterCallback {
        override fun stopwatchCommandToService(method: Int, what: Int) {
            mPlankService!!.timerTaskCommand(method, what)
        }

        override fun updateWidgetsByCurrentState(method: Int) {
            updateFragmentsWidget(method)
        }
    }

    private val mTimerCallback = object : TimerPresenter.ITimerPresenterCallback {

        override val timerStartMSec: Long
            get() = mPlankService!!.timerStartTimeMSec

        override fun timerCommandToService(method: Int, what: Int) {
            mPlankService!!.timerTaskCommand(method, what)
        }

        override fun updateWidgetsByCurrentState(method: Int) {
            updateFragmentsWidget(method)
        }
    }

    private var mPlankService: PlankService? = null
    private var mBound = false
    private val mCallback = object : PlankService.IPlankCallback {
        override fun start(method: Int) {
            when (method) {
                Constants.WORK_METHOD.STOPWATCH -> {
                    mStopwatchPresenter.controlFromService(Constants.SERVICE_WHAT.STOPWATCH_START)
                    mTimerPresenter.setWidgetsEnabled(false)
                }
                Constants.WORK_METHOD.TIMER -> {
                    mTimerPresenter.controlFromService(Constants.SERVICE_WHAT.TIMER_START)
                    mStopwatchPresenter.setWidgetsEnabled(false)
                }

                else -> {
                }
            }
        }

        override fun pause(method: Int) {
            when (method) {
                Constants.WORK_METHOD.STOPWATCH -> mStopwatchPresenter.controlFromService(Constants.SERVICE_WHAT.STOPWATCH_PAUSE)
                Constants.WORK_METHOD.TIMER -> mTimerPresenter.controlFromService(Constants.SERVICE_WHAT.TIMER_PAUSE)

                else -> {
                }
            }
        }

        override fun resume(method: Int) {
            when (method) {
                Constants.WORK_METHOD.STOPWATCH -> mStopwatchPresenter.controlFromService(Constants.SERVICE_WHAT.STOPWATCH_RESUME)
                Constants.WORK_METHOD.TIMER -> mTimerPresenter.controlFromService(Constants.SERVICE_WHAT.TIMER_RESUME)

                else -> {
                }
            }
        }

        override fun reset(method: Int) {
            when (method) {
                Constants.WORK_METHOD.STOPWATCH -> {
                    mStopwatchPresenter.controlFromService(Constants.SERVICE_WHAT.STOPWATCH_RESET)
                    mTimerPresenter.setWidgetsEnabled(true)

                    mStopwatchPresenter.clearLapTimeItem()
                }
                Constants.WORK_METHOD.TIMER -> {
                    mTimerPresenter.controlFromService(Constants.SERVICE_WHAT.TIMER_RESET)
                    mStopwatchPresenter.setWidgetsEnabled(true)

                    mTimerPresenter.clearLapTimeItem()
                }

                else -> {
                }
            }
        }

        override fun lap(method: Int, passedTime: Long, intervalTime: Long) {
            when (method) {
                Constants.WORK_METHOD.STOPWATCH -> mStopwatchPresenter.addLapTimeItem(passedTime, intervalTime)
                Constants.WORK_METHOD.TIMER -> mTimerPresenter.addLapTimeItem(passedTime, intervalTime)

                else -> {
                }
            }
        }

        override fun updateViews(method: Int, mSec: Long) {
            when (method) {
                Constants.WORK_METHOD.STOPWATCH -> mStopwatchPresenter.updateWidgetsOnFragment(mSec)
                Constants.WORK_METHOD.TIMER -> mTimerPresenter.updateWidgetsOnFragment(mSec)

                else -> {
                }
            }
        }

        override fun getStartTimeMSec(method: Int): Long {
            var startTimeMSec: Long = 0

            when (method) {
                Constants.WORK_METHOD.STOPWATCH -> {
                }
                Constants.WORK_METHOD.TIMER -> startTimeMSec = mTimerPresenter.startTimeMSec

                else -> {
                }
            }

            return startTimeMSec
        }

        override fun getLapCount(method: Int): Int {
            var lapCount = 0

            when (method) {
                Constants.WORK_METHOD.STOPWATCH -> lapCount = mStopwatchPresenter.lapCount
                Constants.WORK_METHOD.TIMER -> lapCount = mTimerPresenter.lapCount

                else -> {
                }
            }

            return lapCount
        }

        override fun getLastLapMSec(method: Int): Long {
            var lastLapMSec: Long = 0

            when (method) {
                Constants.WORK_METHOD.STOPWATCH -> lastLapMSec = mStopwatchPresenter.lastLapMSec
                Constants.WORK_METHOD.TIMER -> lastLapMSec = mTimerPresenter.lastLapMSec

                else -> {
                }
            }

            return lastLapMSec
        }

        override fun savePlankLog(method: Int) {
            when (method) {
                Constants.WORK_METHOD.STOPWATCH -> mStopwatchPresenter.savePlankLogData()
                Constants.WORK_METHOD.TIMER -> mTimerPresenter.savePlankLogData()

                else -> {
                }
            }
        }

        override fun appExit() {
            unbindService(mActivityContext)
            ActivityCompat.finishAffinity(mActivityContext as Activity)
            System.exit(0)
        }
    }
    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as PlankService.LocalBinder
            mPlankService = binder.service
            mPlankService!!.registerCallback(mCallback)

            mBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mBound = false
        }
    }

    fun bindService(context: Context) {
        if (!mBound) {
            val intent = Intent(context, PlankService::class.java)
            context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE)

            mStopwatchPresenter.registerCallback(mStopwatchCallback)
            mTimerPresenter.registerCallback(mTimerCallback)
        }
    }

    fun unbindService(context: Context) {
        if (mBound) {
            context.unbindService(mConnection)
            mBound = false
        }
    }

    fun plankActivityDestroyed() {
        mPlankService!!.timerTaskCommand(Constants.SERVICE_WHAT.NOTIFICATION_READY, Constants.SERVICE_WHAT.APP_EXIT)
    }

    private fun updateFragmentsWidget(method: Int) {
        if (mBound) {
            val justBeforeWhat = mPlankService!!.justBeforeWhat

            when (justBeforeWhat) {
                Constants.SERVICE_WHAT.STOPWATCH_START, Constants.SERVICE_WHAT.STOPWATCH_PAUSE, Constants.SERVICE_WHAT.STOPWATCH_RESUME -> {
                    mStopwatchPresenter.controlFromService(justBeforeWhat)
                    mTimerPresenter.controlFromService(Constants.SERVICE_WHAT.TIMER_RESET)
                    mTimerPresenter.setWidgetsEnabled(false)
                }

                Constants.SERVICE_WHAT.TIMER_START, Constants.SERVICE_WHAT.TIMER_PAUSE, Constants.SERVICE_WHAT.TIMER_RESUME -> {
                    mTimerPresenter.controlFromService(justBeforeWhat)
                    mStopwatchPresenter.controlFromService(Constants.SERVICE_WHAT.STOPWATCH_RESET)
                    mStopwatchPresenter.setWidgetsEnabled(false)
                }

                else -> {
                    mStopwatchPresenter.controlFromService(Constants.SERVICE_WHAT.STOPWATCH_RESET)
                    mTimerPresenter.controlFromService(Constants.SERVICE_WHAT.TIMER_RESET)
                    mStopwatchPresenter.setWidgetsEnabled(true)
                    mTimerPresenter.setWidgetsEnabled(true)
                }
            }
        } else {
            when (method) {
                Constants.WORK_METHOD.STOPWATCH -> {
                    mStopwatchPresenter.controlFromService(Constants.SERVICE_WHAT.STOPWATCH_RESET)
                    mStopwatchPresenter.setWidgetsEnabled(true)
                }

                Constants.WORK_METHOD.TIMER -> {
                    mTimerPresenter.controlFromService(Constants.SERVICE_WHAT.TIMER_RESET)
                    mTimerPresenter.setWidgetsEnabled(true)
                }

                else -> {
                }
            }
        }
    }
}
