package kr.kro.awesometic.plankhelper.statistics.log

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

import butterknife.BindView
import butterknife.ButterKnife
import kr.kro.awesometic.plankhelper.R
import kr.kro.awesometic.plankhelper.util.Constants

import com.google.common.base.Preconditions.checkNotNull

/**
 * Created by Awesometic on 2017-05-17.
 */

class LogFragment : Fragment(), LogContract.View {

    private var mPresenter: LogContract.Presenter? = null
    private var mContext: Context? = null

    @BindView(R.id.statistics_log_frag_animator)
    internal var mViewAnimator: ViewAnimator? = null

    private var mRecyclerView: RecyclerView? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null

    private var mIsViewsBound: Boolean = false

    override val applicationContext: Any?
        get() = mContext

    override fun setPresenter(presenter: LogContract.Presenter) {
        mPresenter = checkNotNull(presenter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mContext = activity.applicationContext
        mIsViewsBound = false
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.statistics_log_frag, container, false)
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

    override fun showLoading() {
        mViewAnimator!!.displayedChild = Constants.COMMON_ANIMATOR_POSITION.LOADING
    }

    override fun showLog() {
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

        fun newInstance(): LogFragment {
            return LogFragment()
        }
    }
}
