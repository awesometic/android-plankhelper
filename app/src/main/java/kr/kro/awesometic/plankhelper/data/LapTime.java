package kr.kro.awesometic.plankhelper.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.UUID;

import kr.kro.awesometic.plankhelper.util.Constants;

/**
 * Created by Awesometic on 2017-04-18.
 */

public class LapTime {

    @NonNull
    private final String mId;

    @NonNull
    private final String mParentId;

    // 순서
    private final int mOrderNumber;

    // 각 시간은 밀리초
    private final long mPassedTimeMSec;

    // 스탑워치 모드일 경우 null
    private final long mLeftTimeMSec;

    // 바로 전 기록과 시간차
    private final long mIntervalMSec;

    public LapTime(@NonNull String parentId, int orderNumber, long passedTimeMSec, long intervalMSec) {
        this(UUID.randomUUID().toString(), parentId, orderNumber, passedTimeMSec, Constants.LAPTIME_ENTRY.NULL_INTERVAL, intervalMSec);
    }

    public LapTime(@NonNull String parentId, int orderNumber, long passedTimeMSec, long leftTimeMSec, long intervalMSec) {
        this(UUID.randomUUID().toString(), parentId, orderNumber, passedTimeMSec, leftTimeMSec, intervalMSec);
    }

    public LapTime(@NonNull String id, @NonNull String parentId, int orderNumber, long passedTimeMSec, long leftTimeMSec, long intervalMSec) {
        mId = id;
        mParentId = parentId;
        mOrderNumber = orderNumber;
        mPassedTimeMSec = passedTimeMSec;
        mLeftTimeMSec = leftTimeMSec;
        mIntervalMSec = intervalMSec;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @NonNull
    public String getParentId() {
        return mParentId;
    }

    public int getOrderNumber() {
        return mOrderNumber;
    }

    public long getPassedTimeMSec() {
        return mPassedTimeMSec;
    }

    public long getLeftTimeMSec() {
        return mLeftTimeMSec;
    }

    public long getIntervalMSec() {
        return mIntervalMSec;
    }
}
