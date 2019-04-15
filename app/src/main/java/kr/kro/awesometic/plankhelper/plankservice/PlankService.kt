package kr.kro.awesometic.plankhelper.plankservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.*
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi

import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss

import java.util.Timer
import java.util.TimerTask

import kr.kro.awesometic.plankhelper.R
import kr.kro.awesometic.plankhelper.plank.PlankActivity
import kr.kro.awesometic.plankhelper.util.Constants

/**
 * Created by Awesometic on 2017-04-22.
 */

class PlankService : Service() {

    /* 기능 추가 아이디어 */
    // * LAP 보기 - 안드로이드 N 이상에서만, 등록한 LAP을 알림에서 펴 보여주는 기능

    /* 기능 보완 아이디어 */
    // * 반응성 보완 - LAP 추가 시 바로 알림에 적용하기 위해 알림을 업데이트하지만,
    // 이 때 Builder를 새로 생성하기 때문에 새로운 알림을 뿌려 렉이 생김

    private val mBinder = LocalBinder()

    private var mServiceContext: Context? = null

    private var mServiceLooper: Looper? = null
    private var mServiceHandler: ServiceHandler? = null

    private var mTimer: Timer? = null
    private var mTimerTaskMSec: Long = 0
    var timerStartTimeMSec: Long = 0
        private set
    private var mTimerTaskIntervalMSec: Int = 0
    private var mIsTimerTaskRunning: Boolean = false

    var justBeforeWhat: Int = 0
        private set

    private var mUpdateDisplayLooper: Looper? = null
    private var mUpdateDisplayHandler: UpdateDisplayHandler? = null
    private var mPlankCallback: IPlankCallback? = null

