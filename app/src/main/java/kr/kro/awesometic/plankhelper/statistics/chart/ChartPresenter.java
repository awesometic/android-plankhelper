package kr.kro.awesometic.plankhelper.statistics.chart;

import android.content.Context;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import kr.kro.awesometic.plankhelper.data.PlankLog;
import kr.kro.awesometic.plankhelper.data.source.PlankLogsDataSource;
import kr.kro.awesometic.plankhelper.data.source.PlankLogsRepository;

import static androidx.core.util.Preconditions.checkNotNull;

/**
 * Created by Awesometic on 2017-05-17.
 */

public class ChartPresenter implements ChartContract.Presenter {

    private final PlankLogsRepository mPlankLogsRepository;
    private final ChartContract.View mChartView;

    private RecyclerViewAdapter mRecyclerViewAdapter;
    private Context mApplicationContext;

    public ChartPresenter(@NonNull PlankLogsRepository plankLogsRepository,
                             @NonNull ChartContract.View stopwatchView) {
        mPlankLogsRepository = checkNotNull(plankLogsRepository, "plankLogsRepository cannot be null");
        mChartView = checkNotNull(stopwatchView, "chartView cannot be null");

        mChartView.setPresenter(this);
    }

    @Override
    public void start() {
        initPresenter();
        initView();
    }

    private void initPresenter() {
        mApplicationContext = (Context) mChartView.getApplicationContext();
    }

    private void initView() {
        mChartView.showLoading();

        mRecyclerViewAdapter = new RecyclerViewAdapter();
        mChartView.setRecyclerViewAdapter(mRecyclerViewAdapter);

        mPlankLogsRepository.getPlankLogs(new PlankLogsDataSource.LoadPlankLogsCallback() {
            @Override
            public void onPlankLogsLoaded(List<PlankLog> plankLogs) {
                mRecyclerViewAdapter.setPlankLogs((ArrayList<PlankLog>) plankLogs);
                mRecyclerViewAdapter.notifyDataSetChanged();

                mChartView.showChart();
            }

            @Override
            public void onDataNotAvailable() {
                mRecyclerViewAdapter.setPlankLogs(null);

                mChartView.showChart();
            }
        });
    }

    @Override
    public void bindViewsFromViewHolderToFrag() {
        mChartView.bindViewsFromViewHolder();
    }
}