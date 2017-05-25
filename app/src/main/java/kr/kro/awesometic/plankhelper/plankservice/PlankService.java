package kr.kro.awesometic.plankhelper.plankservice;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import kr.kro.awesometic.plankhelper.R;
import kr.kro.awesometic.plankhelper.plank.PlankActivity;
import kr.kro.awesometic.plankhelper.util.Constants;
import kr.kro.awesometic.plankhelper.util.TimeUtils;

/**
 * Created by Awesometic on 2017-04-22.
 */

public class PlankService extends Service {

    /* 기능 추가 아이디어 */
    // * LAP 보기 - 안드로이드 N 이상에서만, 등록한 LAP을 알림에서 펴 보여주는 기능

    /* 기능 보완 아이디어 */
    // * 반응성 보완 - LAP 추가 시 바로 알림에 적용하기 위해 알림을 업데이트하지만,
    // 이 때 Builder를 새로 생성하기 때문에 새로운 알림을 뿌려 렉이 생김

    private final IBinder mBinder = new LocalBinder();

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    private Timer mTimer;
    private long mTimerTaskMSec;
    private long mTimerStartTimeMSec;
    private int mTimerTaskIntervalMSec;
    private boolean mIsTimerTaskRunning;

    private Looper mUpdateDisplayLooper;
    private UpdateDisplayHandler mUpdateDisplayHandler;

