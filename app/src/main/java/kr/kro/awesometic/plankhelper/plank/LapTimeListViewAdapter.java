package kr.kro.awesometic.plankhelper.plank;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import kr.kro.awesometic.plankhelper.R;
import kr.kro.awesometic.plankhelper.data.LapTime;
import kr.kro.awesometic.plankhelper.util.Constants;

/**
 * Created by Awesometic on 2017-04-18.
 */

public class LapTimeListViewAdapter extends BaseAdapter {
    
    private ArrayList<LapTime> lapTimes = new ArrayList<LapTime>();

    public LapTimeListViewAdapter() {

    }

    @Override
    public int getCount() {
        return lapTimes.size();
    }

    @Override
    public LapTime getItem(int position) {
        return lapTimes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        // layout 을 inflate 하여 convertView 참조 획득
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.plank_laptime_listview_item, parent, false);
        }

        // inflate하여 얻은 convertView 로부터 각 위젯에 대한 참조 획득
        TextView orderTextView = (TextView) convertView.findViewById(R.id.textView_lap_order);
        TextView passedTextView = (TextView) convertView.findViewById(R.id.textView_lap_passed);
        TextView leftTextView = (TextView) convertView.findViewById(R.id.textView_lap_left);
        TextView intervalTextView = (TextView) convertView.findViewById(R.id.textView_lap_interval);

        // data set 에서 position에 위치한 데이터 참조 획득
        LapTime lapTime = lapTimes.get(position);

        // 아이템 내 각 뷰에 데이터 반영
        orderTextView.setText(String.valueOf(lapTime.getOrderNumber()));
        passedTextView.setText(String.valueOf(lapTime.getPassedTimeMSec()));
        leftTextView.setText((lapTime.getLeftTimeMSec() == Constants.LAPTIME_ENTRY.NULL_INTERVAL) ? "" : "" + lapTime.getLeftTimeMSec());
        intervalTextView.setText(String.valueOf(lapTime.getIntervalMSec()));

        return convertView;
    }

    public void addItem(LapTime lapTime) {
        lapTimes.add(lapTime);
    }

    public void clear() {
        lapTimes.clear();
    }

    public ArrayList<LapTime> getAllItems() {
        return lapTimes;
    }
}
