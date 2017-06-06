package kr.kro.awesometic.plankhelper.plank.timer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import kr.kro.awesometic.plankhelper.R;
import kr.kro.awesometic.plankhelper.data.LapTime;
import kr.kro.awesometic.plankhelper.data.PlankLog;
import kr.kro.awesometic.plankhelper.data.source.PlankLogsRepository;
import kr.kro.awesometic.plankhelper.plank.LapTimeListViewAdapter;
import kr.kro.awesometic.plankhelper.plankservice.PlankService;
import kr.kro.awesometic.plankhelper.util.Constants;
import kr.kro.awesometic.plankhelper.util.TimeUtils;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Awesometic on 2017-04-19.
 */

public class TimerPresenter implements  TimerContract.Presenter {

    private final PlankLogsRepository mPlankLogsRepository;
    private final TimerContract.View mTimerView;

    private LapTimeListViewAdapter mLapTimeListViewAdapter;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private Context mActivityContext;

    private PlankService mPlankService;
    private boolean mBound = false;
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

    private PlankService.ITimerCallback mCallback = new PlankService.ITimerCallback() {
        @Override
        public void startFromService() {
            timerStart(Constants.CALLER.FROM_SERVICE);
        }

        @Override
        public void pauseFromService() {
            timerPause(Constants.CALLER.FROM_SERVICE);
        }

        @Override
        public void resumeFromService() {
            timerResume(Constants.CALLER.FROM_SERVICE);
        }

        @Override
        public void resetFromService() {
            timerReset(Constants.CALLER.FROM_SERVICE);
        }

        @Override
        public void lapFromService(long passedMSec, long intervalMSec) {
            addLapTimeItem(passedMSec, intervalMSec);
        }

        @Override
        public void updateDisplay(long mSec) {
            updateWidgetsOnFragment(mSec);
        }

        @Override
        public void resetDisplay() {
            timerReset(Constants.CALLER.FROM_SERVICE);
        }

        @Override
        public void setWidgetsEnabled(final boolean isEnabled) {
            ((Activity) mActivityContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTimerView.setOnOffButtonEnabled(isEnabled);
                    mTimerView.setResetLapButtonEnabled(isEnabled);
                }
            });
        }

        @Override
        public void appExitFromService() {
            appExit(Constants.CALLER.FROM_SERVICE);
        }
        
        @Override
        public int getLapCount() {
            return mLapTimeListViewAdapter.getCount();
        }

        @Override
        public long getLastLapMSec() {
            if (mLapTimeListViewAdapter.getCount() > 0)
                return TimeUtils.timeFormatToMSec(
                        mLapTimeListViewAdapter.getItem(mLapTimeListViewAdapter.getCount() - 1).getPassedTime());
            else
                return 0;
        }

        @Override
        public long getStartTimeMSec() {
            return TimeUtils.timeFormatToMSec(mTimerView.getTimeString());
        }

        @Override
        public void timerCompleteFromService() {
            timerComplete(Constants.CALLER.FROM_SERVICE);
        }
    };

    private final int mMethod = Constants.WORK_METHOD.TIMER;

    public TimerPresenter(@NonNull PlankLogsRepository plankLogsRepository,
                          @NonNull TimerContract.View TimerView) {
        mPlankLogsRepository = checkNotNull(plankLogsRepository, "plankLogsRepository cannot be null");
        mTimerView = checkNotNull(TimerView, "TimerView cannot be null");

        mTimerView.setPresenter(this);
    }

    private void initTimerPresenter() {
        mActivityContext = (Context) mTimerView.getActivityContext();
    }

    private void initTimerView() {
        if (!mBound) {
            mTimerView.showLoading();

            mLapTimeListViewAdapter = new LapTimeListViewAdapter();
            mRecyclerViewAdapter = new RecyclerViewAdapter();

            mTimerView.setLapTimeAdapter(mLapTimeListViewAdapter);
            mTimerView.setRecyclerViewAdapter(mRecyclerViewAdapter);

            mRecyclerViewAdapter.setPlankLogs(null);

            mTimerView.showTimer();
        }
    }

    @Override
    public void bindViewsFromViewHolderToFrag() {
        mTimerView.bindViewsFromViewHolder();
        mTimerView.setOnOffButtonValue(mActivityContext.getString(R.string.plank_timer_on));
        mTimerView.setResetLapButtonValue(mActivityContext.getString(R.string.plank_timer_reset));
    }

    private void addLapTimeItem(long passedMSec, long intervalMSec) {
        int order = mLapTimeListViewAdapter.getCount() + 1;
        String passedTime = TimeUtils.mSecToTimeFormat(passedMSec);
        String leftTime = TimeUtils.mSecToTimeFormat(mPlankService.getTimerStartTimeMSec() - passedMSec);
        String interval = TimeUtils.mSecToTimeFormat(intervalMSec);

        LapTime lapTime = new LapTime(
                Constants.DATABASE.EMPTY_PARENT_ID,
                order,
                passedTime,
                leftTime,
                interval
        );

        mLapTimeListViewAdapter.addItem(lapTime);

        ((Activity) mActivityContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLapTimeListViewAdapter.notifyDataSetChanged();
            }
        });
    }

    private void savePlankLogData() {
        int lapCount = mLapTimeListViewAdapter.getCount();

        PlankLog plankLog = new PlankLog(
                TimeUtils.getCurrentDatetimeFormatted(),
                mPlankService.getTimerStartTimeMSec(),
                Constants.DATABASE.METHOD_TIMER,
                lapCount,
                mLapTimeListViewAdapter.getAllItems()
        );

        mPlankLogsRepository.savePlankLog(plankLog);

        if (lapCount > 0) {
            mPlankLogsRepository.saveLapTimes(plankLog.getId(), mLapTimeListViewAdapter.getAllItems());
        }
    }

    private void timerComplete(int caller) {
        ((Activity) mActivityContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTimerView.setHour(0);
                mTimerView.setMin(0);
                mTimerView.setSec(0);

                mTimerView.setAllNumberPickersEnabled(true);
                mTimerView.setOnOffButtonValue(mActivityContext.getString(R.string.plank_timer_on));
                mTimerView.setResetLapButtonValue(mActivityContext.getString(R.string.plank_timer_reset));
            }
        });
    }

    @Override
    public void start() {
        initTimerPresenter();
        initTimerView();
    }

    @Override
    public void bindPlankService() {
        if (!mBound) {
            Intent intent = new Intent(mActivityContext, PlankService.class);
            mActivityContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void unbindPlankService() {
        if (mBound) {
            mActivityContext.unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    public void timerStart(int caller) {
        if (TimeUtils.timeFormatToMSec(mTimerView.getTimeString()) != 0) {
            if (caller == Constants.CALLER.FROM_TIMER_FRAGMENT)
                mPlankService.timerTaskCommand(mMethod, Constants.SERVICE_WHAT.TIMER_START);

            mTimerView.setAllNumberPickersEnabled(false);
            mTimerView.setOnOffButtonValue(mActivityContext.getString(R.string.plank_timer_pause));
            mTimerView.setResetLapButtonValue(mActivityContext.getString(R.string.plank_timer_lap));

            mLapTimeListViewAdapter.clear();
            mLapTimeListViewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void timerPause(int caller) {
        if (caller == Constants.CALLER.FROM_TIMER_FRAGMENT)
            mPlankService.timerTaskCommand(mMethod, Constants.SERVICE_WHAT.TIMER_PAUSE);

        mTimerView.setOnOffButtonValue(mActivityContext.getString(R.string.plank_timer_resume));
        mTimerView.setResetLapButtonValue(mActivityContext.getString(R.string.plank_timer_reset));
    }

    @Override
    public void timerResume(int caller) {
        if (TimeUtils.timeFormatToMSec(mTimerView.getTimeString()) != 0) {
            if (caller == Constants.CALLER.FROM_TIMER_FRAGMENT)
                mPlankService.timerTaskCommand(mMethod, Constants.SERVICE_WHAT.TIMER_RESUME);

            mTimerView.setOnOffButtonValue(mActivityContext.getString(R.string.plank_timer_pause));
            mTimerView.setResetLapButtonValue(mActivityContext.getString(R.string.plank_timer_lap));
        }
    }

    @Override
    public void timerReset(final int caller) {
        // 초기화 시 플랭크 기록 데이터베이스에 저장
        savePlankLogData();

        if (caller == Constants.CALLER.FROM_TIMER_FRAGMENT)
            mPlankService.timerTaskCommand(mMethod, Constants.SERVICE_WHAT.TIMER_RESET);

        ((Activity) mActivityContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTimerView.setHour(0);
                mTimerView.setMin(0);
                mTimerView.setSec(0);

                mTimerView.setAllNumberPickersEnabled(true);
                mTimerView.setOnOffButtonValue(mActivityContext.getString(R.string.plank_timer_on));
                mTimerView.setResetLapButtonValue(mActivityContext.getString(R.string.plank_timer_reset));

                if (TimeUtils.timeFormatToMSec(mTimerView.getTimeString()) == 0) {
                    mLapTimeListViewAdapter.clear();
                    mLapTimeListViewAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void timerLap(int caller) {
        if (caller == Constants.CALLER.FROM_TIMER_FRAGMENT)
            mPlankService.timerTaskCommand(mMethod, Constants.SERVICE_WHAT.TIMER_LAP);
    }

    @Override
    public void updateWidgetsOnFragment(final long mSec) {

        String timeFormat = TimeUtils.mSecToTimeFormat(mSec);
        String[] timeFormatSplit = timeFormat.split(":");

        final int resultHour = Integer.valueOf(timeFormatSplit[0]);
        final int resultMin = Integer.valueOf(timeFormatSplit[1]);
        final int resultSec = Integer.valueOf(timeFormatSplit[2].split("\\.")[0]);

        final String currentTimestamp = mTimerView.getTimeString();
        String[] currentTimeFormatSplit = currentTimestamp.split(":");
        final int currentHour = Integer.valueOf(currentTimeFormatSplit[0]);
        final int currentMin = Integer.valueOf(currentTimeFormatSplit[1]);
        final int currentSec = Integer.valueOf(currentTimeFormatSplit[2].split("\\.")[0]);

        ((Activity) mActivityContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (currentHour > resultHour) {
                    mTimerView.numberPickerChangeValueByOne(Constants.NUMBERPICKER_TYPE.HOUR, false);
                    mTimerView.numberPickerChangeValueByOne(Constants.NUMBERPICKER_TYPE.MIN, false);
                    mTimerView.numberPickerChangeValueByOne(Constants.NUMBERPICKER_TYPE.SEC, false);
                } else if (currentMin > resultMin) {
                    mTimerView.numberPickerChangeValueByOne(Constants.NUMBERPICKER_TYPE.MIN, false);
                    mTimerView.numberPickerChangeValueByOne(Constants.NUMBERPICKER_TYPE.SEC, false);
                } else if ((currentSec > resultSec) && (mSec != 0)) {
                    mTimerView.numberPickerChangeValueByOne(Constants.NUMBERPICKER_TYPE.SEC, false);
                }
            }
        });
    }

    @Override
    public void appExit(int caller) {
        ActivityCompat.finishAffinity((Activity) mActivityContext);
        System.exit(0);
    }
}
