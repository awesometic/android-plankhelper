package kr.kro.awesometic.plankhelper.data.source

import kr.kro.awesometic.plankhelper.data.Lap
import kr.kro.awesometic.plankhelper.data.Plank
import kr.kro.awesometic.plankhelper.data.source.local.PlankHelperLocalDataSource
import kr.kro.awesometic.plankhelper.data.source.remote.PlankHelperRemoteDataSource

class PlankHelperRepository(
        val plankHelperRemoteDataSource: PlankHelperRemoteDataSource,
        val plankHelperLocalDataSource: PlankHelperLocalDataSource
) : PlankHelperDataSource {

    var cachedPlanks: LinkedHashMap<String, Plank> = LinkedHashMap()
    var cachedLaps: LinkedHashMap<String, Lap> = LinkedHashMap()

    var cachePlankIsDirty = false
    var cacheLapIsDirty = false

    override fun getPlanks(callback: PlankHelperDataSource.LoadPlanksCallback) {
        if (cachedPlanks.isNotEmpty() && !cachePlankIsDirty) {
            callback.onPlanksLoaded(ArrayList(cachedPlanks.values))
            return
        }

        if (cachePlankIsDirty) {

        } else {
            plankHelperLocalDataSource.getPlanks(object : PlankHelperDataSource.LoadPlanksCallback {
                override fun onPlanksLoaded(planks: List<Plank>) {
                    refreshCachePlank(planks)
                    callback.onPlanksLoaded(ArrayList(cachedPlanks.values))
                }

                override fun onDataNotAvailable() {

                }
            })
        }
    }

    override fun getPlank(entryId: String, callback: PlankHelperDataSource.GetPlankCallback) {
        val plankInCache = getPlankWithId(entryId)

        if (plankInCache != null) {
            callback.onPlankLoaded(plankInCache)
            return
        }

        plankHelperLocalDataSource.getPlank(entryId, object : PlankHelperDataSource.GetPlankCallback {
            override fun onPlankLoaded(plank: Plank) {
                cachePlankAndPerform(plank) {
                    callback.onPlankLoaded(it)
                }
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        })
    }

    override fun getLaps(callback: PlankHelperDataSource.LoadLapsCallback) {
        if (cachedLaps.isNotEmpty() && !cacheLapIsDirty) {
            callback.onLapsLoaded(ArrayList(cachedLaps.values))
            return
        }

        if (cacheLapIsDirty) {
            plankHelperLocalDataSource.getLaps(object : PlankHelperDataSource.LoadLapsCallback {
                override fun onLapsLoaded(laps: List<Lap>) {
                    refreshCacheLap(laps)
                    callback.onLapsLoaded(ArrayList(cachedLaps.values))
                }

                override fun onDataNotAvailable() {

                }
            })
        }
    }

    override fun getLapsByPlankId(plankId: String, callback: PlankHelperDataSource.LoadLapsCallback) {
        plankHelperLocalDataSource.getLapsByPlankId(plankId, object : PlankHelperDataSource.LoadLapsCallback {
            override fun onLapsLoaded(laps: List<Lap>) {
                refreshCacheLap(laps)
                callback.onLapsLoaded(ArrayList(cachedLaps.values))
            }

            override fun onDataNotAvailable() {

            }
        })
    }

    override fun savePlank(plank: Plank) {
        cachePlankAndPerform(plank) {
            plankHelperLocalDataSource.savePlank(plank)
        }
    }

    override fun saveLap(lap: Lap) {
        cacheLapAndPerform(lap) {
            plankHelperLocalDataSource.saveLap(lap)
        }
    }

    override fun deletePlank(entryId: String) {
        plankHelperLocalDataSource.deletePlank(entryId)
        cachedPlanks.remove(entryId)
    }

    override fun deleteLap(entryId: String) {
        plankHelperLocalDataSource.deleteLap(entryId)
        cachedLaps.remove(entryId)
    }

    private fun refreshCachePlank(planks: List<Plank>) {
        cachedPlanks.clear()
        planks.forEach {
            cachePlankAndPerform(it) {}
        }
        cachePlankIsDirty = false
    }

    private fun refreshCacheLap(laps: List<Lap>) {
        cachedLaps.clear()
        laps.forEach {
            cacheLapAndPerform(it) {}
        }
        cacheLapIsDirty = false
    }

    private fun getPlankWithId(id: String) = cachedPlanks[id]

    private inline fun cachePlankAndPerform(plank: Plank, perform: (Plank) -> Unit) {
        val cachedPlank = Plank(plank.entryId, plank.datetime, plank.duration, plank.type, plank.laps).apply {

        }

        cachedPlanks.put(cachedPlank.entryId, cachedPlank)
        perform(cachedPlank)
    }

    private inline fun cacheLapAndPerform(lap: Lap, perform: (Lap) -> Unit) {
        val cachedLap = Lap(lap.entryId, lap.plankId, lap.orderNum, lap.passedTime).apply {

        }

        cachedLaps.put(cachedLap.entryId, cachedLap)
        perform(cachedLap)
    }

    companion object {

        private var INSTANCE: PlankHelperRepository? = null

        @JvmStatic fun getInstance(plankHelperRemoteDataSource: PlankHelperRemoteDataSource,
                                   plankHelperLocalDataSource: PlankHelperLocalDataSource) : PlankHelperRepository {
            return INSTANCE ?: PlankHelperRepository(plankHelperRemoteDataSource, plankHelperLocalDataSource)
                    .apply { INSTANCE = this }
        }

        @JvmStatic fun destroyInstance() {
            INSTANCE = null
        }
    }
}