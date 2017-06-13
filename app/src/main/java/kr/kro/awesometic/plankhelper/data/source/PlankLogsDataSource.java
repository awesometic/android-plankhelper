package kr.kro.awesometic.plankhelper.data.source;

import android.support.annotation.NonNull;

import java.util.List;

import kr.kro.awesometic.plankhelper.data.LapTime;
import kr.kro.awesometic.plankhelper.data.PlankLog;

/**
 * Created by Awesometic on 2017-04-16.
 */

public interface PlankLogsDataSource {

    interface LoadPlankLogsCallback {

        void onPlankLogsLoaded(List<PlankLog> plankLogs);

        void onDataNotAvailable();
    }

    interface GetPlankLogCallback {

        void onPlankLogLoaded(PlankLog plankLog);

        void onDataNotAvailable();
    }

    interface SavePlankLogCallback {

        void onSavePlankLog(boolean isSuccess);
    }

    void getPlankLogs(@NonNull LoadPlankLogsCallback callback);

    void getPlankLog(@NonNull String plankLogId, @NonNull GetPlankLogCallback callback);

    void savePlankLog(@NonNull PlankLog plankLog, @NonNull SavePlankLogCallback callback);

    void deletePlankLog(@NonNull String plankLogId);

    void deleteAllPlankLogs();


    // 한 번에 하나의 PlankLog에 대한 모든 LapTime을 불러옴
    interface LoadLapTimesCallback {

        void onLapTimesLoaded(List<LapTime> lapTimes);

        void onDataNotAvailable();
    }

    void getLapTimes(@NonNull String plankLogId, @NonNull LoadLapTimesCallback callback);

    void saveLapTimes(@NonNull String plankLogId, @NonNull List<LapTime> lapTimes);

    void deleteLapTimes(@NonNull String lapTimeId);

    // 해당 PlankLog의 모든 LapTime을 지움
    void deleteAllLapTimes(@NonNull String plankLogId);
}
