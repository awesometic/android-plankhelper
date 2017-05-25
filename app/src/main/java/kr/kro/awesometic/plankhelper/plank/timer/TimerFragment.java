package kr.kro.awesometic.plankhelper.plank.timer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import kr.kro.awesometic.plankhelper.R;
import kr.kro.awesometic.plankhelper.plank.LapTimeListViewAdapter;
import kr.kro.awesometic.plankhelper.util.Constants;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Awesometic on 2017-04-19.
 */

public class TimerFragment extends Fragment implements TimerContract.View {

    private TimerContract.Presenter mPresenter;
    
    private NumberPicker npHour;
    private NumberPicker npMin;
    private NumberPicker npSec;
    private ListView lvLapTime;
    private Button btnOnOff;
    private Button btnResetLap;
    
    private LapTimeListViewAdapter mLapTimeListViewAdapter;

    public TimerFragment() {

    }

    public static TimerFragment newInstance() {
        return new TimerFragment();
    }

    @Override
    public void setPresenter(@NonNull TimerContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.plank_timer_frag, container, false);
        
        npHour = (NumberPicker) rootView.findViewById(R.id.numberPicker_hour);
        npMin = (NumberPicker) rootView.findViewById(R.id.numberPicker_min);
        npSec = (NumberPicker) rootView.findViewById(R.id.numberPicker_sec);
        lvLapTime = (ListView) rootView.findViewById(R.id.listView_timer_lap);
        btnOnOff = (Button) rootView.findViewById(R.id.button_timer_on_off);
        btnResetLap = (Button) rootView.findViewById(R.id.button_timer_reset_lap);

        npHour.setMaxValue(99);
        npMin.setMaxValue(59);
        npSec.setMaxValue(59);

        npHour.setMinValue(0);
        npMin.setMinValue(0);
        npSec.setMinValue(0);

        npHour.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format(Locale.getDefault(), "%02d", value);
            }
        });
        npMin.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format(Locale.getDefault(), "%02d", value);
            }
        });
        npSec.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format(Locale.getDefault(), "%02d", value);
            }
        });

        npHour.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        npMin.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        npSec.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        btnOnOff.setOnClickListener(btnOnOffOnClickListener);
        btnResetLap.setOnClickListener(btnResetLapOnClickListener);
        
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        mPresenter.start();
        lvLapTime.setAdapter(mLapTimeListViewAdapter);
        lvLapTime.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        mPresenter.bindPlankService();
    }

    @Override
    public void onDestroy() {
        mPresenter.appExit(Constants.CALLER.FROM_TIMER_FRAGMENT);
        mPresenter.unbindPlankService();
        
        super.onDestroy();
    }

    @Override
    public Object getActivityContext() {
        return getActivity();
    }

    @Override
    public void setLapTimeAdapter(Object lapTimeAdapter) {
        mLapTimeListViewAdapter = (LapTimeListViewAdapter) lapTimeAdapter;
    }

    @Override
    public String getTimeString() {
        return String.valueOf(npHour.getValue()) + ":" +
                String.valueOf(npMin.getValue()) + ":" +
                String.valueOf(npSec.getValue()) + ".000";
    }

    @Override
    public void numberPickerChangeValueByOne(int type, boolean increment) {

        switch (type) {
            case Constants.NUMBERPICKER_TYPE.HOUR:
                changeValueByOne(npHour, increment);
                break;
            case Constants.NUMBERPICKER_TYPE.MIN:
                changeValueByOne(npMin, increment);
                break;
            case Constants.NUMBERPICKER_TYPE.SEC:
                changeValueByOne(npSec, increment);
                break;
        }
    }

    @Override
    public void setHour(int hour) {
        npHour.setValue(hour);
    }

    @Override
    public void setMin(int min) {
        npMin.setValue(min);
    }

    @Override
    public void setSec(int sec) {
        npSec.setValue(sec);
    }

    @Override
    public String getOnOffButtonValue() {
        return btnOnOff.getText().toString();
    }

    @Override
    public String getResetLapButtonValue() {
        return btnResetLap.getText().toString();
    }

    @Override
    public void setOnOffButtonValue(String value) {
        btnOnOff.setText(value);
    }

    @Override
    public void setResetLapButtonValue(String value) {
        btnResetLap.setText(value);
    }

    @Override
    public void setAllNumberPickersEnabled(boolean isEnabled) {
        npHour.setEnabled(isEnabled);
        npMin.setEnabled(isEnabled);
        npSec.setEnabled(isEnabled);
    }

    @Override
    public void setOnOffButtonEnabled(boolean isEnabled) {
        btnOnOff.setEnabled(isEnabled);
    }

    @Override
    public void setResetLapButtonEnabled(boolean isEnabled) {
        btnResetLap.setEnabled(isEnabled);
    }

    /** http://stackoverflow.com/questions/13500852/how-to-change-numberpickers-value-with-animation
     * using reflection to change the value because
     * changeValueByOne is a private function and setValue
     * doesn't call the onValueChange listener.
     *
     * @param higherPicker
     *            the higher picker
     * @param increment
     *            the increment
     */
    private void changeValueByOne(final NumberPicker higherPicker, final boolean increment) {

        Method method;
        try {
            // reflection call for
            // higherPicker.changeValueByOne(true);
            method = higherPicker.getClass().getDeclaredMethod("changeValueByOne", boolean.class);
            method.setAccessible(true);
            method.invoke(higherPicker, increment);

        } catch (final NoSuchMethodException e) {
            e.printStackTrace();
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final IllegalAccessException e) {
            e.printStackTrace();
        } catch (final InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    Button.OnClickListener btnOnOffOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getOnOffButtonValue().equals(getString(R.string.plank_timer_on))) {
                mPresenter.timerStart(Constants.CALLER.FROM_TIMER_FRAGMENT);
            } else if (getOnOffButtonValue().equals(getString(R.string.plank_timer_pause))) {
                mPresenter.timerPause(Constants.CALLER.FROM_TIMER_FRAGMENT);
            } else if (getOnOffButtonValue().equals(getString(R.string.plank_timer_resume))) {
                mPresenter.timerResume(Constants.CALLER.FROM_TIMER_FRAGMENT);
            }
        }
    };

    Button.OnClickListener btnResetLapOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getResetLapButtonValue().equals(getString(R.string.plank_timer_reset))) {
                mPresenter.timerReset(Constants.CALLER.FROM_TIMER_FRAGMENT);
            } else {
                mPresenter.timerLap(Constants.CALLER.FROM_TIMER_FRAGMENT);
            }
        }
    };
}
