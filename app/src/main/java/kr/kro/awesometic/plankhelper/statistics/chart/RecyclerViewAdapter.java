package kr.kro.awesometic.plankhelper.statistics.chart;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Optional;
import kr.kro.awesometic.plankhelper.R;
import kr.kro.awesometic.plankhelper.data.PlankLog;
import kr.kro.awesometic.plankhelper.util.Constants;
import kr.kro.awesometic.plankhelper.util.TimeUtils;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.view.LineChartView;

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
        View view;

        switch (viewType) {
            case Constants.RECYCLERVIEW_ADAPTER_WEEK_VIEWTYPE.TYPE_HEADER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.statistics_chart_line_head, parent, false);
                break;

            case Constants.RECYCLERVIEW_ADAPTER_WEEK_VIEWTYPE.TYPE_BODY:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.statistics_chart_line_body_item, parent, false);
                break;

            default:
                view = null;
                break;
        }

        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? Constants.RECYCLERVIEW_ADAPTER_WEEK_VIEWTYPE.TYPE_HEADER : Constants.RECYCLERVIEW_ADAPTER_WEEK_VIEWTYPE.TYPE_BODY;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // position 이용하기
        switch (getItemViewType(position)) {
            case Constants.RECYCLERVIEW_ADAPTER_WEEK_VIEWTYPE.TYPE_HEADER:
                List<Integer> daysOfWeek = TimeUtils.getDaysOfCurrentWeek();
                int[] averageDurationOfEachDay = {
                        0, 0, 0, 0, 0, 0, 0
                };

                for (int i = 0; i < mPlankLogs.size(); i++) {
                    int plankLogDay = Integer.parseInt(mPlankLogs.get(i).getDatetime().split(" ")[0].split("-")[2]);

                    averageDurationOfEachDay[daysOfWeek.indexOf(plankLogDay)] += mPlankLogs.get(i).getDuration();
                }

                List<PointValue> values = new ArrayList<PointValue>();
                for (int i = 0; i <= 8; i++) {
                    values.add(new PointValue(daysOfWeek.get(i), averageDurationOfEachDay[i]));
                }

                List<Line> lines = new ArrayList<Line>();
                Line line = new Line(values)
                        .setColor(Color.BLUE)
                        .setCubic(true)
                        .setShape(ValueShape.CIRCLE)
                        .setFilled(false)
                        .setHasLabels(true)
                        .setHasLabelsOnlyForSelected(true)
                        .setHasLines(true)
                        .setHasPoints(true);
                lines.add(line);

                LineChartData data = new LineChartData(lines);
                Axis axisX = new Axis();
                Axis axisY = new Axis().setHasLines(true);
                axisX.setName("Date");
                axisY.setName("Time");
                data.setAxisXBottom(axisX);
                data.setAxisYLeft(axisY);

                holder.mLineChartView.setLineChartData(data);
                holder.mLineChartView.setOnValueTouchListener(new LineChartOnValueSelectListener() {
                    @Override
                    public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {

                    }

                    @Override
                    public void onValueDeselected() {

                    }
                });
                break;
            case Constants.RECYCLERVIEW_ADAPTER_WEEK_VIEWTYPE.TYPE_BODY:
                holder.mTextView.setText(mPlankLogs.get(position).toString());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mPlankLogs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        @BindView(R.id.statistics_chart_line_text_view)
        TextView mTextView;

        @BindView(R.id.statistics_chart_line_chart_view)
        LineChartView mLineChartView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}