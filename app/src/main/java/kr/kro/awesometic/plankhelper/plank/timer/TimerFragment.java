package kr.kro.awesometic.plankhelper.plank.timer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ViewAnimator;

import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.kro.awesometic.plankhelper.R;
import kr.kro.awesometic.plankhelper.plank.LapTimeListViewAdapter;
import kr.kro.awesometic.plankhelper.util.Constants;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Awesometic on 2017-04-19.
 */

public class TimerFragment extends Fragment implements TimerContract.View {

    private static final int ANIMATOR_POSITION_LIST = 0;
    private static final int ANIMATOR_POSITION_LOADING = 1;

    private TimerContract.Presenter mPresenter;
    private Context mContext;

    @BindView(R.id.plank_timer_frag_animator)
    ViewAnimator mViewAnimator;

    // ButterKnife 가 아니라 mViewAnimator 에 의해 초기화 됨
    private RecyclerView mRecyclerView;

    private LapTimeListViewAdapter mLapTimeListViewAdapter;

    private RecyclerView.Adapter mRecyclerViewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private boolean mIsViewsBound;
    private NumberPicker npHour;
    private NumberPicker npMin;
    private NumberPicker npSec;
    private ListView lvLapTime;
    private Button btnOnOff;
    private Button btnResetLap;

    public TimerFragment() {

    }

    public static TimerFragment newInstance() {
        return new TimerFragment();
    }

    @Override
    public void setPresenter(@NonNull TimerContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity().getApplicationContext();
        mIsViewsBound = false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.plank_timer_frag, container, false);
        ButterKnife.bind(this, rootView);

        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView = (RecyclerView) mViewAnimator.getChildAt(ANIMATOR_POSITION_LIST);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        mPresenter.start();
        mPresenter.bindPlankService();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        mPresenter.appExit(Constants.CALLER.FROM_TIMER_FRAGMENT);
        mPresenter.unbindPlankService();

        super.onDestroy();
    }

    @Override
    public void showLoading() {
        mViewAnimator.setDisplayedChild(ANIMATOR_POSITION_LOADING);
    }

    @Override
    public void showTimer() {
        mViewAnimator.setDisplayedChild(ANIMATOR_POSITION_LIST);
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
    public void setRecyclerViewAdapter(Object recyclerViewAdapter) {
        mRecyclerViewAdapter = (RecyclerViewAdapter) recyclerViewAdapter;
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addItemDecoration(new MaterialViewPagerHeaderDecorator());

        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!mIsViewsBound) {
                    mPresenter.bindViewsFromViewHolderToFrag();

                    mIsViewsBound = true;
                }
            }
        });
    }

    @Override
    public void bindViewsFromViewHolder() {
        RecyclerViewAdapter.ViewHolder holder = (RecyclerViewAdapter.ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(0);

        if (holder.getItemViewType() == Constants.RECYCLERVIEW_ADAPTER_VIEWTYPE.TYPE_HEAD) {

            npHour = holder.npHour;
            npMin = holder.npMin;
            npSec = holder.npSec;
            lvLapTime = holder.lvLapTime;
            btnOnOff = holder.btnOnOff;
            btnResetLap = holder.btnResetLap;

            npHour.setMaxValue(10);
            npMin.setMaxValue(59);
            npSec.setMaxValue(59);

            npHour.setMinValue(0);
            npMin.setMinValue(0);
            npSec.setMinValue(0);

            setDividerColor(npHour, ContextCompat.getColor(mContext, R.color.numberPickerDivider));
            setDividerColor(npMin, ContextCompat.getColor(mContext, R.color.numberPickerDivider));
            setDividerColor(npSec, ContextCompat.getColor(mContext, R.color.numberPickerDivider));

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

            lvLapTime.setAdapter(mLapTimeListViewAdapter);
            lvLapTime.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        }
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

    /** https://stackoverflow.com/questions/24233556/changing-numberpicker-divider-color
     * */
    private void setDividerColor(NumberPicker picker, int color) {

        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (java.lang.reflect.Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    ColorDrawable colorDrawable = new ColorDrawable(color);
                    pf.set(picker, colorDrawable);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}
