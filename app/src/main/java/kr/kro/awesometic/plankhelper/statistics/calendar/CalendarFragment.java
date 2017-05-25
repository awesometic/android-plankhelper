package kr.kro.awesometic.plankhelper.statistics.calendar;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.kro.awesometic.plankhelper.R;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Awesometic on 2017-05-17.
 */

public class CalendarFragment extends Fragment
        implements CalendarContract.View, OnDateSelectedListener, OnMonthChangedListener {

    private CalendarContract.Presenter mPresenter;

    @BindView(R.id.statistics_calendar)
    MaterialCalendarView mMaterialCalendarView;

    public CalendarFragment() {

    }

    public static CalendarFragment newInstance() {
        return new CalendarFragment();
    }

    public void setPresenter(@NonNull CalendarContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.statistics_calendar_frag, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

    }
}
