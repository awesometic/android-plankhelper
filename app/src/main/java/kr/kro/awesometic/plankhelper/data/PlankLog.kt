package kr.kro.awesometic.plankhelper.data

import java.util.ArrayList
import java.util.UUID

/**
 * Created by Awesometic on 2017-04-15.
 */

class PlankLog(val id: String, val datetime: String, val duration: Long, val method: String, val lapCount: Int, var lapTimes: ArrayList<LapTime>) {

    constructor(datetime: String, duration: Long, method: String, lapCount: Int, lapTimes: ArrayList<LapTime>) : this(UUID.randomUUID().toString(), datetime, duration, method, lapCount, lapTimes) {}

    override fun toString(): String {
        return "Plank log performed at $datetime with $lapCount lap times"
    }
}
