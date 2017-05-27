package kr.kro.awesometic.plankhelper.statistics.chart;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.kro.awesometic.plankhelper.R;
import kr.kro.awesometic.plankhelper.data.PlankLog;
import kr.kro.awesometic.plankhelper.util.Constants;
import kr.kro.awesometic.plankhelper.util.Singleton;
import kr.kro.awesometic.plankhelper.util.TimeUtils;
import lecho.lib.hellocharts.formatter.AxisValueFormatter;
import lecho.lib.hellocharts.formatter.SimpleAxisValueFormatter;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by Awesometic on 2017-05-23.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<PlankLog> mPlankLogs = new ArrayList<PlankLog>();
    private Singleton mSingleton = Singleton.getInstance();

    private int[] totalDurationOfEachDay;
    private List<Integer> daysOfWeek;

    private int maxPlankDuration;

    public RecyclerViewAdapter() {
        totalDurationOfEachDay = new int[] {
                0, 0, 0, 0, 0, 0, 0
        };
        daysOfWeek = TimeUtils.getDaysOfCurrentWeek(mSingleton.getStartOfTheWeek());
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? Constants.RECYCLERVIEW_ADAPTER_WEEK_VIEWTYPE.TYPE_HEADER : Constants.RECYCLERVIEW_ADAPTER_WEEK_VIEWTYPE.TYPE_BODY;
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(Constants.LOG_TAG, getClass().toString() + " position: " + position);
        Log.d(Constants.LOG_TAG, getClass().toString() + " this week: " + daysOfWeek.toString());

        switch (getItemViewType(position)) {
            case Constants.RECYCLERVIEW_ADAPTER_WEEK_VIEWTYPE.TYPE_HEADER:
                for (int i = 1; i < mPlankLogs.size(); i++) {
                    int plankLogDay = Integer.parseInt(mPlankLogs.get(i).getDatetime().split(" ")[0].split("-")[2]);
                    int orderOfWeek = daysOfWeek.indexOf(plankLogDay);

                    if (orderOfWeek > -1) {
                        totalDurationOfEachDay[orderOfWeek] += mPlankLogs.get(i).getDuration();
                    }
                }

                List<PointValue> values = new ArrayList<PointValue>();
                maxPlankDuration = 0;
                for (int i = 0; i < 7; i++) {
                    values.add(new PointValue(daysOfWeek.get(i), totalDurationOfEachDay[i]));
                    Log.d(Constants.LOG_TAG, String.valueOf(totalDurationOfEachDay[i]));

                    if (maxPlankDuration < totalDurationOfEachDay[i])
                        maxPlankDuration = totalDurationOfEachDay[i];
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

                List<AxisValue> axisXValues = new ArrayList<AxisValue>();
                for (int day : daysOfWeek) {
                    AxisValue axisValue = new AxisValue(day);
                    axisXValues.add(axisValue);
                }
                axisX.setValues(axisXValues);

                Log.d(Constants.LOG_TAG, getClass().toString() + " maxPlankDuration: " + maxPlankDuration);
                List<AxisValue> axisYValues = new ArrayList<AxisValue>();
                int lineChartUnitOfAxisY = mSingleton.getLineChartUnitOfAxisY();
                for (int i = 0; i <= ((maxPlankDuration > lineChartUnitOfAxisY) ? maxPlankDuration : lineChartUnitOfAxisY); i += lineChartUnitOfAxisY) {
                    AxisValue axisValue = new AxisValue(i);

                    axisValue.setLabel(TimeUtils.mSecToTimeFormat(i).split("\\.")[0]);
                    axisYValues.add(axisValue);

                    Log.d(Constants.LOG_TAG, getClass().toString() + " axisYValue: " + axisValue.getValue());
                }
                axisY.setValues(axisYValues);

                data.setAxisXBottom(axisX);
                data.setAxisYLeft(axisY);
                holder.mLineChartView.setLineChartData(data);

                final Viewport viewport = new Viewport(holder.mLineChartView.getMaximumViewport());
                viewport.top = (maxPlankDuration > lineChartUnitOfAxisY) ? maxPlankDuration : lineChartUnitOfAxisY;
                viewport.bottom = 0;
                holder.mLineChartView.setMaximumViewport(viewport);
                holder.mLineChartView.setCurrentViewport(viewport);

                // disable viewport recalculations, thanks to this animations will not change viewport automatically
                holder.mLineChartView.setViewportCalculationEnabled(false);

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
                holder.mDatetimeTextView.setText(mPlankLogs.get(position).getDatetime());
                holder.mDurationTextView.setText(TimeUtils.mSecToTimeFormat(mPlankLogs.get(position).getDuration()));
                holder.mLapCountTextView.setText(String.valueOf(mPlankLogs.get(position).getLapCount()));
                holder.mMethodTextView.setText(mPlankLogs.get(position).getMethod());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mPlankLogs.size();
    }

    public void setPlankLogs(ArrayList<PlankLog> plankLogs) {
        // position 0 에 차트를 그리기 위한 더미 데이터(null) 삽입
        mPlankLogs.add(null);

        // position 1부터 실제 데이터 삽입
        mPlankLogs.addAll(plankLogs);

        // 데이터셋 변경 알림
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        @BindView(R.id.statistics_chart_line_text_view_datetime)
        TextView mDatetimeTextView;

        @Nullable
        @BindView(R.id.statistics_chart_line_text_view_duration)
        TextView mDurationTextView;

        @Nullable
        @BindView(R.id.statistics_chart_line_text_view_lap_count)
        TextView mLapCountTextView;

        @Nullable
        @BindView(R.id.statistics_chart_line_text_view_method)
        TextView mMethodTextView;

        @Nullable
        @BindView(R.id.statistics_chart_line_chart_view)
        LineChartView mLineChartView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}