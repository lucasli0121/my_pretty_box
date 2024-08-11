package com.effectsar.labcv.core.algorithm;

import android.content.Context;

import com.effectsar.labcv.core.algorithm.base.AlgorithmResourceProvider;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTask;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTaskKey;
import com.effectsar.labcv.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.effectsar.labcv.core.license.EffectLicenseProvider;
import com.effectsar.labcv.core.util.timer_record.LogTimerRecord;
import com.effectsar.labcv.effectsdk.BefLicenseCakeInfo;
import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants;
import com.effectsar.labcv.effectsdk.LicenseCakeDetect;
import com.effectsar.labcv.licenselibrary.EffectsSDKLicenseInterface;

import java.nio.ByteBuffer;

public class LicenseCakeAlgorithmTask extends AlgorithmTask<LicenseCakeAlgorithmTask.LicenseCakeResourceProvider, BefLicenseCakeInfo> {

    public static final AlgorithmTaskKey LICENSE_CAKE = AlgorithmTaskKeyFactory.create("licenseface_detection", true);

    private final LicenseCakeDetect mDetector;

    public LicenseCakeAlgorithmTask(Context context, LicenseCakeResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);
        mDetector = new LicenseCakeDetect();
    }

    @Override
    public int initTask() {
        boolean onlineLicenseFlag = mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE;
        int ret = mDetector.init(mContext, mResourceProvider.licenseCakeModel(),
                mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.LICENSE_CAKE), onlineLicenseFlag);
        if (!checkResult("initLicenseCake", ret)) return ret;
        return ret;
    }

    @Override
    public BefLicenseCakeInfo process(ByteBuffer buffer, int width, int height, int stride, EffectsSDKEffectConstants.PixlFormat pixlFormat, EffectsSDKEffectConstants.Rotation rotation) {
        LogTimerRecord.RECORD("LicenseCake");
        BefLicenseCakeInfo info = mDetector.detect(buffer, pixlFormat, width, height, stride, rotation);
        LogTimerRecord.STOP("LicenseCake");
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
        return LICENSE_CAKE;
    }


    public interface LicenseCakeResourceProvider extends AlgorithmResourceProvider {
        String licenseCakeModel();
    }
}
