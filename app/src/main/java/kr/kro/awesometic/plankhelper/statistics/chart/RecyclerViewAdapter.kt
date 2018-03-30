package kr.kro.awesometic.plankhelper.statistics.chart

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife
import kr.kro.awesometic.plankhelper.R
import kr.kro.awesometic.plankhelper.data.PlankLog
import kr.kro.awesometic.plankhelper.util.Constants
import kr.kro.awesometic.plankhelper.util.Singleton
import kr.kro.awesometic.plankhelper.util.TimeUtils
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener
import lecho.lib.hellocharts.model.Axis
import lecho.lib.hellocharts.model.AxisValue
import lecho.lib.hellocharts.model.Line
import lecho.lib.hellocharts.model.LineChartData
import lecho.lib.hellocharts.model.PointValue
import lecho.lib.hellocharts.model.ValueShape
import lecho.lib.hellocharts.model.Viewport
import lecho.lib.hellocharts.view.LineChartView

/**
 * Created by Awesometic on 2017-05-23.
 */

class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    private val mPlankLogs = ArrayList<PlankLog>()
    private val mSingleton = Singleton.instance

    private val totalDurationOfEachDay: IntArray
    private val daysOfWeek: List<Int>

    private var maxPlankDuration: Int = 0

    init {
        totalDurationOfEachDay = intArrayOf(0, 0, 0, 0, 0, 0, 0)
        daysOfWeek = TimeUtils.getDaysOfCurrentWeek(mSingleton.startOfTheWeek)
    }

    override fun getItemViewType(position: Int): Int {
        when (position) {
            0 -> return Constants.RECYCLERVIEW_ADAPTER_VIEWTYPE.TYPE_HEAD
            else -> return Constants.RECYCLERVIEW_ADAPTER_VIEWTYPE.TYPE_BODY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewAdapter.ViewHolder {
        val view: View?

        when (viewType) {
            Constants.RECYCLERVIEW_ADAPTER_VIEWTYPE.TYPE_HEAD -> view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.statistics_chart_line_head, parent, false)

            Constants.RECYCLERVIEW_ADAPTER_VIEWTYPE.TYPE_BODY -> view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.statistics_chart_line_body, parent, false)

            else -> view = null
        }

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            Constants.RECYCLERVIEW_ADAPTER_VIEWTYPE.TYPE_HEAD -> {
                for (i in 1 until mPlankLogs.size) {
                    val plankLogDay = Integer.parseInt(mPlankLogs[i].datetime.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[2])
                    val orderOfWeek = daysOfWeek.indexOf(plankLogDay)

                    if (orderOfWeek > -1) {
                        totalDurationOfEachDay[orderOfWeek] += mPlankLogs[i].duration.toInt()
                    }
                }

                val values = ArrayList<PointValue>()
                maxPlankDuration = 0
                for (i in 0..6) {
                    values.add(PointValue(daysOfWeek[i].toFloat(), totalDurationOfEachDay[i].toFloat()))
                    Log.d(Constants.LOG_TAG, totalDurationOfEachDay[i].toString())

                    if (maxPlankDuration < totalDurationOfEachDay[i])
                        maxPlankDuration = totalDurationOfEachDay[i]
                }

                val lines = ArrayList<Line>()
                val line = Line(values)
                        .setColor(Color.BLUE)
                        .setCubic(true)
                        .setShape(ValueShape.CIRCLE)
                        .setFilled(false)
                        .setHasLabels(true)
                        .setHasLabelsOnlyForSelected(true)
                        .setHasLines(true)
                        .setHasPoints(true)
                lines.add(line)

                val data = LineChartData(lines)
                val axisX = Axis()
                val axisY = Axis().setHasLines(true)
                axisX.name = "Date"
                axisY.name = "Time"

                val axisXValues = ArrayList<AxisValue>()
                for (day in daysOfWeek) {
                    val axisValue = AxisValue(day.toFloat())
                    axisXValues.add(axisValue)
                }
                axisX.values = axisXValues

                Log.d(Constants.LOG_TAG, javaClass.toString() + " maxPlankDuration: " + maxPlankDuration)
                val axisYValues = ArrayList<AxisValue>()
                val lineChartUnitOfAxisY = mSingleton.lineChartUnitOfAxisY
                var i = 0
                while (i <= if (maxPlankDuration > lineChartUnitOfAxisY) maxPlankDuration else lineChartUnitOfAxisY) {
                    val axisValue = AxisValue(i.toFloat())

                    axisValue.setLabel(TimeUtils.mSecToTimeFormat(i.toLong()).split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])
                    axisYValues.add(axisValue)

                    Log.d(Constants.LOG_TAG, javaClass.toString() + " axisYValue: " + axisValue.value)
                    i += lineChartUnitOfAxisY
                }
                axisY.values = axisYValues

                data.axisXBottom = axisX
                data.axisYLeft = axisY
                holder.mLineChartView!!.lineChartData = data

                val viewport = Viewport(holder.mLineChartView!!.maximumViewport)
                viewport.top = (if (maxPlankDuration > lineChartUnitOfAxisY) maxPlankDuration else lineChartUnitOfAxisY).toFloat()
                viewport.bottom = 0f
                holder.mLineChartView!!.maximumViewport = viewport
                holder.mLineChartView!!.currentViewport = viewport

                // disable viewport recalculations, thanks to this animations will not change viewport automatically
                holder.mLineChartView!!.isViewportCalculationEnabled = false

                holder.mLineChartView!!.onValueTouchListener = object : LineChartOnValueSelectListener {
                    override fun onValueSelected(lineIndex: Int, pointIndex: Int, value: PointValue) {

                    }

                    override fun onValueDeselected() {

                    }
                }
            }
            Constants.RECYCLERVIEW_ADAPTER_VIEWTYPE.TYPE_BODY -> {
                holder.mDatetimeTextView!!.text = mPlankLogs[position].datetime
                holder.mDurationTextView!!.text = TimeUtils.mSecToTimeFormat(mPlankLogs[position].duration)
                holder.mLapCountTextView!!.text = mPlankLogs[position].lapCount.toString()
                holder.mMethodTextView!!.text = mPlankLogs[position].method
            }
        }
    }

    override fun getItemCount(): Int {
        return mPlankLogs.size
    }

    fun setPlankLogs(plankLogs: ArrayList<PlankLog>?) {
        mPlankLogs.clear()
        notifyDataSetChanged()

        // position 0 에 차트를 그리기 위한 더미 데이터(null) 삽입
        mPlankLogs.add(null)
        if (plankLogs != null && plankLogs.size > 0) {
            // position 1부터 실제 데이터 삽입
            mPlankLogs.addAll(plankLogs)
        }
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.statistics_chart_line_textview_datetime)
        internal var mDatetimeTextView: TextView? = null

        @BindView(R.id.statistics_chart_line_textview_duration)
        internal var mDurationTextView: TextView? = null

        @BindView(R.id.statistics_chart_line_textview_lap_count)
        internal var mLapCountTextView: TextView? = null

        @BindView(R.id.statistics_chart_line_textview_method)
        internal var mMethodTextView: TextView? = null

        @BindView(R.id.statistics_chart_line_chart_view)
        internal var mLineChartView: LineChartView? = null

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}