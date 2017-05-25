package kr.kro.awesometic.plankhelper.statistics.calendar;

import android.support.annotation.NonNull;

import kr.kro.awesometic.plankhelper.data.source.PlankLogsRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Awesometic on 2017-05-17.
 */

public class CalendarPresenter implements CalendarContract.Presenter {

    private final PlankLogsRepository mPlankLogsRepository;
    private final CalendarContract.View mCalendarView;

    @Override
    public void start() {

    }

    public CalendarPresenter(@NonNull PlankLogsRepository plankLogsRepository,
                              @NonNull CalendarContract.View stopwatchView) {
        mPlankLogsRepository = checkNotNull(plankLogsRepository, "plankLogsRepository cannot be null");
        mCalendarView = checkNotNull(stopwatchView, "calendarView cannot be null");

        mCalendarView.setPresenter(this);
    }
}
