package kr.kro.awesometic.plankhelper.statistics.calendar;

import android.content.Context;
import android.support.annotation.NonNull;

import kr.kro.awesometic.plankhelper.data.source.PlankLogsRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Awesometic on 2017-05-17.
 */

public class CalendarPresenter implements CalendarContract.Presenter {

    private final PlankLogsRepository mPlankLogsRepository;
    private final CalendarContract.View mCalendarView;

    private RecyclerViewAdapter mRecyclerViewAdapter;
    private Context mApplicationContext;

    public CalendarPresenter(@NonNull PlankLogsRepository plankLogsRepository,
                              @NonNull CalendarContract.View stopwatchView) {
        mPlankLogsRepository = checkNotNull(plankLogsRepository, "plankLogsRepository cannot be null");
        mCalendarView = checkNotNull(stopwatchView, "calendarView cannot be null");

        mCalendarView.setPresenter(this);
    }

    @Override
    public void start() {
        initPresenter();
        initView();
    }

    private void initPresenter() {
        mApplicationContext = (Context) mCalendarView.getApplicationContext();
    }

    private void initView() {
        mCalendarView.showLoading();

        mRecyclerViewAdapter = new RecyclerViewAdapter();
        mCalendarView.setRecyclerViewAdapter(mRecyclerViewAdapter);

        mRecyclerViewAdapter.setPlankLogs(null);

        mCalendarView.showCalendar();
    }

    @Override
    public void bindViewsFromViewHolderToFrag() {
        mCalendarView.bindViewsFromViewHolder();
    }
}
