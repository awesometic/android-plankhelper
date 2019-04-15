package kr.kro.awesometic.plankhelper.plank.stopwatch;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.kro.awesometic.plankhelper.R;
import kr.kro.awesometic.plankhelper.data.PlankLog;
import kr.kro.awesometic.plankhelper.util.Constants;

/**
 * Created by Awesometic on 2017-05-27.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<PlankLog> mPlankLogs = new ArrayList<PlankLog>();
    
    public RecyclerViewAdapter() {

    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return Constants.RECYCLERVIEW_ADAPTER_VIEWTYPE.TYPE_HEAD;
            default:
                return Constants.RECYCLERVIEW_ADAPTER_VIEWTYPE.TYPE_BODY;
        }
    }

    @Override
    public int getItemCount() {
        return mPlankLogs.size();
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        switch (viewType) {
            case Constants.RECYCLERVIEW_ADAPTER_VIEWTYPE.TYPE_HEAD:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.plank_stopwatch_head, parent, false);
                break;
            case Constants.RECYCLERVIEW_ADAPTER_VIEWTYPE.TYPE_BODY:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.plank_stopwatch_body, parent, false);
                break;

            default:
                view = null;
                break;
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case Constants.RECYCLERVIEW_ADAPTER_VIEWTYPE.TYPE_HEAD:

                break;
            case Constants.RECYCLERVIEW_ADAPTER_VIEWTYPE.TYPE_BODY:

                break;
        }
    }

    public void setPlankLogs(ArrayList<PlankLog> plankLogs) {
        mPlankLogs.clear();
        notifyDataSetChanged();

        // position 0 에 카드를 그리기 위한 더미 데이터(null) 삽입
        mPlankLogs.add(null);
        if (plankLogs != null && plankLogs.size() > 0) {
            // position 1부터 실제 데이터 삽입
            mPlankLogs.addAll(plankLogs);
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        @BindView(R.id.textView_hour)
        TextView tvHour;

        @Nullable
        @BindView(R.id.textView_min)
        TextView tvMin;

        @Nullable
        @BindView(R.id.textView_sec)
        TextView tvSec;

        @Nullable
        @BindView(R.id.textView_mSec)
        TextView tvMSec;

        @Nullable
        @BindView(R.id.listView_stopwatch_lap)
        ListView lvLapTime;

        @Nullable
        @BindView(R.id.button_stopwatch_on_off)
        Button btnOnOff;

        @Nullable
        @BindView(R.id.button_stopwatch_reset_lap)
        Button btnResetLap;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
