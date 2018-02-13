package kr.kro.awesometic.plankhelper.plankservice;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.NonNull;
import android.view.WindowManager;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Timer;
import java.util.TimerTask;

import kr.kro.awesometic.plankhelper.R;
import kr.kro.awesometic.plankhelper.plank.PlankActivity;
import kr.kro.awesometic.plankhelper.util.Constants;

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

    private Context mServiceContext;

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    private Timer mTimer;
    private long mTimerTaskMSec;
    private long mTimerStartTimeMSec;
    private int mTimerTaskIntervalMSec;
    private boolean mIsTimerTaskRunning;

    private int mJustBeforeWhat;

    private Looper mUpdateDisplayLooper;
    private UpdateDisplayHandler mUpdateDisplayHandler;

    public interface IPlankCallback {
        void start(int method);
        void pause(int method);
        void resume(int method);
        void reset(int method);
        void lap(int method, long passedTime, long intervalTime);

        void updateViews(int method, long mSec);

        long getStartTimeMSec(int method);
        int getLapCount(int method);
        long getLastLapMSec(int method);

        void savePlankLog(int method);

        void appExit();
    }
    private IPlankCallback mPlankCallback;

    private final BroadcastReceiver mNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Constants.BROADCAST_ACTION.STOPWATCH_READY: {
                    PlankNotificationManager.reset(getApplicationContext(), Constants.WORK_METHOD.STOPWATCH);
                    break;
                }
                case Constants.BROADCAST_ACTION.STOPWATCH_START: {
                    timerTaskCommand(Constants.WORK_METHOD.STOPWATCH, Constants.SERVICE_WHAT.STOPWATCH_START);
                    break;
                }
                case Constants.BROADCAST_ACTION.STOPWATCH_PAUSE: {
                    timerTaskCommand(Constants.WORK_METHOD.STOPWATCH, Constants.SERVICE_WHAT.STOPWATCH_PAUSE);
                    break;
                }
                case Constants.BROADCAST_ACTION.STOPWATCH_RESUME: {
                    timerTaskCommand(Constants.WORK_METHOD.STOPWATCH, Constants.SERVICE_WHAT.STOPWATCH_RESUME);
                    break;
                }
                case Constants.BROADCAST_ACTION.STOPWATCH_LAP: {
                    timerTaskCommand(Constants.WORK_METHOD.STOPWATCH, Constants.SERVICE_WHAT.STOPWATCH_LAP);
                    break;
                }
                case Constants.BROADCAST_ACTION.STOPWATCH_RESET: {
                    timerTaskCommand(Constants.WORK_METHOD.STOPWATCH, Constants.SERVICE_WHAT.STOPWATCH_RESET);
                    break;
                }
                case Constants.BROADCAST_ACTION.TIMER_READY: {
                    PlankNotificationManager.reset(getApplicationContext(), Constants.WORK_METHOD.TIMER);
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
                        timerTaskCommand(Constants.WORK_METHOD.TIMER, Constants.SERVICE_WHAT.TIMER_START);
                    }
                    break;
                }
                case Constants.BROADCAST_ACTION.TIMER_PAUSE: {
                    timerTaskCommand(Constants.WORK_METHOD.TIMER, Constants.SERVICE_WHAT.TIMER_PAUSE);
                    break;
                }
                case Constants.BROADCAST_ACTION.TIMER_RESUME: {
                    timerTaskCommand(Constants.WORK_METHOD.TIMER, Constants.SERVICE_WHAT.TIMER_RESUME);
                    break;
                }
                case Constants.BROADCAST_ACTION.TIMER_LAP: {
                    timerTaskCommand(Constants.WORK_METHOD.TIMER, Constants.SERVICE_WHAT.TIMER_LAP);
                    break;
                }
                case Constants.BROADCAST_ACTION.TIMER_RESET: {
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

                        PlankNotificationManager.reset(getApplicationContext(), Constants.WORK_METHOD.NOTIFICATION_READY);
                    }
                    break;
                }
                case Constants.BROADCAST_ACTION.APP_EXIT: {
                    timerTaskCommand(Constants.WORK_METHOD.NOTIFICATION_READY, Constants.SERVICE_WHAT.APP_EXIT);
                    break;
                }
                default:
                    break;
            }
        }
    };

    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            final int method = msg.arg1;

            if (msg.what != Constants.SERVICE_WHAT.STOPWATCH_LAP
                    && msg.what != Constants.SERVICE_WHAT.TIMER_LAP) {
                mJustBeforeWhat = msg.what;
            }

            switch (msg.what) {
                case Constants.SERVICE_WHAT.STOPWATCH_START:
                case Constants.SERVICE_WHAT.TIMER_START: {
                    mPlankCallback.start(method);
                    mIsTimerTaskRunning = true;

                    switch (method) {
                        case Constants.WORK_METHOD.STOPWATCH: {
                            mTimerTaskMSec++;
                            mTimerTaskIntervalMSec++;

                            PlankNotificationManager.update(
                                    getApplicationContext(),
                                    Constants.WORK_METHOD.STOPWATCH,
                                    mTimerTaskMSec,
                                    mTimerTaskIntervalMSec,
                                    mPlankCallback.getLastLapMSec(method),
                                    mPlankCallback.getLapCount(method),
                                    mIsTimerTaskRunning
                            );
                            mTimer = new Timer();
                            mTimer.scheduleAtFixedRate(new TimerTask() {
                                @Override
                                public void run() {
                                    mTimerTaskMSec++;
                                    mTimerTaskIntervalMSec++;

                                    if (mTimerTaskMSec % 1000 == 0) {
                                        PlankNotificationManager.update(
                                                getApplicationContext(),
                                                Constants.WORK_METHOD.STOPWATCH,
                                                mTimerTaskMSec,
                                                mTimerTaskIntervalMSec,
                                                mPlankCallback.getLastLapMSec(method),
                                                mPlankCallback.getLapCount(method),
                                                mIsTimerTaskRunning
                                        );
                                    }
                                }
                            }, 0, 1);
                            break;
                        }
                        case Constants.WORK_METHOD.TIMER: {
                            mTimerStartTimeMSec = mPlankCallback.getStartTimeMSec(method);
                            mTimerTaskMSec = mTimerStartTimeMSec;

                            PlankNotificationManager.update(
                                    getApplicationContext(),
                                    Constants.WORK_METHOD.TIMER,
                                    mTimerTaskMSec,
                                    mTimerTaskIntervalMSec,
                                    mPlankCallback.getLastLapMSec(method),
                                    mPlankCallback.getLapCount(method),
                                    mIsTimerTaskRunning
                            );
                            mTimer = new Timer();
                            mTimer.scheduleAtFixedRate(new TimerTask() {
                                @Override
                                public void run() {
                                    if (mTimerTaskMSec == 0) {
                                        timerTaskCommand(method, Constants.SERVICE_WHAT.TIMER_RESET);
                                    } else {
                                        mTimerTaskMSec--;
                                        mTimerTaskIntervalMSec++;

                                        if (mTimerTaskMSec % 1000 == 0) {
                                            mPlankCallback.updateViews(method, mTimerTaskMSec);
                                            PlankNotificationManager.update(
                                                    getApplicationContext(),
                                                    Constants.WORK_METHOD.TIMER,
                                                    mTimerTaskMSec,
                                                    mTimerTaskIntervalMSec,
                                                    mPlankCallback.getLastLapMSec(method),
                                                    mPlankCallback.getLapCount(method),
                                                    mIsTimerTaskRunning
                                            );
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
                    mPlankCallback.pause(method);
                    mIsTimerTaskRunning = false;

                    mTimer.cancel();

                    PlankNotificationManager.update(
                            getApplicationContext(),
                            method,
                            mTimerTaskMSec,
                            mTimerTaskIntervalMSec,
                            mPlankCallback.getLastLapMSec(method),
                            mPlankCallback.getLapCount(method),
                            mIsTimerTaskRunning
                    );

                    break;
                }
                case Constants.SERVICE_WHAT.STOPWATCH_RESUME:
                case Constants.SERVICE_WHAT.TIMER_RESUME: {
                    mPlankCallback.resume(method);
                    mIsTimerTaskRunning = true;

                    switch (method) {
                        case Constants.WORK_METHOD.STOPWATCH: {
                            PlankNotificationManager.update(
                                    getApplicationContext(),
                                    Constants.WORK_METHOD.STOPWATCH,
                                    mTimerTaskMSec,
                                    mTimerTaskIntervalMSec,
                                    mPlankCallback.getLastLapMSec(method),
                                    mPlankCallback.getLapCount(method),
                                    mIsTimerTaskRunning
                            );
                            mTimer = new Timer();
                            mTimer.scheduleAtFixedRate(new TimerTask() {
                                @Override
                                public void run() {
                                    mTimerTaskMSec++;
                                    mTimerTaskIntervalMSec++;

                                    if (mTimerTaskMSec % 1000 == 0) {
                                        PlankNotificationManager.update(
                                                getApplicationContext(),
                                                Constants.WORK_METHOD.STOPWATCH,
                                                mTimerTaskMSec,
                                                mTimerTaskIntervalMSec,
                                                mPlankCallback.getLastLapMSec(method),
                                                mPlankCallback.getLapCount(method),
                                                mIsTimerTaskRunning
                                        );
                                    }
                                }
                            }, 0, 1);
                            break;
                        }
                        case Constants.WORK_METHOD.TIMER: {
                            PlankNotificationManager.update(
                                    getApplicationContext(),
                                    Constants.WORK_METHOD.TIMER,
                                    mTimerTaskMSec,
                                    mTimerTaskIntervalMSec,
                                    mPlankCallback.getLastLapMSec(method),
                                    mPlankCallback.getLapCount(method),
                                    mIsTimerTaskRunning
                            );
                            mTimer = new Timer();
                            mTimer.scheduleAtFixedRate(new TimerTask() {
                                @Override
                                public void run() {
                                    if (mTimerTaskMSec == 0) {
                                        timerTaskCommand(method, Constants.SERVICE_WHAT.TIMER_RESET);
                                    } else {
                                        mTimerTaskMSec--;
                                        mTimerTaskIntervalMSec++;

                                        if (mTimerTaskMSec % 1000 == 0) {
                                            mPlankCallback.updateViews(method, mTimerTaskMSec);
                                            PlankNotificationManager.update(
                                                    getApplicationContext(),
                                                    Constants.WORK_METHOD.TIMER,
                                                    mTimerTaskMSec,
                                                    mTimerTaskIntervalMSec,
                                                    mPlankCallback.getLastLapMSec(method),
                                                    mPlankCallback.getLapCount(method),
                                                    mIsTimerTaskRunning
                                            );
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
                case Constants.SERVICE_WHAT.STOPWATCH_RESET:
                case Constants.SERVICE_WHAT.TIMER_RESET: {
                    if (mTimerTaskMSec > 0) {
                        Intent dismissNotificationBarIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                        getApplicationContext().sendBroadcast(dismissNotificationBarIntent);

                        MaterialDialog materialDialog = new MaterialDialog.Builder(mServiceContext)
                                .title(R.string.plank_dialog_save_planklog_title)
                                .content(R.string.plank_dialog_save_planklog_content)
                                .positiveText(R.string.plank_dialog_save_planklog_positive)
                                .negativeText(R.string.plank_dialog_save_planklog_negative)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        mPlankCallback.savePlankLog(method);
                                    }
                                })
                                .dismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        resetService(method);
                                    }
                                })
                                .cancelable(false)
                                .build();

                        materialDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                        materialDialog.show();
                    } else {
                        resetService(method);
                    }
                    break;
                }
                case Constants.SERVICE_WHAT.STOPWATCH_LAP:
                case Constants.SERVICE_WHAT.TIMER_LAP: {
                    switch (method) {
                        case Constants.WORK_METHOD.STOPWATCH:
                            mPlankCallback.lap(method, mTimerTaskMSec, mTimerTaskIntervalMSec);
                            PlankNotificationManager.update(
                                    getApplicationContext(),
                                    Constants.WORK_METHOD.STOPWATCH,
                                    mTimerTaskMSec,
                                    mTimerTaskIntervalMSec,
                                    mPlankCallback.getLastLapMSec(method),
                                    mPlankCallback.getLapCount(method),
                                    mIsTimerTaskRunning
                            );
                            break;
                        case Constants.WORK_METHOD.TIMER:
                            mPlankCallback.lap(method, mTimerTaskMSec, mTimerTaskIntervalMSec);
                            PlankNotificationManager.update(
                                    getApplicationContext(),
                                    Constants.WORK_METHOD.TIMER,
                                    mTimerTaskMSec,
                                    mTimerTaskIntervalMSec,
                                    mPlankCallback.getLastLapMSec(method),
                                    mPlankCallback.getLapCount(method),
                                    mIsTimerTaskRunning
                            );
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
                    }

                    PlankNotificationManager.setNotificationForeground(mServiceContext, false);
                    mPlankCallback.appExit();
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
                                mPlankCallback.updateViews(Constants.WORK_METHOD.STOPWATCH, mTimerTaskMSec);
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
        mServiceContext = this;

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

        mJustBeforeWhat = Constants.SERVICE_WHAT.NOTIFICATION_READY;
        mIsTimerTaskRunning = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        PlankNotificationManager.setNotificationForeground(mServiceContext, true);

        return mBinder;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mNotificationReceiver);
    }

    public void registerCallback(IPlankCallback callback) {
        mPlankCallback = callback;
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

    public int getJustBeforeWhat() {
        return mJustBeforeWhat;
    }

    private void updateFragmentDisplay(int method) {
        if (method == Constants.WORK_METHOD.STOPWATCH) {
            Message msg = mUpdateDisplayHandler.obtainMessage();
            msg.what = Constants.UPDATE_DISPLAY_WHAT.UPDATE;
            msg.arg1 = method;
            mUpdateDisplayHandler.sendMessage(msg);
        }
    }

    private void resetService(int method) {
        PlankNotificationManager.reset(getApplicationContext(), method);
        mPlankCallback.reset(method);

        mIsTimerTaskRunning = false;
        if (mTimer != null)
            mTimer.cancel();

        mTimerTaskMSec = 0;
        mTimerTaskIntervalMSec = 0;
        mTimerStartTimeMSec = 0;
    }
}
