package kr.kro.awesometic.plankhelper.plank.timer

import kr.kro.awesometic.plankhelper.BasePresenter
import kr.kro.awesometic.plankhelper.BaseView

/**
 * Created by Awesometic on 2017-04-19.
 */

interface TimerContract {

    interface BoundViewsCallback {
        fun onBoundViews()
    }

    interface View : BaseView<Presenter> {

        val activityContext: Any

        val timeString: String

        var hour: Int
        var min: Int
        var sec: Int

        var onOffButtonValue: String
        var resetLapButtonValue: String

        fun showLoading()
        fun showTimer()

        fun setLapTimeAdapter(lapTimeAdapter: Any)
        fun setRecyclerViewAdapter(recyclerViewAdapter: Any)

        fun bindViewsFromViewHolder(boundViewsCallback: BoundViewsCallback)
        fun numberPickerChangeValueByOne(type: Int, increment: Boolean)

        fun setAllNumberPickersEnabled(isEnabled: Boolean)
        fun setOnOffButtonEnabled(isEnabled: Boolean)
        fun setResetLapButtonEnabled(isEnabled: Boolean)
    }

    interface Presenter : BasePresenter {

        val timerStart: Boolean

        fun bindViewsFromViewHolderToFrag()

        fun controlFromFrag(what: Int)
    }
}
