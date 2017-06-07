package kr.kro.awesometic.plankhelper.plankservice;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;

import kr.kro.awesometic.plankhelper.R;
import kr.kro.awesometic.plankhelper.plank.PlankActivity;
import kr.kro.awesometic.plankhelper.util.Constants;
import kr.kro.awesometic.plankhelper.util.TimeUtils;

/**
 * Created by Awesometic on 2017-06-06.
 */

public class PlankNotificationManager {

    private static NotificationManager mNotificationManager;
    private static NotificationCompat.Builder mReadyBuilder;
    private static NotificationCompat.Builder mStopwatchBuilder;
    private static NotificationCompat.Builder mTimerBuilder;

    private static ArrayList<NotificationCompat.Action> mActionsNotificationReady = new ArrayList<>();
    private static ArrayList<NotificationCompat.Action> mActionsStopwatchRunning = new ArrayList<>();
    private static ArrayList<NotificationCompat.Action> mActionsStopwatchReady = new ArrayList<>();
    private static ArrayList<NotificationCompat.Action> mActionsStopwatchPaused = new ArrayList<>();
    private static ArrayList<NotificationCompat.Action> mActionsTimerRunning = new ArrayList<>();
    private static ArrayList<NotificationCompat.Action> mActionsTimerReady = new ArrayList<>();
    private static ArrayList<NotificationCompat.Action> mActionsTimerPaused = new ArrayList<>();

    public static void setNotificationForeground(Context context, boolean isForeground) {
        PlankService plankService = (PlankService) context;

        if (isForeground) {
            init(context, Constants.WORK_METHOD.NOTIFICATION_READY);

            plankService.startForeground(
                    Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                    mReadyBuilder.build()
            );
        } else {
            plankService.stopForeground(true);
        }
    }

    public static void update(Context context, int method, long mSec, long intervalMSec, long lastLapMSec, int lapCount, boolean isRunning) {
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        switch (method) {
            case Constants.WORK_METHOD.STOPWATCH: {
                if (isRunning) {
                    mStopwatchBuilder
                            .setContentTitle(context.getString(R.string.plank_stopwatch_notification_title_ongoing))
                            .mActions = mActionsStopwatchRunning;
                } else if (mSec == 0){
                    mStopwatchBuilder
                            .setContentTitle(context.getString(R.string.plank_stopwatch_notification_title_ready))
                            .mActions = mActionsStopwatchReady;
                } else {
                    mStopwatchBuilder
                            .setContentTitle(context.getString(R.string.plank_stopwatch_notification_title_ongoing))
                            .mActions = mActionsStopwatchPaused;
                }

                if (isRunning && lapCount > 0) {
                    mStopwatchBuilder.setContentText(
                            TimeUtils.mSecToTimeFormat(mSec).substring(0, 8) +
                                    " - " + TimeUtils.mSecToTimeFormat(intervalMSec).substring(0, 8) +
                                    " - " + TimeUtils.mSecToTimeFormat(lastLapMSec).substring(0, 8) +
                                    " (" + lapCount + ")"
                    );
                } else {
                    mStopwatchBuilder.setContentText(TimeUtils.mSecToTimeFormat(mSec).substring(0, 8));
                }

                mNotificationManager.notify(
                        Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                        mStopwatchBuilder.build()
                );
                break;
            }
            case Constants.WORK_METHOD.TIMER: {
                if (isRunning) {
                    mTimerBuilder
                            .setContentTitle(context.getString(R.string.plank_timer_notification_title_ongoing))
                            .mActions = mActionsTimerRunning;
                } else if (mSec == 0){
                    mTimerBuilder
                            .setContentTitle(context.getString(R.string.plank_timer_notification_title_ready))
                            .mActions = mActionsTimerReady;
                } else {
                    mTimerBuilder
                            .setContentTitle(context.getString(R.string.plank_timer_notification_title_ongoing))
                            .mActions = mActionsTimerPaused;
                }

                if (isRunning && lapCount > 0) {
                    mTimerBuilder.setContentText(
                            TimeUtils.mSecToTimeFormat(mSec).substring(0, 8) +
                                    " - " + TimeUtils.mSecToTimeFormat(intervalMSec).substring(0, 8) +
                                    " - " + TimeUtils.mSecToTimeFormat(lastLapMSec).substring(0, 8) +
                                    " (" + lapCount + ")"
                    );
                } else {
                    mTimerBuilder.setContentText(TimeUtils.mSecToTimeFormat(mSec).substring(0, 8));
                }

                mNotificationManager.notify(
                        Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                        mTimerBuilder.build()
                );
                break;
            }

            default:
                break;
        }
    }

