package kr.kro.awesometic.plankhelper.statistics;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import kr.kro.awesometic.plankhelper.R;
import kr.kro.awesometic.plankhelper.data.source.PlankLogsRepository;
import kr.kro.awesometic.plankhelper.data.source.local.PlankLogsLocalDataSource;
import kr.kro.awesometic.plankhelper.statistics.calendar.CalendarFragment;
import kr.kro.awesometic.plankhelper.statistics.calendar.CalendarPresenter;
import kr.kro.awesometic.plankhelper.statistics.chart.ChartFragment;
import kr.kro.awesometic.plankhelper.statistics.chart.ChartPresenter;
import kr.kro.awesometic.plankhelper.statistics.log.LogFragment;
import kr.kro.awesometic.plankhelper.statistics.log.LogPresenter;

/**
 * Created by Awesometic on 2017-05-17.
 */

// TODO add filter to select date range to toolbar when users are going to chart fragment

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    private final int mPageCount;
    private final String[] mTabTitles;

    private CalendarFragment mCalendarFragment;
    private ChartFragment mChartFragment;
    private LogFragment mLogFragment;

    private CalendarPresenter mCalendarPresenter;
    private ChartPresenter mChartPresenter;
    private LogPresenter mLogPresenter;

    public ViewPagerAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);

        mContext = context;

        mPageCount = 3;
        mTabTitles = new String[] {
                mContext.getResources().getString(R.string.statistics_calendar_title),
                mContext.getResources().getString(R.string.statistics_chart_title),
                mContext.getResources().getString(R.string.statistics_log_title)
        };

        if (mCalendarFragment == null) {
            mCalendarFragment = CalendarFragment.newInstance();
        }
        
        if (mChartFragment == null) {
            mChartFragment = ChartFragment.newInstance();
        }
        
        if (mLogFragment == null) {
            mLogFragment = LogFragment.newInstance();
        }

        mCalendarPresenter = new CalendarPresenter(
                PlankLogsRepository.getInstance(
                        PlankLogsLocalDataSource.getInstance(mContext)),
                mCalendarFragment);
        mChartPresenter = new ChartPresenter(
                PlankLogsRepository.getInstance(
                        PlankLogsLocalDataSource.getInstance(mContext)),
                mChartFragment);
        mLogPresenter = new LogPresenter(
                PlankLogsRepository.getInstance(
                        PlankLogsLocalDataSource.getInstance(mContext)),
                mLogFragment);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitles[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return mCalendarFragment;
            case 1:
                return mChartFragment;
            case 2:
                return mLogFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mPageCount;
    }
}
