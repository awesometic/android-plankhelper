package kr.kro.awesometic.plankhelper.plankservice

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat

import java.util.ArrayList

import kr.kro.awesometic.plankhelper.R
import kr.kro.awesometic.plankhelper.plank.PlankActivity
import kr.kro.awesometic.plankhelper.util.Constants
import kr.kro.awesometic.plankhelper.util.TimeUtils

/**
 * Created by Awesometic on 2017-06-06.
 */

object PlankNotificationManager {

    private var mNotificationManager: NotificationManager? = null
    private var mNotificationBuilder: NotificationCompat.Builder? = null

    private val mActionsNotificationReady = ArrayList<NotificationCompat.Action>()
    private val mActionsStopwatchRunning = ArrayList<NotificationCompat.Action>()
    private val mActionsStopwatchReady = ArrayList<NotificationCompat.Action>()
    private val mActionsStopwatchPaused = ArrayList<NotificationCompat.Action>()
    private val mActionsTimerRunning = ArrayList<NotificationCompat.Action>()
    private val mActionsTimerReady = ArrayList<NotificationCompat.Action>()
    private val mActionsTimerPaused = ArrayList<NotificationCompat.Action>()

    fun setNotificationForeground(context: Context, isForeground: Boolean) {
        val plankService = context as PlankService

        if (isForeground) {
            init(context)

            plankService.startForeground(
                    Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                    mNotificationBuilder!!.build()
            )

            reset(context, Constants.WORK_METHOD.NOTIFICATION_READY)
        } else {
            plankService.stopForeground(true)
        }
    }

    fun update(context: Context, method: Int, mSec: Long, intervalMSec: Long, lastLapMSec: Long, lapCount: Int, isRunning: Boolean) {
        mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        when (method) {
            Constants.WORK_METHOD.STOPWATCH -> {
                if (isRunning) {
                    mNotificationBuilder!!
                            .setContentTitle(context.getString(R.string.plank_stopwatch_notification_title_ongoing))
                            .mActions = mActionsStopwatchRunning
                } else if (mSec == 0L) {
                    mNotificationBuilder!!
                            .setContentTitle(context.getString(R.string.plank_stopwatch_notification_title_ready))
                            .mActions = mActionsStopwatchReady
                } else {
                    mNotificationBuilder!!
                            .setContentTitle(context.getString(R.string.plank_stopwatch_notification_title_ongoing))
                            .mActions = mActionsStopwatchPaused
                }

                if (isRunning && lapCount > 0) {
                    mNotificationBuilder!!.setContentText(
                            TimeUtils.mSecToTimeFormat(mSec).substring(0, 8) +
                                    " - " + TimeUtils.mSecToTimeFormat(intervalMSec).substring(0, 8) +
                                    " - " + TimeUtils.mSecToTimeFormat(lastLapMSec).substring(0, 8) +
                                    " (" + lapCount + ")"
                    )
                } else {
                    mNotificationBuilder!!.setContentText(TimeUtils.mSecToTimeFormat(mSec).substring(0, 8))
                }

                mNotificationManager!!.notify(
                        Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                        mNotificationBuilder!!.build()
                )
            }
            Constants.WORK_METHOD.TIMER -> {
                if (isRunning) {
                    mNotificationBuilder!!
                            .setContentTitle(context.getString(R.string.plank_timer_notification_title_ongoing))
                            .mActions = mActionsTimerRunning
                } else if (mSec == 0L) {
                    mNotificationBuilder!!
                            .setContentTitle(context.getString(R.string.plank_timer_notification_title_ready))
                            .mActions = mActionsTimerReady
                } else {
                    mNotificationBuilder!!
                            .setContentTitle(context.getString(R.string.plank_timer_notification_title_ongoing))
                            .mActions = mActionsTimerPaused
                }

                if (isRunning && lapCount > 0) {
                    mNotificationBuilder!!.setContentText(
                            TimeUtils.mSecToTimeFormat(mSec).substring(0, 8) +
                                    " - " + TimeUtils.mSecToTimeFormat(intervalMSec).substring(0, 8) +
                                    " - " + TimeUtils.mSecToTimeFormat(lastLapMSec).substring(0, 8) +
                                    " (" + lapCount + ")"
                    )
                } else {
                    mNotificationBuilder!!.setContentText(TimeUtils.mSecToTimeFormat(mSec).substring(0, 8))
                }

                mNotificationManager!!.notify(
                        Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                        mNotificationBuilder!!.build()
                )
            }

            else -> {
            }
        }
    }

