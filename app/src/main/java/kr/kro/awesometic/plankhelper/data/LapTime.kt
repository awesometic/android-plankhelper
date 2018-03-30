package kr.kro.awesometic.plankhelper.data

import java.util.UUID

/**
 * Created by Awesometic on 2017-04-18.
 */

class LapTime(val id: String, val parentId: String, // 순서
              val orderNumber: Int, // 각 시간은 HH:mm:ss.SSS 포맷
              val passedTime: String, // 스탑워치 모드일 경우 null
              val leftTime: String?, // 바로 전 기록과 시간차
              val interval: String) {

    constructor(parentId: String, orderNumber: Int, passedTime: String, interval: String) : this(UUID.randomUUID().toString(), parentId, orderNumber, passedTime, null!!, interval) {}

    constructor(parentId: String, orderNumber: Int, passedTime: String, leftTime: String, interval: String) : this(UUID.randomUUID().toString(), parentId, orderNumber, passedTime, leftTime, interval) {}
}
