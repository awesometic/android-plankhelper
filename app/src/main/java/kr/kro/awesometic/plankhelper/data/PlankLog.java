package kr.kro.awesometic.plankhelper.data;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Awesometic on 2017-04-15.
 */

public class PlankLog {

    @NonNull
    private final String mId;

    private final long mDatetimeMSec;

    private final long mDuration;

    @NonNull
    private final String mMethod;

    private final int mLapCount;

    @NonNull
    private ArrayList<LapTime> mLapTimes;

    public PlankLog(long datetimeMSec, long duration, @NonNull String method, int lapCount, @NonNull ArrayList<LapTime> lapTimes) {
        this(UUID.randomUUID().toString(), datetimeMSec, duration, method, lapCount, lapTimes);
    }

    public PlankLog(@NonNull String id, long datetimeMSec, long duration, @NonNull String method, int lapCount, @NonNull ArrayList<LapTime> lapTimes) {
        mId = id;
        mDatetimeMSec = datetimeMSec;
        mDuration = duration;
        mMethod = method;
        mLapCount = lapCount;
        mLapTimes = lapTimes;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    public long getDatetimeMSec() {
        return mDatetimeMSec;
    }

    public long getDuration() {
        return mDuration;
    }

    @NonNull
    public String getMethod() {
        return mMethod;
    }

    public int getLapCount() {
        return mLapCount;
    }

    public ArrayList<LapTime> getLapTimes() {
        return mLapTimes;
    }

    public void setLapTimes(ArrayList<LapTime> lapTimes) {
        mLapTimes = lapTimes;
    }

    @Override
    public String toString() {
        return "Plank log performed at " + mDatetimeMSec + " with " + mLapCount + " lap times";
    }
}
