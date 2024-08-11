package com.effectsar.labcv.core.algorithm;

import android.content.Context;

import com.effectsar.labcv.core.algorithm.base.AlgorithmResourceProvider;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTask;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTaskKey;
import com.effectsar.labcv.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.effectsar.labcv.core.license.EffectLicenseProvider;
import com.effectsar.labcv.core.util.timer_record.LogTimerRecord;
import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants;
import com.effectsar.labcv.effectsdk.PortraitMatting;
import com.effectsar.labcv.licenselibrary.EffectsSDKLicenseInterface;

import java.nio.ByteBuffer;

import static com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants.PortraitMatting.BEF_PORTAITMATTING_SMALL_MODEL;

public class PortraitMattingAlgorithmTask extends AlgorithmTask<PortraitMattingAlgorithmTask.PortraitMattingResourceProvider, PortraitMatting.MattingMask> {
    public static final AlgorithmTaskKey PORTRAIT_MATTING = AlgorithmTaskKeyFactory.create("portraitMatting", true);

    public static final boolean FLIP_ALPHA = false;

    private final PortraitMatting mDetector;

    public PortraitMattingAlgorithmTask(Context context, PortraitMattingResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);

        mDetector = new PortraitMatting();
    }

    @Override
    public int initTask() {
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        String licensePath = mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.PORTRAIT_MATTING);
        int ret = mDetector.init(mContext, mResourceProvider.portraitMattingModel(), BEF_PORTAITMATTING_SMALL_MODEL, licensePath,
                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initPortraitMatting", ret)) return ret;

        return ret;
    }

    @Override
    public PortraitMatting.MattingMask process(ByteBuffer buffer, int width, int height,
                                                  int stride, EffectsSDKEffectConstants.PixlFormat pixlFormat, EffectsSDKEffectConstants.Rotation rotation) {
        LogTimerRecord.RECORD("detectMatting");
        PortraitMatting.MattingMask mattingMask = mDetector.detectMatting(buffer, pixlFormat, width, height, stride, rotation, FLIP_ALPHA);
        LogTimerRecord.STOP("detectMatting");
        return mattingMask;
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
        return PORTRAIT_MATTING;
    }

    public interface PortraitMattingResourceProvider extends AlgorithmResourceProvider {
        String portraitMattingModel();
    }
}
