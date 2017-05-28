package kr.kro.awesometic.plankhelper.plank.stopwatch;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import java.util.Locale;

import kr.kro.awesometic.plankhelper.R;
import kr.kro.awesometic.plankhelper.data.LapTime;
import kr.kro.awesometic.plankhelper.data.PlankLog;
import kr.kro.awesometic.plankhelper.data.source.PlankLogsRepository;
import kr.kro.awesometic.plankhelper.plank.LapTimeListViewAdapter;
import kr.kro.awesometic.plankhelper.plankservice.PlankService;
import kr.kro.awesometic.plankhelper.plankservice.PlankService.LocalBinder;
import kr.kro.awesometic.plankhelper.util.Constants;
import kr.kro.awesometic.plankhelper.util.TimeUtils;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Awesometic on 2017-04-19.
 */

public class StopwatchPresenter implements StopwatchContract.Presenter {

    private final PlankLogsRepository mPlankLogsRepository;
    private final StopwatchContract.View mStopwatchView;

    private LapTimeListViewAdapter mLapTimeListViewAdapter;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private Context mActivityContext;

    private boolean mIsLoadingData;

    private PlankService mPlankService;
    private boolean mBound = false;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocalBinder binder = (LocalBinder) service;
            mPlankService = binder.getService();
            mPlankService.registerCallback(mCallback);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    private PlankService.IStopwatchCallback mCallback = new PlankService.IStopwatchCallback() {
        @Override
        public void startFromNotification() {
            stopwatchStart(Constants.CALLER.FROM_SERVICE);
        }

        @Override
        public void pauseFromNotification() {
            stopwatchPause(Constants.CALLER.FROM_SERVICE);
        }

        @Override
        public void resumeFromNotification() {
            stopwatchResume(Constants.CALLER.FROM_SERVICE);
        }

        @Override
        public void resetFromNotification() {
            stopwatchReset(Constants.CALLER.FROM_SERVICE);
        }

        @Override
        public void lapFromNotification(long passedMSec, long intervalMSec) {
            addLapTimeItem(passedMSec, intervalMSec);
        }

        @Override
        public void updateDisplay(long mSec) {
            updateWidgetsOnFragment(mSec);
        }

        @Override
        public void setWidgetsEnabled(final boolean isEnabled) {
            ((Activity) mActivityContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mStopwatchView.setOnOffButtonEnabled(isEnabled);
                    mStopwatchView.setResetLapButtonEnabled(isEnabled);
                }
            });
        }

        @Override
        public void appExitFromNotification() {
            appExit(Constants.CALLER.FROM_SERVICE);
        }

        @Override
        public int getLapCount() {
            return mLapTimeListViewAdapter.getCount();
        }

        @Override
        public long getLastLapMSec() {
            return TimeUtils.timeFormatToMSec(
                    mLapTimeListViewAdapter.getItem(mLapTimeListViewAdapter.getCount() - 1).getPassedTime());
        }
    };

    private final int mMethod = Constants.WORK_METHOD.STOPWATCH;

    public StopwatchPresenter(@NonNull PlankLogsRepository plankLogsRepository,
                              @NonNull StopwatchContract.View stopwatchView) {
        mPlankLogsRepository = checkNotNull(plankLogsRepository, "plankLogsRepository cannot be null");
        mStopwatchView = checkNotNull(stopwatchView, "stopwatchView cannot be null");

        mStopwatchView.setPresenter(this);
    }


    private void initStopwatchPresenter() {
        mActivityContext = (Context) mStopwatchView.getActivityContext();
    }

    private void initStopwatchView() {
        if (!mBound && !mIsLoadingData) {
            mStopwatchView.showLoading();
            new LoadDataTask().execute();

            mRecyclerViewAdapter = new RecyclerViewAdapter();
            mLapTimeListViewAdapter = new LapTimeListViewAdapter();
            mStopwatchView.setRecyclerViewAdapter(mRecyclerViewAdapter);
            mStopwatchView.setLapTimeAdapter(mLapTimeListViewAdapter);
            mStopwatchView.bindViewsFromViewHolder();
            mStopwatchView.setOnOffButtonValue(mActivityContext.getString(R.string.plank_stopwatch_on));
            mStopwatchView.setResetLapButtonValue(mActivityContext.getString(R.string.plank_stopwatch_reset));

            mStopwatchView.showStopwatch();
        }
    }

    private void addLapTimeItem(long passedMSec, long intervalMSec) {
        int order = mLapTimeListViewAdapter.getCount() + 1;
        String passedTime = TimeUtils.mSecToTimeFormat(passedMSec);
        String interval = TimeUtils.mSecToTimeFormat(intervalMSec);

        LapTime lapTime = new LapTime(
                Constants.DATABASE.EMPTY_PARENT_ID,
                order,
                passedTime,
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
                TimeUtils.timeFormatToMSec(mStopwatchView.getTimeString()),
                Constants.DATABASE.METHOD_STOPWATCH,
                lapCount,
                mLapTimeListViewAdapter.getAllItems()
        );

        mPlankLogsRepository.savePlankLog(plankLog);

        if (lapCount > 0) {
            mPlankLogsRepository.saveLapTimes(plankLog.getId(), mLapTimeListViewAdapter.getAllItems());
        }
    }

    @Override
    public void start() {
        initStopwatchPresenter();
        initStopwatchView();
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
    public void stopwatchStart(int caller) {
        if (caller == Constants.CALLER.FROM_STOPWATCH_FRAGMENT)
            mPlankService.timerTaskCommand(mMethod, Constants.SERVICE_WHAT.STOPWATCH_START);

        mStopwatchView.setOnOffButtonValue(mActivityContext.getString(R.string.plank_stopwatch_pause));
        mStopwatchView.setResetLapButtonValue(mActivityContext.getString(R.string.plank_stopwatch_lap));

        mLapTimeListViewAdapter.clear();
        mLapTimeListViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void stopwatchPause(int caller) {
        if (caller == Constants.CALLER.FROM_STOPWATCH_FRAGMENT)
            mPlankService.timerTaskCommand(mMethod, Constants.SERVICE_WHAT.STOPWATCH_PAUSE);

        mStopwatchView.setOnOffButtonValue(mActivityContext.getString(R.string.plank_stopwatch_resume));
        mStopwatchView.setResetLapButtonValue(mActivityContext.getString(R.string.plank_stopwatch_reset));
    }

    @Override
    public void stopwatchResume(int caller) {
        if (caller == Constants.CALLER.FROM_STOPWATCH_FRAGMENT)
            mPlankService.timerTaskCommand(mMethod, Constants.SERVICE_WHAT.STOPWATCH_RESUME);

        mStopwatchView.setOnOffButtonValue(mActivityContext.getString(R.string.plank_stopwatch_pause));
        mStopwatchView.setResetLapButtonValue(mActivityContext.getString(R.string.plank_stopwatch_lap));
    }

    @Override
    public void stopwatchReset(int caller) {
        if (TimeUtils.timeFormatToMSec(mStopwatchView.getTimeString()) > 0) {
            // 초기화 시 플랭크 기록 데이터베이스에 저장
            savePlankLogData();
        }

        if (caller == Constants.CALLER.FROM_STOPWATCH_FRAGMENT)
            mPlankService.timerTaskCommand(mMethod, Constants.SERVICE_WHAT.STOPWATCH_RESET);

        mStopwatchView.setOnOffButtonValue(mActivityContext.getString(R.string.plank_stopwatch_on));
        mStopwatchView.setResetLapButtonValue(mActivityContext.getString(R.string.plank_stopwatch_reset));

        if (TimeUtils.timeFormatToMSec(mStopwatchView.getTimeString()) == 0) {
            mLapTimeListViewAdapter.clear();
            mLapTimeListViewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void stopwatchLap(int caller) {
        if (caller == Constants.CALLER.FROM_STOPWATCH_FRAGMENT)
            mPlankService.timerTaskCommand(mMethod, Constants.SERVICE_WHAT.STOPWATCH_LAP);
    }

    @Override
    public void updateWidgetsOnFragment(long mSec) {

        String timeFormat = TimeUtils.mSecToTimeFormat(mSec);
        String[] timeFormatSplit = timeFormat.split(":");

        final int resultHour = Integer.valueOf(timeFormatSplit[0]);
        final int resultMin = Integer.valueOf(timeFormatSplit[1]);
        final int resultSec = Integer.valueOf(timeFormatSplit[2].split("\\.")[0]);
        final int resultMSec = Integer.valueOf(timeFormatSplit[2].split("\\.")[1]);

        ((Activity) mActivityContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mStopwatchView.setHour(String.format(Locale.getDefault(), "%02d", resultHour));
                mStopwatchView.setMin(String.format(Locale.getDefault(), "%02d", resultMin));
                mStopwatchView.setSec(String.format(Locale.getDefault(), "%02d", resultSec));
                mStopwatchView.setMSec(String.format(Locale.getDefault(), "%03d", resultMSec));
            }
        });
    }

    @Override
    public void appExit(int caller) {
        ActivityCompat.finishAffinity((Activity) mActivityContext);
        System.exit(0);
    }

    // It's OK for this class not to be static and to keep a reference to the Presenter, as this
    // is retained during orientation changes and is lightweight (has no activity/view reference)
    private class LoadDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            SystemClock.sleep(3000);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mIsLoadingData = false;
        }
    }
}
