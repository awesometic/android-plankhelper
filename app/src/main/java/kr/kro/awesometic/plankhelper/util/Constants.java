package kr.kro.awesometic.plankhelper.util;

/**
 * Created by Awesometic on 2017-04-24.
 */

public class Constants {

    public static final String LOG_TAG = "Awesometic";
    
    public interface NOTIFICATION_ID {
        int FOREGROUND_SERVICE = 12071;
    }

    public interface SINGLETON {
        String NEVER_PERFORMED = "undefined";
    }

    public interface DATABASE {
        String EMPTY_PARENT_ID = "empty_parent_id";

        String METHOD_STOPWATCH = "stopwatch";
        String METHOD_TIMER = "timer";
    }

    public interface DATABASE_GETPLANKLOGS_OPTION {
        int FROM_PARAM_YEAR = 0;
        int FROM_PARAM_YEAR_MONTH = 1;
        int FROM_PARAM_YEAR_MONTH_DATE = 2;

        int TO_PARAM_YEAR = 3;
        int TO_PARAM_YEAR_MONTH = 4;
        int TO_PARAM_YEAR_MONTH_DATE = 5;
    }

    public interface LAPTIME_ENTRY {
        int NULL_INTERVAL = -1;
    }

    public interface BROADCAST_ACTION {
        String NOTIFICATION_READY = "kr.kro.awesometic.plankhelper.NOTI_ACTION_NOTIFICATION_READY";
        String APP_EXIT = "kr.kro.awesometic.plankhelper.NOTI_ACTION_APP_EXIT";

        String STOPWATCH_READY = "kr.kro.awesometic.plankhelper.NOTI_ACTION_STOPWATCH_READY";
        String STOPWATCH_START = "kr.kro.awesometic.plankhelper.NOTI_ACTION_STOPWATCH_START";
        String STOPWATCH_PAUSE = "kr.kro.awesometic.plankhelper.NOTI_ACTION_STOPWATCH_PAUSE";
        String STOPWATCH_RESUME = "kr.kro.awesometic.plankhelper.NOTI_ACTION_STOPWATCH_RESUME";
        String STOPWATCH_LAP = "kr.kro.awesometic.plankhelper.NOTI_ACTION_STOPWATCH_LAP";
        String STOPWATCH_RESET = "kr.kro.awesometic.plankhelper.NOTI_ACTION_STOPWATCH_RESET";

        String TIMER_READY = "kr.kro.awesometic.plankhelper.NOTI_ACTION_TIMER_READY";
        String TIMER_SET = "kr.kro.awesometic.plankhelper.NOTI_ACTION_TIMER_SET";
        String TIMER_START = "kr.kro.awesometic.plankhelper.NOTI_ACTION_TIMER_START";
        String TIMER_PAUSE = "kr.kro.awesometic.plankhelper.NOTI_ACTION_TIMER_PAUSE";
        String TIMER_RESUME = "kr.kro.awesometic.plankhelper.NOTI_ACTION_TIMER_RESUME";
        String TIMER_LAP = "kr.kro.awesometic.plankhelper.NOTI_ACTION_TIMER_LAP";
        String TIMER_RESET = "kr.kro.awesometic.plankhelper.NOTI_ACTION_TIMER_RESET";
    }

    public interface SERVICE_WHAT {
        int NOTIFICATION_READY = 190;
        int APP_EXIT = 191;

        int STOPWATCH_READY = 100;
        int STOPWATCH_START = 101;
        int STOPWATCH_PAUSE = 103;
        int STOPWATCH_RESUME = 104;
        int STOPWATCH_LAP = 105;
        int STOPWATCH_RESET = 106;

        int TIMER_READY = 150;
        int TIMER_START = 151;
        int TIMER_PAUSE = 152;
        int TIMER_RESUME = 153;
        int TIMER_LAP = 154;
        int TIMER_RESET = 155;
    }
    
    public interface WORK_METHOD {
        int NOTIFICATION_READY = 200;
        int STOPWATCH = 201;
        int TIMER = 251;   
    }

    public interface UPDATE_DISPLAY_WHAT {
        int UPDATE = 400;
    }

    public interface NUMBERPICKER_TYPE {
        int HOUR = 500;
        int MIN = 501;
        int SEC = 502;
    }

    public interface UPDATE_DISPLAY {
        int STOPWATCH_FREQUENCY_IN_MILLISECOND = 125;
    }

    public interface RECYCLERVIEW_ADAPTER_VIEWTYPE {
        int TYPE_HEAD =  0;
        int TYPE_BODY = 1;
    }
    
    public interface COMMON_ANIMATOR_POSITION {
        int RECYCLERVIEW = 0;
        int LOADING = 1;
    }
}
