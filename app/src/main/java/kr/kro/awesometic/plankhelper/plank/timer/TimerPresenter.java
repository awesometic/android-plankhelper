package kr.kro.awesometic.plankhelper.plank.timer;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import kr.kro.awesometic.plankhelper.R;
import kr.kro.awesometic.plankhelper.data.LapTime;
import kr.kro.awesometic.plankhelper.data.PlankLog;
import kr.kro.awesometic.plankhelper.data.source.PlankLogsDataSource;
import kr.kro.awesometic.plankhelper.data.source.PlankLogsRepository;
import kr.kro.awesometic.plankhelper.plank.LapTimeListViewAdapter;
import kr.kro.awesometic.plankhelper.util.Constants;
import kr.kro.awesometic.plankhelper.util.LogManager;
import kr.kro.awesometic.plankhelper.util.TimeUtils;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Awesometic on 2017-04-19.
 */

public class TimerPresenter implements TimerContract.Presenter {

    private final PlankLogsRepository mPlankLogsRepository;
    private final TimerContract.View mTimerView;

    public interface ITimerPresenterCallback {
        void timerCommandToService(int method, int what);
        long getTimerStartMSec();
        void updateWidgetsByCurrentState(int method);
    }
    private TimerPresenter.ITimerPresenterCallback mTimerPresenterCallback;
    private final int mMethod = Constants.WORK_METHOD.TIMER;

    private LapTimeListViewAdapter mLapTimeListViewAdapter;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private Context mActivityContext;

    private boolean mIsStart;

    public TimerPresenter(@NonNull PlankLogsRepository plankLogsRepository,
                          @NonNull TimerContract.View TimerView) {
        mPlankLogsRepository = checkNotNull(plankLogsRepository, "plankLogsRepository cannot be null");
        mTimerView = checkNotNull(TimerView, "TimerView cannot be null");

        mTimerView.setPresenter(this);
    }

    private void initTimerPresenter() {
        mActivityContext = (Context) mTimerView.getActivityContext();
        mIsStart = false;
    }

    private void initTimerView() {
        mTimerView.showLoading();

        if (mLapTimeListViewAdapter == null && mRecyclerViewAdapter == null) {
            mLapTimeListViewAdapter = new LapTimeListViewAdapter();
            mRecyclerViewAdapter = new RecyclerViewAdapter();
        }

        mTimerView.setLapTimeAdapter(mLapTimeListViewAdapter);
        mTimerView.setRecyclerViewAdapter(mRecyclerViewAdapter);

        mRecyclerViewAdapter.setPlankLogs(null);

        mTimerView.showTimer();
    }

    @Override
    public void start() {
        initTimerPresenter();
        initTimerView();
    }

    @Override
    public void bindViewsFromViewHolderToFrag() {
        mTimerView.bindViewsFromViewHolder(new TimerContract.BoundViewsCallback() {
            @Override
            public void onBoundViews() {
                mTimerPresenterCallback.updateWidgetsByCurrentState(mMethod);
            }
        });
    }

    @Override
    public void controlFromFrag(int what) {
        mTimerPresenterCallback.timerCommandToService(mMethod, what);
    }

    @Override
    public boolean getTimerStart() {
        return mIsStart;
    }

