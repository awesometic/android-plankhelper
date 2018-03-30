package kr.kro.awesometic.plankhelper.data.source.local

import android.provider.BaseColumns

/**
 * Created by Awesometic on 2017-04-15.
 */

object PlankLogsPersistentContract {

    abstract class PlankLogEntry : BaseColumns {
        companion object {
            val TABLE_NAME = "planklog"
            val COLUMN_NAME_ENTRY_ID = "entryid"
            val COLUMN_NAME_DATETIME = "datetime"
            val COLUMN_NAME_DURATION = "duration"
            val COLUMN_NAME_METHOD = "method"
            val COLUMN_NAME_LAP_COUNT = "lapcount"
        }
    }

    abstract class LapTimeEntry : BaseColumns {
        companion object {
            val TABLE_NAME = "laptime"
            val COLUMN_NAME_ENTRY_ID = "entryid"
            val COLUMN_NAME_PARENT_ENTRY_ID = "parentid"
            val COLUMN_NAME_ORDER_NUMBER = "ordernumber"
            val COLUMN_NAME_PASSED_TIME = "passedtime"
            val COLUMN_NAME_LEFT_TIME = "lefttime"
            val COLUMN_NAME_INTERVAL = "interval"
        }
    }
}
