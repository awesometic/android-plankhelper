package kr.kro.awesometic.plankhelper.plank.stopwatch;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import java.util.Locale;

import kr.kro.awesometic.plankhelper.R;
import kr.kro.awesometic.plankhelper.data.LapTime;
import kr.kro.awesometic.plankhelper.data.PlankLog;
import kr.kro.awesometic.plankhelper.data.source.PlankLogsRepository;
import kr.kro.awesometic.plankhelper.plank.LapTimeListViewAdapter;
import kr.kro.awesometic.plankhelper.util.Constants;
import kr.kro.awesometic.plankhelper.util.TimeUtils;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Awesometic on 2017-04-19.
 */

public class StopwatchPresenter implements StopwatchContract.Presenter {

    private final PlankLogsRepository mPlankLogsRepository;
    private final StopwatchContract.View mStopwatchView;

    public interface IStopwatchPresenterCallback {
        void stopwatchCommandToService(int method, int what);
    }
    private IStopwatchPresenterCallback mStopwatchPresenterCallback;
    private final int mMethod = Constants.WORK_METHOD.STOPWATCH;

    private LapTimeListViewAdapter mLapTimeListViewAdapter;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private Context mActivityContext;

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
        mStopwatchView.showLoading();

        mLapTimeListViewAdapter = new LapTimeListViewAdapter();
        mRecyclerViewAdapter = new RecyclerViewAdapter();

        mStopwatchView.setLapTimeAdapter(mLapTimeListViewAdapter);
        mStopwatchView.setRecyclerViewAdapter(mRecyclerViewAdapter);

        mRecyclerViewAdapter.setPlankLogs(null);

        mStopwatchView.showStopwatch();
    }

    public void registerCallback(IStopwatchPresenterCallback callback) {
        mStopwatchPresenterCallback = callback;
    }

    @Override
    public void start() {
        initStopwatchPresenter();
        initStopwatchView();
    }

    @Override
    public void bindViewsFromViewHolderToFrag() {
        mStopwatchView.bindViewsFromViewHolder();
        mStopwatchView.setOnOffButtonValue(mActivityContext.getString(R.string.plank_stopwatch_on));
        mStopwatchView.setResetLapButtonValue(mActivityContext.getString(R.string.plank_stopwatch_reset));
    }

    @Override
    public void controlFromFrag(int what) {
        mStopwatchPresenterCallback.stopwatchCommandToService(mMethod, what);
        stopwatchControl(what);
    }

    public void controlFromService(int what) {
        stopwatchControl(what);
    }

    private void stopwatchControl(final int what) {
        ((Activity) mActivityContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (what) {
                    case Constants.SERVICE_WHAT.STOPWATCH_START:
                        mStopwatchView.setOnOffButtonValue(mActivityContext.getString(R.string.plank_stopwatch_pause));
                        mStopwatchView.setResetLapButtonValue(mActivityContext.getString(R.string.plank_stopwatch_lap));

                        mLapTimeListViewAdapter.clear();
                        mLapTimeListViewAdapter.notifyDataSetChanged();
                        break;

                    case Constants.SERVICE_WHAT.STOPWATCH_PAUSE:
                        mStopwatchView.setOnOffButtonValue(mActivityContext.getString(R.string.plank_stopwatch_resume));
                        mStopwatchView.setResetLapButtonValue(mActivityContext.getString(R.string.plank_stopwatch_reset));
                        break;

                    case Constants.SERVICE_WHAT.STOPWATCH_RESUME:
                        mStopwatchPresenterCallback.stopwatchCommandToService(mMethod, Constants.SERVICE_WHAT.STOPWATCH_RESUME);

                        mStopwatchView.setOnOffButtonValue(mActivityContext.getString(R.string.plank_stopwatch_pause));
                        mStopwatchView.setResetLapButtonValue(mActivityContext.getString(R.string.plank_stopwatch_lap));
                        break;

                    case Constants.SERVICE_WHAT.STOPWATCH_RESET:
                        if (TimeUtils.timeFormatToMSec(mStopwatchView.getTimeString()) > 0) {
                            savePlankLogData();
                        }

                        mStopwatchView.setOnOffButtonValue(mActivityContext.getString(R.string.plank_stopwatch_on));
                        mStopwatchView.setResetLapButtonValue(mActivityContext.getString(R.string.plank_stopwatch_reset));

                        if (TimeUtils.timeFormatToMSec(mStopwatchView.getTimeString()) == 0) {
                            mLapTimeListViewAdapter.clear();
                            mLapTimeListViewAdapter.notifyDataSetChanged();
                        }
                        break;

                    default:
                        break;
                }
            }
        });
    }

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

    public void addLapTimeItem(long passedMSec, long intervalMSec) {
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

    public void setWidgetsEnabled(final boolean isEnabled) {
        ((Activity) mActivityContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mStopwatchView.setOnOffButtonEnabled(isEnabled);
                mStopwatchView.setResetLapButtonEnabled(isEnabled);
            }
        });
    }

    public int getLapCount() {
        return mLapTimeListViewAdapter.getCount();
    }

    public long getLastLapMSec() {
        if (mLapTimeListViewAdapter.getCount() > 0)
            return TimeUtils.timeFormatToMSec(
                    mLapTimeListViewAdapter.getItem(mLapTimeListViewAdapter.getCount() - 1).getPassedTime());
        else
            return 0;
    }
}
