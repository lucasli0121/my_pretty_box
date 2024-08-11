package com.effectsar.labcv.core.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants;

public class LogUtils {
    private static boolean isDebug = true;


    private static final String TAG = EffectsSDKEffectConstants.TAG;

    public static void v(String msg) {
        if (isDebug){
            Log.v(TAG, msg);
        }

    }

    public static void i(String msg)
    {
        if (isDebug){
            Log.i(TAG, msg);
        }
    }

    public static void d(String msg) {
        if (isDebug){
            Log.d(TAG, msg);

        }
    }

    public static void e(String msg) {
        Log.e(TAG, msg);
    }


    public static void syncIsDebug(Context context) {
            isDebug = context.getApplicationInfo() != null &&
                    (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
            Log.d(TAG, "isDebug ="+isDebug);

    }
}

