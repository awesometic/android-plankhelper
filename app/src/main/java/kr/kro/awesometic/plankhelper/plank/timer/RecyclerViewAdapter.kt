package kr.kro.awesometic.plankhelper.plank.timer

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.NumberPicker

import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife
import kr.kro.awesometic.plankhelper.R
import kr.kro.awesometic.plankhelper.data.PlankLog
import kr.kro.awesometic.plankhelper.util.Constants

/**
 * Created by Awesometic on 2017-05-29.
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
                    .inflate(R.layout.plank_timer_head, parent, false)
            Constants.RECYCLERVIEW_ADAPTER_VIEWTYPE.TYPE_BODY -> view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.plank_timer_body, parent, false)

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

        @BindView(R.id.numberPicker_hour)
        internal var npHour: NumberPicker? = null

        @BindView(R.id.numberPicker_min)
        internal var npMin: NumberPicker? = null

        @BindView(R.id.numberPicker_sec)
        internal var npSec: NumberPicker? = null

        @BindView(R.id.listView_timer_lap)
        internal var lvLapTime: ListView? = null

        @BindView(R.id.button_timer_on_off)
        internal var btnOnOff: Button? = null

        @BindView(R.id.button_timer_reset_lap)
        internal var btnResetLap: Button? = null

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}