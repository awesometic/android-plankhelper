package kr.kro.awesometic.plankhelper.statistics.log

import android.content.Context

import java.util.ArrayList

import kr.kro.awesometic.plankhelper.data.LapTime
import kr.kro.awesometic.plankhelper.data.PlankLog
import kr.kro.awesometic.plankhelper.data.source.PlankLogsDataSource
import kr.kro.awesometic.plankhelper.data.source.PlankLogsRepository

import com.google.common.base.Preconditions.checkNotNull

/**
 * Created by Awesometic on 2017-05-17.
 */

class LogPresenter(plankLogsRepository: PlankLogsRepository,
                   stopwatchView: LogContract.View) : LogContract.Presenter {

    private val mPlankLogsRepository: PlankLogsRepository
    private val mLogView: LogContract.View

    private var mRecyclerViewAdapter: RecyclerViewAdapter? = null
    private var mApplicationContext: Context? = null

    init {
        mPlankLogsRepository = checkNotNull(plankLogsRepository, "plankLogsRepository cannot be null")
        mLogView = checkNotNull(stopwatchView, "logView cannot be null")

        mLogView.setPresenter(this)
    }

    override fun start() {
        initPresenter()
        initView()

        loadAllPlankLogs()
    }

    private fun initPresenter() {
        mApplicationContext = mLogView.applicationContext as Context
    }

    private fun initView() {
        mLogView.showLoading()

        mRecyclerViewAdapter = RecyclerViewAdapter()
        mLogView.setRecyclerViewAdapter(mRecyclerViewAdapter)

        mRecyclerViewAdapter!!.setPlankLogs(null)

        mLogView.showLog()
    }

    private fun loadAllPlankLogs() {
        mPlankLogsRepository.getPlankLogs(object : PlankLogsDataSource.LoadPlankLogsCallback {
            override fun onPlankLogsLoaded(plankLogs: List<PlankLog>) {
                for (plankLog in plankLogs) {

                    mPlankLogsRepository.getLapTimes(plankLog.id, object : PlankLogsDataSource.LoadLapTimesCallback {
                        override fun onLapTimesLoaded(lapTimes: List<LapTime>) {
                            plankLog.lapTimes = lapTimes as ArrayList<LapTime>

                            mRecyclerViewAdapter!!.addPlankLog(plankLog)
                            mRecyclerViewAdapter!!.notifyDataSetChanged()
                        }

                        override fun onDataNotAvailable() {
                            mRecyclerViewAdapter!!.addPlankLog(plankLog)
                            mRecyclerViewAdapter!!.notifyDataSetChanged()
                        }
                    })
                }
            }

            override fun onDataNotAvailable() {

            }
        })
    }

    override fun bindViewsFromViewHolderToFrag() {
        mLogView.bindViewsFromViewHolder()
    }
}