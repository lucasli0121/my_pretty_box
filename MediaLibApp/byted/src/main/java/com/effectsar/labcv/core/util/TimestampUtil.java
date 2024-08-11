package com.effectsar.labcv.core.util;

import android.annotation.TargetApi;
import android.os.SystemClock;
import android.util.Log;



public final class TimestampUtil {
    private static final String TAG = "TimestampUtil";
    public final static int CAMERA = 0;
    public final static int CAMERA2 = 1;

    public final static int TIMESTAMP_UNKNOWN = -1;
    public final static int TIMESTAMP_REALTIME = 0;
    public final static int TIMESTAMP_ELAPSED = 1;
    private final static int IMU_TYPE = TIMESTAMP_ELAPSED;

    @TargetApi(17)
    private static int getTimestampType(long timestamp) {
        assert(timestamp > 0);
        // Log.e(TAG, "timestamp type" + System.nanoTime() + "," + SystemClock.elapsedRealtimeNanos() + "," + timestamp);
        long delta_realtime_nano = Math.abs(System.nanoTime() - timestamp);
        long delta_elapsed_nano = Math.abs(SystemClock.elapsedRealtimeNanos() - timestamp);
        if (delta_realtime_nano < delta_elapsed_nano) {
            return TIMESTAMP_REALTIME;
        } else  {
            return TIMESTAMP_ELAPSED;
        }
    }

    @TargetApi(17)
    private static long convertTimestamp(long timestamp, int imageType, long imuDeltaTime) {
        if (imageType == IMU_TYPE) {
            return timestamp + imuDeltaTime;
        } else {
            return timestamp - System.nanoTime() + SystemClock.elapsedRealtimeNanos() + imuDeltaTime;
        }
    }

    @TargetApi(17)
    public static long getDelta(long timestamp) {
        long delta = timestamp - SystemClock.elapsedRealtimeNanos();
        Log.e(TAG, "timestamp delta " + timestamp + "," + SystemClock.elapsedRealtimeNanos());
        if (Math.abs(delta) >= 1000000000) {  // 1s
            return delta;
        } else {
            return 0;
        }
    }

    @TargetApi(17)
    public static long convertImageTimestamp(long timestamp, int cameraType, int imageType, long imuDeltaTime) {
        long result = timestamp;
        if (cameraType == CAMERA) {
            if (imageType == TIMESTAMP_UNKNOWN) {
                imageType = getTimestampType(timestamp);
            }
            result = convertTimestamp(timestamp, imageType, imuDeltaTime);

            Log.e(TAG, "convertImageTimestamp timeStamp: " + timestamp + ", systemTime: " + SystemClock.elapsedRealtimeNanos()
                    + ", imageType: " + imageType + ", imuDeltaTime :" + imuDeltaTime + ", System.nanoTime() " + System.nanoTime());
        } else if(cameraType == CAMERA2) {
            // do nothing
//            Log.e(TAG, "convertImageTimestamp timeStamp: " + timestamp + ", systemTime: " + SystemClock.elapsedRealtimeNanos()
//                    + ", imageType: " + imageType + ", imuDeltaTime :" + imuDeltaTime + ", System.nanoTime() " + System.nanoTime());
        } else {
            Log.e(TAG, "convertImageTimestamp cameraType error");
        }
        return result;
    }
}
