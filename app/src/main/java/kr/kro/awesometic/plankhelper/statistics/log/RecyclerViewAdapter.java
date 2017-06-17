package kr.kro.awesometic.plankhelper.statistics.log;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.kro.awesometic.plankhelper.R;
import kr.kro.awesometic.plankhelper.data.PlankLog;
import kr.kro.awesometic.plankhelper.util.Constants;

/**
 * Created by Awesometic on 2017-06-01.
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
                        .inflate(R.layout.statistics_log_head, parent, false);
                break;
            case Constants.RECYCLERVIEW_ADAPTER_VIEWTYPE.TYPE_BODY:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.statistics_log_body, parent, false);
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

                holder.tvDatetime.setText(new Date(mPlankLogs.get(position).getDatetimeMSec()).toString());
                holder.tvDuration.setText(String.valueOf(mPlankLogs.get(position).getDuration()));
                holder.tvMethod.setText(mPlankLogs.get(position).getMethod());

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

    public void addPlankLog(PlankLog plankLog) {
        mPlankLogs.add(plankLog);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        @BindView(R.id.statistics_log_datetime_textview)
        TextView tvDatetime;

        @Nullable
        @BindView(R.id.statistics_log_duration_textview)
        TextView tvDuration;

        @Nullable
        @BindView(R.id.statistics_log_method_textview)
        TextView tvMethod;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
