package kr.kro.awesometic.plankhelper.plank.timer;

import kr.kro.awesometic.plankhelper.BasePresenter;
import kr.kro.awesometic.plankhelper.BaseView;

/**
 * Created by Awesometic on 2017-04-19.
 */

public interface TimerContract {

    interface BoundViewsCallback {
        void onBoundViews();
    }

    interface View extends BaseView<Presenter> {

        void showLoading();
        void showTimer();

        Object getActivityContext();

        void setLapTimeAdapter(Object lapTimeAdapter);
        void setRecyclerViewAdapter(Object recyclerViewAdapter);

        void bindViewsFromViewHolder(BoundViewsCallback boundViewsCallback);

        String getTimeString();
        void numberPickerChangeValueByOne(int type, boolean increment);

        int getHour();
        int getMin();
        int getSec();
        
        void setHour(int hour);
        void setMin(int min);
        void setSec(int sec);

        String getOnOffButtonValue();
        String getResetLapButtonValue();

        void setOnOffButtonValue(String value);
        void setResetLapButtonValue(String value);

        void setAllNumberPickersEnabled(boolean isEnabled);
        void setOnOffButtonEnabled(boolean isEnabled);
        void setResetLapButtonEnabled(boolean isEnabled);
    }

    interface Presenter extends BasePresenter {

        void bindViewsFromViewHolderToFrag();

        void controlFromFrag(int what);

        boolean getTimerStart();
    }
}
