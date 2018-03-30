package kr.kro.awesometic.plankhelper.statistics.chart

import kr.kro.awesometic.plankhelper.BasePresenter
import kr.kro.awesometic.plankhelper.BaseView

/**
 * Created by Awesometic on 2017-05-17.
 */

interface ChartContract {

    interface View : BaseView<Presenter> {

        val applicationContext: Any

        fun showLoading()
        fun showChart()
        fun setRecyclerViewAdapter(recyclerViewAdapter: Any)

        fun bindViewsFromViewHolder()
    }

    interface Presenter : BasePresenter {
        fun bindViewsFromViewHolderToFrag()
    }
}
