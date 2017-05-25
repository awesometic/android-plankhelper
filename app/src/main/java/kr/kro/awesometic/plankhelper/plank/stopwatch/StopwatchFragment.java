package kr.kro.awesometic.plankhelper.plank.stopwatch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import kr.kro.awesometic.plankhelper.R;
import kr.kro.awesometic.plankhelper.plank.LapTimeListViewAdapter;
import kr.kro.awesometic.plankhelper.util.Constants;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Awesometic on 2017-04-19.
 */

public class StopwatchFragment extends Fragment implements StopwatchContract.View {

    private StopwatchContract.Presenter mPresenter;

    private TextView tvHour;
    private TextView tvMin;
    private TextView tvSec;
    private TextView tvMSec;
    private ListView lvLapTime;
    private Button btnOnOff;
    private Button btnResetLap;

    private LapTimeListViewAdapter mLapTimeListViewAdapter;

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

        tvHour = (TextView) rootView.findViewById(R.id.textView_hour);
        tvMin = (TextView) rootView.findViewById(R.id.textView_min);
        tvSec = (TextView) rootView.findViewById(R.id.textView_sec);
        tvMSec = (TextView) rootView.findViewById(R.id.textView_mSec);
        lvLapTime = (ListView) rootView.findViewById(R.id.listView_stopwatch_lap);
        btnOnOff = (Button) rootView.findViewById(R.id.button_stopwatch_on_off);
        btnResetLap = (Button) rootView.findViewById(R.id.button_stopwatch_reset_lap);

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
        mPresenter.appExit(Constants.CALLER.FROM_STOPWATCH_FRAGMENT);
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
