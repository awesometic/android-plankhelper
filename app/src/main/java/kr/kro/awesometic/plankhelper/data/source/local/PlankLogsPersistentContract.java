package kr.kro.awesometic.plankhelper.data.source.local;

import android.provider.BaseColumns;

/**
 * Created by Awesometic on 2017-04-15.
 */

public class PlankLogsPersistentContract {

    private PlankLogsPersistentContract() {}

    public static abstract class PlankLogEntry implements BaseColumns {
        public static final String TABLE_NAME = "planklog";
        public static final String COLUMN_NAME_ENTRY_ID = "entryid";
        public static final String COLUMN_NAME_DATETIME = "datetime";
        public static final String COLUMN_NAME_DURATION = "duration";
        public static final String COLUMN_NAME_METHOD = "method";
        public static final String COLUMN_NAME_LAP_COUNT = "lapcount";
    }

    public static abstract class LapTimeEntry implements BaseColumns {
        public static final String TABLE_NAME = "laptime";
        public static final String COLUMN_NAME_ENTRY_ID = "entryid";
        public static final String COLUMN_NAME_PARENT_ENTRY_ID = "parentid";
        public static final String COLUMN_NAME_ORDER_NUMBER = "ordernumber";
        public static final String COLUMN_NAME_PASSED_TIME = "passedtime";
        public static final String COLUMN_NAME_LEFT_TIME = "lefttime";
        public static final String COLUMN_NAME_INTERVAL = "interval";
    }
}
