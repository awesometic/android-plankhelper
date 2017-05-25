package kr.kro.awesometic.plankhelper.plank.timer;

import kr.kro.awesometic.plankhelper.BasePresenter;
import kr.kro.awesometic.plankhelper.BaseView;

/**
 * Created by Awesometic on 2017-04-19.
 */

public interface TimerContract {

    interface View extends BaseView<Presenter> {

        Object getActivityContext();

        void setLapTimeAdapter(Object lapTimeAdapter);

        String getTimeString();
        void numberPickerChangeValueByOne(int type, boolean increment);
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

        void bindPlankService();
        void unbindPlankService();

        void timerStart(int caller);
        void timerPause(int caller);
        void timerResume(int caller);
        void timerReset(int caller);
        void timerLap(int caller);
        void appExit(int caller);

        void updateWidgetsOnFragment(long mSec);
    }
}
