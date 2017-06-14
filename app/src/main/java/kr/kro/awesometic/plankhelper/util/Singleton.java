package kr.kro.awesometic.plankhelper.util;

/**
 * Created by Awesometic on 2017-05-26.
 */

public class Singleton {
    private static final Singleton ourInstance = new Singleton();

    public static Singleton getInstance() {
        return ourInstance;
    }

    private String mFirstPlankDatetime;

    private int mStartOfTheWeek;
    private int mLineChartUnitOfAxisY;

    private Singleton() {
        mFirstPlankDatetime = Constants.SINGLETON.NEVER_PERFORMED;
        mStartOfTheWeek = 0;
    }

    public String getFirstPlankDatetime() {
        return mFirstPlankDatetime;
    }

    public void setFirstPlankDatetime(String mFirstPlankDatetime) {
        this.mFirstPlankDatetime = mFirstPlankDatetime;
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
