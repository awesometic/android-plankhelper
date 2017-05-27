package kr.kro.awesometic.plankhelper.plank.stopwatch;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    private ViewGroup mHeadViewGroup;
    
    public RecyclerViewAdapter() {
        mHeadViewGroup = null;

        // position 0 에 차트를 그리기 위한 더미 데이터(null) 삽입
        mPlankLogs.add(null);
        notifyDataSetChanged();
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
    public long getItemId(int position) {
        return mPlankLogs.get(position).hashCode();
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
                holder.tvHour.setTag(Constants.PLANK_STOPWATCH_RCV_TAGS.TV_HOUR);
                holder.tvMin.setTag(Constants.PLANK_STOPWATCH_RCV_TAGS.TV_MIN);
                holder.tvSec.setTag(Constants.PLANK_STOPWATCH_RCV_TAGS.TV_SEC);
                holder.tvMSec.setTag(Constants.PLANK_STOPWATCH_RCV_TAGS.TV_MSEC);
                holder.lvLapTime.setTag(Constants.PLANK_STOPWATCH_RCV_TAGS.LV_LAPTIME);
                holder.btnOnOff.setTag(Constants.PLANK_STOPWATCH_RCV_TAGS.BTN_ONOFF);
                holder.btnResetLap.setTag(Constants.PLANK_STOPWATCH_RCV_TAGS.BTN_RESETLAP);

                ArrayList<View> views = new ArrayList<>();
                views.add(holder.tvHour);
                views.add(holder.tvMin);
                views.add(holder.tvSec);
                views.add(holder.tvMSec);
                views.add(holder.lvLapTime);
                views.add(holder.btnOnOff);
                views.add(holder.btnResetLap);

                mHeadViewGroup.addChildrenForAccessibility(views);
                Log.d(Constants.LOG_TAG, "mHeadViewGroup ready");

                break;
            case Constants.RECYCLERVIEW_ADAPTER_VIEWTYPE.TYPE_BODY:

                break;
        }
    }

    public ViewGroup getHeadViewGroup() {
        Log.d(Constants.LOG_TAG, "mHeadViewGroup returned");
        return mHeadViewGroup;
    }

    public void setPlankLogs(ArrayList<PlankLog> plankLogs) {
        // position 1부터 실제 데이터 삽입
        mPlankLogs.addAll(plankLogs);
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
