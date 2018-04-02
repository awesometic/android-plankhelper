package kr.kro.awesometic.plankhelper.data.source.local

/*
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

import java.util.ArrayList

import kr.kro.awesometic.plankhelper.data.LapTime
import kr.kro.awesometic.plankhelper.data.PlankLog
import kr.kro.awesometic.plankhelper.data.source.PlankLogsDataSource
import kr.kro.awesometic.plankhelper.data.source.local.PlankLogsPersistentContract.LapTimeEntry
import kr.kro.awesometic.plankhelper.data.source.local.PlankLogsPersistentContract.PlankLogEntry

import com.google.common.base.Preconditions.checkNotNull


class PlankLogsLocalDataSource private constructor(context: Context) : PlankLogsDataSource {
    private val mDbHelper: PlankLogsDbHelper

    init {
        checkNotNull(context)
        mDbHelper = PlankLogsDbHelper(context)
    }

    override fun getPlankLogs(callback: PlankLogsDataSource.LoadPlankLogsCallback) {
        val plankLogs = ArrayList<PlankLog>()
        val db = mDbHelper.readableDatabase

        val projection = arrayOf(PlankLogEntry.COLUMN_NAME_ENTRY_ID, PlankLogEntry.COLUMN_NAME_DATETIME, PlankLogEntry.COLUMN_NAME_DURATION, PlankLogEntry.COLUMN_NAME_METHOD, PlankLogEntry.COLUMN_NAME_LAP_COUNT)

        val c = db.query(PlankLogEntry.TABLE_NAME, projection, null, null, null, null, null)

        if (c != null && c.count > 0) {
            while (c.moveToNext()) {
                val itemId = c.getString(c.getColumnIndexOrThrow(PlankLogEntry.COLUMN_NAME_ENTRY_ID))
                val datetime = c.getString(c.getColumnIndexOrThrow(PlankLogEntry.COLUMN_NAME_DATETIME))
                val duration = c.getInt(c.getColumnIndexOrThrow(PlankLogEntry.COLUMN_NAME_DURATION))
                val method = c.getString(c.getColumnIndexOrThrow(PlankLogEntry.COLUMN_NAME_METHOD))
                val lapCount = c.getInt(c.getColumnIndexOrThrow(PlankLogEntry.COLUMN_NAME_LAP_COUNT))

                val plankLog = PlankLog(itemId, datetime, duration.toLong(), method, lapCount, ArrayList())
                plankLogs.add(plankLog)
            }
        }
        c?.close()

        db.close()

        if (plankLogs.isEmpty()) {
            callback.onDataNotAvailable()
        } else {
            callback.onPlankLogsLoaded(plankLogs)
        }
    }

    override fun getPlankLog(plankLogId: String, callback: PlankLogsDataSource.GetPlankLogCallback) {
        val db = mDbHelper.readableDatabase

        val projection = arrayOf(PlankLogEntry.COLUMN_NAME_ENTRY_ID, PlankLogEntry.COLUMN_NAME_DATETIME, PlankLogEntry.COLUMN_NAME_DURATION, PlankLogEntry.COLUMN_NAME_METHOD, PlankLogEntry.COLUMN_NAME_LAP_COUNT)

        val selection = PlankLogEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?"
        val selectionArgs = arrayOf(plankLogId)

        val c = db.query(PlankLogEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null)

        var plankLog: PlankLog? = null

        if (c != null && c.count > 0) {
            c.moveToFirst()
            val itemId = c.getString(c.getColumnIndexOrThrow(PlankLogEntry.COLUMN_NAME_ENTRY_ID))
            val datetime = c.getString(c.getColumnIndexOrThrow(PlankLogEntry.COLUMN_NAME_DATETIME))
            val duration = c.getInt(c.getColumnIndexOrThrow(PlankLogEntry.COLUMN_NAME_DURATION))
            val method = c.getString(c.getColumnIndexOrThrow(PlankLogEntry.COLUMN_NAME_METHOD))
            val lapCount = c.getInt(c.getColumnIndexOrThrow(PlankLogEntry.COLUMN_NAME_LAP_COUNT))

            plankLog = PlankLog(itemId, datetime, duration.toLong(), method, lapCount, ArrayList())
        }

        c?.close()

        db.close()

        if (plankLog == null) {
            callback.onDataNotAvailable()
        } else {
            callback.onPlankLogLoaded(plankLog)
        }
    }

    override fun savePlankLog(plankLog: PlankLog, callback: PlankLogsDataSource.SavePlankLogCallback) {
        checkNotNull(plankLog)
        val db = mDbHelper.writableDatabase

        val values = ContentValues()
        values.put(PlankLogEntry.COLUMN_NAME_ENTRY_ID, plankLog.id)
        values.put(PlankLogEntry.COLUMN_NAME_DATETIME, plankLog.datetime)
        values.put(PlankLogEntry.COLUMN_NAME_DURATION, plankLog.duration)
        values.put(PlankLogEntry.COLUMN_NAME_METHOD, plankLog.method)
        values.put(PlankLogEntry.COLUMN_NAME_LAP_COUNT, plankLog.lapCount)

        if (db.insert(PlankLogEntry.TABLE_NAME, null, values) > -1) {
            callback.onSavePlankLog(true)
        } else {
            callback.onSavePlankLog(false)
        }

        db.close()
    }

    override fun deletePlankLog(plankLogId: String) {
        val db = mDbHelper.writableDatabase

        val selection = PlankLogEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?"
        val selectionArgs = arrayOf(plankLogId)

        db.delete(PlankLogEntry.TABLE_NAME, selection, selectionArgs)

        db.close()
    }

    override fun deleteAllPlankLogs() {
        val db = mDbHelper.writableDatabase

        db.delete(PlankLogEntry.TABLE_NAME, null, null)

        db.close()
    }

    override fun getLapTimes(plankLogId: String, callback: PlankLogsDataSource.LoadLapTimesCallback) {
        val lapTimes = ArrayList<LapTime>()
        val db = mDbHelper.readableDatabase

        val projection = arrayOf(LapTimeEntry.COLUMN_NAME_ENTRY_ID, LapTimeEntry.COLUMN_NAME_PARENT_ENTRY_ID, LapTimeEntry.COLUMN_NAME_ORDER_NUMBER, LapTimeEntry.COLUMN_NAME_PASSED_TIME, LapTimeEntry.COLUMN_NAME_LEFT_TIME, LapTimeEntry.COLUMN_NAME_INTERVAL)

        val selection = LapTimeEntry.COLUMN_NAME_PARENT_ENTRY_ID + " LIKE ?"
        val selectionArgs = arrayOf(plankLogId)

        val c = db.query(LapTimeEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null)

        if (c != null && c.count > 0) {
            while (c.moveToNext()) {
                val entryId = c.getString(c.getColumnIndexOrThrow(LapTimeEntry.COLUMN_NAME_ENTRY_ID))
                val parentEntryId = c.getString(c.getColumnIndexOrThrow(LapTimeEntry.COLUMN_NAME_PARENT_ENTRY_ID))
                val orderNumber = c.getInt(c.getColumnIndexOrThrow(LapTimeEntry.COLUMN_NAME_ORDER_NUMBER))
                val passedTime = c.getString(c.getColumnIndexOrThrow(LapTimeEntry.COLUMN_NAME_PASSED_TIME))
                val leftTime = c.getString(c.getColumnIndexOrThrow(LapTimeEntry.COLUMN_NAME_LEFT_TIME))
                val interval = c.getString(c.getColumnIndexOrThrow(LapTimeEntry.COLUMN_NAME_INTERVAL))

                val lapTime = LapTime(entryId, parentEntryId, orderNumber, passedTime, leftTime, interval)
                lapTimes.add(lapTime)
            }
        }
        c?.close()

        db.close()

        if (lapTimes.isEmpty()) {
            callback.onDataNotAvailable()
        } else {
            callback.onLapTimesLoaded(lapTimes)
        }
    }

    override fun saveLapTimes(plankLogId: String, lapTimes: List<LapTime>) {
        checkNotNull(lapTimes)

        val db = mDbHelper.writableDatabase

        for (lapTime in lapTimes) {
            val values = ContentValues()
            values.put(LapTimeEntry.COLUMN_NAME_ENTRY_ID, lapTime.id)
            values.put(LapTimeEntry.COLUMN_NAME_PARENT_ENTRY_ID, plankLogId)
            values.put(LapTimeEntry.COLUMN_NAME_ORDER_NUMBER, lapTime.orderNumber)
            values.put(LapTimeEntry.COLUMN_NAME_PASSED_TIME, lapTime.passedTime)
            values.put(LapTimeEntry.COLUMN_NAME_LEFT_TIME, lapTime.leftTime)
            values.put(LapTimeEntry.COLUMN_NAME_INTERVAL, lapTime.interval)

            db.insert(LapTimeEntry.TABLE_NAME, null, values)
        }

        db.close()
    }

    override fun deleteLapTimes(lapTimeId: String) {
        val db = mDbHelper.writableDatabase

        val selection = LapTimeEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?"
        val selectionArgs = arrayOf(lapTimeId)

        db.delete(LapTimeEntry.TABLE_NAME, selection, selectionArgs)

        db.close()
    }

    override fun deleteAllLapTimes(plankLogId: String) {
        val db = mDbHelper.writableDatabase

        val selection = LapTimeEntry.COLUMN_NAME_PARENT_ENTRY_ID + " LIKE ?"
        val selectionArgs = arrayOf(plankLogId)

        db.delete(LapTimeEntry.TABLE_NAME, selection, selectionArgs)

        db.close()
    }

    companion object {

        private var INSTANCE: PlankLogsLocalDataSource? = null

        fun getInstance(context: Context): PlankLogsLocalDataSource {
            if (INSTANCE == null) {
                INSTANCE = PlankLogsLocalDataSource(context)
            }

            return INSTANCE
        }
    }
}
*/