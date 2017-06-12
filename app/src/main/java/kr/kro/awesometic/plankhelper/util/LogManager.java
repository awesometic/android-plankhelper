package kr.kro.awesometic.plankhelper.util;

import android.util.Log;

/**
 * Created by Awesometic on 2017-06-12.
 */

public class LogManager {

    public static void v(String string) {
        Log.v(Constants.LOG_TAG, string);
    }

    public static void d(String string) {
        Log.d(Constants.LOG_TAG, string);
    }

    public static void i(String string) {
        Log.i(Constants.LOG_TAG, string);
    }

    public static void w(String string) {
        Log.w(Constants.LOG_TAG, string);
    }

    public static void e(String string) {
        Log.e(Constants.LOG_TAG, string);
    }
}
