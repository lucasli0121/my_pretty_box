package com.effectsar.labcv.core.util.timer_record;

import android.util.Log;

public class LogTimerRecord extends TimerRecord {
    private volatile static LogTimerRecord sInstance;

    private static LogTimerRecord getInstance() {
        if (sInstance == null) {
            synchronized (LogTimerRecord.class) {
                if (sInstance == null) {
                    sInstance = new LogTimerRecord(1000, true, false);
                }
            }
        }
        return sInstance;
    }

    public static synchronized void RECORD(String tag) {
        getInstance().record(tag);
    }

    public static synchronized void STOP(String tag) {
        getInstance().stop(tag);
    }

    public LogTimerRecord() {
        super();
    }

    public LogTimerRecord(int maxRound, boolean showTime, boolean showAverage) {
        super(maxRound, showTime, showAverage);
    }

    @Override
    protected void recordOnce(String tag, long time) {
        Log.i(TAG, String.format("%s %f", tag, time * 0.000001));
    }

    @Override
    protected void recordAverage(String tag, double time, int round) {
        Log.i(TAG, String.format("%s average time is %f ms in %d round", tag, time * 0.000001, round));
    }
}
