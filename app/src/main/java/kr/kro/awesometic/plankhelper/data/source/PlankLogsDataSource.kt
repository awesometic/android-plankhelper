package kr.kro.awesometic.plankhelper.data.source
/*
import kr.kro.awesometic.plankhelper.data.LapTime
import kr.kro.awesometic.plankhelper.data.PlankLog


interface PlankLogsDataSource {

    interface LoadPlankLogsCallback {

        fun onPlankLogsLoaded(plankLogs: List<PlankLog>)

        fun onDataNotAvailable()
    }

    interface GetPlankLogCallback {

        fun onPlankLogLoaded(plankLog: PlankLog)

        fun onDataNotAvailable()
    }

    interface SavePlankLogCallback {

        fun onSavePlankLog(isSuccess: Boolean)
    }

    fun getPlankLogs(callback: LoadPlankLogsCallback)

    fun getPlankLog(plankLogId: String, callback: GetPlankLogCallback)

    fun savePlankLog(plankLog: PlankLog, callback: SavePlankLogCallback)

    fun deletePlankLog(plankLogId: String)

    fun deleteAllPlankLogs()


    // 한 번에 하나의 PlankLog에 대한 모든 LapTime을 불러옴
    interface LoadLapTimesCallback {

        fun onLapTimesLoaded(lapTimes: List<LapTime>)

        fun onDataNotAvailable()
    }

    fun getLapTimes(plankLogId: String, callback: LoadLapTimesCallback)

    fun saveLapTimes(plankLogId: String, lapTimes: List<LapTime>)

    fun deleteLapTimes(lapTimeId: String)

    // 해당 PlankLog의 모든 LapTime을 지움
    fun deleteAllLapTimes(plankLogId: String)
}
*/