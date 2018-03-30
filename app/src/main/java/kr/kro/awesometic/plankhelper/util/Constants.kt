package kr.kro.awesometic.plankhelper.util

/**
 * Created by Awesometic on 2017-04-24.
 */

object Constants {

    val LOG_TAG = "Awesometic"

    interface NOTIFICATION_ID {
        companion object {
            val FOREGROUND_SERVICE = 12071
        }
    }

    interface BROADCAST_ACTION {
        companion object {
            val NOTIFICATION_READY = "kr.kro.awesometic.plankhelper.NOTI_ACTION_NOTIFICATION_READY"
            val APP_EXIT = "kr.kro.awesometic.plankhelper.NOTI_ACTION_APP_EXIT"

            val STOPWATCH_READY = "kr.kro.awesometic.plankhelper.NOTI_ACTION_STOPWATCH_READY"
            val STOPWATCH_START = "kr.kro.awesometic.plankhelper.NOTI_ACTION_STOPWATCH_START"
            val STOPWATCH_PAUSE = "kr.kro.awesometic.plankhelper.NOTI_ACTION_STOPWATCH_PAUSE"
            val STOPWATCH_RESUME = "kr.kro.awesometic.plankhelper.NOTI_ACTION_STOPWATCH_RESUME"
            val STOPWATCH_LAP = "kr.kro.awesometic.plankhelper.NOTI_ACTION_STOPWATCH_LAP"
            val STOPWATCH_RESET = "kr.kro.awesometic.plankhelper.NOTI_ACTION_STOPWATCH_RESET"

            val TIMER_READY = "kr.kro.awesometic.plankhelper.NOTI_ACTION_TIMER_READY"
            val TIMER_SET = "kr.kro.awesometic.plankhelper.NOTI_ACTION_TIMER_SET"
            val TIMER_START = "kr.kro.awesometic.plankhelper.NOTI_ACTION_TIMER_START"
            val TIMER_PAUSE = "kr.kro.awesometic.plankhelper.NOTI_ACTION_TIMER_PAUSE"
            val TIMER_RESUME = "kr.kro.awesometic.plankhelper.NOTI_ACTION_TIMER_RESUME"
            val TIMER_LAP = "kr.kro.awesometic.plankhelper.NOTI_ACTION_TIMER_LAP"
            val TIMER_RESET = "kr.kro.awesometic.plankhelper.NOTI_ACTION_TIMER_RESET"
        }
    }

    interface SERVICE_WHAT {
        companion object {
            val NOTIFICATION_READY = 190
            val APP_EXIT = 191

            val STOPWATCH_READY = 100
            val STOPWATCH_START = 101
            val STOPWATCH_PAUSE = 103
            val STOPWATCH_RESUME = 104
            val STOPWATCH_LAP = 105
            val STOPWATCH_RESET = 106

            val TIMER_READY = 150
            val TIMER_START = 151
            val TIMER_PAUSE = 152
            val TIMER_RESUME = 153
            val TIMER_LAP = 154
            val TIMER_RESET = 155
        }
    }

    interface WORK_METHOD {
        companion object {
            val NOTIFICATION_READY = 200
            val STOPWATCH = 201
            val TIMER = 251
        }
    }

    interface UPDATE_DISPLAY_WHAT {
        companion object {
            val UPDATE = 400
        }
    }

    interface NUMBERPICKER_TYPE {
        companion object {
            val HOUR = 500
            val MIN = 501
            val SEC = 502
        }
    }

    interface UPDATE_DISPLAY {
        companion object {
            val STOPWATCH_FREQUENCY_IN_MILLISECOND = 125
        }
    }

    interface RECYCLERVIEW_ADAPTER_VIEWTYPE {
        companion object {
            val TYPE_HEAD = 0
            val TYPE_BODY = 1
        }
    }

    interface COMMON_ANIMATOR_POSITION {
        companion object {
            val RECYCLERVIEW = 0
            val LOADING = 1
        }
    }

    interface DATABASE {
        companion object {
            val EMPTY_PARENT_ID = "empty_parent_id"

            val METHOD_STOPWATCH = "stopwatch"
            val METHOD_TIMER = "timer"
        }
    }
}