    public static void reset(Context context, int method) {
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        switch (method) {
            case Constants.WORK_METHOD.NOTIFICATION_READY: {
                mReadyBuilder
                        .setContentTitle(context.getString(R.string.notification_ready_title))
                        .setContentText(context.getString(R.string.notification_ready_text))
                        .mActions = mActionsNotificationReady;

                mNotificationManager.notify(
                        Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                        mReadyBuilder.build()
                );
                break;
            }
            case Constants.WORK_METHOD.STOPWATCH: {
                mStopwatchBuilder
                        .setContentTitle(context.getString(R.string.plank_stopwatch_notification_title_ready))
                        .setContentText(TimeUtils.mSecToTimeFormat(0).substring(0, 8))
                        .mActions = mActionsStopwatchReady;

                mNotificationManager.notify(
                        Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                        mStopwatchBuilder.build()
                );
                break;
            }
            case Constants.WORK_METHOD.TIMER: {
                mTimerBuilder
                        .setContentTitle(context.getString(R.string.plank_timer_notification_title_ready))
                        .setContentText(TimeUtils.mSecToTimeFormat(0).substring(0, 8))
                        .mActions = mActionsTimerReady;

                mNotificationManager.notify(
                        Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                        mTimerBuilder.build()
                );
                break;
            }

            default:
                break;
        }
    }

    private static void init(Context context, int method) {
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mReadyBuilder = getNotificationBuilder(context, Constants.WORK_METHOD.NOTIFICATION_READY);
        mStopwatchBuilder = getNotificationBuilder(context, Constants.WORK_METHOD.STOPWATCH);
        mTimerBuilder = getNotificationBuilder(context, Constants.WORK_METHOD.TIMER);

        mActionsNotificationReady.add(getNotificationAction(context, Constants.SERVICE_WHAT.STOPWATCH_READY));
        mActionsNotificationReady.add(getNotificationAction(context, Constants.SERVICE_WHAT.TIMER_READY));
        mActionsNotificationReady.add(getNotificationAction(context, Constants.SERVICE_WHAT.APP_EXIT));

        mActionsStopwatchRunning.add(getNotificationAction(context, Constants.SERVICE_WHAT.STOPWATCH_PAUSE));
        mActionsStopwatchRunning.add(getNotificationAction(context, Constants.SERVICE_WHAT.STOPWATCH_LAP));
        mActionsStopwatchRunning.add(getNotificationAction(context, Constants.SERVICE_WHAT.NOTIFICATION_READY));

        mActionsStopwatchReady.add(getNotificationAction(context, Constants.SERVICE_WHAT.STOPWATCH_START));
        mActionsStopwatchReady.add(getNotificationAction(context, Constants.SERVICE_WHAT.STOPWATCH_RESET));
        mActionsStopwatchReady.add(getNotificationAction(context, Constants.SERVICE_WHAT.NOTIFICATION_READY));

        mActionsStopwatchPaused.add(getNotificationAction(context, Constants.SERVICE_WHAT.STOPWATCH_RESUME));
        mActionsStopwatchPaused.add(getNotificationAction(context, Constants.SERVICE_WHAT.STOPWATCH_RESET));
        mActionsStopwatchPaused.add(getNotificationAction(context, Constants.SERVICE_WHAT.NOTIFICATION_READY));

        mActionsTimerRunning.add(getNotificationAction(context, Constants.SERVICE_WHAT.TIMER_PAUSE));
        mActionsTimerRunning.add(getNotificationAction(context, Constants.SERVICE_WHAT.TIMER_LAP));
        mActionsTimerRunning.add(getNotificationAction(context, Constants.SERVICE_WHAT.NOTIFICATION_READY));

        mActionsTimerReady.add(getNotificationAction(context, Constants.SERVICE_WHAT.TIMER_START));
        mActionsTimerReady.add(getNotificationAction(context, Constants.SERVICE_WHAT.TIMER_RESET));
        mActionsTimerReady.add(getNotificationAction(context, Constants.SERVICE_WHAT.NOTIFICATION_READY));

        mActionsTimerPaused.add(getNotificationAction(context, Constants.SERVICE_WHAT.TIMER_RESUME));
        mActionsTimerPaused.add(getNotificationAction(context, Constants.SERVICE_WHAT.TIMER_RESET));
        mActionsTimerPaused.add(getNotificationAction(context, Constants.SERVICE_WHAT.NOTIFICATION_READY));

        switch (method) {
            case Constants.WORK_METHOD.NOTIFICATION_READY:
                mNotificationManager.notify(
                        Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                        mReadyBuilder.build());
                break;
            case Constants.WORK_METHOD.STOPWATCH:
                mNotificationManager.notify(
                        Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                        mStopwatchBuilder.build());
                break;
            case Constants.WORK_METHOD.TIMER:
                mNotificationManager.notify(
                        Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                        mTimerBuilder.build());
                break;
        }
    }

