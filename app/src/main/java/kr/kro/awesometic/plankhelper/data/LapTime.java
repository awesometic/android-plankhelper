package kr.kro.awesometic.plankhelper.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.UUID;

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

    // 각 시간은 HH:mm:ss.SSS 포맷
    @NonNull
    private final String mPassedTime;

    // 스탑워치 모드일 경우 null
    @Nullable
    private final String mLeftTime;

    // 바로 전 기록과 시간차
    @NonNull
    private final String mInterval;

    public LapTime(@NonNull String parentId, int orderNumber, @NonNull String passedTime, @NonNull String interval) {
        this(UUID.randomUUID().toString(), parentId, orderNumber, passedTime, null, interval);
    }

    public LapTime(@NonNull String parentId, int orderNumber, @NonNull String passedTime, @NonNull String leftTime, @NonNull String interval) {
        this(UUID.randomUUID().toString(), parentId, orderNumber, passedTime, leftTime, interval);
    }

    public LapTime(@NonNull String id, @NonNull String parentId, int orderNumber, @NonNull String passedTime, @NonNull String leftTime, @NonNull String interval) {
        mId = id;
        mParentId = parentId;
        mOrderNumber = orderNumber;
        mPassedTime = passedTime;
        mLeftTime = leftTime;
        mInterval = interval;
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

    @NonNull
    public String getPassedTime() {
        return mPassedTime;
    }

    @Nullable
    public String getLeftTime() {
        return mLeftTime;
    }

    @NonNull
    public String getInterval() {
        return mInterval;
    }
}
