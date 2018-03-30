package kr.kro.awesometic.plankhelper.statistics.chart

import android.content.Context

import java.util.ArrayList

import kr.kro.awesometic.plankhelper.data.PlankLog
import kr.kro.awesometic.plankhelper.data.source.PlankLogsDataSource
import kr.kro.awesometic.plankhelper.data.source.PlankLogsRepository

import com.google.common.base.Preconditions.checkNotNull

/**
 * Created by Awesometic on 2017-05-17.
 */

class ChartPresenter(plankLogsRepository: PlankLogsRepository,
                     stopwatchView: ChartContract.View) : ChartContract.Presenter {

    private val mPlankLogsRepository: PlankLogsRepository
    private val mChartView: ChartContract.View

    private var mRecyclerViewAdapter: RecyclerViewAdapter? = null
    private var mApplicationContext: Context? = null

    init {
        mPlankLogsRepository = checkNotNull(plankLogsRepository, "plankLogsRepository cannot be null")
        mChartView = checkNotNull(stopwatchView, "chartView cannot be null")

        mChartView.setPresenter(this)
    }

    override fun start() {
        initPresenter()
        initView()
    }

    private fun initPresenter() {
        mApplicationContext = mChartView.applicationContext as Context
    }

    private fun initView() {
        mChartView.showLoading()

        mRecyclerViewAdapter = RecyclerViewAdapter()
        mChartView.setRecyclerViewAdapter(mRecyclerViewAdapter)

        mPlankLogsRepository.getPlankLogs(object : PlankLogsDataSource.LoadPlankLogsCallback {
            override fun onPlankLogsLoaded(plankLogs: List<PlankLog>) {
                mRecyclerViewAdapter!!.setPlankLogs(plankLogs as ArrayList<PlankLog>)
                mRecyclerViewAdapter!!.notifyDataSetChanged()

                mChartView.showChart()
            }

            override fun onDataNotAvailable() {
                mRecyclerViewAdapter!!.setPlankLogs(null)

                mChartView.showChart()
            }
        })
    }

    override fun bindViewsFromViewHolderToFrag() {
        mChartView.bindViewsFromViewHolder()
    }
}