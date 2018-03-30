package kr.kro.awesometic.plankhelper.plank

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

import java.util.ArrayList

import kr.kro.awesometic.plankhelper.R
import kr.kro.awesometic.plankhelper.data.LapTime

/**
 * Created by Awesometic on 2017-04-18.
 */

class LapTimeListViewAdapter : BaseAdapter() {

    val allItems = ArrayList<LapTime>()

    override fun getCount(): Int {
        return allItems.size
    }

    override fun getItem(position: Int): LapTime {
        return allItems[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val context = parent.context

        // layout 을 inflate 하여 convertView 참조 획득
        if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.plank_laptime_listview_item, parent, false)
        }

        // inflate하여 얻은 convertView 로부터 각 위젯에 대한 참조 획득
        val orderTextView = convertView!!.findViewById(R.id.textView_lap_order) as TextView
        val passedTextView = convertView.findViewById(R.id.textView_lap_passed) as TextView
        val leftTextView = convertView.findViewById(R.id.textView_lap_left) as TextView
        val intervalTextView = convertView.findViewById(R.id.textView_lap_interval) as TextView

        // data set 에서 position에 위치한 데이터 참조 획득
        val lapTime = allItems[position]

        // 아이템 내 각 뷰에 데이터 반영
        orderTextView.text = lapTime.orderNumber.toString()
        passedTextView.text = lapTime.passedTime
        leftTextView.text = if (lapTime.leftTime == null) "" else lapTime.leftTime
        intervalTextView.text = lapTime.interval

        return convertView
    }

    fun addItem(lapTime: LapTime) {
        allItems.add(lapTime)
    }

    fun clear() {
        allItems.clear()
    }
}
