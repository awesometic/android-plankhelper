package kr.kro.awesometic.plankhelper.statistics.chart;

import android.graphics.Color;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import kr.kro.awesometic.plankhelper.data.PlankLog;
import kr.kro.awesometic.plankhelper.data.source.PlankLogsDataSource;
import kr.kro.awesometic.plankhelper.data.source.PlankLogsRepository;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Awesometic on 2017-05-17.
 */

public class ChartPresenter implements ChartContract.Presenter {

    private final PlankLogsRepository mPlankLogsRepository;
    private final ChartContract.View mChartView;

    private RecyclerViewAdapter mRecyclerViewAdapter;

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
        mPlankLogsRepository.getPlankLogs(new PlankLogsDataSource.LoadPlankLogsCallback() {
            @Override
            public void onPlankLogsLoaded(List<PlankLog> plankLogs) {
                mRecyclerViewAdapter = new RecyclerViewAdapter((ArrayList<PlankLog>) plankLogs);
            }

            @Override
            public void onDataNotAvailable() {
                mRecyclerViewAdapter = new RecyclerViewAdapter(null);
            }
        });
    }

    private void initView() {
        mChartView.setRecyclerViewAdapter(mRecyclerViewAdapter);
    }
}