    private val mNotificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Constants.BROADCAST_ACTION.STOPWATCH_READY -> {
                    PlankNotificationManager.reset(applicationContext, Constants.WORK_METHOD.STOPWATCH)
                }
                Constants.BROADCAST_ACTION.STOPWATCH_START -> {
                    timerTaskCommand(Constants.WORK_METHOD.STOPWATCH, Constants.SERVICE_WHAT.STOPWATCH_START)
                }
                Constants.BROADCAST_ACTION.STOPWATCH_PAUSE -> {
                    timerTaskCommand(Constants.WORK_METHOD.STOPWATCH, Constants.SERVICE_WHAT.STOPWATCH_PAUSE)
                }
                Constants.BROADCAST_ACTION.STOPWATCH_RESUME -> {
                    timerTaskCommand(Constants.WORK_METHOD.STOPWATCH, Constants.SERVICE_WHAT.STOPWATCH_RESUME)
                }
                Constants.BROADCAST_ACTION.STOPWATCH_LAP -> {
                    timerTaskCommand(Constants.WORK_METHOD.STOPWATCH, Constants.SERVICE_WHAT.STOPWATCH_LAP)
                }
                Constants.BROADCAST_ACTION.STOPWATCH_RESET -> {
                    timerTaskCommand(Constants.WORK_METHOD.STOPWATCH, Constants.SERVICE_WHAT.STOPWATCH_RESET)
                }
                Constants.BROADCAST_ACTION.TIMER_READY -> {
                    PlankNotificationManager.reset(applicationContext, Constants.WORK_METHOD.TIMER)
                }
                Constants.BROADCAST_ACTION.TIMER_START -> {
                    if (mTimerTaskMSec == 0L) {
                        val dismissNotificationBarIntent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
                        applicationContext.sendBroadcast(dismissNotificationBarIntent)

                        val startPlankActivityIntent = Intent(applicationContext, PlankActivity::class.java)
                        startPlankActivityIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(startPlankActivityIntent)
                    } else {
                        timerTaskCommand(Constants.WORK_METHOD.TIMER, Constants.SERVICE_WHAT.TIMER_START)
                    }
                }
                Constants.BROADCAST_ACTION.TIMER_PAUSE -> {
                    timerTaskCommand(Constants.WORK_METHOD.TIMER, Constants.SERVICE_WHAT.TIMER_PAUSE)
                }
                Constants.BROADCAST_ACTION.TIMER_RESUME -> {
                    timerTaskCommand(Constants.WORK_METHOD.TIMER, Constants.SERVICE_WHAT.TIMER_RESUME)
                }
                Constants.BROADCAST_ACTION.TIMER_LAP -> {
                    timerTaskCommand(Constants.WORK_METHOD.TIMER, Constants.SERVICE_WHAT.TIMER_LAP)
                }
                Constants.BROADCAST_ACTION.TIMER_RESET -> {
                    timerTaskCommand(Constants.WORK_METHOD.TIMER, Constants.SERVICE_WHAT.TIMER_RESET)
                }
                Constants.BROADCAST_ACTION.NOTIFICATION_READY -> {
                    if (mIsTimerTaskRunning) {
                        Toast.makeText(applicationContext,
                                getString(R.string.notification_timer_task_ongoing), Toast.LENGTH_SHORT).show()
                    } else {
                        mTimerTaskMSec = 0
                        mTimerTaskIntervalMSec = 0
                        mIsTimerTaskRunning = false

                        PlankNotificationManager.reset(applicationContext, Constants.WORK_METHOD.NOTIFICATION_READY)
                    }
                }
                Constants.BROADCAST_ACTION.APP_EXIT -> {
                    timerTaskCommand(Constants.WORK_METHOD.NOTIFICATION_READY, Constants.SERVICE_WHAT.APP_EXIT)
                }
                else -> {
                }
            }
        }
    }

    interface IPlankCallback {
        fun start(method: Int)
        fun pause(method: Int)
        fun resume(method: Int)
        fun reset(method: Int)
        fun lap(method: Int, passedTime: Long, intervalTime: Long)

        fun updateViews(method: Int, mSec: Long)

        fun getStartTimeMSec(method: Int): Long
        fun getLapCount(method: Int): Int
        fun getLastLapMSec(method: Int): Long

        fun savePlankLog(method: Int)

        fun appExit()
    }

    private inner class ServiceHandler(looper: Looper) : Handler(looper) {

        override fun handleMessage(msg: Message) {
            val method = msg.arg1

            if (msg.what != Constants.SERVICE_WHAT.STOPWATCH_LAP && msg.what != Constants.SERVICE_WHAT.TIMER_LAP) {
                justBeforeWhat = msg.what
            }

            when (msg.what) {
                Constants.SERVICE_WHAT.STOPWATCH_START, Constants.SERVICE_WHAT.TIMER_START -> {
                    mPlankCallback!!.start(method)
                    mIsTimerTaskRunning = true

                    when (method) {
                        Constants.WORK_METHOD.STOPWATCH -> {
                            mTimerTaskMSec++
                            mTimerTaskIntervalMSec++

                            PlankNotificationManager.update(
                                    applicationContext,
                                    Constants.WORK_METHOD.STOPWATCH,
                                    mTimerTaskMSec,
                                    mTimerTaskIntervalMSec.toLong(),
                                    mPlankCallback!!.getLastLapMSec(method),
                                    mPlankCallback!!.getLapCount(method),
                                    mIsTimerTaskRunning
                            )
                            mTimer = Timer()
                            mTimer!!.scheduleAtFixedRate(object : TimerTask() {
                                override fun run() {
                                    mTimerTaskMSec++
                                    mTimerTaskIntervalMSec++

                                    if (mTimerTaskMSec % 1000 == 0L) {
                                        PlankNotificationManager.update(
                                                applicationContext,
                                                Constants.WORK_METHOD.STOPWATCH,
                                                mTimerTaskMSec,
                                                mTimerTaskIntervalMSec.toLong(),
                                                mPlankCallback!!.getLastLapMSec(method),
                                                mPlankCallback!!.getLapCount(method),
                                                mIsTimerTaskRunning
                                        )
                                    }
                                }
                            }, 0, 1)
                        }
                        Constants.WORK_METHOD.TIMER -> {
                            timerStartTimeMSec = mPlankCallback!!.getStartTimeMSec(method)
                            mTimerTaskMSec = timerStartTimeMSec

                            PlankNotificationManager.update(
                                    applicationContext,
                                    Constants.WORK_METHOD.TIMER,
                                    mTimerTaskMSec,
                                    mTimerTaskIntervalMSec.toLong(),
                                    mPlankCallback!!.getLastLapMSec(method),
                                    mPlankCallback!!.getLapCount(method),
                                    mIsTimerTaskRunning
                            )
                            mTimer = Timer()
                            mTimer!!.scheduleAtFixedRate(object : TimerTask() {
                                override fun run() {
                                    if (mTimerTaskMSec == 0L) {
                                        timerTaskCommand(method, Constants.SERVICE_WHAT.TIMER_RESET)
                                    } else {
                                        mTimerTaskMSec--
                                        mTimerTaskIntervalMSec++

                                        if (mTimerTaskMSec % 1000 == 0L) {
                                            mPlankCallback!!.updateViews(method, mTimerTaskMSec)
                                            PlankNotificationManager.update(
                                                    applicationContext,
                                                    Constants.WORK_METHOD.TIMER,
                                                    mTimerTaskMSec,
                                                    mTimerTaskIntervalMSec.toLong(),
                                                    mPlankCallback!!.getLastLapMSec(method),
                                                    mPlankCallback!!.getLapCount(method),
                                                    mIsTimerTaskRunning
                                            )
                                        }
                                    }
                                }
                            }, 0, 1)
                        }
                        else -> {
                        }
                    }
                    updateFragmentDisplay(method)
                }
                Constants.SERVICE_WHAT.STOPWATCH_PAUSE, Constants.SERVICE_WHAT.TIMER_PAUSE -> {
                    mPlankCallback!!.pause(method)
                    mIsTimerTaskRunning = false

                    mTimer!!.cancel()

                    PlankNotificationManager.update(
                            applicationContext,
                            method,
                            mTimerTaskMSec,
                            mTimerTaskIntervalMSec.toLong(),
                            mPlankCallback!!.getLastLapMSec(method),
                            mPlankCallback!!.getLapCount(method),
                            mIsTimerTaskRunning
                    )
                }
                Constants.SERVICE_WHAT.STOPWATCH_RESUME, Constants.SERVICE_WHAT.TIMER_RESUME -> {
                    mPlankCallback!!.resume(method)
                    mIsTimerTaskRunning = true

                    when (method) {
                        Constants.WORK_METHOD.STOPWATCH -> {
                            PlankNotificationManager.update(
                                    applicationContext,
                                    Constants.WORK_METHOD.STOPWATCH,
                                    mTimerTaskMSec,
                                    mTimerTaskIntervalMSec.toLong(),
                                    mPlankCallback!!.getLastLapMSec(method),
                                    mPlankCallback!!.getLapCount(method),
                                    mIsTimerTaskRunning
                            )
                            mTimer = Timer()
                            mTimer!!.scheduleAtFixedRate(object : TimerTask() {
                                override fun run() {
                                    mTimerTaskMSec++
                                    mTimerTaskIntervalMSec++

                                    if (mTimerTaskMSec % 1000 == 0L) {
                                        PlankNotificationManager.update(
                                                applicationContext,
                                                Constants.WORK_METHOD.STOPWATCH,
                                                mTimerTaskMSec,
                                                mTimerTaskIntervalMSec.toLong(),
                                                mPlankCallback!!.getLastLapMSec(method),
                                                mPlankCallback!!.getLapCount(method),
                                                mIsTimerTaskRunning
                                        )
                                    }
                                }
                            }, 0, 1)
                        }
                        Constants.WORK_METHOD.TIMER -> {
                            PlankNotificationManager.update(
                                    applicationContext,
                                    Constants.WORK_METHOD.TIMER,
                                    mTimerTaskMSec,
                                    mTimerTaskIntervalMSec.toLong(),
                                    mPlankCallback!!.getLastLapMSec(method),
                                    mPlankCallback!!.getLapCount(method),
                                    mIsTimerTaskRunning
                            )
                            mTimer = Timer()
                            mTimer!!.scheduleAtFixedRate(object : TimerTask() {
                                override fun run() {
                                    if (mTimerTaskMSec == 0L) {
                                        timerTaskCommand(method, Constants.SERVICE_WHAT.TIMER_RESET)
                                    } else {
                                        mTimerTaskMSec--
                                        mTimerTaskIntervalMSec++

                                        if (mTimerTaskMSec % 1000 == 0L) {
                                            mPlankCallback!!.updateViews(method, mTimerTaskMSec)
                                            PlankNotificationManager.update(
                                                    applicationContext,
                                                    Constants.WORK_METHOD.TIMER,
                                                    mTimerTaskMSec,
                                                    mTimerTaskIntervalMSec.toLong(),
                                                    mPlankCallback!!.getLastLapMSec(method),
                                                    mPlankCallback!!.getLapCount(method),
                                                    mIsTimerTaskRunning
                                            )
                                        }
                                    }
                                }
                            }, 0, 1)
                        }
                        else -> {
                        }
                    }
                    updateFragmentDisplay(method)
                }
                Constants.SERVICE_WHAT.STOPWATCH_RESET, Constants.SERVICE_WHAT.TIMER_RESET -> {
                    if (mTimerTaskMSec > 0) {
                        val dismissNotificationBarIntent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
                        applicationContext.sendBroadcast(dismissNotificationBarIntent)


                        val materialDialog = MaterialDialog(mServiceContext!!)
                                .title(R.string.plank_dialog_save_planklog_title)
                                .message(R.string.plank_dialog_save_planklog_content)
                                .cancelable(false)
                                .show {
                                    positiveButton(R.string.plank_dialog_save_planklog_positive) { dialog ->
                                        mPlankCallback!!.savePlankLog(method)
                                    }
                                    negativeButton(R.string.plank_dialog_save_planklog_negative) { dialog ->
                                        // Do nothing
                                    }
                                    onDismiss { dialog ->
                                        resetService(method)
                                    }
                                }

                        materialDialog.getWindow()!!.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
                        materialDialog.show()
                    } else {
                        resetService(method)
                    }
                }
                Constants.SERVICE_WHAT.STOPWATCH_LAP, Constants.SERVICE_WHAT.TIMER_LAP -> {
                    when (method) {
                        Constants.WORK_METHOD.STOPWATCH -> {
                            mPlankCallback!!.lap(method, mTimerTaskMSec, mTimerTaskIntervalMSec.toLong())
                            PlankNotificationManager.update(
                                    applicationContext,
                                    Constants.WORK_METHOD.STOPWATCH,
                                    mTimerTaskMSec,
                                    mTimerTaskIntervalMSec.toLong(),
                                    mPlankCallback!!.getLastLapMSec(method),
                                    mPlankCallback!!.getLapCount(method),
                                    mIsTimerTaskRunning
                            )
                        }
                        Constants.WORK_METHOD.TIMER -> {
                            mPlankCallback!!.lap(method, mTimerTaskMSec, mTimerTaskIntervalMSec.toLong())
                            PlankNotificationManager.update(
                                    applicationContext,
                                    Constants.WORK_METHOD.TIMER,
                                    mTimerTaskMSec,
                                    mTimerTaskIntervalMSec.toLong(),
                                    mPlankCallback!!.getLastLapMSec(method),
                                    mPlankCallback!!.getLapCount(method),
                                    mIsTimerTaskRunning
                            )
                        }

                        else -> {
                        }
                    }

                    mTimerTaskIntervalMSec = 0
                }
                Constants.SERVICE_WHAT.APP_EXIT -> {
                    if (mIsTimerTaskRunning) {
                        mTimer!!.cancel()
                    }

                    PlankNotificationManager.setNotificationForeground(mServiceContext!!, false)
                    mPlankCallback!!.appExit()
                }
                else -> {
                }
            }
        }
    }

    private inner class UpdateDisplayHandler(looper: Looper) : Handler(looper) {

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                Constants.UPDATE_DISPLAY_WHAT.UPDATE -> {
                    while (mIsTimerTaskRunning) {
                        when (msg.arg1) {
                            Constants.WORK_METHOD.STOPWATCH -> {
                                mPlankCallback!!.updateViews(Constants.WORK_METHOD.STOPWATCH, mTimerTaskMSec)
                                try {
                                    Thread.sleep(Constants.UPDATE_DISPLAY.STOPWATCH_FREQUENCY_IN_MILLISECOND.toLong())
                                } catch (ex: InterruptedException) {
                                    ex.printStackTrace()
                                }

                            }
                            else -> {
                            }
                        }
                    }
                }
            }
        }
    }

    inner class LocalBinder : Binder() {
        val service: PlankService
            get() = this@PlankService
    }



    override fun onCreate() {
        mServiceContext = this

        // 서비스에 대한 백그라운드 스레드 준비
        val thread = HandlerThread("AwesometicPlankHelperService",
                Process.THREAD_PRIORITY_BACKGROUND)
        thread.start()
        mServiceLooper = thread.looper
        mServiceHandler = ServiceHandler(mServiceLooper!!)

        // 플랭크 진행 시 뷰 업데이트를 위한 백그라운드 서비스 준비
        val updateDisplayThread = HandlerThread("AwesometicPlankHelperServiceUpdateDisplay",
                Process.THREAD_PRIORITY_BACKGROUND)
        updateDisplayThread.start()
        mUpdateDisplayLooper = updateDisplayThread.looper
        mUpdateDisplayHandler = UpdateDisplayHandler(mUpdateDisplayLooper!!)

        // 알림의 액션에 대한 IntentFilter와 해당 리시버 등록
        val notificationBRIntentFilter = IntentFilter()
        notificationBRIntentFilter.addAction(Constants.BROADCAST_ACTION.NOTIFICATION_READY)
        notificationBRIntentFilter.addAction(Constants.BROADCAST_ACTION.APP_EXIT)
        notificationBRIntentFilter.addAction(Constants.BROADCAST_ACTION.STOPWATCH_READY)
        notificationBRIntentFilter.addAction(Constants.BROADCAST_ACTION.STOPWATCH_START)
        notificationBRIntentFilter.addAction(Constants.BROADCAST_ACTION.STOPWATCH_RESUME)
        notificationBRIntentFilter.addAction(Constants.BROADCAST_ACTION.STOPWATCH_PAUSE)
        notificationBRIntentFilter.addAction(Constants.BROADCAST_ACTION.STOPWATCH_LAP)
        notificationBRIntentFilter.addAction(Constants.BROADCAST_ACTION.STOPWATCH_RESET)
        notificationBRIntentFilter.addAction(Constants.BROADCAST_ACTION.TIMER_READY)
        notificationBRIntentFilter.addAction(Constants.BROADCAST_ACTION.TIMER_SET)
        notificationBRIntentFilter.addAction(Constants.BROADCAST_ACTION.TIMER_START)
        notificationBRIntentFilter.addAction(Constants.BROADCAST_ACTION.TIMER_RESUME)
        notificationBRIntentFilter.addAction(Constants.BROADCAST_ACTION.TIMER_PAUSE)
        notificationBRIntentFilter.addAction(Constants.BROADCAST_ACTION.TIMER_LAP)
        notificationBRIntentFilter.addAction(Constants.BROADCAST_ACTION.TIMER_RESET)
        registerReceiver(mNotificationReceiver, notificationBRIntentFilter)

        justBeforeWhat = Constants.SERVICE_WHAT.NOTIFICATION_READY
        mIsTimerTaskRunning = false
    }

    override fun onBind(intent: Intent): IBinder? {
        PlankNotificationManager.setNotificationForeground(mServiceContext!!, true)

        return mBinder
    }

    override fun onDestroy() {
        unregisterReceiver(mNotificationReceiver)
    }

    fun registerCallback(callback: IPlankCallback) {
        mPlankCallback = callback
    }

    fun timerTaskCommand(method: Int, what: Int) {
        val msg = mServiceHandler!!.obtainMessage()
        msg.what = what
        msg.arg1 = method
        mServiceHandler!!.sendMessage(msg)
    }

    private fun updateFragmentDisplay(method: Int) {
        if (method == Constants.WORK_METHOD.STOPWATCH) {
            val msg = mUpdateDisplayHandler!!.obtainMessage()
            msg.what = Constants.UPDATE_DISPLAY_WHAT.UPDATE
            msg.arg1 = method
            mUpdateDisplayHandler!!.sendMessage(msg)
        }
    }

    private fun resetService(method: Int) {
        PlankNotificationManager.reset(applicationContext, method)
        mPlankCallback!!.reset(method)

        mIsTimerTaskRunning = false
        if (mTimer != null)
            mTimer!!.cancel()

        mTimerTaskMSec = 0
        mTimerTaskIntervalMSec = 0
        timerStartTimeMSec = 0
    }
}
