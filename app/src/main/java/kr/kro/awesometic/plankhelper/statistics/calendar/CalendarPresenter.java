package kr.kro.awesometic.plankhelper.statistics.calendar;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.google.common.base.Objects;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import kr.kro.awesometic.plankhelper.data.PlankLog;
import kr.kro.awesometic.plankhelper.data.source.PlankLogsDataSource;
import kr.kro.awesometic.plankhelper.data.source.PlankLogsRepository;
import kr.kro.awesometic.plankhelper.util.Constants;
import kr.kro.awesometic.plankhelper.util.Singleton;
import kr.kro.awesometic.plankhelper.util.TimeUtils;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Awesometic on 2017-05-17.
 */

public class CalendarPresenter implements CalendarContract.Presenter {

    private final PlankLogsRepository mPlankLogsRepository;
    private final CalendarContract.View mCalendarView;

    private Singleton mSingleton = Singleton.getInstance();

    private RecyclerViewAdapter mRecyclerViewAdapter;
    private Context mActivityContext;

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
        mActivityContext = (Context) mCalendarView.getActivityContext();
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

        String firstPlankDatetime = mSingleton.getFirstPlankDatetime();
        Calendar thisMonthCalendar = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());

        mCalendarView.setCalendarMaxDate(
                thisMonthCalendar.get(Calendar.YEAR),
                thisMonthCalendar.get(Calendar.MONTH) + 1,
                thisMonthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        );

        if (firstPlankDatetime.equals(Constants.SINGLETON.NEVER_PERFORMED)) {
            mCalendarView.setCalendarMinDate(
                    thisMonthCalendar.get(Calendar.YEAR),
                    thisMonthCalendar.get(Calendar.MONTH) + 1,
                    thisMonthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            );
        } else {
            String[] firstPlankDatetimeSplit = firstPlankDatetime.split(" ")[0].split("-");
            int year = Integer.valueOf(firstPlankDatetimeSplit[0]);
            int month = Integer.valueOf(firstPlankDatetimeSplit[1]);

            mCalendarView.setCalendarMinDate(year, month, 1);
        }
    }

    @Override
    public void onMonthChanged(Object calendarDay) {
        final CalendarDay currentCalendarDay = (CalendarDay) calendarDay;

        mPlankLogsRepository.getPlankLogs(new PlankLogsDataSource.LoadPlankLogsCallback() {
            @Override
            public void onPlankLogsLoaded(List<PlankLog> plankLogs) {
                NotPlankDayDisableDecorator decorator = new NotPlankDayDisableDecorator();

                Calendar calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
                int plankMonth, thisMonth;
                for (PlankLog plankLog : plankLogs) {
                    calendar.setTime(new Date(plankLog.getDatetimeMSec()));

                    plankMonth = calendar.get(Calendar.MONTH);
                    thisMonth = currentCalendarDay.getMonth();
                    if (plankMonth == thisMonth)
                        decorator.setPlankDay(calendar.get(Calendar.DATE));
                }

                mCalendarView.addCalendarDecorator(decorator);
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }
}
