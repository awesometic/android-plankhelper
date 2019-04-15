package kr.kro.awesometic.plankhelper.plank;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import kr.kro.awesometic.plankhelper.R;
import kr.kro.awesometic.plankhelper.data.source.PlankLogsRepository;
import kr.kro.awesometic.plankhelper.data.source.local.PlankLogsLocalDataSource;
import kr.kro.awesometic.plankhelper.plank.stopwatch.StopwatchFragment;
import kr.kro.awesometic.plankhelper.plank.stopwatch.StopwatchPresenter;
import kr.kro.awesometic.plankhelper.plank.timer.TimerFragment;
import kr.kro.awesometic.plankhelper.plank.timer.TimerPresenter;

/**
 * Created by Awesometic on 2017-04-19.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    private final int mPageCount;
    private final String[] mTabTitles;

    private StopwatchFragment mStopwatchFragment;
    private TimerFragment mTimerFragment;

    private PlankServiceManager mPlankServiceManager;

    public ViewPagerAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);

        mContext = context;

        mPageCount = 2;
        mTabTitles = new String[] {
                mContext.getResources().getString(R.string.plank_stopwatch_title),
                mContext.getResources().getString(R.string.plank_timer_title)
        };

        if (mStopwatchFragment == null) {
            mStopwatchFragment = StopwatchFragment.newInstance();
        }

        if (mTimerFragment == null) {
            mTimerFragment = TimerFragment.newInstance();
        }

        mPlankServiceManager = new PlankServiceManager(
                new StopwatchPresenter(
                        PlankLogsRepository.getInstance(
                                PlankLogsLocalDataSource.getInstance(mContext)),
                        mStopwatchFragment
                ),
                new TimerPresenter(
                        PlankLogsRepository.getInstance(
                                PlankLogsLocalDataSource.getInstance(mContext)),
                        mTimerFragment
                ),
                mContext
        );
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitles[position];
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return mStopwatchFragment;
            case 1:
                return mTimerFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mPageCount;
    }

    public PlankServiceManager getPlankServiceManager() {
        return mPlankServiceManager;
    }
}
