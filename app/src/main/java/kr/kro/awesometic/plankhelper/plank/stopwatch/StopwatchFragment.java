package kr.kro.awesometic.plankhelper.plank.stopwatch;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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

import static androidx.core.util.Preconditions.checkNotNull;

/**
 * Created by Awesometic on 2017-04-19.
 */

public class StopwatchFragment extends Fragment implements StopwatchContract.View {

    private StopwatchContract.Presenter mPresenter;
    private Context mContext;

    @BindView(R.id.plank_stopwatch_frag_animator)
    ViewAnimator mViewAnimator;

    // ButterKnife 가 아니라 mViewAnimator 에 의해 초기화 됨
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private LapTimeListViewAdapter mLapTimeListViewAdapter;

    private boolean mIsViewsBound;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity().getApplicationContext();
        mIsViewsBound = false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.plank_stopwatch_frag, container, false);
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
                        if (mPresenter.getStopwatchStart()) {
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
    public void showStopwatch() {
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
    public void bindViewsFromViewHolder(StopwatchContract.BoundViewsCallback callback) {
        RecyclerViewAdapter.ViewHolder holder = (RecyclerViewAdapter.ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(0);

        if (holder.getItemViewType() == Constants.RECYCLERVIEW_ADAPTER_VIEWTYPE.TYPE_HEAD) {
            tvHour = holder.tvHour;
            tvMin = holder.tvMin;
            tvSec = holder.tvSec;
            tvMSec = holder.tvMSec;
            lvLapTime = holder.lvLapTime;
            btnOnOff = holder.btnOnOff;
            btnResetLap = holder.btnResetLap;

            btnOnOff.setOnClickListener(btnOnOffOnClickListener);
            btnResetLap.setOnClickListener(btnResetLapOnClickListener);

            lvLapTime.setAdapter(mLapTimeListViewAdapter);
            lvLapTime.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

            callback.onBoundViews();
        }
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
                mPresenter.controlFromFrag(Constants.SERVICE_WHAT.STOPWATCH_START);
            } else if (getOnOffButtonValue().equals(getString(R.string.plank_stopwatch_pause))) {
                mPresenter.controlFromFrag(Constants.SERVICE_WHAT.STOPWATCH_PAUSE);
            } else if (getOnOffButtonValue().equals(getString(R.string.plank_stopwatch_resume))) {
                mPresenter.controlFromFrag(Constants.SERVICE_WHAT.STOPWATCH_RESUME);
            }
        }
    };

    Button.OnClickListener btnResetLapOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getResetLapButtonValue().equals(getString(R.string.plank_stopwatch_reset))) {
                mPresenter.controlFromFrag(Constants.SERVICE_WHAT.STOPWATCH_RESET);
            } else {
                mPresenter.controlFromFrag(Constants.SERVICE_WHAT.STOPWATCH_LAP);
            }
        }
    };
}
