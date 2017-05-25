package kr.kro.awesometic.plankhelper.statistics.log;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import kr.kro.awesometic.plankhelper.R;
import kr.kro.awesometic.plankhelper.data.LapTime;
import kr.kro.awesometic.plankhelper.data.PlankLog;
import kr.kro.awesometic.plankhelper.util.TimeUtils;

/**
 * Created by Awesometic on 2017-04-30.
 */

public class LogListViewAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    
    private ArrayList<PlankLog> mPlankLogs = new ArrayList<PlankLog>();

    public LogListViewAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() {
        return mPlankLogs.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mPlankLogs.get(groupPosition).getLapCount();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mPlankLogs.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mPlankLogs.get(groupPosition).getLapTimes().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.statistics_planklog_expandablelistview_group_item, null);
        }

        TextView datetimeTextView = (TextView) convertView.findViewById(R.id.textView_statistics_datetime);
        TextView durationTextView = (TextView) convertView.findViewById(R.id.textView_statistics_duration);
        TextView methodTextView = (TextView) convertView.findViewById(R.id.textView_statistics_method);

        PlankLog plankLog = mPlankLogs.get(groupPosition);

        datetimeTextView.setText(plankLog.getDatetime());
        durationTextView.setText(TimeUtils.mSecToTimeFormat(plankLog.getDuration()));
        methodTextView.setText(plankLog.getMethod());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.statistics_planklog_expandablelistview_child_item, null);
        }

        TextView orderTextView = (TextView) convertView.findViewById(R.id.textView_statistics_lap_order);
        TextView passedTimeTextView = (TextView) convertView.findViewById(R.id.textView_statistics_lap_passedtime);
        TextView intervalTextView = (TextView) convertView.findViewById(R.id.textView_statistics_lap_interval);

        LapTime lapTime = mPlankLogs.get(groupPosition).getLapTimes().get(childPosition);

        orderTextView.setText(String.valueOf(lapTime.getOrderNumber()));
        passedTimeTextView.setText(lapTime.getPassedTime());
        intervalTextView.setText(lapTime.getInterval());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public void addItem(PlankLog plankLog) {
        mPlankLogs.add(plankLog);
    }

    public void clear() {
        mPlankLogs.clear();
    }
}
