package kr.kro.awesometic.plankhelper.statistics.log;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import kr.kro.awesometic.plankhelper.R;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Awesometic on 2017-05-17.
 */

public class LogFragment extends Fragment implements LogContract.View {

    private LogContract.Presenter mPresenter;

    private ExpandableListView elvPlankLog;

    private LogListViewAdapter mLogListViewAdapter;

    public LogFragment() {

    }

    public static LogFragment newInstance() {
        return new LogFragment();
    }

    public void setPresenter(@NonNull LogContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.statistics_log_frag, container, false);

        elvPlankLog = (ExpandableListView) rootView.findViewById(R.id.expandableListView_plankLog);
        
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        mPresenter.start();
        elvPlankLog.setAdapter(mLogListViewAdapter);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    public Object getActivityContext() {
        return getActivity();
    }

    @Override
    public void setPlankLogAdapter(Object plankLogAdapter) {
        mLogListViewAdapter = (LogListViewAdapter) plankLogAdapter;
    }
}
