package kr.kro.awesometic.plankhelper.util

import android.util.Log

/**
 * Created by Awesometic on 2017-06-12.
 */

object LogManager {

    fun v(content: Any) {
        if (content is String) {
            Log.d(Constants.LOG_TAG, content)
        } else {
            Log.d(Constants.LOG_TAG, content.toString())
        }
    }

    fun d(content: Any) {
        if (content is String) {
            Log.d(Constants.LOG_TAG, content)
        } else {
            Log.d(Constants.LOG_TAG, content.toString())
        }
    }

    fun i(content: Any) {
        if (content is String) {
            Log.d(Constants.LOG_TAG, content)
        } else {
            Log.d(Constants.LOG_TAG, content.toString())
        }
    }

    fun w(content: Any) {
        if (content is String) {
            Log.d(Constants.LOG_TAG, content)
        } else {
            Log.d(Constants.LOG_TAG, content.toString())
        }
    }

    fun e(content: Any) {
        if (content is String) {
            Log.d(Constants.LOG_TAG, content)
        } else {
            Log.d(Constants.LOG_TAG, content.toString())
        }
    }
}
