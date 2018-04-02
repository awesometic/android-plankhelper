package kr.kro.awesometic.plankhelper.data.source

import kr.kro.awesometic.plankhelper.data.Lap
import kr.kro.awesometic.plankhelper.data.Plank

interface PlankHelperDataSource {

    interface LoadPlanksCallback {

        fun onPlanksLoaded(planks: List<Plank>)

        fun onDataNotAvailable()
    }

    interface GetPlankCallback {

        fun onPlankLoaded(plank: Plank)

        fun onDataNotAvailable()
    }

    interface LoadLapsCallback {

        fun onLapsLoaded(laps: List<Lap>)

        fun onDataNotAvailable()
    }

    interface GetLapCallback {

        fun onLapLoaded(lap: Lap)

        fun onDataNotAvailable()
    }

    fun getPlanks(callback: LoadPlanksCallback)

    fun getPlank(entryId: String, callback: GetPlankCallback)

    fun getLaps(callback: LoadLapsCallback)

    fun getLapsByPlankId(plankId: String, callback: LoadLapsCallback)

    fun savePlank(plank: Plank)

    fun saveLap(lap: Lap)

    fun deletePlank(entryId: String)

    fun deleteLap(entryId: String)
}