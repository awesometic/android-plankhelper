package kr.kro.awesometic.plankhelper.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import kr.kro.awesometic.plankhelper.data.LapTime;
import kr.kro.awesometic.plankhelper.data.PlankLog;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Awesometic on 2017-04-16.
 */

public class PlankLogsRepository implements PlankLogsDataSource {

    private static PlankLogsRepository INSTANCE = null;
    private final PlankLogsDataSource mPlankLogsLocalDataSource;

    Map<String, PlankLog> mCachedPlankLogs;

    boolean mCacheIsDirty = false;

    public PlankLogsRepository(@NonNull PlankLogsDataSource plankLogsLocalDataSource) {
        mPlankLogsLocalDataSource = checkNotNull(plankLogsLocalDataSource);
    }

    public static PlankLogsRepository getInstance(PlankLogsDataSource plankLogsLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new PlankLogsRepository(plankLogsLocalDataSource);
        }

        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void getPlankLogs(@NonNull final LoadPlankLogsCallback callback) {
        checkNotNull(callback);

        if (mCachedPlankLogs != null && !mCacheIsDirty) {
            callback.onPlankLogsLoaded(new ArrayList<>(mCachedPlankLogs.values()));
            return;
        }

        if (mCacheIsDirty) {

        } else {
            mPlankLogsLocalDataSource.getPlankLogs(new LoadPlankLogsCallback() {
                @Override
                public void onPlankLogsLoaded(List<PlankLog> plankLogs) {
                    refreshCache(plankLogs);
                    callback.onPlankLogsLoaded(new ArrayList<>(mCachedPlankLogs.values()));
                }

                @Override
                public void onDataNotAvailable() {

                }
            });
        }
    }

    @Override
    public void getPlankLog(@NonNull final String plankLogId, @NonNull final GetPlankLogCallback callback) {
        checkNotNull(plankLogId);
        checkNotNull(callback);

        PlankLog cachedPlankLog = getPlankLogWithId(plankLogId);

        if (cachedPlankLog != null) {
            callback.onPlankLogLoaded(cachedPlankLog);
            return;
        }

        mPlankLogsLocalDataSource.getPlankLog(plankLogId, new GetPlankLogCallback() {
            @Override
            public void onPlankLogLoaded(PlankLog plankLog) {
                if (mCachedPlankLogs == null) {
                    mCachedPlankLogs = new LinkedHashMap<>();
                }
                mCachedPlankLogs.put(plankLog.getId(), plankLog);
                callback.onPlankLogLoaded(plankLog);
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }

    @Override
    public void savePlankLog(@NonNull PlankLog plankLog, @NonNull SavePlankLogCallback callback) {
        checkNotNull(plankLog);

        mPlankLogsLocalDataSource.savePlankLog(plankLog, callback);

        if (mCachedPlankLogs == null) {
            mCachedPlankLogs = new LinkedHashMap<>();
        }
        mCachedPlankLogs.put(plankLog.getId(), plankLog);
    }

    @Override
    public void deletePlankLog(@NonNull String plankLogId) {
        mPlankLogsLocalDataSource.deletePlankLog(plankLogId);

        mCachedPlankLogs.clear();
    }

    @Override
    public void deleteAllPlankLogs() {
        mPlankLogsLocalDataSource.deleteAllPlankLogs();

        if (mCachedPlankLogs == null) {
            mCachedPlankLogs = new LinkedHashMap<>();
        }
        mCachedPlankLogs.clear();
    }

    private void refreshCache(List<PlankLog> plankLogs) {
        if (mCachedPlankLogs == null) {
            mCachedPlankLogs = new LinkedHashMap<>();
        }
        mCachedPlankLogs.clear();
        for (PlankLog plankLog : plankLogs) {
            mCachedPlankLogs.put(plankLog.getId(), plankLog);
        }
        mCacheIsDirty = false;
    }

    private void refreshLocalDataSource(List<PlankLog> plankLogs) {
        mPlankLogsLocalDataSource.deleteAllPlankLogs();
        for (PlankLog plankLog : plankLogs) {
            mPlankLogsLocalDataSource.savePlankLog(plankLog, new SavePlankLogCallback() {
                @Override
                public void onSavePlankLog(boolean isSuccess) {

                }
            });
        }
    }

    @Nullable
    private PlankLog getPlankLogWithId(@NonNull String id) {
        checkNotNull(id);
        if (mCachedPlankLogs == null || mCachedPlankLogs.isEmpty()) {
            return null;
        } else {
            return mCachedPlankLogs.get(id);
        }
    }

    @Override
    public void getLapTimes(@NonNull String plankLogId, @NonNull final LoadLapTimesCallback callback) {
        checkNotNull(plankLogId);

        mPlankLogsLocalDataSource.getLapTimes(plankLogId, new LoadLapTimesCallback() {
            @Override
            public void onLapTimesLoaded(List<LapTime> lapTimes) {
                callback.onLapTimesLoaded(lapTimes);
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }

    @Override
    public void saveLapTimes(@NonNull String plankLogId, @NonNull List<LapTime> lapTimes) {
        checkNotNull(plankLogId);
        checkNotNull(lapTimes);

        mPlankLogsLocalDataSource.saveLapTimes(plankLogId, lapTimes);
    }

    @Override
    public void deleteLapTimes(@NonNull String lapTimeId) {
        checkNotNull(lapTimeId);

        mPlankLogsLocalDataSource.deleteLapTimes(lapTimeId);
    }

    @Override
    public void deleteAllLapTimes(@NonNull String plankLogId) {
        checkNotNull(plankLogId);

        mPlankLogsLocalDataSource.deleteAllLapTimes(plankLogId);
    }
}
