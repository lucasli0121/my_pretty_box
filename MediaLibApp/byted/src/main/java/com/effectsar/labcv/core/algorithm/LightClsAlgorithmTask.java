package com.effectsar.labcv.core.algorithm;

import android.content.Context;

import com.effectsar.labcv.core.algorithm.base.AlgorithmResourceProvider;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTask;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTaskKey;
import com.effectsar.labcv.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.effectsar.labcv.core.license.EffectLicenseProvider;
import com.effectsar.labcv.core.util.timer_record.LogTimerRecord;
import com.effectsar.labcv.effectsdk.BefLightclsInfo;
import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants;
import com.effectsar.labcv.effectsdk.LightClsDetect;
import com.effectsar.labcv.licenselibrary.EffectsSDKLicenseInterface;

import java.nio.ByteBuffer;

public class LightClsAlgorithmTask extends AlgorithmTask<LightClsAlgorithmTask.LightClsResourceProvider, BefLightclsInfo> {
    public static final AlgorithmTaskKey LIGHT_CLS = AlgorithmTaskKeyFactory.create("lightCls", true);

    private static final int FPS = 5;

    private final LightClsDetect mDetector;

    public LightClsAlgorithmTask(Context context, LightClsResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);

        mDetector = new LightClsDetect();
    }

    @Override
    public int initTask() {
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        String licensePath = mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.LIGHT_CLS);
        int ret = mDetector.init(mContext, mResourceProvider.lightClsModel(), licensePath, FPS,
                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initLightCls", ret)) return ret;

        return ret;
    }

    @Override
    public BefLightclsInfo process(ByteBuffer buffer, int width, int height, int stride, EffectsSDKEffectConstants.PixlFormat pixlFormat, EffectsSDKEffectConstants.Rotation rotation) {
        LogTimerRecord.RECORD("detectLight");
        BefLightclsInfo lightclsInfo = mDetector.detectLightCls(buffer, pixlFormat, width, height, stride, rotation);
        LogTimerRecord.STOP("detectLight");
        return lightclsInfo;
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
        return LIGHT_CLS;
    }

    public interface LightClsResourceProvider extends AlgorithmResourceProvider {
        String lightClsModel();
    }
}