    private static NotificationCompat.Builder getNotificationBuilder(Context context, int method) {
        Intent notificationIntent = new Intent(context, PlankActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_timer_white_48dp)
                .setContentIntent(pendingIntent)
                .setOngoing(true);

        switch (method) {
            case Constants.WORK_METHOD.NOTIFICATION_READY: {
                notificationBuilder
                        .setContentTitle(context.getString(R.string.notification_ready_title))
                        .setContentText(context.getString(R.string.notification_ready_text))
                        .mActions = mActionsNotificationReady;
                break;
            }
            case Constants.WORK_METHOD.STOPWATCH: {
                notificationBuilder
                        .setContentTitle(context.getString(R.string.plank_stopwatch_notification_title_ready))
                        .setContentText(TimeUtils.mSecToTimeFormat(0).substring(0, 8))
                        .mActions = mActionsStopwatchReady;
                break;
            }
            case Constants.WORK_METHOD.TIMER: {
                notificationBuilder
                        .setContentTitle(context.getString(R.string.plank_timer_notification_title_ready))
                        .setContentText(TimeUtils.mSecToTimeFormat(0).substring(0, 8))
                        .mActions = mActionsTimerReady;
                break;
            }
            default:
                break;
        }

        return notificationBuilder;
    }

    @Nullable
    private static NotificationCompat.Action getNotificationAction(Context context, int what) {
        Intent intent = new Intent();
        PendingIntent pendingIntent;
        Context appContext = context.getApplicationContext();

        switch (what) {
            case Constants.SERVICE_WHAT.STOPWATCH_READY: {
                intent.setAction(Constants.BROADCAST_ACTION.STOPWATCH_READY);
                pendingIntent = PendingIntent.getBroadcast(
                        appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                return new NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        context.getString(R.string.notification_stopwatch),
                        pendingIntent)
                        .build();
            }
            case Constants.SERVICE_WHAT.STOPWATCH_START: {
                intent.setAction(Constants.BROADCAST_ACTION.STOPWATCH_START);
                pendingIntent = PendingIntent.getBroadcast(
                        appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                return new NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        context.getString(R.string.notification_stopwatch_start),
                        pendingIntent)
                        .build();
            }
            case Constants.SERVICE_WHAT.STOPWATCH_PAUSE: {
                intent.setAction(Constants.BROADCAST_ACTION.STOPWATCH_PAUSE);
                pendingIntent = PendingIntent.getBroadcast(
                        appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                return new NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        context.getString(R.string.notification_pause),
                        pendingIntent)
                        .build();
            }
            case Constants.SERVICE_WHAT.STOPWATCH_RESUME: {
                intent.setAction(Constants.BROADCAST_ACTION.STOPWATCH_RESUME);
                pendingIntent = PendingIntent.getBroadcast(
                        appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                return new NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        context.getString(R.string.notification_resume),
                        pendingIntent)
                        .build();
            }
            case Constants.SERVICE_WHAT.STOPWATCH_LAP: {
                intent.setAction(Constants.BROADCAST_ACTION.STOPWATCH_LAP);
                pendingIntent = PendingIntent.getBroadcast(
                        appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                return new NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        context.getString(R.string.notification_lap),
                        pendingIntent)
                        .build();
            }
            case Constants.SERVICE_WHAT.STOPWATCH_RESET: {
                intent.setAction(Constants.BROADCAST_ACTION.STOPWATCH_RESET);
                pendingIntent = PendingIntent.getBroadcast(
                        appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                return new NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        context.getString(R.string.notification_reset),
                        pendingIntent)
                        .build();
            }
            case Constants.SERVICE_WHAT.TIMER_READY: {
                intent.setAction(Constants.BROADCAST_ACTION.TIMER_READY);
                pendingIntent = PendingIntent.getBroadcast(
                        appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                return new NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        context.getString(R.string.notification_timer),
                        pendingIntent)
                        .build();
            }
            case Constants.SERVICE_WHAT.TIMER_START: {
                intent.setAction(Constants.BROADCAST_ACTION.TIMER_START);
                pendingIntent = PendingIntent.getBroadcast(
                        appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                return new NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        context.getString(R.string.notification_timer_start),
                        pendingIntent)
                        .build();
            }
            case Constants.SERVICE_WHAT.TIMER_PAUSE: {
                intent.setAction(Constants.BROADCAST_ACTION.TIMER_PAUSE);
                pendingIntent = PendingIntent.getBroadcast(
                        appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                return new NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        context.getString(R.string.notification_pause),
                        pendingIntent)
                        .build();
            }
            case Constants.SERVICE_WHAT.TIMER_RESUME: {
                intent.setAction(Constants.BROADCAST_ACTION.TIMER_RESUME);
                pendingIntent = PendingIntent.getBroadcast(
                        appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                return new NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        context.getString(R.string.notification_resume),
                        pendingIntent)
                        .build();
            }
            case Constants.SERVICE_WHAT.TIMER_LAP: {
                intent.setAction(Constants.BROADCAST_ACTION.TIMER_LAP);
                pendingIntent = PendingIntent.getBroadcast(
                        appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                return new NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        context.getString(R.string.notification_lap),
                        pendingIntent)
                        .build();
            }
            case Constants.SERVICE_WHAT.TIMER_RESET: {
                intent.setAction(Constants.BROADCAST_ACTION.TIMER_RESET);
                pendingIntent = PendingIntent.getBroadcast(
                        appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                return new NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        context.getString(R.string.notification_reset),
                        pendingIntent)
                        .build();
            }
            case Constants.SERVICE_WHAT.NOTIFICATION_READY: {
                intent.setAction(Constants.BROADCAST_ACTION.NOTIFICATION_READY);
                pendingIntent = PendingIntent.getBroadcast(
                        appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                return new NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        context.getString(R.string.notification_go_to_ready),
                        pendingIntent)
                        .build();
            }
            case Constants.SERVICE_WHAT.APP_EXIT: {
                intent.setAction(Constants.BROADCAST_ACTION.APP_EXIT);
                pendingIntent = PendingIntent.getBroadcast(
                        appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                return new NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        context.getString(R.string.notification_app_exit),
                        pendingIntent)
                        .build();
            }
            default:
                return null;
        }
    }
}
