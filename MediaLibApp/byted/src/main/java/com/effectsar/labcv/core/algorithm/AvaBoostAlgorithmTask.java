package com.effectsar.labcv.core.algorithm;

import android.content.Context;

import com.effectsar.labcv.core.algorithm.base.AlgorithmResourceProvider;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTask;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTaskKey;
import com.effectsar.labcv.core.license.EffectLicenseProvider;
import com.effectsar.labcv.core.util.timer_record.LogTimerRecord;
import com.effectsar.labcv.effectsdk.AvaBoost;
import com.effectsar.labcv.effectsdk.BefAvaBoostInfo;
import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants;
import com.effectsar.labcv.licenselibrary.EffectsSDKLicenseInterface;

import java.nio.ByteBuffer;

public class AvaBoostAlgorithmTask extends AlgorithmTask<AvaBoostAlgorithmTask.AvaBoostResourceProvider, BefAvaBoostInfo> {
    public static final AlgorithmTaskKey AVABOOST = AlgorithmTaskKey.createKey("AVABOOST", true);
    private final AvaBoost mDetect;

    public AvaBoostAlgorithmTask(Context context, AvaBoostAlgorithmTask.AvaBoostResourceProvider resourceProvider, EffectLicenseProvider provider){
        super(context, resourceProvider, provider);
        mDetect = new AvaBoost();
    }

    @Override
    public int initTask() {
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        String licensePath = mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.AVABOOST);
        boolean onlineLicense = mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE;
        int ret = mDetect.init(mContext, mResourceProvider.avaBoostModel(), licensePath, onlineLicense);
        if (!checkResult("initAvaBoost", ret)) return ret;
        return ret;
    }

    @Override
    public BefAvaBoostInfo process(ByteBuffer buffer, int width, int height, int stride, EffectsSDKEffectConstants.PixlFormat pixlFormat, EffectsSDKEffectConstants.Rotation rotation) {
        if (!mDetect.isInited()) return null;
        LogTimerRecord.RECORD("detectAvaBoost");
        BefAvaBoostInfo info = mDetect.detect(buffer, pixlFormat, width, height, stride, rotation);
        LogTimerRecord.STOP("detectAvaBoost");
        return info;
    }

    @Override
    public int destroyTask() {
        mDetect.release();
        return 0;
    }

    @Override
    public int[] preferBufferSize() {
        return new int[0];
    }

    @Override
    public AlgorithmTaskKey key() {
        return AVABOOST;
    }

    public interface AvaBoostResourceProvider extends AlgorithmResourceProvider {
        String avaBoostModel();
    }
}
