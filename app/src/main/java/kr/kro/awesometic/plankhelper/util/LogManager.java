package kr.kro.awesometic.plankhelper.util;

import android.util.Log;

/**
 * Created by Awesometic on 2017-06-12.
 */

public class LogManager {

    public static void v(Object content) {
        if (content instanceof String) {
            Log.d(Constants.LOG_TAG, (String) content);
        } else {
            Log.d(Constants.LOG_TAG, String.valueOf(content));
        }
    }

    public static void d(Object content) {
        if (content instanceof String) {
            Log.d(Constants.LOG_TAG, (String) content);
        } else {
            Log.d(Constants.LOG_TAG, String.valueOf(content));   
        }
    }

    public static void i(Object content) {
        if (content instanceof String) {
            Log.d(Constants.LOG_TAG, (String) content);
        } else {
            Log.d(Constants.LOG_TAG, String.valueOf(content));
        }
    }

    public static void w(Object content) {
        if (content instanceof String) {
            Log.d(Constants.LOG_TAG, (String) content);
        } else {
            Log.d(Constants.LOG_TAG, String.valueOf(content));
        }
    }

    public static void e(Object content) {
        if (content instanceof String) {
            Log.d(Constants.LOG_TAG, (String) content);
        } else {
            Log.d(Constants.LOG_TAG, String.valueOf(content));
        }
    }
}
