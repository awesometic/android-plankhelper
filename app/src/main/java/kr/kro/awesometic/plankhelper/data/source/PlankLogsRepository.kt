package kr.kro.awesometic.plankhelper.data.source
/*
import java.util.ArrayList
import java.util.LinkedHashMap

import kr.kro.awesometic.plankhelper.data.LapTime
import kr.kro.awesometic.plankhelper.data.PlankLog

import com.google.common.base.Preconditions.checkNotNull


class PlankLogsRepository(plankLogsLocalDataSource: PlankLogsDataSource) : PlankLogsDataSource {
    private val mPlankLogsLocalDataSource: PlankLogsDataSource

    internal var mCachedPlankLogs: MutableMap<String, PlankLog>? = null

    internal var mCacheIsDirty = false

    init {
        mPlankLogsLocalDataSource = checkNotNull(plankLogsLocalDataSource)
    }

    override fun getPlankLogs(callback: PlankLogsDataSource.LoadPlankLogsCallback) {
        checkNotNull(callback)

        if (mCachedPlankLogs != null && !mCacheIsDirty) {
            callback.onPlankLogsLoaded(ArrayList(mCachedPlankLogs!!.values))
            return
        }

        if (mCacheIsDirty) {

        } else {
            mPlankLogsLocalDataSource.getPlankLogs(object : PlankLogsDataSource.LoadPlankLogsCallback {
                override fun onPlankLogsLoaded(plankLogs: List<PlankLog>) {
                    refreshCache(plankLogs)
                    callback.onPlankLogsLoaded(ArrayList(mCachedPlankLogs!!.values))
                }

                override fun onDataNotAvailable() {

                }
            })
        }
    }

    override fun getPlankLog(plankLogId: String, callback: PlankLogsDataSource.GetPlankLogCallback) {
        checkNotNull(plankLogId)
        checkNotNull(callback)

        val cachedPlankLog = getPlankLogWithId(plankLogId)

        if (cachedPlankLog != null) {
            callback.onPlankLogLoaded(cachedPlankLog)
            return
        }

        mPlankLogsLocalDataSource.getPlankLog(plankLogId, object : PlankLogsDataSource.GetPlankLogCallback {
            override fun onPlankLogLoaded(plankLog: PlankLog) {
                if (mCachedPlankLogs == null) {
                    mCachedPlankLogs = LinkedHashMap()
                }
                mCachedPlankLogs!![plankLog.id] = plankLog
                callback.onPlankLogLoaded(plankLog)
            }

            override fun onDataNotAvailable() {

            }
        })
    }

    override fun savePlankLog(plankLog: PlankLog, callback: PlankLogsDataSource.SavePlankLogCallback) {
        checkNotNull(plankLog)

        mPlankLogsLocalDataSource.savePlankLog(plankLog, callback)

        if (mCachedPlankLogs == null) {
            mCachedPlankLogs = LinkedHashMap()
        }
        mCachedPlankLogs!![plankLog.id] = plankLog
    }

    override fun deletePlankLog(plankLogId: String) {
        mPlankLogsLocalDataSource.deletePlankLog(plankLogId)

        mCachedPlankLogs!!.clear()
    }

    override fun deleteAllPlankLogs() {
        mPlankLogsLocalDataSource.deleteAllPlankLogs()

        if (mCachedPlankLogs == null) {
            mCachedPlankLogs = LinkedHashMap()
        }
        mCachedPlankLogs!!.clear()
    }

    private fun refreshCache(plankLogs: List<PlankLog>) {
        if (mCachedPlankLogs == null) {
            mCachedPlankLogs = LinkedHashMap()
        }
        mCachedPlankLogs!!.clear()
        for (plankLog in plankLogs) {
            mCachedPlankLogs!![plankLog.id] = plankLog
        }
        mCacheIsDirty = false
    }

    private fun refreshLocalDataSource(plankLogs: List<PlankLog>) {
        mPlankLogsLocalDataSource.deleteAllPlankLogs()
        for (plankLog in plankLogs) {
            mPlankLogsLocalDataSource.savePlankLog(plankLog) { }
        }
    }

    private fun getPlankLogWithId(id: String): PlankLog? {
        checkNotNull(id)
        return if (mCachedPlankLogs == null || mCachedPlankLogs!!.isEmpty()) {
            null
        } else {
            mCachedPlankLogs!![id]
        }
    }

    override fun getLapTimes(plankLogId: String, callback: PlankLogsDataSource.LoadLapTimesCallback) {
        checkNotNull(plankLogId)

        mPlankLogsLocalDataSource.getLapTimes(plankLogId, object : PlankLogsDataSource.LoadLapTimesCallback {
            override fun onLapTimesLoaded(lapTimes: List<LapTime>) {
                callback.onLapTimesLoaded(lapTimes)
            }

            override fun onDataNotAvailable() {

            }
        })
    }

    override fun saveLapTimes(plankLogId: String, lapTimes: List<LapTime>) {
        checkNotNull(plankLogId)
        checkNotNull(lapTimes)

        mPlankLogsLocalDataSource.saveLapTimes(plankLogId, lapTimes)
    }

    override fun deleteLapTimes(lapTimeId: String) {
        checkNotNull(lapTimeId)

        mPlankLogsLocalDataSource.deleteLapTimes(lapTimeId)
    }

    override fun deleteAllLapTimes(plankLogId: String) {
        checkNotNull(plankLogId)

        mPlankLogsLocalDataSource.deleteAllLapTimes(plankLogId)
    }

    companion object {

        private var INSTANCE: PlankLogsRepository? = null

        fun getInstance(plankLogsLocalDataSource: PlankLogsDataSource): PlankLogsRepository {
            if (INSTANCE == null) {
                INSTANCE = PlankLogsRepository(plankLogsLocalDataSource)
            }

            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
*/