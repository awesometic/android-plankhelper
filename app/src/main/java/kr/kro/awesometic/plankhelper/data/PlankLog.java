package kr.kro.awesometic.plankhelper.data;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Awesometic on 2017-04-15.
 */

public class PlankLog {

    @NonNull
    private final String mId;

    @NonNull
    private final String mDatetime;

    private final long mDuration;

    @NonNull
    private final String mMethod;

    private final int mLapCount;

    @NonNull
    private ArrayList<LapTime> mLapTimes;

    public PlankLog(@NonNull String datetime, long duration, @NonNull String method, int lapCount, @NonNull ArrayList<LapTime> lapTimes) {
        this(UUID.randomUUID().toString(), datetime, duration, method, lapCount, lapTimes);
    }

    public PlankLog(@NonNull String id, @NonNull String datetime, long duration, @NonNull String method, int lapCount, @NonNull ArrayList<LapTime> lapTimes) {
        mId = id;
        mDatetime = datetime;
        mDuration = duration;
        mMethod = method;
        mLapCount = lapCount;
        mLapTimes = lapTimes;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @NonNull
    public String getDatetime() {
        return mDatetime;
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

    @NonNull
    public void setLapTimes(ArrayList<LapTime> lapTimes) {
        mLapTimes = lapTimes;
    }

    @Override
    public String toString() {
        return "Plank log performed at " + mDatetime + " with " + mLapCount + " lap times";
    }
}
