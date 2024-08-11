package com.effectsar.labcv.core.algorithm;

import android.content.Context;

import com.effectsar.labcv.core.algorithm.base.AlgorithmResourceProvider;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTask;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTaskKey;
import com.effectsar.labcv.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.effectsar.labcv.core.license.EffectLicenseProvider;
import com.effectsar.labcv.core.util.timer_record.LogTimerRecord;
import com.effectsar.labcv.effectsdk.BefDynamicGestureInfo;
import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants;
import com.effectsar.labcv.effectsdk.DynamicGestureDetect;
import com.effectsar.labcv.effectsdk.FaceDetect;
import com.effectsar.labcv.licenselibrary.EffectsSDKLicenseInterface;

import java.nio.ByteBuffer;

public class DynamicGestureAlgorithmTask extends AlgorithmTask<DynamicGestureAlgorithmTask.DynamicGestureResourceProvider, BefDynamicGestureInfo> {

    public static final AlgorithmTaskKey DYNAMIC_GESTURE = AlgorithmTaskKeyFactory.create("dynamicGesture", true);

    private final DynamicGestureDetect mDetector;

    public DynamicGestureAlgorithmTask(Context context, DynamicGestureResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);
        mDetector = new DynamicGestureDetect();
    }

    @Override
    public int initTask() {
        boolean onlineLicenseFlag = mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE;
        int ret = mDetector.init(mContext, mResourceProvider.dynamicGestureModel(),
                mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.DYNAMIC_GESTURE), onlineLicenseFlag);
        if (!checkResult("initDynamicGesture", ret)) return ret;
        return ret;
    }

    @Override
    public BefDynamicGestureInfo process(ByteBuffer buffer, int width, int height, int stride, EffectsSDKEffectConstants.PixlFormat pixlFormat, EffectsSDKEffectConstants.Rotation rotation) {
        LogTimerRecord.RECORD("dynamicGesture");
        BefDynamicGestureInfo info = mDetector.detect(buffer, pixlFormat, width, height, stride, rotation);
        LogTimerRecord.STOP("dynamicGesture");
        return info;
    }

    @Override
    public int destroyTask() {
        mDetector.release();
        return 0;
    }

    @Override
    public int[] preferBufferSize() {
        return new int[0];
    }

    @Override
    public AlgorithmTaskKey key() {
        return DYNAMIC_GESTURE;
    }


    public interface DynamicGestureResourceProvider extends AlgorithmResourceProvider {
        String dynamicGestureModel();
    }
}
