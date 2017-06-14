package kr.kro.awesometic.plankhelper.statistics.log;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import kr.kro.awesometic.plankhelper.R;
import kr.kro.awesometic.plankhelper.data.LapTime;
import kr.kro.awesometic.plankhelper.data.PlankLog;
import kr.kro.awesometic.plankhelper.data.source.PlankLogsDataSource;
import kr.kro.awesometic.plankhelper.data.source.PlankLogsRepository;
import kr.kro.awesometic.plankhelper.plank.stopwatch.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Awesometic on 2017-05-17.
 */

public class LogPresenter implements LogContract.Presenter {

    private final PlankLogsRepository mPlankLogsRepository;
    private final LogContract.View mLogView;

    private RecyclerViewAdapter mRecyclerViewAdapter;
    private Context mActivityContext;

    public LogPresenter(@NonNull PlankLogsRepository plankLogsRepository,
                             @NonNull LogContract.View stopwatchView) {
        mPlankLogsRepository = checkNotNull(plankLogsRepository, "plankLogsRepository cannot be null");
        mLogView = checkNotNull(stopwatchView, "logView cannot be null");

        mLogView.setPresenter(this);
    }

    @Override
    public void start() {
        initPresenter();
        initView();

        loadAllPlankLogs();
    }

    private void initPresenter() {
        mActivityContext = (Context) mLogView.getActivityContext();
    }

    private void initView() {
        mLogView.showLoading();

        mRecyclerViewAdapter = new RecyclerViewAdapter();
        mLogView.setRecyclerViewAdapter(mRecyclerViewAdapter);

        mRecyclerViewAdapter.setPlankLogs(null);

        mLogView.showLog();
    }

    private void loadAllPlankLogs() {
        mPlankLogsRepository.getPlankLogs(new PlankLogsDataSource.LoadPlankLogsCallback() {
            @Override
            public void onPlankLogsLoaded(List<PlankLog> plankLogs) {
                for (final PlankLog plankLog : plankLogs) {

                    mPlankLogsRepository.getLapTimes(plankLog.getId(), new PlankLogsDataSource.LoadLapTimesCallback() {
                        @Override
                        public void onLapTimesLoaded(List<LapTime> lapTimes) {
                            plankLog.setLapTimes((ArrayList<LapTime>) lapTimes);

                            mRecyclerViewAdapter.addPlankLog(plankLog);
                            mRecyclerViewAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onDataNotAvailable() {
                            mRecyclerViewAdapter.addPlankLog(plankLog);
                            mRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }

            @Override
            public void onDataNotAvailable() {
                
            }
        });
    }

    @Override
    public void bindViewsFromViewHolderToFrag() {
        mLogView.bindViewsFromViewHolder();
    }
}