package kr.kro.awesometic.plankhelper.plank.stopwatch;

import kr.kro.awesometic.plankhelper.BasePresenter;
import kr.kro.awesometic.plankhelper.BaseView;

/**
 * Created by Awesometic on 2017-04-19.
 */

public interface StopwatchContract {

    interface BoundViewsCallback {
        void onBoundViews();
    }

    interface View extends BaseView<Presenter> {

        void showLoading();
        void showStopwatch();

        Object getActivityContext();

        void setRecyclerViewAdapter(Object recyclerViewAdapter);
        void setLapTimeAdapter(Object lapTimeAdapter);

        void bindViewsFromViewHolder(BoundViewsCallback boundViewsCallback);

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

        void controlFromFrag(int what);

        boolean getStopwatchStart();
    }
}
