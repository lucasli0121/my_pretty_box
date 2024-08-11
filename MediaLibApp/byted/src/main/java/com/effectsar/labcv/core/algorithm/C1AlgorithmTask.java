package com.effectsar.labcv.core.algorithm;

import android.content.Context;

import com.effectsar.labcv.core.algorithm.base.AlgorithmResourceProvider;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTask;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTaskKey;
import com.effectsar.labcv.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.effectsar.labcv.core.license.EffectLicenseProvider;
import com.effectsar.labcv.core.util.timer_record.LogTimerRecord;
import com.effectsar.labcv.effectsdk.BefC1Info;
import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants;
import com.effectsar.labcv.effectsdk.C1Detect;
import com.effectsar.labcv.licenselibrary.EffectsSDKLicenseInterface;

import java.nio.ByteBuffer;

public class C1AlgorithmTask extends AlgorithmTask<C1AlgorithmTask.C1ResourceProvider, BefC1Info> {

    public static final AlgorithmTaskKey C1 = AlgorithmTaskKeyFactory.create("c1", true);

    private final C1Detect mDetector;

    public C1AlgorithmTask(Context context, C1ResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);

        mDetector = new C1Detect();
    }

    @Override
    public int initTask() {
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        String licensePath = mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.C1);
        int ret = mDetector.init(EffectsSDKEffectConstants.C1ModelType.BEF_AI_C1_MODEL_SMALL, mResourceProvider.c1Model(), licensePath,
                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initC1", ret)) return ret;
        ret = mDetector.setParam(EffectsSDKEffectConstants.C1ParamType.BEF_AI_C1_USE_MultiLabels, 1);
        if (!checkResult("initC1", ret)) return ret;
        return ret;
    }

    @Override
    public BefC1Info process(ByteBuffer buffer, int width, int height, int stride, EffectsSDKEffectConstants.PixlFormat pixlFormat, EffectsSDKEffectConstants.Rotation rotation) {

        LogTimerRecord.RECORD("c1");
        BefC1Info c1Info = mDetector.detect(buffer, pixlFormat, width, height, stride, rotation);
        LogTimerRecord.STOP("c1");
        return c1Info;
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
        return C1;
    }

    public interface C1ResourceProvider extends AlgorithmResourceProvider {
        String c1Model();
    }
}
