package kr.kro.awesometic.plankhelper.statistics.calendar;

import kr.kro.awesometic.plankhelper.BasePresenter;
import kr.kro.awesometic.plankhelper.BaseView;

/**
 * Created by Awesometic on 2017-05-17.
 */

public interface CalendarContract {

    interface View extends BaseView<Presenter> {

        void showLoading();
        void showCalendar();

        Object getActivityContext();
        void setRecyclerViewAdapter(Object recyclerViewAdapter);

        void setCalendarMinDate(int year, int month, int date);
        void setCalendarMaxDate(int year, int month, int date);

        void addCalendarDecorator(Object decorator);

        void bindViewsFromViewHolder();
    }

    interface Presenter extends BasePresenter {

        void bindViewsFromViewHolderToFrag();

        void onMonthChanged(Object calendarDay);
    }
}
