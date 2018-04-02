package kr.kro.awesometic.plankhelper.data.source.local

import kr.kro.awesometic.plankhelper.data.Lap
import kr.kro.awesometic.plankhelper.data.Plank
import kr.kro.awesometic.plankhelper.data.source.PlankHelperDataSource
import kr.kro.awesometic.plankhelper.util.AppExecutors

class PlankHelperLocalDataSource private constructor(
        val appExecutors: AppExecutors,
        val plankHelperDao: PlankHelperDao
) : PlankHelperDataSource {

    override fun getPlanks(callback: PlankHelperDataSource.LoadPlanksCallback) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPlank(entryId: String, callback: PlankHelperDataSource.GetPlankCallback) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLaps(callback: PlankHelperDataSource.LoadLapsCallback) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLapsByPlankId(plankId: String, callback: PlankHelperDataSource.LoadLapsCallback) {
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