package kr.kro.awesometic.plankhelper.data.source.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Awesometic on 2017-04-15.
 */

public class PlankLogsDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "PlankLog.db";

    private static final String SQL_CREATE_PLANKLOG_ENTRIES =
            "CREATE TABLE " + PlankLogsPersistentContract.PlankLogEntry.TABLE_NAME + " (" +
                    PlankLogsPersistentContract.PlankLogEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    PlankLogsPersistentContract.PlankLogEntry.COLUMN_NAME_ENTRY_ID + " TEXT NOT NULL, " +
                    PlankLogsPersistentContract.PlankLogEntry.COLUMN_NAME_DATETIME + " TEXT NOT NULL, " +
                    PlankLogsPersistentContract.PlankLogEntry.COLUMN_NAME_DURATION + " INTEGER NOT NULL, " +
                    PlankLogsPersistentContract.PlankLogEntry.COLUMN_NAME_METHOD + " TEXT NOT NULL, " +
                    PlankLogsPersistentContract.PlankLogEntry.COLUMN_NAME_LAP_COUNT + " INTEGER NOT NULL" +
                    " )";
    
    private static final String SQL_CREATE_LAPTIME_ENTRIES = 
            "CREATE TABLE " + PlankLogsPersistentContract.LapTimeEntry.TABLE_NAME  + " (" +
                    PlankLogsPersistentContract.LapTimeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    PlankLogsPersistentContract.LapTimeEntry.COLUMN_NAME_ENTRY_ID + " TEXT NOT NULL, " +
                    PlankLogsPersistentContract.LapTimeEntry.COLUMN_NAME_PARENT_ENTRY_ID + " INTEGER NOT NULL, " +
                    PlankLogsPersistentContract.LapTimeEntry.COLUMN_NAME_ORDER_NUMBER + " INTEGER NOT NULL, " +
                    PlankLogsPersistentContract.LapTimeEntry.COLUMN_NAME_PASSED_TIME + " INTEGER NOT NULL, " +
                    PlankLogsPersistentContract.LapTimeEntry.COLUMN_NAME_LEFT_TIME + " INTEGER, " +
                    PlankLogsPersistentContract.LapTimeEntry.COLUMN_NAME_INTERVAL + " INTEGER NOT NULL " +
                    " )";
    
    public PlankLogsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PLANKLOG_ENTRIES);
        db.execSQL(SQL_CREATE_LAPTIME_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PlankLogsPersistentContract.PlankLogEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PlankLogsPersistentContract.LapTimeEntry.TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }
}
