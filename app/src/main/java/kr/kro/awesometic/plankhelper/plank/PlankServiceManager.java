package kr.kro.awesometic.plankhelper.plank;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;

import kr.kro.awesometic.plankhelper.plank.stopwatch.StopwatchPresenter;
import kr.kro.awesometic.plankhelper.plank.timer.TimerPresenter;
import kr.kro.awesometic.plankhelper.plankservice.PlankService;
import kr.kro.awesometic.plankhelper.util.Constants;

/**
 * Created by Awesometic on 2017-06-07.
 */

public class PlankServiceManager {

    private StopwatchPresenter mStopwatchPresenter;
    private TimerPresenter mTimerPresenter;

    private Context mActivityContext;

    private StopwatchPresenter.IStopwatchPresenterCallback mStopwatchCallback = new StopwatchPresenter.IStopwatchPresenterCallback() {
        @Override
        public void stopwatchCommandToService(int method, int what) {
            mPlankService.timerTaskCommand(method, what);
        }

        @Override
        public void updateWidgetsByCurrentState(int method) {
            updateFragmentsWidget(method);
        }
    };

    private TimerPresenter.ITimerPresenterCallback mTimerCallback = new TimerPresenter.ITimerPresenterCallback() {
        @Override
        public void timerCommandToService(int method, int what) {
            mPlankService.timerTaskCommand(method, what);
        }

        @Override
        public long getTimerStartMSec() {
            return mPlankService.getTimerStartTimeMSec();
        }

        @Override
        public void updateWidgetsByCurrentState(int method) {
            updateFragmentsWidget(method);
        }
    };

    private PlankService mPlankService;
    private boolean mBound = false;
    private PlankService.IPlankCallback mCallback = new PlankService.IPlankCallback() {
        @Override
        public void start(int method) {
            switch (method) {
                case Constants.WORK_METHOD.STOPWATCH:
                    mStopwatchPresenter.controlFromService(Constants.SERVICE_WHAT.STOPWATCH_START);
                    mTimerPresenter.setWidgetsEnabled(false);
                    break;
                case Constants.WORK_METHOD.TIMER:
                    mTimerPresenter.controlFromService(Constants.SERVICE_WHAT.TIMER_START);
                    mStopwatchPresenter.setWidgetsEnabled(false);
                    break;

                default:
                    break;
            }
        }

        @Override
        public void pause(int method) {
            switch (method) {
                case Constants.WORK_METHOD.STOPWATCH:
                    mStopwatchPresenter.controlFromService(Constants.SERVICE_WHAT.STOPWATCH_PAUSE);
                    break;
                case Constants.WORK_METHOD.TIMER:
                    mTimerPresenter.controlFromService(Constants.SERVICE_WHAT.TIMER_PAUSE);
                    break;

                default:
                    break;
            }
        }

        @Override
        public void resume(int method) {
            switch (method) {
                case Constants.WORK_METHOD.STOPWATCH:
                    mStopwatchPresenter.controlFromService(Constants.SERVICE_WHAT.STOPWATCH_RESUME);
                    break;
                case Constants.WORK_METHOD.TIMER:
                    mTimerPresenter.controlFromService(Constants.SERVICE_WHAT.TIMER_RESUME);
                    break;

                default:
                    break;
            }
        }

        @Override
        public void reset(int method) {
            switch (method) {
                case Constants.WORK_METHOD.STOPWATCH:
                    mStopwatchPresenter.controlFromService(Constants.SERVICE_WHAT.STOPWATCH_RESET);
                    mTimerPresenter.setWidgetsEnabled(true);

                    mStopwatchPresenter.clearLapTimeItem();
                    break;
                case Constants.WORK_METHOD.TIMER:
                    mTimerPresenter.controlFromService(Constants.SERVICE_WHAT.TIMER_RESET);
                    mStopwatchPresenter.setWidgetsEnabled(true);

                    mTimerPresenter.clearLapTimeItem();
                    break;

                default:
                    break;
            }
        }

        @Override
        public void lap(int method, long passedTime, long intervalTime) {
            switch (method) {
                case Constants.WORK_METHOD.STOPWATCH:
                    mStopwatchPresenter.addLapTimeItem(passedTime, intervalTime);
                    break;
                case Constants.WORK_METHOD.TIMER:
                    mTimerPresenter.addLapTimeItem(passedTime, intervalTime);
                    break;

                default:
                    break;
            }
        }

        @Override
        public void updateViews(int method, long mSec) {
            switch (method) {
                case Constants.WORK_METHOD.STOPWATCH:
                    mStopwatchPresenter.updateWidgetsOnFragment(mSec);
                    break;
                case Constants.WORK_METHOD.TIMER:
                    mTimerPresenter.updateWidgetsOnFragment(mSec);
                    break;

                default:
                    break;
            }
        }

        @Override
        public long getStartTimeMSec(int method) {
            long startTimeMSec = 0;

            switch (method) {
                case Constants.WORK_METHOD.STOPWATCH:

                    break;
                case Constants.WORK_METHOD.TIMER:
                    startTimeMSec = mTimerPresenter.getStartTimeMSec();
                    break;

                default:
                    break;
            }

            return startTimeMSec;
        }

        @Override
        public int getLapCount(int method) {
            int lapCount = 0;

            switch (method) {
                case Constants.WORK_METHOD.STOPWATCH:
                    lapCount = mStopwatchPresenter.getLapCount();
                    break;
                case Constants.WORK_METHOD.TIMER:
                    lapCount = mTimerPresenter.getLapCount();
                    break;

                default:
                    break;
            }

            return lapCount;
        }

        @Override
        public long getLastLapMSec(int method) {
            long lastLapMSec = 0;

            switch (method) {
                case Constants.WORK_METHOD.STOPWATCH:
                    lastLapMSec = mStopwatchPresenter.getLastLapMSec();
                    break;
                case Constants.WORK_METHOD.TIMER:
                    lastLapMSec = mTimerPresenter.getLastLapMSec();
                    break;

                default:
                    break;
            }

            return lastLapMSec;
        }

        @Override
        public void appExit() {
            unbindService(mActivityContext);
            ActivityCompat.finishAffinity((Activity) mActivityContext);
            System.exit(0);
        }
    };
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlankService.LocalBinder binder = (PlankService.LocalBinder) service;
            mPlankService = binder.getService();
            mPlankService.registerCallback(mCallback);

            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    public PlankServiceManager(StopwatchPresenter stopwatchPresenter, TimerPresenter timerPresenter, Context context) {
        mStopwatchPresenter = stopwatchPresenter;
        mTimerPresenter = timerPresenter;
        mActivityContext = context;
    }

