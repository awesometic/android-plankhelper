package kr.kro.awesometic.plankhelper.plank.timer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

import static androidx.core.util.Preconditions.checkNotNull;

/**
 * Created by Awesometic on 2017-04-19.
 */

public class TimerFragment extends Fragment implements TimerContract.View {

    private TimerContract.Presenter mPresenter;
    private Context mContext;

    @BindView(R.id.plank_timer_frag_animator)
    ViewAnimator mViewAnimator;

    // ButterKnife 가 아니라 mViewAnimator 에 의해 초기화 됨
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private LapTimeListViewAdapter mLapTimeListViewAdapter;

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
        mRecyclerView = (RecyclerView) mViewAnimator.getChildAt(Constants.COMMON_ANIMATOR_POSITION.RECYCLERVIEW);
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
        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                if(rv.getChildCount() > 0) {
                    View childView = rv.findChildViewUnder(e.getX(), e.getY());

                    if (rv.getChildAdapterPosition(childView) == Constants.RECYCLERVIEW_ADAPTER_VIEWTYPE.TYPE_HEAD) {
                        if (mPresenter.getTimerStart()) {
                            switch (e.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    rv.requestDisallowInterceptTouchEvent(true);
                            }
                        }
                    }
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        mPresenter.start();
    }

    @Override
    public void onStop() {
        super.onStop();

        mIsViewsBound = false;
    }

    @Override
    public void showLoading() {
        mViewAnimator.setDisplayedChild(Constants.COMMON_ANIMATOR_POSITION.LOADING);
    }

    @Override
    public void showTimer() {
        mViewAnimator.setDisplayedChild(Constants.COMMON_ANIMATOR_POSITION.RECYCLERVIEW);
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
        mRecyclerView.setAdapter((RecyclerViewAdapter) recyclerViewAdapter);
    }

    @Override
    public void bindViewsFromViewHolder(TimerContract.BoundViewsCallback callback) {
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

            btnOnOff.setOnClickListener(btnOnOffOnClickListener);
            btnResetLap.setOnClickListener(btnResetLapOnClickListener);

            lvLapTime.setAdapter(mLapTimeListViewAdapter);
            lvLapTime.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

            callback.onBoundViews();
        }
    }

    @Override
    public String getTimeString() {
        String resultHour = (npHour.getValue() >= 10) ? "" + npHour.getValue() : "0" + npHour.getValue();
        String resultMin = (npMin.getValue() >= 10) ? "" + npMin.getValue() : "0" + npMin.getValue();
        String resultSec = (npSec.getValue() >= 10) ? "" + npSec.getValue() : "0" + npSec.getValue();
        
        return resultHour + ":" + resultMin + ":" + resultSec + ".000";
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
    public int getHour() {
        return npHour.getValue();
    }

    @Override
    public int getMin() {
        return npMin.getValue();
    }

    @Override
    public int getSec() {
        return npSec.getValue();
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
                mPresenter.controlFromFrag(Constants.SERVICE_WHAT.TIMER_START);
            } else if (getOnOffButtonValue().equals(getString(R.string.plank_timer_pause))) {
                mPresenter.controlFromFrag(Constants.SERVICE_WHAT.TIMER_PAUSE);
            } else if (getOnOffButtonValue().equals(getString(R.string.plank_timer_resume))) {
                mPresenter.controlFromFrag(Constants.SERVICE_WHAT.TIMER_RESUME);
            }
        }
    };

    Button.OnClickListener btnResetLapOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getResetLapButtonValue().equals(getString(R.string.plank_timer_reset))) {
                mPresenter.controlFromFrag(Constants.SERVICE_WHAT.TIMER_RESET);
            } else {
                mPresenter.controlFromFrag(Constants.SERVICE_WHAT.TIMER_LAP);
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