    private NotificationManager mNotificationManager;
    private final BroadcastReceiver mNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Constants.BROADCAST_ACTION.STOPWATCH_READY: {
                    mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    initNotification(Constants.WORK_METHOD.STOPWATCH);
                    break;
                }
                case Constants.BROADCAST_ACTION.STOPWATCH_START: {
                    mStopwatchCallback.startFromNotification();
                    timerTaskCommand(Constants.WORK_METHOD.STOPWATCH, Constants.SERVICE_WHAT.STOPWATCH_START);
                    break;
                }
                case Constants.BROADCAST_ACTION.STOPWATCH_PAUSE: {
                    mStopwatchCallback.pauseFromNotification();
                    timerTaskCommand(Constants.WORK_METHOD.STOPWATCH, Constants.SERVICE_WHAT.STOPWATCH_PAUSE);
                    break;
                }
                case Constants.BROADCAST_ACTION.STOPWATCH_RESUME: {
                    mStopwatchCallback.resumeFromNotification();
                    timerTaskCommand(Constants.WORK_METHOD.STOPWATCH, Constants.SERVICE_WHAT.STOPWATCH_RESUME);
                    break;
                }
                case Constants.BROADCAST_ACTION.STOPWATCH_LAP: {
                    timerTaskCommand(Constants.WORK_METHOD.STOPWATCH, Constants.SERVICE_WHAT.STOPWATCH_LAP);
                    break;
                }
                case Constants.BROADCAST_ACTION.STOPWATCH_RESET: {
                    mStopwatchCallback.resetFromNotification();
                    timerTaskCommand(Constants.WORK_METHOD.STOPWATCH, Constants.SERVICE_WHAT.STOPWATCH_RESET);
                    break;
                }
                case Constants.BROADCAST_ACTION.TIMER_READY: {
                    mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    initNotification(Constants.WORK_METHOD.TIMER);
                    break;
                }
                case Constants.BROADCAST_ACTION.TIMER_START: {
                    if (mTimerTaskMSec == 0) {
                        Intent dismissNotificationBarIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                        getApplicationContext().sendBroadcast(dismissNotificationBarIntent);

                        Intent startPlankActivityIntent = new Intent(getApplicationContext(), PlankActivity.class);
                        startPlankActivityIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(startPlankActivityIntent);
                    } else {
                        mTimerCallback.startFromNotification();
                        timerTaskCommand(Constants.WORK_METHOD.TIMER, Constants.SERVICE_WHAT.TIMER_START);
                    }
                    break;
                }
                case Constants.BROADCAST_ACTION.TIMER_PAUSE: {
                    mTimerCallback.pauseFromNotification();
                    timerTaskCommand(Constants.WORK_METHOD.TIMER, Constants.SERVICE_WHAT.TIMER_PAUSE);
                    break;
                }
                case Constants.BROADCAST_ACTION.TIMER_RESUME: {
                    mTimerCallback.resumeFromNotification();
                    timerTaskCommand(Constants.WORK_METHOD.TIMER, Constants.SERVICE_WHAT.TIMER_RESUME);
                    break;
                }
                case Constants.BROADCAST_ACTION.TIMER_LAP: {
                    timerTaskCommand(Constants.WORK_METHOD.TIMER, Constants.SERVICE_WHAT.TIMER_LAP);
                    break;
                }
                case Constants.BROADCAST_ACTION.TIMER_RESET: {
                    mTimerCallback.resetFromNotification();
                    timerTaskCommand(Constants.WORK_METHOD.TIMER, Constants.SERVICE_WHAT.TIMER_RESET);
                    break;
                }
                case Constants.BROADCAST_ACTION.NOTIFICATION_READY: {
                    if (mIsTimerTaskRunning) {
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.notification_timer_task_ongoing), Toast.LENGTH_SHORT).show();
                    } else {
                        mTimerTaskMSec = 0;
                        mTimerTaskIntervalMSec = 0;
                        mIsTimerTaskRunning = false;

                        mStopwatchCallback.resetFromNotification();
                        mStopwatchCallback.updateDisplay(mTimerTaskMSec);
                        initNotification(Constants.WORK_METHOD.NOTIFICATION_READY);
                    }
                    break;
                }
                case Constants.BROADCAST_ACTION.APP_EXIT: {
                    // It doesn't need to be separated into each mode.
                    // So it calls one's method for exit
                    timerTaskCommand(Constants.WORK_METHOD.STOPWATCH, Constants.SERVICE_WHAT.APP_EXIT);
                    mStopwatchCallback.appExitFromNotification();
                    break;
                }
                default:
                    break;
            }
        }
    };

    public interface IStopwatchCallback {
        void startFromNotification();
        void pauseFromNotification();
        void resumeFromNotification();
        void resetFromNotification();
        void lapFromNotification(long passedMSec, long intervalMSec);
        void updateDisplay(long mSec);
        void appExitFromNotification();

        void setWidgetsEnabled(boolean isEnabled);

        int getLapCount();
        long getLastLapMSec();
    }

    public interface ITimerCallback {
        void startFromNotification();
        void pauseFromNotification();
        void resumeFromNotification();
        void resetFromNotification();
        void lapFromNotification(long passedMSec, long intervalMSec);
        void updateDisplay(long mSec);
        void resetDisplay();
        void appExitFromNotification();

        void setWidgetsEnabled(boolean isEnabled);

        int getLapCount();
        long getLastLapMSec();

        long getStartTimeMSec();
        void timerCompleteFromService();
    }

    private IStopwatchCallback mStopwatchCallback;
    private ITimerCallback mTimerCallback;

    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            final int method = msg.arg1;

            switch (msg.what) {
                case Constants.SERVICE_WHAT.STOPWATCH_START:
                case Constants.SERVICE_WHAT.TIMER_START: {
                    mIsTimerTaskRunning = true;

                    switch (method) {
                        case Constants.WORK_METHOD.STOPWATCH: {
                            mTimerCallback.setWidgetsEnabled(false);

                            mTimerTaskMSec++;
                            mTimerTaskIntervalMSec++;

                            final NotificationCompat.Builder notificationBuilder = getNotificationBuilder(method);
                            updateNotification(notificationBuilder, method);
                            mTimer = new Timer();
                            mTimer.scheduleAtFixedRate(new TimerTask() {
                                @Override
                                public void run() {
                                    mTimerTaskMSec++;
                                    mTimerTaskIntervalMSec++;

                                    if (mTimerTaskMSec % 1000 == 0) {
                                        updateNotification(notificationBuilder, method);
                                    }
                                }
                            }, 0, 1);
                            break;
                        }
                        case Constants.WORK_METHOD.TIMER: {
                            mStopwatchCallback.setWidgetsEnabled(false);

                            mTimerStartTimeMSec = mTimerCallback.getStartTimeMSec();
                            mTimerTaskMSec = mTimerStartTimeMSec;

                            final NotificationCompat.Builder notificationBuilder = getNotificationBuilder(method);
                            updateNotification(notificationBuilder, method);
                            mTimer = new Timer();
                            mTimer.scheduleAtFixedRate(new TimerTask() {
                                @Override
                                public void run() {
                                    if (mTimerTaskMSec == 0) {
                                        mStopwatchCallback.setWidgetsEnabled(true);

                                        resetNotification(method);
                                        mTimerCallback.timerCompleteFromService();
                                    } else {
                                        mTimerTaskMSec--;
                                        mTimerTaskIntervalMSec++;

                                        if (mTimerTaskMSec % 1000 == 0) {
                                            mTimerCallback.updateDisplay(mTimerTaskMSec);
                                            updateNotification(notificationBuilder, method);
                                        }
                                    }
                                }
                            }, 0, 1);

                            break;
                        }
                        default:
                            break;
                    }
                    updateFragmentDisplay(method);

                    break;
                }
                case Constants.SERVICE_WHAT.STOPWATCH_PAUSE:
                case Constants.SERVICE_WHAT.TIMER_PAUSE: {
                    mIsTimerTaskRunning = false;

                    mTimer.cancel();

                    NotificationCompat.Builder notificationBuilder = getNotificationBuilder(method);
                    updateNotification(notificationBuilder, method);

                    break;
                }
                case Constants.SERVICE_WHAT.STOPWATCH_RESUME:
                case Constants.SERVICE_WHAT.TIMER_RESUME: {
                    mIsTimerTaskRunning = true;

                    switch (method) {
                        case Constants.WORK_METHOD.STOPWATCH: {
                            final NotificationCompat.Builder notificationBuilder = getNotificationBuilder(method);
                            updateNotification(notificationBuilder, method);
                            mTimer = new Timer();
                            mTimer.scheduleAtFixedRate(new TimerTask() {
                                @Override
                                public void run() {
                                    mTimerTaskMSec++;
                                    mTimerTaskIntervalMSec++;

                                    if (mTimerTaskMSec % 1000 == 0) {
                                        updateNotification(notificationBuilder, method);
                                    }
                                }
                            }, 0, 1);
                            break;
                        }
                        case Constants.WORK_METHOD.TIMER: {
                            final NotificationCompat.Builder notificationBuilder = getNotificationBuilder(method);
                            updateNotification(notificationBuilder, method);
                            mTimer = new Timer();
                            mTimer.scheduleAtFixedRate(new TimerTask() {
                                @Override
                                public void run() {
                                    if (mTimerTaskMSec == 0) {
                                        resetNotification(method);
                                        mTimerCallback.timerCompleteFromService();
                                    } else {
                                        mTimerTaskMSec--;
                                        mTimerTaskIntervalMSec++;

                                        if (mTimerTaskMSec % 1000 == 0) {
                                            mTimerCallback.updateDisplay(mTimerTaskMSec);
                                            updateNotification(notificationBuilder, method);
                                        }
                                    }
                                }
                            }, 0, 1);

                            break;
                        }
                        default:
                            break;
                    }
                    updateFragmentDisplay(method);

                    break;
                }
                case Constants.SERVICE_WHAT.STOPWATCH_RESET: {
                    mTimerCallback.setWidgetsEnabled(true);

                    resetNotification(method);

                    mStopwatchCallback.updateDisplay(mTimerTaskMSec);
                    break;
                }
                case Constants.SERVICE_WHAT.TIMER_RESET: {
                    mStopwatchCallback.setWidgetsEnabled(true);

                    resetNotification(method);

                    mTimerCallback.resetDisplay();
                    break;
                }
                case Constants.SERVICE_WHAT.STOPWATCH_LAP:
                case Constants.SERVICE_WHAT.TIMER_LAP: {
                    switch (method) {
                        case Constants.WORK_METHOD.STOPWATCH:
                            mStopwatchCallback.lapFromNotification(mTimerTaskMSec, mTimerTaskIntervalMSec);
                            break;
                        case Constants.WORK_METHOD.TIMER:
                            mTimerCallback.lapFromNotification(mTimerTaskMSec, mTimerTaskIntervalMSec);
                            break;

                        default:
                            break;
                    }

                    mTimerTaskIntervalMSec = 0;

                    break;
                }
                case Constants.SERVICE_WHAT.APP_EXIT: {
                    if (mIsTimerTaskRunning) {
                        mTimer.cancel();

                        stopForeground(true);
                    }
                    break;
                }
                default:
                    break;
            }
        }
    }

    private final class UpdateDisplayHandler extends Handler {

        public UpdateDisplayHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.UPDATE_DISPLAY_WHAT.UPDATE: {
                    while (mIsTimerTaskRunning) {
                        switch (msg.arg1) {
                            case Constants.WORK_METHOD.STOPWATCH:
                                mStopwatchCallback.updateDisplay(mTimerTaskMSec);
                                try {
                                    Thread.sleep(Constants.UPDATE_DISPLAY.STOPWATCH_FREQUENCY_IN_MILLISECOND);
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                }
            }
        }
    }

    public class LocalBinder extends Binder {
        public PlankService getService() {
            return PlankService.this;
        }
    }

    @Override
    public void onCreate() {
        // 서비스에 대한 백그라운드 스레드 준비
        HandlerThread thread = new HandlerThread("AwesometicPlankHelperService",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

        // 플랭크 진행 시 뷰 업데이트를 위한 백그라운드 서비스 준비
        HandlerThread updateDisplayThread = new HandlerThread("AwesometicPlankHelperServiceUpdateDisplay",
                Process.THREAD_PRIORITY_BACKGROUND);
        updateDisplayThread.start();
        mUpdateDisplayLooper = updateDisplayThread.getLooper();
        mUpdateDisplayHandler = new UpdateDisplayHandler(mUpdateDisplayLooper);

        // 알림의 액션에 대한 IntentFilter와 해당 리시버 등록
        IntentFilter notificationBRIntentFilter = new IntentFilter();
        notificationBRIntentFilter.addAction(Constants.BROADCAST_ACTION.NOTIFICATION_READY);
        notificationBRIntentFilter.addAction(Constants.BROADCAST_ACTION.APP_EXIT);
        notificationBRIntentFilter.addAction(Constants.BROADCAST_ACTION.STOPWATCH_READY);
        notificationBRIntentFilter.addAction(Constants.BROADCAST_ACTION.STOPWATCH_START);
        notificationBRIntentFilter.addAction(Constants.BROADCAST_ACTION.STOPWATCH_RESUME);
        notificationBRIntentFilter.addAction(Constants.BROADCAST_ACTION.STOPWATCH_PAUSE);
        notificationBRIntentFilter.addAction(Constants.BROADCAST_ACTION.STOPWATCH_LAP);
        notificationBRIntentFilter.addAction(Constants.BROADCAST_ACTION.STOPWATCH_RESET);
        notificationBRIntentFilter.addAction(Constants.BROADCAST_ACTION.TIMER_READY);
        notificationBRIntentFilter.addAction(Constants.BROADCAST_ACTION.TIMER_SET);
        notificationBRIntentFilter.addAction(Constants.BROADCAST_ACTION.TIMER_START);
        notificationBRIntentFilter.addAction(Constants.BROADCAST_ACTION.TIMER_RESUME);
        notificationBRIntentFilter.addAction(Constants.BROADCAST_ACTION.TIMER_PAUSE);
        notificationBRIntentFilter.addAction(Constants.BROADCAST_ACTION.TIMER_LAP);
        notificationBRIntentFilter.addAction(Constants.BROADCAST_ACTION.TIMER_RESET);
        registerReceiver(mNotificationReceiver, notificationBRIntentFilter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        startForeground(
                Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                getNotificationBuilder(Constants.WORK_METHOD.NOTIFICATION_READY).build());

        mIsTimerTaskRunning = false;

        return mBinder;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mNotificationReceiver);
    }

    public void registerCallback(IStopwatchCallback callback) {
        mStopwatchCallback = callback;
    }
    public void registerCallback(ITimerCallback callback) {
        mTimerCallback = callback;
    }

    public long getTimerStartTimeMSec() {
        return mTimerStartTimeMSec;
    }

    public void timerTaskCommand(int method, int what) {
        Message msg = mServiceHandler.obtainMessage();
        msg.what = what;
        msg.arg1 = method;
        mServiceHandler.sendMessage(msg);
    }

    private void updateFragmentDisplay(int method) {
        if (method == Constants.WORK_METHOD.STOPWATCH) {
            Message msg = mUpdateDisplayHandler.obtainMessage();
            msg.what = Constants.UPDATE_DISPLAY_WHAT.UPDATE;
            msg.arg1 = method;
            mUpdateDisplayHandler.sendMessage(msg);
        }
    }

    private NotificationCompat.Builder getNotificationBuilder(int method) {
        Intent notificationIntent = new Intent(this, PlankActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_timer_white_48dp)
                .setContentIntent(pendingIntent)
                .setOngoing(true);

        switch (method) {
            case Constants.WORK_METHOD.NOTIFICATION_READY: {
                notificationBuilder
                        .setContentTitle(getString(R.string.notification_ready_title))
                        .setContentText(getString(R.string.notification_ready_text))
                        .addAction(getNotificationAction(Constants.SERVICE_WHAT.STOPWATCH_READY))
                        .addAction(getNotificationAction(Constants.SERVICE_WHAT.TIMER_READY))
                        .addAction(getNotificationAction(Constants.SERVICE_WHAT.APP_EXIT));
                break;
            }
            case Constants.WORK_METHOD.STOPWATCH: {
                notificationBuilder
                        .setContentText(TimeUtils.mSecToTimeFormat(mTimerTaskMSec).substring(0, 8));
                if (mIsTimerTaskRunning)
                    notificationBuilder
                            .setContentTitle(getString(R.string.plank_stopwatch_notification_title_ongoing))
                            .addAction(getNotificationAction(Constants.SERVICE_WHAT.STOPWATCH_PAUSE))
                            .addAction(getNotificationAction(Constants.SERVICE_WHAT.STOPWATCH_LAP))
                            .addAction(getNotificationAction(Constants.SERVICE_WHAT.NOTIFICATION_READY));
                else if (mTimerTaskMSec == 0)
                    notificationBuilder
                            .setContentTitle(getString(R.string.plank_stopwatch_notification_title_ready))
                            .addAction(getNotificationAction(Constants.SERVICE_WHAT.STOPWATCH_START))
                            .addAction(getNotificationAction(Constants.SERVICE_WHAT.STOPWATCH_RESET))
                            .addAction(getNotificationAction(Constants.SERVICE_WHAT.NOTIFICATION_READY));
                else
                    notificationBuilder
                            .setContentTitle(getString(R.string.plank_stopwatch_notification_title_ongoing))
                            .addAction(getNotificationAction(Constants.SERVICE_WHAT.STOPWATCH_RESUME))
                            .addAction(getNotificationAction(Constants.SERVICE_WHAT.STOPWATCH_RESET))
                            .addAction(getNotificationAction(Constants.SERVICE_WHAT.NOTIFICATION_READY));
                break;
            }
            case Constants.WORK_METHOD.TIMER: {
                notificationBuilder
                        .setContentText(TimeUtils.mSecToTimeFormat(mTimerTaskMSec).substring(0, 8));
                if (mIsTimerTaskRunning)
                    notificationBuilder
                            .setContentTitle(getString(R.string.plank_timer_notification_title_ongoing))
                            .addAction(getNotificationAction(Constants.SERVICE_WHAT.TIMER_PAUSE))
                            .addAction(getNotificationAction(Constants.SERVICE_WHAT.TIMER_LAP))
                            .addAction(getNotificationAction(Constants.SERVICE_WHAT.NOTIFICATION_READY));
                else if (mTimerTaskMSec == 0)
                    notificationBuilder
                            .setContentTitle(getString(R.string.plank_timer_notification_title_ready))
                            .addAction(getNotificationAction(Constants.SERVICE_WHAT.TIMER_START))
                            .addAction(getNotificationAction(Constants.SERVICE_WHAT.TIMER_RESET))
                            .addAction(getNotificationAction(Constants.SERVICE_WHAT.NOTIFICATION_READY));
                else
                    notificationBuilder
                            .setContentTitle(getString(R.string.plank_timer_notification_title_ongoing))
                            .addAction(getNotificationAction(Constants.SERVICE_WHAT.TIMER_RESUME))
                            .addAction(getNotificationAction(Constants.SERVICE_WHAT.TIMER_RESET))
                            .addAction(getNotificationAction(Constants.SERVICE_WHAT.NOTIFICATION_READY));
                break;
            }
            default:
                break;
        }

        return notificationBuilder;
    }

    @Nullable
    private NotificationCompat.Action getNotificationAction(int what) {
        Intent intent = new Intent();
        PendingIntent pendingIntent;

        switch (what) {
            case Constants.SERVICE_WHAT.STOPWATCH_READY: {
                intent.setAction(Constants.BROADCAST_ACTION.STOPWATCH_READY);
                pendingIntent = PendingIntent.getBroadcast(
                        getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                return new NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        getString(R.string.notification_stopwatch),
                        pendingIntent)
                        .build();
            }
            case Constants.SERVICE_WHAT.STOPWATCH_START: {
                intent.setAction(Constants.BROADCAST_ACTION.STOPWATCH_START);
                pendingIntent = PendingIntent.getBroadcast(
                        getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                return new NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        getString(R.string.notification_stopwatch_start),
                        pendingIntent)
                        .build();
            }
            case Constants.SERVICE_WHAT.STOPWATCH_PAUSE: {
                intent.setAction(Constants.BROADCAST_ACTION.STOPWATCH_PAUSE);
                pendingIntent = PendingIntent.getBroadcast(
                        getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                return new NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        getString(R.string.notification_pause),
                        pendingIntent)
                        .build();
            }
            case Constants.SERVICE_WHAT.STOPWATCH_RESUME: {
                intent.setAction(Constants.BROADCAST_ACTION.STOPWATCH_RESUME);
                pendingIntent = PendingIntent.getBroadcast(
                        getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                return new NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        getString(R.string.notification_resume),
                        pendingIntent)
                        .build();
            }
            case Constants.SERVICE_WHAT.STOPWATCH_LAP: {
                intent.setAction(Constants.BROADCAST_ACTION.STOPWATCH_LAP);
                pendingIntent = PendingIntent.getBroadcast(
                        getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                return new NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        getString(R.string.notification_lap),
                        pendingIntent)
                        .build();
            }
            case Constants.SERVICE_WHAT.STOPWATCH_RESET: {
                intent.setAction(Constants.BROADCAST_ACTION.STOPWATCH_RESET);
                pendingIntent = PendingIntent.getBroadcast(
                        getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                return new NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        getString(R.string.notification_reset),
                        pendingIntent)
                        .build();
            }
            case Constants.SERVICE_WHAT.TIMER_READY: {
                intent.setAction(Constants.BROADCAST_ACTION.TIMER_READY);
                pendingIntent = PendingIntent.getBroadcast(
                        getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                return new NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        getString(R.string.notification_timer),
                        pendingIntent)
                        .build();
            }
            case Constants.SERVICE_WHAT.TIMER_START: {
                intent.setAction(Constants.BROADCAST_ACTION.TIMER_START);
                pendingIntent = PendingIntent.getBroadcast(
                        getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                return new NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        getString(R.string.notification_timer_start),
                        pendingIntent)
                        .build();
            }
            case Constants.SERVICE_WHAT.TIMER_PAUSE: {
                intent.setAction(Constants.BROADCAST_ACTION.TIMER_PAUSE);
                pendingIntent = PendingIntent.getBroadcast(
                        getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                return new NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        getString(R.string.notification_pause),
                        pendingIntent)
                        .build();
            }
            case Constants.SERVICE_WHAT.TIMER_RESUME: {
                intent.setAction(Constants.BROADCAST_ACTION.TIMER_RESUME);
                pendingIntent = PendingIntent.getBroadcast(
                        getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                return new NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        getString(R.string.notification_resume),
                        pendingIntent)
                        .build();
            }
            case Constants.SERVICE_WHAT.TIMER_LAP: {
                intent.setAction(Constants.BROADCAST_ACTION.TIMER_LAP);
                pendingIntent = PendingIntent.getBroadcast(
                        getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                return new NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        getString(R.string.notification_lap),
                        pendingIntent)
                        .build();
            }
            case Constants.SERVICE_WHAT.TIMER_RESET: {
                intent.setAction(Constants.BROADCAST_ACTION.TIMER_RESET);
                pendingIntent = PendingIntent.getBroadcast(
                        getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                return new NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        getString(R.string.notification_reset),
                        pendingIntent)
                        .build();
            }
            case Constants.SERVICE_WHAT.NOTIFICATION_READY: {
                intent.setAction(Constants.BROADCAST_ACTION.NOTIFICATION_READY);
                pendingIntent = PendingIntent.getBroadcast(
                        getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                return new NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        getString(R.string.notification_go_to_ready),
                        pendingIntent)
                        .build();
            }
            case Constants.SERVICE_WHAT.APP_EXIT: {
                intent.setAction(Constants.BROADCAST_ACTION.APP_EXIT);
                pendingIntent = PendingIntent.getBroadcast(
                        getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                return new NotificationCompat.Action.Builder(
                        R.drawable.ic_timer_white_48dp,
                        getString(R.string.notification_app_exit),
                        pendingIntent)
                        .build();
            }
            default:
                return null;
        }
    }

    private void initNotification(int method) {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        switch (method) {
            case Constants.WORK_METHOD.NOTIFICATION_READY:
                mNotificationManager.notify(
                        Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                        getNotificationBuilder(Constants.WORK_METHOD.NOTIFICATION_READY).build());
                break;
            case Constants.WORK_METHOD.STOPWATCH:
                mNotificationManager.notify(
                        Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                        getNotificationBuilder(Constants.WORK_METHOD.STOPWATCH).build());
                break;
            case Constants.WORK_METHOD.TIMER:
                mNotificationManager.notify(
                        Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                        getNotificationBuilder(Constants.WORK_METHOD.TIMER).build());
                break;
        }
    }

    private void updateNotification(NotificationCompat.Builder notificationBuilder, int method) {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        switch (method) {
            case Constants.WORK_METHOD.STOPWATCH: {
                if (mStopwatchCallback.getLapCount() > 0) {
                    mNotificationManager.notify(
                            Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                            notificationBuilder.setContentText(
                                    TimeUtils.mSecToTimeFormat(mTimerTaskMSec).substring(0, 8) +
                                            " - " + TimeUtils.mSecToTimeFormat(mTimerTaskIntervalMSec).substring(0, 8) +
                                            " - " + TimeUtils.mSecToTimeFormat(mStopwatchCallback.getLastLapMSec()).substring(0, 8) +
                                            " (" + mStopwatchCallback.getLapCount() + ")").build());
                } else {
                    mNotificationManager.notify(
                            Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                            notificationBuilder.setContentText(TimeUtils.mSecToTimeFormat(mTimerTaskMSec).substring(0, 8)).build());
                }
                break;
            }
            case Constants.WORK_METHOD.TIMER: {
                if (mTimerCallback.getLapCount() > 0) {
                    mNotificationManager.notify(
                            Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                            notificationBuilder.setContentText(
                                    TimeUtils.mSecToTimeFormat(mTimerTaskMSec).substring(0, 8) +
                                            " - " + TimeUtils.mSecToTimeFormat(mTimerTaskIntervalMSec).substring(0, 8) +
                                            " - " + TimeUtils.mSecToTimeFormat(mTimerCallback.getLastLapMSec()).substring(0, 8) +
                                            " (" + mTimerCallback.getLapCount() + ")").build());
                } else {
                    mNotificationManager.notify(
                            Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                            notificationBuilder.setContentText(TimeUtils.mSecToTimeFormat(mTimerTaskMSec).substring(0, 8)).build());
                }
                break;
            }

            default:
                break;
        }

    }

    private void resetNotification(int method) {
        if (mIsTimerTaskRunning) {
            mTimer.cancel();
        }

        mTimerTaskMSec = 0;
        mTimerTaskIntervalMSec = 0;
        mTimerStartTimeMSec = 0;
        mIsTimerTaskRunning = false;

        initNotification(method);
    }
}