    public void controlFromService(final int what) {
        ((Activity) mActivityContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (what) {
                    case Constants.SERVICE_WHAT.TIMER_START:
                        mTimerView.setAllNumberPickersEnabled(false);
                        mTimerView.setOnOffButtonValue(mActivityContext.getString(R.string.plank_timer_pause));
                        mTimerView.setResetLapButtonValue(mActivityContext.getString(R.string.plank_timer_lap));

                        mIsStart = true;
                        break;

                    case Constants.SERVICE_WHAT.TIMER_PAUSE:
                        mTimerView.setOnOffButtonValue(mActivityContext.getString(R.string.plank_timer_resume));
                        mTimerView.setResetLapButtonValue(mActivityContext.getString(R.string.plank_timer_reset));
                        break;

                    case Constants.SERVICE_WHAT.TIMER_RESUME:
                        mTimerView.setOnOffButtonValue(mActivityContext.getString(R.string.plank_timer_pause));
                        mTimerView.setResetLapButtonValue(mActivityContext.getString(R.string.plank_timer_lap));
                        break;

                    case Constants.SERVICE_WHAT.TIMER_RESET:
                        updateWidgetsOnFragment(0);
                        mTimerView.setAllNumberPickersEnabled(true);
                        mTimerView.setOnOffButtonValue(mActivityContext.getString(R.string.plank_timer_on));
                        mTimerView.setResetLapButtonValue(mActivityContext.getString(R.string.plank_timer_reset));

                        mIsStart = false;
                        break;

                    default:
                        break;
                }
            }
        });
    }

    public void registerCallback(ITimerPresenterCallback callback) {
        mTimerPresenterCallback = callback;
    }

    public void updateWidgetsOnFragment(final long mSec) {
        String resultTimeFormat = TimeUtils.mSecToTimeFormat(mSec);
        String[] resultTimeFormatSplit = resultTimeFormat.split(":");

        final int resultHour = Integer.valueOf(resultTimeFormatSplit[0]);
        final int resultMin = Integer.valueOf(resultTimeFormatSplit[1]);
        final int resultSec = Integer.valueOf(resultTimeFormatSplit[2].split("\\.")[0]);

        final String currentTimeFormat = TimeUtils.mSecToTimeFormat(mSec + 1000);

        ((Activity) mActivityContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mTimerView.getTimeString().equals(currentTimeFormat)) {
                    if (mTimerView.getHour() > resultHour) {
                        mTimerView.numberPickerChangeValueByOne(Constants.NUMBERPICKER_TYPE.HOUR, false);
                        mTimerView.numberPickerChangeValueByOne(Constants.NUMBERPICKER_TYPE.MIN, false);
                        mTimerView.numberPickerChangeValueByOne(Constants.NUMBERPICKER_TYPE.SEC, false);
                    } else if (mTimerView.getMin() > resultMin) {
                        mTimerView.numberPickerChangeValueByOne(Constants.NUMBERPICKER_TYPE.MIN, false);
                        mTimerView.numberPickerChangeValueByOne(Constants.NUMBERPICKER_TYPE.SEC, false);
                    } else if ((mTimerView.getSec() > resultSec) && resultSec >= 0) {
                        mTimerView.numberPickerChangeValueByOne(Constants.NUMBERPICKER_TYPE.SEC, false);
                    }
                } else {
                    mTimerView.setHour(resultHour);
                    mTimerView.setMin(resultMin);
                    mTimerView.setSec(resultSec);
                }
            }
        });
    }

    public void addLapTimeItem(long passedMSec, long intervalMSec) {
        int order = mLapTimeListViewAdapter.getCount() + 1;
        String passedTime = TimeUtils.mSecToTimeFormat(passedMSec);
        String leftTime = TimeUtils.mSecToTimeFormat(mTimerPresenterCallback.getTimerStartMSec() - passedMSec);
        String interval = TimeUtils.mSecToTimeFormat(intervalMSec);

        final LapTime lapTime = new LapTime(
                Constants.DATABASE.EMPTY_PARENT_ID,
                order,
                passedTime,
                leftTime,
                interval
        );

        ((Activity) mActivityContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLapTimeListViewAdapter.addItem(lapTime);
                mLapTimeListViewAdapter.notifyDataSetChanged();
            }
        });
    }

    public void clearLapTimeItem() {
        ((Activity) mActivityContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLapTimeListViewAdapter.clear();
                mLapTimeListViewAdapter.notifyDataSetChanged();
            }
        });
    }

    public void savePlankLogData() {
        int lapCount = mLapTimeListViewAdapter.getCount();

        PlankLog plankLog = new PlankLog(
                TimeUtils.getCurrentDatetimeFormatted(),
                mTimerPresenterCallback.getTimerStartMSec(),
                Constants.DATABASE.METHOD_TIMER,
                lapCount,
                mLapTimeListViewAdapter.getAllItems()
        );

        mPlankLogsRepository.savePlankLog(plankLog, new PlankLogsDataSource.SavePlankLogCallback() {
            @Override
            public void onSavePlankLog(boolean isSuccess) {
                if (isSuccess) {
                    Toast.makeText(
                            mActivityContext,
                            mActivityContext.getString(R.string.plank_toast_save_planklog_success),
                            Toast.LENGTH_SHORT
                    ).show();
                } else {
                    Toast.makeText(
                            mActivityContext,
                            mActivityContext.getString(R.string.plank_toast_save_planklog_fail),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });

        if (lapCount > 0) {
            mPlankLogsRepository.saveLapTimes(plankLog.getId(), mLapTimeListViewAdapter.getAllItems());
        }
    }

    public void setWidgetsEnabled(final boolean isEnabled) {
        ((Activity) mActivityContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTimerView.setAllNumberPickersEnabled(isEnabled);
                mTimerView.setOnOffButtonEnabled(isEnabled);
                mTimerView.setResetLapButtonEnabled(isEnabled);
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

    public long getStartTimeMSec() {
        return TimeUtils.timeFormatToMSec(mTimerView.getTimeString());
    }
}
