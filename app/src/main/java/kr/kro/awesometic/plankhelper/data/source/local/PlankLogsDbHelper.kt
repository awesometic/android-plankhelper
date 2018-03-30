package kr.kro.awesometic.plankhelper.data.source.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by Awesometic on 2017-04-15.
 */

class PlankLogsDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_PLANKLOG_ENTRIES)
        db.execSQL(SQL_CREATE_LAPTIME_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + PlankLogsPersistentContract.PlankLogEntry.TABLE_NAME)
        db.execSQL("DROP TABLE IF EXISTS " + PlankLogsPersistentContract.LapTimeEntry.TABLE_NAME)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        super.onDowngrade(db, oldVersion, newVersion)
    }

    companion object {

        val DATABASE_VERSION = 4
        val DATABASE_NAME = "PlankLog.db"

        private val SQL_CREATE_PLANKLOG_ENTRIES = "CREATE TABLE " + PlankLogsPersistentContract.PlankLogEntry.TABLE_NAME + " (" +
                PlankLogsPersistentContract.PlankLogEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PlankLogsPersistentContract.PlankLogEntry.COLUMN_NAME_ENTRY_ID + " TEXT NOT NULL, " +
                PlankLogsPersistentContract.PlankLogEntry.COLUMN_NAME_DATETIME + " TEXT NOT NULL, " +
                PlankLogsPersistentContract.PlankLogEntry.COLUMN_NAME_DURATION + " INTEGER NOT NULL, " +
                PlankLogsPersistentContract.PlankLogEntry.COLUMN_NAME_METHOD + " TEXT NOT NULL, " +
                PlankLogsPersistentContract.PlankLogEntry.COLUMN_NAME_LAP_COUNT + " INTEGER NOT NULL" +
                " )"

        private val SQL_CREATE_LAPTIME_ENTRIES = "CREATE TABLE " + PlankLogsPersistentContract.LapTimeEntry.TABLE_NAME + " (" +
                PlankLogsPersistentContract.LapTimeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PlankLogsPersistentContract.LapTimeEntry.COLUMN_NAME_ENTRY_ID + " TEXT NOT NULL, " +
                PlankLogsPersistentContract.LapTimeEntry.COLUMN_NAME_PARENT_ENTRY_ID + " TEXT NOT NULL, " +
                PlankLogsPersistentContract.LapTimeEntry.COLUMN_NAME_ORDER_NUMBER + " INTEGER NOT NULL, " +
                PlankLogsPersistentContract.LapTimeEntry.COLUMN_NAME_PASSED_TIME + " TEXT NOT NULL, " +
                PlankLogsPersistentContract.LapTimeEntry.COLUMN_NAME_LEFT_TIME + " TEXT, " +
                PlankLogsPersistentContract.LapTimeEntry.COLUMN_NAME_INTERVAL + " TEXT NOT NULL " +
                " )"
    }
}
