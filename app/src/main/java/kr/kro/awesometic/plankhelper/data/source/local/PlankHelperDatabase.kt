package kr.kro.awesometic.plankhelper.data.source.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import kr.kro.awesometic.plankhelper.data.Lap
import kr.kro.awesometic.plankhelper.data.Plank

@Database(entities = [Plank::class, Lap::class], version = 1)
abstract class PlankHelperDatabase : RoomDatabase() {

    abstract fun plankHelperDao(): PlankHelperDao

    companion object {

        private var INSTANCE: PlankHelperDatabase? = null

        private val lock = Any()

        fun getInstance(context: Context): PlankHelperDatabase {
            synchronized(lock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            PlankHelperDatabase::class.java, "plankhelper.db")
                            .build()
                }
                return INSTANCE!!
            }
        }
    }

}