package kr.kro.awesometic.plankhelper.statistics.chart;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.kro.awesometic.plankhelper.R;
import kr.kro.awesometic.plankhelper.data.PlankLog;

/**
 * Created by Awesometic on 2017-05-23.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<PlankLog> mPlankLogs;

    public RecyclerViewAdapter(ArrayList<PlankLog> plankLogs) {
        mPlankLogs = plankLogs;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.statistics_chart_line_recyclerview_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView.setText(mPlankLogs.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return mPlankLogs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.statistics_chart_line_text_view)
        TextView mTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}