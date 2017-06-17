package kr.kro.awesometic.plankhelper.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import kr.kro.awesometic.plankhelper.data.LapTime;
import kr.kro.awesometic.plankhelper.data.PlankLog;
import kr.kro.awesometic.plankhelper.data.source.PlankLogsDataSource;
import kr.kro.awesometic.plankhelper.data.source.local.PlankLogsPersistentContract.PlankLogEntry;
import kr.kro.awesometic.plankhelper.data.source.local.PlankLogsPersistentContract.LapTimeEntry;
import kr.kro.awesometic.plankhelper.util.Constants;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Awesometic on 2017-04-15.
 */

public class PlankLogsLocalDataSource implements PlankLogsDataSource {

    private static PlankLogsLocalDataSource INSTANCE;
    private PlankLogsDbHelper mDbHelper;

    private PlankLogsLocalDataSource(@NonNull Context context) {
        checkNotNull(context);
        mDbHelper = new PlankLogsDbHelper(context);
    }

    public static PlankLogsLocalDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new PlankLogsLocalDataSource(context);
        }

        return INSTANCE;
    }

    @Override
    public void getPlankLogs(@NonNull LoadPlankLogsCallback callback) {
        List<PlankLog> plankLogs = new ArrayList<PlankLog>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                PlankLogEntry.COLUMN_NAME_ENTRY_ID,
                PlankLogEntry.COLUMN_NAME_DATETIME,
                PlankLogEntry.COLUMN_NAME_DURATION,
                PlankLogEntry.COLUMN_NAME_METHOD,
                PlankLogEntry.COLUMN_NAME_LAP_COUNT
        };

        Cursor c = db.query(PlankLogEntry.TABLE_NAME, projection, null, null, null, null, null);

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String itemId = c.getString(c.getColumnIndexOrThrow(PlankLogEntry.COLUMN_NAME_ENTRY_ID));
                int datetime = c.getInt(c.getColumnIndexOrThrow(PlankLogEntry.COLUMN_NAME_DATETIME));
                int duration = c.getInt(c.getColumnIndexOrThrow(PlankLogEntry.COLUMN_NAME_DURATION));
                String method = c.getString(c.getColumnIndexOrThrow(PlankLogEntry.COLUMN_NAME_METHOD));
                int lapCount = c.getInt(c.getColumnIndexOrThrow(PlankLogEntry.COLUMN_NAME_LAP_COUNT));

                PlankLog plankLog = new PlankLog(itemId, datetime, duration, method, lapCount, new ArrayList<LapTime>());
                plankLogs.add(plankLog);
            }
        }
        if (c != null) {
            c.close();
        }

        db.close();

        if (plankLogs.isEmpty()) {
            callback.onDataNotAvailable();
        } else {
            callback.onPlankLogsLoaded(plankLogs);
        }
    }

    @Override
    public void getPlankLogs(@NonNull Calendar calendar, int option, @NonNull LoadPlankLogsCallback callback) {
        List<PlankLog> plankLogs = new ArrayList<PlankLog>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                PlankLogEntry.COLUMN_NAME_ENTRY_ID,
                PlankLogEntry.COLUMN_NAME_DATETIME,
                PlankLogEntry.COLUMN_NAME_DURATION,
                PlankLogEntry.COLUMN_NAME_METHOD,
                PlankLogEntry.COLUMN_NAME_LAP_COUNT
        };

        String selection = "";

        switch (option) {
            case Constants.DATABASE_GETPLANKLOGS_OPTION.FROM_PARAM_YEAR:
                selection = PlankLogEntry.COLUMN_NAME_DATETIME + " > " + calendar.get(Calendar.YEAR);
                break;

            case Constants.DATABASE_GETPLANKLOGS_OPTION.FROM_PARAM_YEAR_MONTH:

                break;

            case Constants.DATABASE_GETPLANKLOGS_OPTION.FROM_PARAM_YEAR_MONTH_DATE:

                break;

            default:
                break;
        }

        Cursor c = db.query(PlankLogEntry.TABLE_NAME, projection, selection, null, null, null, null);

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String itemId = c.getString(c.getColumnIndexOrThrow(PlankLogEntry.COLUMN_NAME_ENTRY_ID));
                int datetime = c.getInt(c.getColumnIndexOrThrow(PlankLogEntry.COLUMN_NAME_DATETIME));
                int duration = c.getInt(c.getColumnIndexOrThrow(PlankLogEntry.COLUMN_NAME_DURATION));
                String method = c.getString(c.getColumnIndexOrThrow(PlankLogEntry.COLUMN_NAME_METHOD));
                int lapCount = c.getInt(c.getColumnIndexOrThrow(PlankLogEntry.COLUMN_NAME_LAP_COUNT));

                PlankLog plankLog = new PlankLog(itemId, datetime, duration, method, lapCount, new ArrayList<LapTime>());
                plankLogs.add(plankLog);
            }
        }
        if (c != null) {
            c.close();
        }

        db.close();

        if (plankLogs.isEmpty()) {
            callback.onDataNotAvailable();
        } else {
            callback.onPlankLogsLoaded(plankLogs);
        }
    }

    @Override
    public void getPlankLog(@NonNull String plankLogId, @NonNull GetPlankLogCallback callback) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                PlankLogEntry.COLUMN_NAME_ENTRY_ID,
                PlankLogEntry.COLUMN_NAME_DATETIME,
                PlankLogEntry.COLUMN_NAME_DURATION,
                PlankLogEntry.COLUMN_NAME_METHOD,
                PlankLogEntry.COLUMN_NAME_LAP_COUNT
        };

        String selection = PlankLogEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = { plankLogId };

        Cursor c = db.query(PlankLogEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        PlankLog plankLog = null;

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            String itemId = c.getString(c.getColumnIndexOrThrow(PlankLogEntry.COLUMN_NAME_ENTRY_ID));
            int datetime = c.getInt(c.getColumnIndexOrThrow(PlankLogEntry.COLUMN_NAME_DATETIME));
            int duration = c.getInt(c.getColumnIndexOrThrow(PlankLogEntry.COLUMN_NAME_DURATION));
            String method = c.getString(c.getColumnIndexOrThrow(PlankLogEntry.COLUMN_NAME_METHOD));
            int lapCount = c.getInt(c.getColumnIndexOrThrow(PlankLogEntry.COLUMN_NAME_LAP_COUNT));

            plankLog = new PlankLog(itemId, datetime, duration, method, lapCount, new ArrayList<LapTime>());
        }

        if (c != null) {
            c.close();
        }

        db.close();

        if (plankLog == null) {
            callback.onDataNotAvailable();
        } else {
            callback.onPlankLogLoaded(plankLog);
        }
    }

    @Override
    public void savePlankLog(@NonNull PlankLog plankLog, @NonNull SavePlankLogCallback callback) {
        checkNotNull(plankLog);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PlankLogEntry.COLUMN_NAME_ENTRY_ID, plankLog.getId());
        values.put(PlankLogEntry.COLUMN_NAME_DATETIME, plankLog.getDatetimeMSec());
        values.put(PlankLogEntry.COLUMN_NAME_DURATION, plankLog.getDuration());
        values.put(PlankLogEntry.COLUMN_NAME_METHOD, plankLog.getMethod());
        values.put(PlankLogEntry.COLUMN_NAME_LAP_COUNT, plankLog.getLapCount());

        if (db.insert(PlankLogEntry.TABLE_NAME, null, values) > -1) {
            callback.onSavePlankLog(true);
        } else {
            callback.onSavePlankLog(false);
        }

        db.close();
    }

    @Override
    public void deletePlankLog(@NonNull String plankLogId) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection = PlankLogEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = { plankLogId };

        db.delete(PlankLogEntry.TABLE_NAME, selection, selectionArgs);

        db.close();
    }

    @Override
    public void deleteAllPlankLogs() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.delete(PlankLogEntry.TABLE_NAME, null, null);

        db.close();
    }

    @Override
    public void getLapTimes(@NonNull String plankLogId, @NonNull LoadLapTimesCallback callback) {
        List<LapTime> lapTimes = new ArrayList<LapTime>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                LapTimeEntry.COLUMN_NAME_ENTRY_ID,
                LapTimeEntry.COLUMN_NAME_PARENT_ENTRY_ID,
                LapTimeEntry.COLUMN_NAME_ORDER_NUMBER,
                LapTimeEntry.COLUMN_NAME_PASSED_TIME,
                LapTimeEntry.COLUMN_NAME_LEFT_TIME,
                LapTimeEntry.COLUMN_NAME_INTERVAL
        };

        String selection = LapTimeEntry.COLUMN_NAME_PARENT_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = { plankLogId };

        Cursor c = db.query(LapTimeEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String entryId = c.getString(c.getColumnIndexOrThrow(LapTimeEntry.COLUMN_NAME_ENTRY_ID));
                String parentEntryId = c.getString(c.getColumnIndexOrThrow(LapTimeEntry.COLUMN_NAME_PARENT_ENTRY_ID));
                int orderNumber = c.getInt(c.getColumnIndexOrThrow(LapTimeEntry.COLUMN_NAME_ORDER_NUMBER));
                int passedTime = c.getInt(c.getColumnIndexOrThrow(LapTimeEntry.COLUMN_NAME_PASSED_TIME));
                int leftTime = c.getInt(c.getColumnIndexOrThrow(LapTimeEntry.COLUMN_NAME_LEFT_TIME));
                int interval = c.getInt(c.getColumnIndexOrThrow(LapTimeEntry.COLUMN_NAME_INTERVAL));

                LapTime lapTime = new LapTime(entryId, parentEntryId, orderNumber, passedTime, leftTime, interval);
                lapTimes.add(lapTime);
            }
        }
        if (c != null) {
            c.close();
        }

        db.close();

        if (lapTimes.isEmpty()) {
            callback.onDataNotAvailable();
        } else {
            callback.onLapTimesLoaded(lapTimes);
        }
    }

    @Override
    public void saveLapTimes(@NonNull String plankLogId, @NonNull List<LapTime> lapTimes) {
        checkNotNull(lapTimes);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        for (LapTime lapTime : lapTimes) {
            ContentValues values = new ContentValues();
            values.put(LapTimeEntry.COLUMN_NAME_ENTRY_ID, lapTime.getId());
            values.put(LapTimeEntry.COLUMN_NAME_PARENT_ENTRY_ID, plankLogId);
            values.put(LapTimeEntry.COLUMN_NAME_ORDER_NUMBER, lapTime.getOrderNumber());
            values.put(LapTimeEntry.COLUMN_NAME_PASSED_TIME, lapTime.getPassedTimeMSec());
            values.put(LapTimeEntry.COLUMN_NAME_LEFT_TIME, lapTime.getLeftTimeMSec());
            values.put(LapTimeEntry.COLUMN_NAME_INTERVAL, lapTime.getIntervalMSec());

            db.insert(LapTimeEntry.TABLE_NAME, null, values);
        }

        db.close();
    }

    @Override
    public void deleteLapTimes(@NonNull String lapTimeId) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection = LapTimeEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = { lapTimeId };

        db.delete(LapTimeEntry.TABLE_NAME, selection, selectionArgs);

        db.close();
    }

    @Override
    public void deleteAllLapTimes(@NonNull String plankLogId) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection = LapTimeEntry.COLUMN_NAME_PARENT_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = { plankLogId };

        db.delete(LapTimeEntry.TABLE_NAME, selection, selectionArgs);

        db.close();
    }
}