    fun reset(context: Context, method: Int) {
        mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        when (method) {
            Constants.WORK_METHOD.NOTIFICATION_READY -> {
                mNotificationBuilder!!
                        .setContentTitle(context.getString(R.string.notification_ready_title))
                        .setContentText(context.getString(R.string.notification_ready_text))
                        .mActions = mActionsNotificationReady

                mNotificationManager!!.notify(
                        Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                        mNotificationBuilder!!.build()
                )
            }
            Constants.WORK_METHOD.STOPWATCH -> {
                mNotificationBuilder!!
                        .setContentTitle(context.getString(R.string.plank_stopwatch_notification_title_ready))
                        .setContentText(TimeUtils.mSecToTimeFormat(0).substring(0, 8))
                        .mActions = mActionsStopwatchReady

                mNotificationManager!!.notify(
                        Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                        mNotificationBuilder!!.build()
                )
            }
            Constants.WORK_METHOD.TIMER -> {
                mNotificationBuilder!!
                        .setContentTitle(context.getString(R.string.plank_timer_notification_title_ready))
                        .setContentText(TimeUtils.mSecToTimeFormat(0).substring(0, 8))
                        .mActions = mActionsTimerReady

                mNotificationManager!!.notify(
                        Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                        mNotificationBuilder!!.build()
                )
            }

            else -> {
            }
        }
    }

    private fun init(context: Context) {
        mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationBuilder = getNotificationBuilder(context)

        mActionsNotificationReady.add(getNotificationAction(context, Constants.SERVICE_WHAT.STOPWATCH_READY))
        mActionsNotificationReady.add(getNotificationAction(context, Constants.SERVICE_WHAT.TIMER_READY))
        mActionsNotificationReady.add(getNotificationAction(context, Constants.SERVICE_WHAT.APP_EXIT))

        mActionsStopwatchRunning.add(getNotificationAction(context, Constants.SERVICE_WHAT.STOPWATCH_PAUSE))
        mActionsStopwatchRunning.add(getNotificationAction(context, Constants.SERVICE_WHAT.STOPWATCH_LAP))
        mActionsStopwatchRunning.add(getNotificationAction(context, Constants.SERVICE_WHAT.NOTIFICATION_READY))

        mActionsStopwatchReady.add(getNotificationAction(context, Constants.SERVICE_WHAT.STOPWATCH_START))
        mActionsStopwatchReady.add(getNotificationAction(context, Constants.SERVICE_WHAT.STOPWATCH_RESET))
        mActionsStopwatchReady.add(getNotificationAction(context, Constants.SERVICE_WHAT.NOTIFICATION_READY))

        mActionsStopwatchPaused.add(getNotificationAction(context, Constants.SERVICE_WHAT.STOPWATCH_RESUME))
        mActionsStopwatchPaused.add(getNotificationAction(context, Constants.SERVICE_WHAT.STOPWATCH_RESET))
        mActionsStopwatchPaused.add(getNotificationAction(context, Constants.SERVICE_WHAT.NOTIFICATION_READY))

        mActionsTimerRunning.add(getNotificationAction(context, Constants.SERVICE_WHAT.TIMER_PAUSE))
        mActionsTimerRunning.add(getNotificationAction(context, Constants.SERVICE_WHAT.TIMER_LAP))
        mActionsTimerRunning.add(getNotificationAction(context, Constants.SERVICE_WHAT.NOTIFICATION_READY))

        mActionsTimerReady.add(getNotificationAction(context, Constants.SERVICE_WHAT.TIMER_START))
        mActionsTimerReady.add(getNotificationAction(context, Constants.SERVICE_WHAT.TIMER_RESET))
        mActionsTimerReady.add(getNotificationAction(context, Constants.SERVICE_WHAT.NOTIFICATION_READY))

        mActionsTimerPaused.add(getNotificationAction(context, Constants.SERVICE_WHAT.TIMER_RESUME))
        mActionsTimerPaused.add(getNotificationAction(context, Constants.SERVICE_WHAT.TIMER_RESET))
        mActionsTimerPaused.add(getNotificationAction(context, Constants.SERVICE_WHAT.NOTIFICATION_READY))

        mNotificationManager!!.notify(
                Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                mNotificationBuilder!!.build())
    }

