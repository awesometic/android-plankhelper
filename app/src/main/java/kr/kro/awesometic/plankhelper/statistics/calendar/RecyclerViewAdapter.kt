package kr.kro.awesometic.plankhelper.statistics.calendar

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.prolificinteractive.materialcalendarview.MaterialCalendarView

import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife
import kr.kro.awesometic.plankhelper.R
import kr.kro.awesometic.plankhelper.data.PlankLog
import kr.kro.awesometic.plankhelper.util.Constants

/**
 * Created by Awesometic on 2017-05-30.
 */

class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    private val mPlankLogs = ArrayList<PlankLog>()

    override fun getItemViewType(position: Int): Int {
        when (position) {
            0 -> return Constants.RECYCLERVIEW_ADAPTER_VIEWTYPE.TYPE_HEAD
            else -> return Constants.RECYCLERVIEW_ADAPTER_VIEWTYPE.TYPE_BODY
        }
    }

    override fun getItemCount(): Int {
        return mPlankLogs.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewAdapter.ViewHolder {
        val view: View?

        when (viewType) {
            Constants.RECYCLERVIEW_ADAPTER_VIEWTYPE.TYPE_HEAD -> view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.statistics_calendar_head, parent, false)
            Constants.RECYCLERVIEW_ADAPTER_VIEWTYPE.TYPE_BODY -> view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.statistics_calendar_body, parent, false)

            else -> view = null
        }

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            Constants.RECYCLERVIEW_ADAPTER_VIEWTYPE.TYPE_HEAD -> {
            }
            Constants.RECYCLERVIEW_ADAPTER_VIEWTYPE.TYPE_BODY -> {
            }
        }
    }

    fun setPlankLogs(plankLogs: ArrayList<PlankLog>?) {
        mPlankLogs.clear()
        notifyDataSetChanged()

        // position 0 에 카드를 그리기 위한 더미 데이터(null) 삽입
        mPlankLogs.add(null)
        if (plankLogs != null && plankLogs.size > 0) {
            // position 1부터 실제 데이터 삽입
            mPlankLogs.addAll(plankLogs)
        }
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.statistics_calendar)
        internal var mMaterialCalendarView: MaterialCalendarView? = null

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}
