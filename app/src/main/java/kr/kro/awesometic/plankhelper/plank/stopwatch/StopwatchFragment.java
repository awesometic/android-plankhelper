package kr.kro.awesometic.plankhelper.plank.stopwatch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.kro.awesometic.plankhelper.R;
import kr.kro.awesometic.plankhelper.plank.LapTimeListViewAdapter;
import kr.kro.awesometic.plankhelper.util.Constants;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Awesometic on 2017-04-19.
 */

public class StopwatchFragment extends Fragment implements StopwatchContract.View {

    private static final int ANIMATOR_POSITION_LIST = 0;
    private static final int ANIMATOR_POSITION_LOADING = 1;

    private StopwatchContract.Presenter mPresenter;

    @BindView(R.id.plank_stopwatch_frag_animator)
    ViewAnimator mViewAnimator;

    // ButterKnife 가 아니라 mViewAnimator 에 의해 초기화 됨
    private RecyclerView mRecyclerView;

    private LapTimeListViewAdapter mLapTimeListViewAdapter;
    private RecyclerViewAdapter mRecyclerViewAdapter;

    private ViewGroup mViewGroup;
    private TextView tvHour;
    private TextView tvMin;
    private TextView tvSec;
    private TextView tvMSec;
    private ListView lvLapTime;
    private Button btnOnOff;
    private Button btnResetLap;

    public StopwatchFragment() {

    }

    public static StopwatchFragment newInstance() {
        return new StopwatchFragment();
    }

    public void setPresenter(@NonNull StopwatchContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.plank_stopwatch_frag, container, false);
        ButterKnife.bind(this, rootView);

        mRecyclerView = (RecyclerView) mViewAnimator.getChildAt(ANIMATOR_POSITION_LIST);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new MaterialViewPagerHeaderDecorator());

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        mPresenter.start();
        mPresenter.bindPlankService();
    }

    @Override
    public void onDestroy() {
        mPresenter.appExit(Constants.CALLER.FROM_STOPWATCH_FRAGMENT);
        mPresenter.unbindPlankService();

        super.onDestroy();
    }

    @Override
    public void showLoading() {
        mViewAnimator.setDisplayedChild(ANIMATOR_POSITION_LOADING);
    }

    @Override
    public void showStopwatch() {
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
        mRecyclerViewAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
    }

    @Override
    public void bindViewsFromViewHolder() {
        mViewGroup = mRecyclerViewAdapter.getHeadViewGroup();
        tvHour = (TextView) mViewGroup.findViewWithTag(Constants.PLANK_STOPWATCH_RCV_TAGS.TV_HOUR);
        tvMin = (TextView) mViewGroup.findViewWithTag(Constants.PLANK_STOPWATCH_RCV_TAGS.TV_MIN);
        tvSec = (TextView) mViewGroup.findViewWithTag(Constants.PLANK_STOPWATCH_RCV_TAGS.TV_SEC);
        tvMSec = (TextView) mViewGroup.findViewWithTag(Constants.PLANK_STOPWATCH_RCV_TAGS.TV_MSEC);
        lvLapTime = (ListView) mViewGroup.findViewWithTag(Constants.PLANK_STOPWATCH_RCV_TAGS.LV_LAPTIME);
        btnOnOff = (Button) mViewGroup.findViewWithTag(Constants.PLANK_STOPWATCH_RCV_TAGS.BTN_ONOFF);
        btnResetLap = (Button) mViewGroup.findViewWithTag(Constants.PLANK_STOPWATCH_RCV_TAGS.BTN_RESETLAP);

        btnOnOff.setOnClickListener(btnOnOffOnClickListener);
        btnResetLap.setOnClickListener(btnResetLapOnClickListener);

        lvLapTime.setAdapter(mLapTimeListViewAdapter);
        lvLapTime.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
    }

    @Override
    public String getTimeString() {
        return tvHour.getText().toString() + ":" + tvMin.getText().toString() + ":" +
                tvSec.getText().toString() + "." + tvMSec.getText().toString();
    }

    @Override
    public void setHour(String hour) {
        tvHour.setText(hour);
    }

    @Override
    public void setMin(String min) {
        tvMin.setText(min);
    }

    @Override
    public void setSec(String sec) {
        tvSec.setText(sec);
    }

    @Override
    public void setMSec(String mSec) {
        tvMSec.setText(mSec);
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
    public void setOnOffButtonEnabled(boolean isEnabled) {
        btnOnOff.setEnabled(isEnabled);
    }

    @Override
    public void setResetLapButtonEnabled(boolean isEnabled) {
        btnResetLap.setEnabled(isEnabled);
    }

    Button.OnClickListener btnOnOffOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getOnOffButtonValue().equals(getString(R.string.plank_stopwatch_on))) {
                mPresenter.stopwatchStart(Constants.CALLER.FROM_STOPWATCH_FRAGMENT);
            } else if (getOnOffButtonValue().equals(getString(R.string.plank_stopwatch_pause))) {
                mPresenter.stopwatchPause(Constants.CALLER.FROM_STOPWATCH_FRAGMENT);
            } else if (getOnOffButtonValue().equals(getString(R.string.plank_stopwatch_resume))) {
                mPresenter.stopwatchResume(Constants.CALLER.FROM_STOPWATCH_FRAGMENT);
            }
        }
    };

    Button.OnClickListener btnResetLapOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getResetLapButtonValue().equals(getString(R.string.plank_stopwatch_reset))) {
                mPresenter.stopwatchReset(Constants.CALLER.FROM_STOPWATCH_FRAGMENT);
            } else {
                mPresenter.stopwatchLap(Constants.CALLER.FROM_STOPWATCH_FRAGMENT);
            }
        }
    };
}
