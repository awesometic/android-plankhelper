package kr.kro.awesometic.plankhelper.data.source

import kr.kro.awesometic.plankhelper.data.Lap
import kr.kro.awesometic.plankhelper.data.Plank

interface PlankHelperDataSource {

    interface LoadPlankCallback {

        fun onPlanksLoaded(plank: List<Plank>)

        fun onDataNotAvailable()
    }

    interface GetPlankCallback {

        fun onPlankLoaded(plank: Plank)

        fun onDataNotAvailable()
    }

    interface LoadLapCallback {

        fun onLapsLoaded(lap: List<Lap>)

        fun onDataNotAvailable()
    }

    interface GetLapCallback {

        fun onLapLoaded(lap: Lap)

        fun onDataNotAvailable()
    }

    fun getPlanks(callback: LoadPlankCallback)

    fun getPlank(entryId: String, callback: GetPlankCallback)

    fun getLaps(callback: LoadLapCallback)

    fun getLapsByPlankId(plankId: String, callback: LoadLapCallback)

    fun savePlank(plank: Plank)

    fun saveLap(lap: Lap)

    fun deletePlank(entryId: String)

    fun deleteLap(entryId: String)
}