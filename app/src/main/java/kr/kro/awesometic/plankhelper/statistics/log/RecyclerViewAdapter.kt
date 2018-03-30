package kr.kro.awesometic.plankhelper.statistics.log

import android.support.v7.widget.RecyclerView
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

/**
 * Created by Awesometic on 2017-06-01.
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
                    .inflate(R.layout.statistics_log_head, parent, false)
            Constants.RECYCLERVIEW_ADAPTER_VIEWTYPE.TYPE_BODY -> view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.statistics_log_body, parent, false)

            else -> view = null
        }

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            Constants.RECYCLERVIEW_ADAPTER_VIEWTYPE.TYPE_HEAD -> {
            }
            Constants.RECYCLERVIEW_ADAPTER_VIEWTYPE.TYPE_BODY -> {

                holder.tvDatetime!!.text = mPlankLogs[position].datetime
                holder.tvDuration!!.text = mPlankLogs[position].duration.toString()
                holder.tvMethod!!.text = mPlankLogs[position].method
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

    fun addPlankLog(plankLog: PlankLog) {
        mPlankLogs.add(plankLog)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.statistics_log_datetime_textview)
        internal var tvDatetime: TextView? = null

        @BindView(R.id.statistics_log_duration_textview)
        internal var tvDuration: TextView? = null

        @BindView(R.id.statistics_log_method_textview)
        internal var tvMethod: TextView? = null

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}
