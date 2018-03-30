package kr.kro.awesometic.plankhelper.util

/**
 * Created by Awesometic on 2017-05-26.
 */

class Singleton private constructor() {

    var startOfTheWeek: Int = 0
    var lineChartUnitOfAxisY: Int = 0

    init {
        startOfTheWeek = 0
    }

    companion object {
        val instance = Singleton()
    }
}
