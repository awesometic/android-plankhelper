package kr.kro.awesometic.plankhelper.data.source.local

import android.arch.persistence.room.*
import kr.kro.awesometic.plankhelper.data.Lap
import kr.kro.awesometic.plankhelper.data.Plank

@Dao interface PlankHelperDao {

    @Query("SELECT * FROM plank") fun getPlanks(): List<Plank>

    @Query("SELECT * FROM plank WHERE entry_id = :plankId") fun getPlankById(plankId: String)

    @Query("SELECT * FROM lap") fun getLaps(): List<Lap>

    @Query("SELECT * FROM lap WHERE plank_id = :plankId") fun getLapsByPlankId(plankId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE) fun insertPlank(plank: Plank)

    @Insert(onConflict = OnConflictStrategy.REPLACE) fun insertLap(lap: Lap)

    @Query("DELETE FROM plank WHERE entry_id = :entryId") fun deletePlankById(entryId: String)

    @Query("DELETE FROM lap WHERE entry_id = :entryId") fun deleteLapById(entryId: String)
}