    private fun getNotificationBuilder(context: Context): NotificationCompat.Builder {
        val notificationIntent = Intent(context, PlankActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0)

        return NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_timer_white_48dp)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
    }

    private fun getNotificationAction(context: Context, what: Int): NotificationCompat.Action? {
        val intent = Intent()
        val pendingIntent: PendingIntent
        val appContext = context.applicationContext

        when (what) {
            Constants.SERVICE_WHAT.STOPWATCH_READY -> {
                intent.action = Constants.BROADCAST_ACTION.STOPWATCH_READY
                pendingIntent = PendingIntent.getBroadcast(
                        appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                return NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        context.getString(R.string.notification_stopwatch),
                        pendingIntent)
                        .build()
            }
            Constants.SERVICE_WHAT.STOPWATCH_START -> {
                intent.action = Constants.BROADCAST_ACTION.STOPWATCH_START
                pendingIntent = PendingIntent.getBroadcast(
                        appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                return NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        context.getString(R.string.notification_stopwatch_start),
                        pendingIntent)
                        .build()
            }
            Constants.SERVICE_WHAT.STOPWATCH_PAUSE -> {
                intent.action = Constants.BROADCAST_ACTION.STOPWATCH_PAUSE
                pendingIntent = PendingIntent.getBroadcast(
                        appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                return NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        context.getString(R.string.notification_pause),
                        pendingIntent)
                        .build()
            }
            Constants.SERVICE_WHAT.STOPWATCH_RESUME -> {
                intent.action = Constants.BROADCAST_ACTION.STOPWATCH_RESUME
                pendingIntent = PendingIntent.getBroadcast(
                        appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                return NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        context.getString(R.string.notification_resume),
                        pendingIntent)
                        .build()
            }
            Constants.SERVICE_WHAT.STOPWATCH_LAP -> {
                intent.action = Constants.BROADCAST_ACTION.STOPWATCH_LAP
                pendingIntent = PendingIntent.getBroadcast(
                        appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                return NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        context.getString(R.string.notification_lap),
                        pendingIntent)
                        .build()
            }
            Constants.SERVICE_WHAT.STOPWATCH_RESET -> {
                intent.action = Constants.BROADCAST_ACTION.STOPWATCH_RESET
                pendingIntent = PendingIntent.getBroadcast(
                        appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                return NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        context.getString(R.string.notification_reset),
                        pendingIntent)
                        .build()
            }
            Constants.SERVICE_WHAT.TIMER_READY -> {
                intent.action = Constants.BROADCAST_ACTION.TIMER_READY
                pendingIntent = PendingIntent.getBroadcast(
                        appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                return NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        context.getString(R.string.notification_timer),
                        pendingIntent)
                        .build()
            }
            Constants.SERVICE_WHAT.TIMER_START -> {
                intent.action = Constants.BROADCAST_ACTION.TIMER_START
                pendingIntent = PendingIntent.getBroadcast(
                        appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                return NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        context.getString(R.string.notification_timer_start),
                        pendingIntent)
                        .build()
            }
            Constants.SERVICE_WHAT.TIMER_PAUSE -> {
                intent.action = Constants.BROADCAST_ACTION.TIMER_PAUSE
                pendingIntent = PendingIntent.getBroadcast(
                        appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                return NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        context.getString(R.string.notification_pause),
                        pendingIntent)
                        .build()
            }
            Constants.SERVICE_WHAT.TIMER_RESUME -> {
                intent.action = Constants.BROADCAST_ACTION.TIMER_RESUME
                pendingIntent = PendingIntent.getBroadcast(
                        appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                return NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        context.getString(R.string.notification_resume),
                        pendingIntent)
                        .build()
            }
            Constants.SERVICE_WHAT.TIMER_LAP -> {
                intent.action = Constants.BROADCAST_ACTION.TIMER_LAP
                pendingIntent = PendingIntent.getBroadcast(
                        appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                return NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        context.getString(R.string.notification_lap),
                        pendingIntent)
                        .build()
            }
            Constants.SERVICE_WHAT.TIMER_RESET -> {
                intent.action = Constants.BROADCAST_ACTION.TIMER_RESET
                pendingIntent = PendingIntent.getBroadcast(
                        appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                return NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        context.getString(R.string.notification_reset),
                        pendingIntent)
                        .build()
            }
            Constants.SERVICE_WHAT.NOTIFICATION_READY -> {
                intent.action = Constants.BROADCAST_ACTION.NOTIFICATION_READY
                pendingIntent = PendingIntent.getBroadcast(
                        appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                return NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        context.getString(R.string.notification_go_to_ready),
                        pendingIntent)
                        .build()
            }
            Constants.SERVICE_WHAT.APP_EXIT -> {
                intent.action = Constants.BROADCAST_ACTION.APP_EXIT
                pendingIntent = PendingIntent.getBroadcast(
                        appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                return NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        context.getString(R.string.notification_app_exit),
                        pendingIntent)
                        .build()
            }
            else -> return null
        }
    }
}
