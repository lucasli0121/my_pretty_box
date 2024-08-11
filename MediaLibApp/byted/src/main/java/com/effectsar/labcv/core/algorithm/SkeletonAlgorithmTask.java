package com.effectsar.labcv.core.algorithm;

import android.content.Context;

import com.effectsar.labcv.core.algorithm.base.AlgorithmResourceProvider;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTask;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTaskKey;
import com.effectsar.labcv.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.effectsar.labcv.core.license.EffectLicenseProvider;
import com.effectsar.labcv.core.util.timer_record.LogTimerRecord;
import com.effectsar.labcv.effectsdk.BefSkeletonInfo;
import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants;
import com.effectsar.labcv.effectsdk.SkeletonDetect;
import com.effectsar.labcv.licenselibrary.EffectsSDKLicenseInterface;

import java.nio.ByteBuffer;

public class SkeletonAlgorithmTask extends AlgorithmTask<SkeletonAlgorithmTask.SkeletonResourceProvider, BefSkeletonInfo> {
    public static final AlgorithmTaskKey SKELETON = AlgorithmTaskKeyFactory.create("skeleton", true);

    private final SkeletonDetect mDetector;

    public SkeletonAlgorithmTask(Context context, SkeletonResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);

        mDetector = new SkeletonDetect();
    }

    @Override
    public int initTask() {
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        String licensePath = mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.SKENETON);
        int ret = mDetector.init(mContext, mResourceProvider.skeletonModel(), licensePath,
                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initSkeleton", ret)) return ret;

        return ret;
    }

    @Override
    public BefSkeletonInfo process(ByteBuffer buffer, int width, int height, int stride,
                                      EffectsSDKEffectConstants.PixlFormat pixlFormat, EffectsSDKEffectConstants.Rotation rotation) {
        LogTimerRecord.RECORD("detectSkeleton");
        BefSkeletonInfo skeletonInfo = mDetector.detectSkeleton(buffer, pixlFormat, width, height,
                stride, rotation);
        LogTimerRecord.STOP("detectSkeleton");
        return skeletonInfo;
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
        return SKELETON;
    }

    public interface SkeletonResourceProvider extends AlgorithmResourceProvider {
        String skeletonModel();
    }
}
