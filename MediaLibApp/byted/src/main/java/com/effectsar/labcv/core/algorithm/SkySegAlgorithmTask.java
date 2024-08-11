package com.effectsar.labcv.core.algorithm;

import android.content.Context;

import com.effectsar.labcv.core.algorithm.base.AlgorithmResourceProvider;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTask;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTaskKey;
import com.effectsar.labcv.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.effectsar.labcv.core.license.EffectLicenseProvider;
import com.effectsar.labcv.core.util.timer_record.LogTimerRecord;
import com.effectsar.labcv.effectsdk.BefSkyInfo;
import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants;
import com.effectsar.labcv.effectsdk.SkySegment;
import com.effectsar.labcv.licenselibrary.EffectsSDKLicenseInterface;

import java.nio.ByteBuffer;

public class SkySegAlgorithmTask extends AlgorithmTask<SkySegAlgorithmTask.SkeSegResourceProvider, BefSkyInfo> {

    public static final AlgorithmTaskKey SKY_SEGMENT = AlgorithmTaskKeyFactory.create("skySegment", true);

    public static final boolean FLIP_ALPHA = false;
    public static final boolean SYK_CHECK = true;
    private final SkySegment mDetector;

    public SkySegAlgorithmTask(Context context, SkySegAlgorithmTask.SkeSegResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);

        mDetector = new SkySegment();
    }

    @Override
    public int initTask() {
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        String licensePath = mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.SKY_SEG);
        int ret = mDetector.init(mContext, mResourceProvider.skySegModel(), licensePath,
                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initSkySegment", ret)) return ret;

        ret = mDetector.setParam(preferBufferSize()[0], preferBufferSize()[1]);
        if (!checkResult("SetSkySegmentParam", ret)){
            return ret;
        }
        return ret;
    }

    @Override
    public BefSkyInfo process(ByteBuffer buffer, int width, int height, int stride, EffectsSDKEffectConstants.PixlFormat pixlFormat, EffectsSDKEffectConstants.Rotation rotation) {
        LogTimerRecord.RECORD("skySegment");
        BefSkyInfo skyInfo = mDetector.detectSky(buffer, pixlFormat, width, height, stride, rotation, FLIP_ALPHA, SYK_CHECK);
        LogTimerRecord.STOP("skySegment");
        return skyInfo;
    }

    @Override
    public int destroyTask() {
        mDetector.release();
        return 0;
    }

    @Override
    public int[] preferBufferSize() {
        return new int[]{128, 224};
    }

    @Override
    public AlgorithmTaskKey key() {
        return SKY_SEGMENT;
    }

    public interface SkeSegResourceProvider extends AlgorithmResourceProvider {
        String skySegModel();
    }
}
