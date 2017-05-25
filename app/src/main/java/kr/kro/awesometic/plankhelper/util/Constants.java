package kr.kro.awesometic.plankhelper.util;

/**
 * Created by Awesometic on 2017-04-24.
 */

public class Constants {
    
    public interface NOTIFICATION_ID {
        int FOREGROUND_SERVICE = 12071;
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

    public interface CALLER {
        int FROM_SERVICE = 300;
        int FROM_STOPWATCH_FRAGMENT = 301;
        int FROM_TIMER_FRAGMENT = 351;
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
        int STOPWATCH_FREQUENCY_IN_MILLISECOND = 100;
    }

    public interface DATABASE {
        String EMPTY_PARENT_ID = "empty_parent_id";

        String METHOD_STOPWATCH = "stopwatch";
        String METHOD_TIMER = "timer";
    }
}
