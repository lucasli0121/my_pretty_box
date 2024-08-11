package com.effectsar.labcv.core.algorithm;

import android.content.Context;

import com.effectsar.labcv.core.algorithm.base.AlgorithmResourceProvider;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTask;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTaskKey;
import com.effectsar.labcv.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.effectsar.labcv.core.license.EffectLicenseProvider;
import com.effectsar.labcv.core.util.timer_record.LogTimerRecord;
import com.effectsar.labcv.effectsdk.BefDynamicGestureInfo;
import com.effectsar.labcv.effectsdk.BefSkinSegInfo;
import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants;
import com.effectsar.labcv.effectsdk.SkinSegmentation;
import com.effectsar.labcv.licenselibrary.EffectsSDKLicenseInterface;

import java.nio.ByteBuffer;

public class SkinSegmentationAlgorithmTask extends AlgorithmTask<SkinSegmentationAlgorithmTask.SkinSegmentationResourceProvider, BefSkinSegInfo> {

    public static final AlgorithmTaskKey SKIN_SEGMENTATION = AlgorithmTaskKeyFactory.create("skinSegmentation", true);

    private final SkinSegmentation mDetector;

    public SkinSegmentationAlgorithmTask(Context context, SkinSegmentationResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);
        mDetector = new SkinSegmentation();
    }

    @Override
    public int initTask() {
        boolean onlineLicenseFlag = mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE;
        int ret = mDetector.init(mContext, mResourceProvider.skinSegmentationModel(),
                mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.SKIN_SEGMENTATION), onlineLicenseFlag);
        if (!checkResult("initSkinSegmentation", ret)) return ret;
        return ret;
    }

    @Override
    public BefSkinSegInfo process(ByteBuffer buffer, int width, int height, int stride, EffectsSDKEffectConstants.PixlFormat pixlFormat, EffectsSDKEffectConstants.Rotation rotation) {
        LogTimerRecord.RECORD("skinSegmentation");
        BefSkinSegInfo info = mDetector.detect(buffer, pixlFormat, width, height, stride, rotation);
        LogTimerRecord.STOP("skinSegmentation");
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
        return SKIN_SEGMENTATION;
    }


    public interface SkinSegmentationResourceProvider extends AlgorithmResourceProvider {
        String skinSegmentationModel();
    }
}
