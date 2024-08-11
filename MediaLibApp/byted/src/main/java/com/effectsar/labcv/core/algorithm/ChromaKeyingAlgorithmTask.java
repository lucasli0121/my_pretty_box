package com.effectsar.labcv.core.algorithm;

import android.content.Context;

import com.effectsar.labcv.core.algorithm.base.AlgorithmResourceProvider;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTask;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTaskKey;
import com.effectsar.labcv.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.effectsar.labcv.core.license.EffectLicenseProvider;
import com.effectsar.labcv.core.util.timer_record.LogTimerRecord;
import com.effectsar.labcv.effectsdk.BefChromaKeyingInfo;
import com.effectsar.labcv.effectsdk.BefDynamicGestureInfo;
import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants;
import com.effectsar.labcv.effectsdk.ChromaKeying;
import com.effectsar.labcv.effectsdk.DynamicGestureDetect;
import com.effectsar.labcv.licenselibrary.EffectsSDKLicenseInterface;

import java.nio.ByteBuffer;

public class ChromaKeyingAlgorithmTask extends AlgorithmTask<ChromaKeyingAlgorithmTask.ChromaKeyingResourceProvider, BefChromaKeyingInfo> {

    public static final AlgorithmTaskKey CHROMA_KEYING = AlgorithmTaskKeyFactory.create("chromaKeying", true);
    public static final AlgorithmTaskKey CHROMA_KEYING_SOFT = AlgorithmTaskKeyFactory.create("chromaKeyingSoft", false);

    private final ChromaKeying mDetector;

    public ChromaKeyingAlgorithmTask(Context context, ChromaKeyingResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);
        mDetector = new ChromaKeying();
    }

    @Override
    public int initTask() {
        boolean onlineLicenseFlag = mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE;
        int ret = mDetector.init(mContext, mResourceProvider.chromaKeyingModel(), mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.CHROMA_KEYING), onlineLicenseFlag);
        if (!checkResult("initChromaKeying", ret)) return ret;
        //  {zh} 设置绿幕抠图算法处理参数  {en} Set green screen cutout algorithm processing parameters
        mDetector.setProcessParam(0.25f, 0.5f, 0.1f, 0.5f, 1.0f);
        return ret;
    }

    @Override
    public BefChromaKeyingInfo process(ByteBuffer buffer, int width, int height, int stride, EffectsSDKEffectConstants.PixlFormat pixlFormat, EffectsSDKEffectConstants.Rotation rotation) {
        boolean softProcess = getBoolConfig(CHROMA_KEYING_SOFT);
        LogTimerRecord.RECORD("chromaKeying");
        BefChromaKeyingInfo info = mDetector.detect(buffer, pixlFormat, width, height, stride, rotation, softProcess);
        LogTimerRecord.STOP("chromaKeying");
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
        return CHROMA_KEYING;
    }


    public interface ChromaKeyingResourceProvider extends AlgorithmResourceProvider {
        String chromaKeyingModel();
    }
}
