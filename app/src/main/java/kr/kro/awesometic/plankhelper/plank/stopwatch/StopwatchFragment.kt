package kr.kro.awesometic.plankhelper.plank.stopwatch

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.ViewAnimator

import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator

import butterknife.BindView
import butterknife.ButterKnife
import kr.kro.awesometic.plankhelper.R
import kr.kro.awesometic.plankhelper.plank.LapTimeListViewAdapter
import kr.kro.awesometic.plankhelper.util.Constants

import com.google.common.base.Preconditions.checkNotNull

/**
 * Created by Awesometic on 2017-04-19.
 */

class StopwatchFragment : Fragment(), StopwatchContract.View {

    private var mPresenter: StopwatchContract.Presenter? = null
    private var mContext: Context? = null

    @BindView(R.id.plank_stopwatch_frag_animator)
    internal var mViewAnimator: ViewAnimator? = null

    // ButterKnife 가 아니라 mViewAnimator 에 의해 초기화 됨
    private var mRecyclerView: RecyclerView? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null

    private var mLapTimeListViewAdapter: LapTimeListViewAdapter? = null

    private var mIsViewsBound: Boolean = false
    private var tvHour: TextView? = null
    private var tvMin: TextView? = null
    private var tvSec: TextView? = null
    private var tvMSec: TextView? = null
    private var lvLapTime: ListView? = null
    private var btnOnOff: Button? = null
    private var btnResetLap: Button? = null

    override val activityContext: Any
        get() = activity

    override val timeString: String
        get() = tvHour!!.text.toString() + ":" + tvMin!!.text.toString() + ":" +
                tvSec!!.text.toString() + "." + tvMSec!!.text.toString()

    override var onOffButtonValue: String
        get() = btnOnOff!!.text.toString()
        set(value) {
            btnOnOff!!.text = value
        }

    override var resetLapButtonValue: String
        get() = btnResetLap!!.text.toString()
        set(value) {
            btnResetLap!!.text = value
        }

    internal var btnOnOffOnClickListener: Button.OnClickListener = View.OnClickListener {
        if (onOffButtonValue == getString(R.string.plank_stopwatch_on)) {
            mPresenter!!.controlFromFrag(Constants.SERVICE_WHAT.STOPWATCH_START)
        } else if (onOffButtonValue == getString(R.string.plank_stopwatch_pause)) {
            mPresenter!!.controlFromFrag(Constants.SERVICE_WHAT.STOPWATCH_PAUSE)
        } else if (onOffButtonValue == getString(R.string.plank_stopwatch_resume)) {
            mPresenter!!.controlFromFrag(Constants.SERVICE_WHAT.STOPWATCH_RESUME)
        }
    }

    internal var btnResetLapOnClickListener: Button.OnClickListener = View.OnClickListener {
        if (resetLapButtonValue == getString(R.string.plank_stopwatch_reset)) {
            mPresenter!!.controlFromFrag(Constants.SERVICE_WHAT.STOPWATCH_RESET)
        } else {
            mPresenter!!.controlFromFrag(Constants.SERVICE_WHAT.STOPWATCH_LAP)
        }
    }

    override fun setPresenter(presenter: StopwatchContract.Presenter) {
        mPresenter = checkNotNull(presenter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mContext = activity.applicationContext
        mIsViewsBound = false
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.plank_stopwatch_frag, container, false)
        ButterKnife.bind(this, rootView)

        mLayoutManager = LinearLayoutManager(mContext)
        mRecyclerView = mViewAnimator!!.getChildAt(Constants.COMMON_ANIMATOR_POSITION.RECYCLERVIEW) as RecyclerView
        mRecyclerView!!.setHasFixedSize(true)
        mRecyclerView!!.layoutManager = mLayoutManager
        mRecyclerView!!.addItemDecoration(MaterialViewPagerHeaderDecorator())
        mRecyclerView!!.viewTreeObserver.addOnGlobalLayoutListener {
            if (!mIsViewsBound) {
                mPresenter!!.bindViewsFromViewHolderToFrag()

                mIsViewsBound = true
            }
        }
        mRecyclerView!!.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                if (rv.childCount > 0) {
                    val childView = rv.findChildViewUnder(e.x, e.y)

                    if (rv.getChildAdapterPosition(childView) == Constants.RECYCLERVIEW_ADAPTER_VIEWTYPE.TYPE_HEAD) {
                        if (mPresenter!!.stopwatchStart) {
                            when (e.action) {
                                MotionEvent.ACTION_DOWN -> rv.requestDisallowInterceptTouchEvent(true)
                            }
                        }
                    }
                }

                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

            }
        })

        return rootView
    }

    override fun onStart() {
        super.onStart()

        mPresenter!!.start()
    }

    override fun onStop() {
        super.onStop()

        mIsViewsBound = false
    }

    override fun showLoading() {
        mViewAnimator!!.displayedChild = Constants.COMMON_ANIMATOR_POSITION.LOADING
    }

    override fun showStopwatch() {
        mViewAnimator!!.displayedChild = Constants.COMMON_ANIMATOR_POSITION.RECYCLERVIEW
    }

    override fun setLapTimeAdapter(lapTimeAdapter: Any) {
        mLapTimeListViewAdapter = lapTimeAdapter as LapTimeListViewAdapter
    }

    override fun setRecyclerViewAdapter(recyclerViewAdapter: Any) {
        mRecyclerView!!.adapter = recyclerViewAdapter as RecyclerViewAdapter
    }

    override fun bindViewsFromViewHolder(callback: StopwatchContract.BoundViewsCallback) {
        val holder = mRecyclerView!!.findViewHolderForAdapterPosition(0) as RecyclerViewAdapter.ViewHolder

        if (holder.itemViewType == Constants.RECYCLERVIEW_ADAPTER_VIEWTYPE.TYPE_HEAD) {
            tvHour = holder.tvHour
            tvMin = holder.tvMin
            tvSec = holder.tvSec
            tvMSec = holder.tvMSec
            lvLapTime = holder.lvLapTime
            btnOnOff = holder.btnOnOff
            btnResetLap = holder.btnResetLap

            btnOnOff!!.setOnClickListener(btnOnOffOnClickListener)
            btnResetLap!!.setOnClickListener(btnResetLapOnClickListener)

            lvLapTime!!.adapter = mLapTimeListViewAdapter
            lvLapTime!!.transcriptMode = ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL

            callback.onBoundViews()
        }
    }

    override fun setHour(hour: String) {
        tvHour!!.text = hour
    }

    override fun setMin(min: String) {
        tvMin!!.text = min
    }

    override fun setSec(sec: String) {
        tvSec!!.text = sec
    }

    override fun setMSec(mSec: String) {
        tvMSec!!.text = mSec
    }

    override fun setOnOffButtonEnabled(isEnabled: Boolean) {
        btnOnOff!!.isEnabled = isEnabled
    }

    override fun setResetLapButtonEnabled(isEnabled: Boolean) {
        btnResetLap!!.isEnabled = isEnabled
    }

    companion object {

        fun newInstance(): StopwatchFragment {
            return StopwatchFragment()
        }
    }
}
