package kr.kro.awesometic.plankhelper.util;

/**
 * Created by Awesometic on 2017-05-26.
 */

public class Singleton {
    private static final Singleton ourInstance = new Singleton();

    public static Singleton getInstance() {
        return ourInstance;
    }

    private int mStartOfTheWeek;
    private int mLineChartUnitOfAxisY;

    private Singleton() {
        mStartOfTheWeek = 0;
    }

    public int getStartOfTheWeek() {
        return mStartOfTheWeek;
    }

    public void setStartOfTheWeek(int mStartOfTheWeek) {
        this.mStartOfTheWeek = mStartOfTheWeek;
    }

    public int getLineChartUnitOfAxisY() {
        return mLineChartUnitOfAxisY;
    }

    public void setLineChartUnitOfAxisY(int mLineChartUnitOfAxisY) {
        this.mLineChartUnitOfAxisY = mLineChartUnitOfAxisY;
    }
}
