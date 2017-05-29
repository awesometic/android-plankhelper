package kr.kro.awesometic.plankhelper.plank.stopwatch;

import kr.kro.awesometic.plankhelper.BasePresenter;
import kr.kro.awesometic.plankhelper.BaseView;

/**
 * Created by Awesometic on 2017-04-19.
 */

public interface StopwatchContract {

    interface View extends BaseView<Presenter> {

        void showLoading();
        void showStopwatch();

        Object getActivityContext();

        void setRecyclerViewAdapter(Object recyclerViewAdapter);
        void setLapTimeAdapter(Object lapTimeAdapter);

        void bindViewsFromViewHolder();

        String getTimeString();
        void setHour(String hour);
        void setMin(String min);
        void setSec(String sec);
        void setMSec(String mSec);

        String getOnOffButtonValue();
        String getResetLapButtonValue();

        void setOnOffButtonValue(String value);
        void setResetLapButtonValue(String value);

        void setOnOffButtonEnabled(boolean isEnabled);
        void setResetLapButtonEnabled(boolean isEnabled);
    }

    interface Presenter extends BasePresenter {

        void bindViewsFromViewHolderToFrag();
        void bindPlankService();
        void unbindPlankService();

        void stopwatchStart(int caller);
        void stopwatchPause(int caller);
        void stopwatchResume(int caller);
        void stopwatchReset(int caller);
        void stopwatchLap(int caller);
        void appExit(int caller);

        void updateWidgetsOnFragment(long mSec);
    }
}
