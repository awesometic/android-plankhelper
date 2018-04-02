package kr.kro.awesometic.plankhelper.data.source

import kr.kro.awesometic.plankhelper.data.Lap
import kr.kro.awesometic.plankhelper.data.Plank
import kr.kro.awesometic.plankhelper.data.source.local.PlankHelperLocalDataSource
import kr.kro.awesometic.plankhelper.data.source.remote.PlankHelperRemoteDataSource

class PlankHelperRepository(
        val plankHelperLocalDataSource: PlankHelperLocalDataSource,
        val plankHelperRemoteDataSource: PlankHelperRemoteDataSource
) : PlankHelperDataSource {

    override fun getPlanks(callback: PlankHelperDataSource.LoadPlankCallback) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPlank(entryId: String, callback: PlankHelperDataSource.GetPlankCallback) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLaps(callback: PlankHelperDataSource.LoadLapCallback) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLapsByPlankId(plankId: String, callback: PlankHelperDataSource.LoadLapCallback) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun savePlank(plank: Plank) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveLap(lap: Lap) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deletePlank(entryId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteLap(entryId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}