    public void bindService(Context context) {
        if (!mBound) {
            Intent intent = new Intent(context, PlankService.class);
            context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

            mStopwatchPresenter.registerCallback(mStopwatchCallback);
            mTimerPresenter.registerCallback(mTimerCallback);
        }
    }

    public void unbindService(Context context) {
        if (mBound) {
            context.unbindService(mConnection);
            mBound = false;
        }
    }

    public void plankActivityDestroyed() {
        mPlankService.timerTaskCommand(Constants.SERVICE_WHAT.NOTIFICATION_READY, Constants.SERVICE_WHAT.APP_EXIT);
    }

    private void updateFragmentsWidget(int method) {
        if (mBound) {
            int justBeforeWhat = mPlankService.getJustBeforeWhat();

            switch (justBeforeWhat) {
                case Constants.SERVICE_WHAT.STOPWATCH_START:
                case Constants.SERVICE_WHAT.STOPWATCH_PAUSE:
                case Constants.SERVICE_WHAT.STOPWATCH_RESUME:
                    mStopwatchPresenter.controlFromService(justBeforeWhat);
                    mTimerPresenter.controlFromService(Constants.SERVICE_WHAT.TIMER_RESET);
                    mTimerPresenter.setWidgetsEnabled(false);
                    break;

                case Constants.SERVICE_WHAT.TIMER_START:
                case Constants.SERVICE_WHAT.TIMER_PAUSE:
                case Constants.SERVICE_WHAT.TIMER_RESUME:
                    mTimerPresenter.controlFromService(justBeforeWhat);
                    mStopwatchPresenter.controlFromService(Constants.SERVICE_WHAT.STOPWATCH_RESET);
                    mStopwatchPresenter.setWidgetsEnabled(false);
                    break;

                default:
                    mStopwatchPresenter.controlFromService(Constants.SERVICE_WHAT.STOPWATCH_RESET);
                    mTimerPresenter.controlFromService(Constants.SERVICE_WHAT.TIMER_RESET);
                    mStopwatchPresenter.setWidgetsEnabled(true);
                    mTimerPresenter.setWidgetsEnabled(true);
                    break;
            }
        } else {
            switch (method) {
                case Constants.WORK_METHOD.STOPWATCH:
                    mStopwatchPresenter.controlFromService(Constants.SERVICE_WHAT.STOPWATCH_RESET);
                    mStopwatchPresenter.setWidgetsEnabled(true);
                    break;

                case Constants.WORK_METHOD.TIMER:
                    mTimerPresenter.controlFromService(Constants.SERVICE_WHAT.TIMER_RESET);
                    mTimerPresenter.setWidgetsEnabled(true);
                    break;

                default:
                    break;
            }
        }
    }
}
