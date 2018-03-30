package kr.kro.awesometic.plankhelper.plank.stopwatch

import kr.kro.awesometic.plankhelper.BasePresenter
import kr.kro.awesometic.plankhelper.BaseView

/**
 * Created by Awesometic on 2017-04-19.
 */

interface StopwatchContract {

    interface BoundViewsCallback {
        fun onBoundViews()
    }

    interface View : BaseView<Presenter> {

        val activityContext: Any

        val timeString: String

        var onOffButtonValue: String
        var resetLapButtonValue: String

        fun showLoading()
        fun showStopwatch()

        fun setRecyclerViewAdapter(recyclerViewAdapter: Any)
        fun setLapTimeAdapter(lapTimeAdapter: Any)

        fun bindViewsFromViewHolder(boundViewsCallback: BoundViewsCallback)
        fun setHour(hour: String)
        fun setMin(min: String)
        fun setSec(sec: String)
        fun setMSec(mSec: String)

        fun setOnOffButtonEnabled(isEnabled: Boolean)
        fun setResetLapButtonEnabled(isEnabled: Boolean)
    }

    interface Presenter : BasePresenter {

        val stopwatchStart: Boolean

        fun bindViewsFromViewHolderToFrag()

        fun controlFromFrag(what: Int)
    }
}
