package kr.kro.awesometic.plankhelper.statistics.calendar

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ViewAnimator

import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener

import butterknife.BindView
import butterknife.ButterKnife
import kr.kro.awesometic.plankhelper.R
import kr.kro.awesometic.plankhelper.util.Constants

import com.google.common.base.Preconditions.checkNotNull

/**
 * Created by Awesometic on 2017-05-17.
 */

class CalendarFragment : Fragment(), CalendarContract.View, OnDateSelectedListener, OnMonthChangedListener {

    private var mPresenter: CalendarContract.Presenter? = null
    private var mContext: Context? = null

    @BindView(R.id.statistics_calendar_frag_animator)
    internal var mViewAnimator: ViewAnimator? = null

    // ButterKnife 가 아니라 mViewAnimator 에 의해 초기화 됨
    private var mRecyclerView: RecyclerView? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null

    private var mIsViewsBound: Boolean = false
    private val mMaterialCalendarView: MaterialCalendarView? = null

    override val applicationContext: Any?
        get() = mContext

    override fun setPresenter(presenter: CalendarContract.Presenter) {
        mPresenter = checkNotNull(presenter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mContext = activity.applicationContext
        mIsViewsBound = false
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.statistics_calendar_frag, container, false)
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

        return rootView
    }

    override fun onStart() {
        super.onStart()

        mPresenter!!.start()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDateSelected(widget: MaterialCalendarView, date: CalendarDay, selected: Boolean) {

    }

    override fun onMonthChanged(widget: MaterialCalendarView, date: CalendarDay) {

    }

    override fun showLoading() {
        mViewAnimator!!.displayedChild = Constants.COMMON_ANIMATOR_POSITION.LOADING
    }

    override fun showCalendar() {
        mViewAnimator!!.displayedChild = Constants.COMMON_ANIMATOR_POSITION.RECYCLERVIEW
    }

    override fun setRecyclerViewAdapter(recyclerViewAdapter: Any) {
        mRecyclerView!!.adapter = recyclerViewAdapter as RecyclerViewAdapter
    }

    override fun bindViewsFromViewHolder() {
        val holder = mRecyclerView!!.findViewHolderForAdapterPosition(0) as RecyclerViewAdapter.ViewHolder

        if (holder.itemViewType == Constants.RECYCLERVIEW_ADAPTER_VIEWTYPE.TYPE_HEAD) {

        }
    }

    companion object {

        fun newInstance(): CalendarFragment {
            return CalendarFragment()
        }
    }
}
